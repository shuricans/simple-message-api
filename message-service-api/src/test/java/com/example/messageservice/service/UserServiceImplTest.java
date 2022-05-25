package com.example.messageservice.service;

import com.example.messageservice.error.UserNotFoundException;
import com.example.persist.model.User;
import com.example.persist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    private UserDetailsService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldGetUserDetailsByUsername() {
        // given
        String username = "existingUsername";

        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(Collections.emptySet());

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        // when
        UserDetails userDetails = underTest.loadUserByUsername(username);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
    }

    @Test
    void willThrowUserNotFoundExceptionWhenUserNotFound() {
        // given
        String username = "notExistingUsername";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.loadUserByUsername(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User [" + username + "] not found.");
    }
}