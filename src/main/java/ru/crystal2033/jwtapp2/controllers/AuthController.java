/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.crystal2033.jwtapp2.dto.JwtRequest;
import ru.crystal2033.jwtapp2.dto.RefreshJwtRequest;
import ru.crystal2033.jwtapp2.dto.RegistrationUserDto;
import ru.crystal2033.jwtapp2.services.AuthService;

@RestController("/privacy")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return authService.createAuthTokens(authRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        return authService.createNewUser(registrationUserDto);
    }


    @PostMapping("/token")
    public ResponseEntity<?> getNewAccessAndRefreshToken(@RequestBody RefreshJwtRequest refreshJwtRequest){
        return authService.updateAccessAndRefreshTokens(refreshJwtRequest);
    }
}
