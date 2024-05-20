/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 19.05.2024
 */

package ru.crystal2033.jwtapp2.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "refresh_token")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 4000)
    private String refreshTokenName;

}
