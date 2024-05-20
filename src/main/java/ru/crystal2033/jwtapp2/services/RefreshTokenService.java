/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 19.05.2024
 */

package ru.crystal2033.jwtapp2.services;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.crystal2033.jwtapp2.entities.RefreshToken;
import ru.crystal2033.jwtapp2.entities.User;
import ru.crystal2033.jwtapp2.repositories.RefreshTokenRepository;

import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserService userService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    public RefreshToken saveToken(String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshTokenName(token);
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Transactional
    public void updateRefreshTokenAndDeletePreviousFromUser(String username, String refreshTokenName) {
        RefreshToken refreshToken = saveToken(refreshTokenName);
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Not found user with current name: " + username);
        }
        RefreshToken prevRefreshToken = userOptional.get().getRefreshToken();
        if (prevRefreshToken != null) {
            refreshTokenRepository.delete(prevRefreshToken);
        }
        userOptional.get().setRefreshToken(refreshToken);
        //userService.saveUser(userOptional.get()); //We don`t need this method because of @Transactional
    }
}
