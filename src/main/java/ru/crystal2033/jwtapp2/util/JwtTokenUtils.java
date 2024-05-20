/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.util;

import io.jsonwebtoken.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class JwtTokenUtils {
    private final PrivateKey accessSecret;
    private final PublicKey accessPublic;

    private final PrivateKey refreshSecret;
    private final PublicKey refreshPublic;

    @Value("${jwt.access-lifetime}")
    private Duration jwtAccessLifetime;

    @Value("${jwt.refresh-lifetime}")
    private Duration jwtRefreshLifetime;


    public JwtTokenUtils() {
        final KeysFromFileExtractor keysFromFileExtractor = new KeysFromFileExtractor();

        try {

            this.accessPublic = keysFromFileExtractor.getPublicKeyFromFile("D:\\Paul\\Programming\\Java\\JWTApp2\\jwtRS256AccessJWT.key.pub");
            this.accessSecret = keysFromFileExtractor.getPrivateKeyFromFile("D:\\Paul\\Programming\\Java\\JWTApp2\\jwtRS256AccessJWT.key");

            this.refreshPublic = keysFromFileExtractor.getPublicKeyFromFile("D:\\Paul\\Programming\\Java\\JWTApp2\\jwtRS256RefreshJWT.key.pub");
            this.refreshSecret = keysFromFileExtractor.getPrivateKeyFromFile("D:\\Paul\\Programming\\Java\\JWTApp2\\jwtRS256RefreshJWT.key");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        try {
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(2048);
//            this.keyPair = keyPairGenerator.generateKeyPair();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
    }

    //generate by user
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); //additional data
        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", rolesList); //inside payload in JWT
        Date createdDate = new Date();
        Date expiredDate = new Date(createdDate.getTime() + jwtAccessLifetime.toMillis());
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(createdDate)
                .expiration(expiredDate)
                .signWith(accessSecret)
                .compact();
    }

    public boolean isTokenValidByPublicKey(String accessToken) {
        try {
            Jwts.parser().verifyWith(accessPublic).build().parseSignedClaims(accessToken).getPayload();
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Date createdDate = new Date();
        Date expiredDate = new Date(createdDate.getTime() + jwtRefreshLifetime.toMillis());
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(createdDate)
                .expiration(expiredDate)
                .signWith(refreshSecret)
                .compact();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, accessPublic);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, refreshPublic);
    }

    //Getting all essential data from JWT accessToken (payload, header, sign)
    private Claims getAllClaimsFromJWT(String token, PublicKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromAccessToken(String token) {
        return getAllClaimsFromJWT(token, accessPublic).getSubject();
    }

    public String getUsernameFromRefreshToken(String token) {
        return getAllClaimsFromJWT(token, refreshPublic).getSubject();
    }

    private boolean validateToken(@NonNull String token, @NonNull PublicKey publicKey) {
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    public List<String> getRoles(String token) {
        return getAllClaimsFromJWT(token, accessPublic).get("roles", List.class);
    }

}
