/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.crystal2033.jwtapp2.dto.JwtRequest;
import ru.crystal2033.jwtapp2.dto.JwtResponse;
import ru.crystal2033.jwtapp2.exceptions.AppError;
import ru.crystal2033.jwtapp2.services.UserService;
import ru.crystal2033.jwtapp2.util.JwtTokenUtils;

@RestController
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtTokenUtils jwtTokenUtils, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

        } catch (BadCredentialsException exc) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect login or password"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.username());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
