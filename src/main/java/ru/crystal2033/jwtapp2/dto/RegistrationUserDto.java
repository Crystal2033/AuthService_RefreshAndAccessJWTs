/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.dto;

import lombok.Data;

@Data
public class RegistrationUserDto {
    private String username;

    private String password;

    private String email;

    private String confirmPassword;
}
