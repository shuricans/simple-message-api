package com.example.persist.repository;

import com.example.persist.model.ERole;
import com.example.persist.model.Role;
import com.example.persist.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldFindUserWithRolesByUsername() {
        // given
        // create role
        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_USER);
        roleUser = roleRepository.save(roleUser);
        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);

        // create user
        String username = "shuricans";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(roles);
        underTest.save(user);

        // when
        Optional<User> result = underTest.findByUsername(username);

        // then
        assertThat(result).isPresent().hasValue(user);
        User userFromDb = result.get();
        assertThat(userFromDb.getUsername()).isEqualTo(username);
        assertThat(userFromDb.getRoles())
                .hasSize(1)
                .contains(roleUser);
    }
}