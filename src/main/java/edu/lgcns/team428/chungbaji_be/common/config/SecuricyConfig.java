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
        config.setAllowedOrigins(List.of("http://localhost:3000"));
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

        http.csrf(csrf -> csrf.disable()) // Cross-Site Request Forgery: 사이트 위변조 비활성화
                .cors(Customizer.withDefaults()) // CORS 규칙
                // 인가 규칙 설정
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/member/signUp",
                        "/member/login").permitAll() // 토큰 없이 접근 가능한 endPoint
                        .requestMatchers("/bookmark/**").authenticated()  
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight 처리(모든 prelight 허용)
                        .anyRequest().authenticated()

                )
                // 세션 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // filter chain 
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
