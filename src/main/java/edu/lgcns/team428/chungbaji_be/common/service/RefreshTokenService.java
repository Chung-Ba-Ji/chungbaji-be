package edu.lgcns.team428.chungbaji_be.common.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

// refresh token 관리용 redis
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 만료기간 지정(7일)
    private static final long REFRESH_TOKEN_TTL = 7 * 60 * 60 * 24;

    public void saveToken(String email, String refreshToken) {
        System.out.println("refreshtokenservice save token");
        redisTemplate.opsForValue()
                .set("RT:" + email, refreshToken, REFRESH_TOKEN_TTL, TimeUnit.SECONDS);
    }

    public void deleteToken(String email) {
        System.out.println("refreshtokenservice delete token");
        redisTemplate.delete("RT" + email);
    }

    public String findByEmail(String email) {
        System.out.println("refreshtokenservice findByEmail");
        return (String) redisTemplate.opsForValue().get("RT:" + email);
    }
}
