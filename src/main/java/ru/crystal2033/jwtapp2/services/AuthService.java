/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.crystal2033.jwtapp2.dto.*;
import ru.crystal2033.jwtapp2.entities.RefreshToken;
import ru.crystal2033.jwtapp2.entities.User;
import ru.crystal2033.jwtapp2.exceptions.AppError;
import ru.crystal2033.jwtapp2.util.JwtTokenUtils;

import java.util.Optional;

@Service
public class AuthService {

    @Lazy
    private AuthService authService;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;


    public AuthService(UserService userService, JwtTokenUtils jwtTokenUtils, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    public boolean isTokenValidByPublicKey(JwtAccessTokenRequest jwtAccessTokenRequest) {
        return jwtTokenUtils.isTokenValidByPublicKey(jwtAccessTokenRequest.accessToken());
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }


    public ResponseEntity<?> createAuthTokens(JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        } catch (BadCredentialsException exc) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect login or password"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.username());

        String accessToken = jwtTokenUtils.generateAccessToken(userDetails);

        String refreshTokenName = jwtTokenUtils.generateRefreshToken(userDetails);
        try {
            authService.updateRefreshTokenAndDeletePreviousFromUser(authRequest.username(), refreshTokenName);
        } catch (UsernameNotFoundException exc) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), exc.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new JwtResponse(accessToken, refreshTokenName));
    }

    @Transactional
    public void updateRefreshTokenAndDeletePreviousFromUser(String username, String refreshTokenName) {
        RefreshToken refreshToken = refreshTokenService.saveToken(refreshTokenName);
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Not found user with current name: " + username);
        }
        RefreshToken prevRefreshToken = userOptional.get().getRefreshToken();
        if (prevRefreshToken != null) {
            refreshTokenService.deleteRefreshToken(prevRefreshToken);
        }
        userOptional.get().setRefreshToken(refreshToken);
        //userService.saveUser(userOptional.get()); //We don`t need this method because of @Transactional
    }

    public ResponseEntity<?> createNewUser(RegistrationUserDto registrationUserDto) {
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Not compatible passwords"), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "User with this username already exists"), HttpStatus.UNAUTHORIZED);
        }
        User user = userService.createNewUser(registrationUserDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
    }

    public ResponseEntity<?> updateAccessAndRefreshTokens(RefreshJwtRequest refreshJwtRequest) {
        final String refreshToken = refreshJwtRequest.refreshToken();

        if (jwtTokenUtils.validateRefreshToken(refreshToken)) {
            String login = jwtTokenUtils.getUsernameFromRefreshToken(refreshToken);
            UserDetails userDetails = userService.loadUserByUsername(login);

//            try {
//                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword()));
//            } catch (BadCredentialsException exc) {
//                return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect login or password"), HttpStatus.UNAUTHORIZED);
//            }

            String newRefreshToken = jwtTokenUtils.generateRefreshToken(userDetails);

            RefreshToken userRefreshToken = userService.findByUsername(login).get().getRefreshToken();
            if (userRefreshToken.getRefreshTokenName().equals(refreshToken)) {
                try {
                    authService.updateRefreshTokenAndDeletePreviousFromUser(login, newRefreshToken);
                } catch (UsernameNotFoundException exc) {
                    return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), exc.getMessage()), HttpStatus.BAD_REQUEST);
                }
                String accessToken = jwtTokenUtils.generateAccessToken(userDetails);

                return ResponseEntity.ok(new JwtResponse(accessToken, newRefreshToken));
            }

            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Refresh token is compromised"), HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Refresh token is invalid"), HttpStatus.BAD_REQUEST);
    }
}
