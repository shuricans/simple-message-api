package com.example.messageservice.api;

import com.example.persist.model.ERole;
import com.example.persist.model.Role;
import com.example.persist.model.User;
import com.example.persist.repository.MessageRepository;
import com.example.persist.repository.RoleRepository;
import com.example.persist.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@AutoConfigureMockMvc
class MessageResourceTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MessageRepository messageRepository;

    private final String username = "username";
    private final String password = "password";


    @BeforeEach
    void setUp() {
        // create role
        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_USER);
        roleUser = roleRepository.save(roleUser);
        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);

        // create user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void shouldGetForbiddenWhenTokenNotProvided() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/message")
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldGetBadRequestWhenBodyNotProvided() throws Exception {
        String token = getAccessToken();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/message")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + token);
        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldSaveMessage() throws Exception {
        String token = getAccessToken();

        String text = "new message";
        MockHttpServletRequestBuilder saveRequest = MockMvcRequestBuilders.post("/api/v1/message")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + token)
                .content(getMessageBody(username, text));
        mvc.perform(saveRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text", is(text)))
                .andExpect(jsonPath("$.username", is(username)));
    }

    @Test
    public void shouldFetchMessages() throws Exception {
        String token = getAccessToken();

        String text_1 = "1 message";
        MockHttpServletRequestBuilder saveRequest_1 = MockMvcRequestBuilders.post("/api/v1/message")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + token)
                .content(getMessageBody(username, text_1));
        mvc.perform(saveRequest_1).andExpect(status().isCreated());

        String text_2 = "2 message";
        MockHttpServletRequestBuilder saveRequest_2 = MockMvcRequestBuilders.post("/api/v1/message")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + token)
                .content(getMessageBody(username, text_2));
        mvc.perform(saveRequest_2).andExpect(status().isCreated());

        MockHttpServletRequestBuilder fetchRequest = MockMvcRequestBuilders.post("/api/v1/message")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + token)
                .content(getMessageBody(username, "history 2"));

        mvc.perform(fetchRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].text", is(text_2)))
                .andExpect(jsonPath("$.[1].text", is(text_1)));
    }

    @Test
    public void shouldGetBlankMessageExceptionForBlankMessage() throws Exception {
        String token = getAccessToken();

        MockHttpServletRequestBuilder saveRequest = MockMvcRequestBuilders.post("/api/v1/message")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + token)
                .content(getMessageBody(username, ""));
        mvc.perform(saveRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("error-0001")))
                .andExpect(jsonPath("$.message", is("Message is blank")));
    }

    @Test
    public void shouldGetUserNotFoundExceptionWhenUserNotExist() throws Exception {
        String token = getAccessToken();

        String notExistUsername = "pepe";

        MockHttpServletRequestBuilder saveRequest = MockMvcRequestBuilders.post("/api/v1/message")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + token)
                .content(getMessageBody(notExistUsername, "text"));
        mvc.perform(saveRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("error-0100")))
                .andExpect(jsonPath("$.message", is("User: " + notExistUsername + " not found.")));
    }

    @Test
    public void shouldGetForbiddenWhenTokenHasExpired() throws Exception {
        String expiredToken = "Bearer_eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4NS9hcGkvdjEvbG9naW4iLCJleHAiOjE2NTM1MDkxOTF9.ZvPqu0EV2UdLr6SWCa3sQola-23YXVMGENzVHL0NK5o";

        MockHttpServletRequestBuilder fetchRequest = MockMvcRequestBuilders.post("/api/v1/message")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", expiredToken)
                .content(getMessageBody(username, "history 5"));
        mvc.perform(fetchRequest)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error_message", matchesRegex("^The Token has expired on.*")));
    }

    private String getAccessToken() throws Exception {
        String authBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        MockHttpServletRequestBuilder authRequest = MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authBody);

        MvcResult authResult = mvc.perform(authRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andReturn();

        String response = authResult.getResponse().getContentAsString();
        return response
                .replace("{\"access_token\":\"", "")
                .replace("\"}", "");
    }

    private String getMessageBody(String newUsername, String newMessage) {
        return "{\"username\":\"" + newUsername + "\",\"message\":\"" + newMessage + "\"}";
    }
}