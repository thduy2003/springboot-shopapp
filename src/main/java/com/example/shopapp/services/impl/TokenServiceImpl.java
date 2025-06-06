package com.example.shopapp.services.impl;

import com.example.shopapp.models.Token;
import com.example.shopapp.models.User;
import com.example.shopapp.repositories.TokenRepository;
import com.example.shopapp.repositories.UserRepository;
import com.example.shopapp.services.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements ITokenService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    //the number of token devices
    private static final int MAX_TOKENS = 3;

    @Value("${jwt.expiration}")
    private int expiration;

    @Transactional
    @Override
    public Token addToken(User user, String token, boolean isMobile) {
        List<Token> userTokens = tokenRepository.findByUser(user);

        int tokensCount = userTokens.size();
        if(tokensCount >= MAX_TOKENS) {
            boolean hasNoMobileToken = !userTokens.stream().allMatch(Token::isMobile);
            Token tokenToDelete;
            if(hasNoMobileToken) {
                tokenToDelete = userTokens.stream()
                        .filter(userToken -> !userToken.isMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            } else {
                tokenToDelete = userTokens.get(0);
            }
            tokenRepository.delete(tokenToDelete);
        }
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .refreshToken(UUID.randomUUID().toString())
                .tokenType("Bearer")
                .expirationTime(Instant.now().plusMillis(expiration))
                .revoked(false)
                .expired(false)
                .isMobile(isMobile)
                .build();
        return tokenRepository.save(newToken);
    }
}
