package com.education.backend.config;

import com.education.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder; // 👈 必须是这个类
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository; // 注入 UserRepository

    // 🏆 这里的 Bean 定义必须存在，否则 AdminController 启动就会报错
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // ⚠️ 注意：这里的路径一定不能带 /api 
                // 因为你的前端 Vite 代理已经把 /api 删掉了，发到后端的是 /login, /auth/...
                .requestMatchers("/auth/**", "/login", "/error").permitAll()
                .requestMatchers("/course/list").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, resp, authEx) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write("{\"code\": 401, \"message\": \"请先登录\"}");
                })
            )
            .formLogin(form -> form
                .loginProcessingUrl("/login") // 👈 匹配前端 axios.post('/api/login') 经过代理后的路径
                .successHandler((req, resp, auth) -> {
                    com.education.backend.entity.User user = userRepository.findByUsername(auth.getName()).get();
                    String role = user.getRoles().stream().findFirst().get().getRoleName().toLowerCase();

                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    String json = String.format("{\"code\": 200, \"message\": \"登录成功\", \"role\": \"%s\", \"firstLogin\": %b}", role, user.isFirstLogin());
                    resp.getWriter().write(json);
                })
                .failureHandler((req, resp, ex) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write("{\"code\": 401, \"message\": \"账号或密码错误\"}");
                })
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}