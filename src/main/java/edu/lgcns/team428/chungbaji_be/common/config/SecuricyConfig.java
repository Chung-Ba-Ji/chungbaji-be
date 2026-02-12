package edu.lgcns.team428.chungbaji_be.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import edu.lgcns.team428.chungbaji_be.common.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecuricyConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 패스워드 해싱
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // cors 설정(preflight 관련 설정)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        // JwtFilter에서 설정한 preflight 요청 시 응답헤더에 담는 정보 (= 혀용 가능한 요청에 대한 정보)
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // filter 관련 핵심 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 및 CORS 설정
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                // 3. 인가(Authorization) 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        // Swagger 및 API 문서 관련 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll()

                        // Preflight 요청(OPTIONS) 전체 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 인증 없이 접근 가능한 회원 관련 엔드포인트
                        .requestMatchers(
                                "/api/members/signUp",
                                "/api/members/login",
                                "/api/members/searchPwd"
                        ).permitAll()

                        // 커뮤니티 관련 조회 기능은 비인가 사용자도 이용 가능
                        .requestMatchers(HttpMethod.GET, "/api/community/**").permitAll()

                        // 인증 없이 접근 가능한 정책 관련 엔드포인트                 
                        .requestMatchers(
                                "/api/policy/**"
                        ).permitAll()
                        
                        // 인증 없이 접근 가능한 코드, 지역 관련 엔드포인트
                        .requestMatchers(
                                "/api/codes/**",
                                "/api/regions/**"
                        ).permitAll()

                        // 인증이 반드시 필요한 엔드포인트
                        .requestMatchers(
                                "/api/members/logout",
                                "/api/members/update/**",
                                "/api/members/delete/**",
                                "/api/bookmarks/**",
                                "/api/schedule/**"
                        ).authenticated()

                        // 커뮤니티 관련 조회, 수정, 삭제 기능은 인가된 사용자만 가능
                        .requestMatchers(HttpMethod.POST, "/api/community/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/community/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/community/**").authenticated()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 4. JWT 필터 배치
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}