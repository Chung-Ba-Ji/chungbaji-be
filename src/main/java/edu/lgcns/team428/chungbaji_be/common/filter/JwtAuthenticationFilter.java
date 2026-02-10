package edu.lgcns.team428.chungbaji_be.common.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String secret;
    private Key key;

    @PostConstruct  // 서버 기동 시 딱 한번 호출되어 key값 초기화 
    private void init() {
        System.out.println("security filter init : " + secret);
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("Spring Security doFilterInternal call");

        // 요청 가로채기 : endpoint 얻어오기
        String endPoint = request.getRequestURI();
        System.out.println(">>> User Endpoint : " + endPoint);

        // 요청의 종류 (GET, POST, PUT, DELETE)
        String method = request.getMethod();
        System.out.println(">>> User Request Method : " + method);

        // preflight 실행(사전검사) -> 요청이 적절한 요청인 경우에만 헤더에 규칙을 붙여 응답
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {

            filterChain.doFilter(request, response);
            return;

        }

        // 토큰 값 가져오기
        String authHeader = request.getHeader("Authorization");
        System.out.println(">>> security filter authheader: " + authHeader);

        // 토큰의 유효성 검사(헤더값이 없거나 Bearer로 시작하지 않는 경우 : reject)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(">>> not authorizated");
            filterChain.doFilter(request, response);
            return;
        }

        // 실제 토큰값 획득
        String token = authHeader.substring(7);
        System.out.println("jwtfilter token : " + token);
        System.out.println("Spring security filter token validation check");

        // 사용자 정보를 서버에 저장하기 위한 Spring Context 사용
        try {
            // Claims = JWT의 데이터
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

            // 사용자 정보 획득(PK값 = email)
            String email = claims.getSubject();
            System.out.println(">>> JwtAuthenticationFilter claims get email : " + email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email, null, List.of());

            // 사용자의 요청과 인증정보 객체를 연결
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // spring context: SecurityContextHolder에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

}
