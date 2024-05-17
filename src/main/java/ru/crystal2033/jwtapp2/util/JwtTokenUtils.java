/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    //generate by user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); //additional data
        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", rolesList); //inside payload in JWT
        Date createdDate = new Date();
        Date expiredDate = new Date(createdDate.getTime() + jwtLifetime.toMillis());
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(createdDate)
                .expiration(expiredDate)
                .signWith(getSigningKey())
                //.signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    //Getting all essential data from JWT token (payload, header, sign)
    private Claims getAllClaimsFFromJWTToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) {
        return getAllClaimsFFromJWTToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return getAllClaimsFFromJWTToken(token).get("roles", List.class);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

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
