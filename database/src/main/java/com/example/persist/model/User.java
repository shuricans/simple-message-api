package com.example.persist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {

    @Id
    @SequenceGenerator(
            name = "users_user_id_seq",
            sequenceName = "users_user_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "users_user_id_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 30)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    private Set<Role> roles;

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Message> messages;
}
