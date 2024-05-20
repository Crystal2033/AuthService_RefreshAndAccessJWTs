/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 20.05.2024
 */

package ru.crystal2033.jwtapp2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.crystal2033.jwtapp2.dto.JWT.JwtAccessTokenRequest;
import ru.crystal2033.jwtapp2.dto.JWT.PublicKeyResponse;
import ru.crystal2033.jwtapp2.dto.JWT.RefreshJwtRequest;
import ru.crystal2033.jwtapp2.services.AccessTokenService;
import ru.crystal2033.jwtapp2.services.AuthService;

@RestController
@RequestMapping("/tokens")
public class TokenController {
    private final AuthService authService;
    private final AccessTokenService accessTokenService;

    public TokenController(AuthService authService, AccessTokenService accessTokenService) {
        this.authService = authService;
        this.accessTokenService = accessTokenService;
    }

    @GetMapping("/public-key")
    public ResponseEntity<PublicKeyResponse> getPublicKeyForAccessToken() {
        return ResponseEntity.ok(new PublicKeyResponse(accessTokenService.getPublicAccessKey()));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccessTokenWithPublicKey(@RequestBody JwtAccessTokenRequest jwtAccessTokenRequest) {
        boolean isTokenCorrect = accessTokenService.isTokenValidByPublicKey(jwtAccessTokenRequest);
        return ResponseEntity.ok("Token correctness: " + isTokenCorrect);
    }

    @PostMapping("/refresh-access")
    public ResponseEntity<?> getNewAccessAndRefreshToken(@RequestBody RefreshJwtRequest refreshJwtRequest) {
        return accessTokenService.updateAccessAndRefreshTokens(refreshJwtRequest);
    }
}
