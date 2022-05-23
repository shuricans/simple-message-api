package com.example.persist.repository;

import com.example.persist.model.ERole;
import com.example.persist.model.Message;
import com.example.persist.model.Role;
import com.example.persist.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    private MessageRepository underTest;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldFindMessagesByUsername() {
        // given
        // create role
        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_USER);
        roleUser = roleRepository.save(roleUser);
        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);

        // create user
        User user = new User();
        String username = "shuricans";
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(roles);
        userRepository.save(user);

        // create message
        String text = "1. Hello World!";
        Message message = new Message();
        message.setCreatedTime(LocalDateTime.now());
        message.setUser(user);
        message.setText(text);
        underTest.save(message);

        // when
        List<Message> messages = underTest.findMessagesByUsername(username);

        // then
        assertThat(messages)
                .hasSize(1)
                .contains(message);
    }
}