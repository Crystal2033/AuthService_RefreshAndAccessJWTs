/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 15.05.2024
 */

package ru.crystal2033.jwtapp2.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;

    private String username;


    private String email;

}
