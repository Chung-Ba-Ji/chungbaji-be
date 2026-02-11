package edu.lgcns.team428.chungbaji_be.common.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {
    // key값 : yaml파일에 등록해둔 key 이름으로 가져올 것
    @Value("${jwt.secret}")
    private String secret;

    // 토큰의 만료시간(단위: millisecond)
    private final Long ACCESS_TOKEN_EXPIRY = 1000L * 60 * 30; // 30분
    private final Long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 7; // 7일

    // 키 생성: 입력받은 값을 알고리즘을 이용해 인코딩해 반환(= 토큰에 서명)
    private Key getStringKey() {
        System.out.println(">>> jwt provider secret key : " + secret);

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // access token 발급
    public String createAT(String email) {
        System.out.println("provider createAT: " + email);
        return Jwts.builder().setSubject(email).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)).signWith(getStringKey())
                .compact();
    }

    // refresh token 발급
    public String createRT(String email) {
        System.out.println("provider createRT: " + email);
        return Jwts.builder().setSubject(email).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)).signWith(getStringKey())
                .compact();
    }

    // token에서 subject 추출 (= 사용자 정보 획득하여 SpringContext에 넣어두고 상태관리)
    public String getUserEmailFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // 실제 토큰값 획득
        }

        // 이메일로 사용자 정보를 저장하고 있는 객체
        Claims claims = Jwts.parser().setSigningKey(getStringKey()).parseClaimsJws(token).getBody();

        return claims.getSubject(); // 사용자의 상태 정보 반환
    }

    public long getATExpire() {
        return ACCESS_TOKEN_EXPIRY;
    }

    public long getRTExpire() {
        return REFRESH_TOKEN_EXPIRY;
    }
}
