package com.example.persist.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @SequenceGenerator(
            name = "roles_role_id_seq",
            sequenceName = "roles_role_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "roles_role_id_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 20)
    private ERole name;

    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    private List<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return name == role.name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}