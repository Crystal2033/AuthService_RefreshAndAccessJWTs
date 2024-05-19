/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 19.05.2024
 */

package ru.crystal2033.jwtapp2.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name="refresh_token")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String refreshTokenName;

}
