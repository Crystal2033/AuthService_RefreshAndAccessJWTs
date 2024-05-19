/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 19.05.2024
 */

package ru.crystal2033.jwtapp2.services;

import org.springframework.stereotype.Service;
import ru.crystal2033.jwtapp2.entities.RefreshToken;
import ru.crystal2033.jwtapp2.repositories.RefreshTokenRepository;

import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Optional<RefreshToken> findRefreshTokenByName(String tokenName) {
        return refreshTokenRepository.findRefreshTokenByRefreshTokenName(tokenName);
    }

    public void deleteRefreshToken(RefreshToken refreshToken){
        refreshTokenRepository.delete(refreshToken);
    }

    public RefreshToken saveToken(String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshTokenName(token);
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
}
