/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.crystal2033.jwtapp2.dto.*;
import ru.crystal2033.jwtapp2.dto.JWT.JwtRequest;
import ru.crystal2033.jwtapp2.services.AccessTokenService;
import ru.crystal2033.jwtapp2.services.AuthService;

@RestController
@RequestMapping("/privacy")
public class AuthController {
    private final AuthService authService;

    private final AccessTokenService accessTokenService;

    public AuthController(AuthService authService, AccessTokenService accessTokenService) {
        this.authService = authService;
        this.accessTokenService = accessTokenService;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return accessTokenService.createAuthTokens(authRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        return authService.createNewUser(registrationUserDto);
    }

}
