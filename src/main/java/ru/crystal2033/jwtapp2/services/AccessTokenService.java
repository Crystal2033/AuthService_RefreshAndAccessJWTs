/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 20.05.2024
 */

package ru.crystal2033.jwtapp2.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.crystal2033.jwtapp2.dto.JWT.JwtAccessTokenRequest;
import ru.crystal2033.jwtapp2.dto.JWT.JwtRequest;
import ru.crystal2033.jwtapp2.dto.JWT.JwtResponse;
import ru.crystal2033.jwtapp2.dto.JWT.RefreshJwtRequest;
import ru.crystal2033.jwtapp2.entities.RefreshToken;
import ru.crystal2033.jwtapp2.exceptions.AppError;
import ru.crystal2033.jwtapp2.util.JwtTokenUtils;

@Service
public class AccessTokenService {

    private final JwtTokenUtils jwtTokenUtils;

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;


    public AccessTokenService(JwtTokenUtils jwtTokenUtils, RefreshTokenService refreshTokenService, UserService userService, AuthenticationManager authenticationManager) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    public String getPublicAccessKey() {
        return jwtTokenUtils.getPublicAccessKey();
    }

    public boolean isTokenValidByPublicKey(JwtAccessTokenRequest jwtAccessTokenRequest) {
        return jwtTokenUtils.isTokenValidByPublicKey(jwtAccessTokenRequest.accessToken());
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
            refreshTokenService.updateRefreshTokenAndDeletePreviousFromUser(authRequest.username(), refreshTokenName);
        } catch (UsernameNotFoundException exc) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), exc.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new JwtResponse(accessToken, refreshTokenName));
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
                    refreshTokenService.updateRefreshTokenAndDeletePreviousFromUser(login, newRefreshToken);
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
