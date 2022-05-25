package com.example.messageservice.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final String secret;

    private final String INVALID_REQUEST_CONTENT_TYPE = "Invalid request content-type.";
    private final String USER_NONE_PROVIDED = "User [NONE_PROVIDED] not found.";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            String errorMessage = "Authentication method not supported:" + request.getMethod();
            log.error(errorMessage);
            throw new AuthenticationServiceException(errorMessage);
        }
        if (request.getHeader("Content-Type") != null &&
                request.getHeader("Content-Type").contains(APPLICATION_JSON_VALUE)) {
            log.info("Trying authenticate with {} content-type", APPLICATION_JSON_VALUE);
            LoginRequest loginRequest = this.getLoginRequest(request);

            if (loginRequest.getPassword() == null) {
                log.warn("Password none provided. Bad request.");
                loginRequest.setUsername(null);
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            return authenticationManager.authenticate(authenticationToken);
        }
        log.error("Invalid request content-type");
        throw new AuthenticationServiceException(INVALID_REQUEST_CONTENT_TYPE);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

        int thirtyMinutes = 30 * 60 * 1000;

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + thirtyMinutes))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        log.info("Successful user authentication: {}", user.getUsername());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        String failedMessage = failed.getMessage();

        response.setContentType(APPLICATION_JSON_VALUE);

        int responseStatus = UNAUTHORIZED.value();

        if (failedMessage.equals(INVALID_REQUEST_CONTENT_TYPE)) {
            responseStatus = BAD_REQUEST.value();
            failedMessage += " Use " + APPLICATION_JSON_VALUE;
        }

        if (failedMessage.equals(USER_NONE_PROVIDED)) {
            responseStatus = BAD_REQUEST.value();
            failedMessage = BAD_REQUEST.toString();
        }
        response.setStatus(responseStatus);
        Map<String, String> error = new HashMap<>();

        error.put("error_message", failedMessage);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
        log.warn("Unsuccessful user authentication. {}", failedMessage);
    }

    private LoginRequest getLoginRequest(HttpServletRequest request) {

        try (BufferedReader reader = request.getReader()) {
            Gson gson = new Gson();
            return gson.fromJson(reader, LoginRequest.class);
        } catch (IOException exception) {
            log.error("Error when trying to get login credentials", exception);
            return new LoginRequest();
        }
    }

    @Data
    private static class LoginRequest {
        String username;
        String password;
    }
}