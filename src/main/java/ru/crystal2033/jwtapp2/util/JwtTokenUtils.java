/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenUtils {
//    @Value("${jwt.access-secret}")
    private final SecretKey accessSecret;

//    @Value("${jwt.refresh-secret}")
    private final SecretKey refreshSecret;

    @Value("${jwt.access-lifetime}")
    private Duration jwtAccessLifetime;

    @Value("${jwt.refresh-lifetime}")
    private Duration jwtRefreshLifetime;

    public JwtTokenUtils(
            @Value("${jwt.access-secret}") String accessSecret,
            @Value("${jwt.refresh-secret}") String refreshSecret
    ) {
        this.accessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.refreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
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
        return validateToken(accessToken, accessSecret);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, refreshSecret);
    }

    //Getting all essential data from JWT accessToken (payload, header, sign)
    private Claims getAllClaimsFromAccessJWT(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromAccessToken(String token) {
        return getAllClaimsFromAccessJWT(token, accessSecret).getSubject();
    }

    public String getUsernameFromRefreshToken(String token) {
        return getAllClaimsFromAccessJWT(token, refreshSecret).getSubject();
    }

    private boolean validateToken(@NonNull String token, @NonNull SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
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
        return getAllClaimsFromAccessJWT(token, accessSecret).get("roles", List.class);
    }

//    private SecretKey getSigningKey() {
//        byte[] keyBytes = this.accessSecret.getBytes(StandardCharsets.UTF_8);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }

//Variaties for SecretKey generation
//    private SecretKey getSecretSigningKey() {
//        return Jwts.SIG.HS256.key().build();
//    }
//
//    private SecretKey getSigningKeyFromMine() {
//        byte[] keyBytes = Decoders.BASE64.decode(secret);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
}
