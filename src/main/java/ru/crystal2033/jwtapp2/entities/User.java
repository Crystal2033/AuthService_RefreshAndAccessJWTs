/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 15.05.2024
 */

package ru.crystal2033.jwtapp2.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    private String password;

    private String email;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @OneToOne(cascade = CascadeType.ALL)
    private RefreshToken refreshToken;
}
