package com.app.noobshop.security.config;

import com.app.noobshop.properties.JwtProperties;
import com.app.noobshop.security.filter.JwtAuthenticationFilter;
import com.app.noobshop.security.handler.JwtAccessDeniedHandler;
import com.app.noobshop.security.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（前后端分离）
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用默认表单登录
            .formLogin(AbstractHttpConfigurer::disable)
            // 禁用 HTTP Basic 认证
            .httpBasic(AbstractHttpConfigurer::disable)
            // 禁用 Session（无状态）
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 异常处理
            .exceptionHandling(exception -> {
                exception.authenticationEntryPoint(authenticationEntryPoint);
                exception.accessDeniedHandler(accessDeniedHandler);
            })
            // 请求授权配置
            .authorizeHttpRequests(auth -> auth
                // 公开接口（无需认证）
                .requestMatchers(
                    "/api/user/login/**",
                    "/api/user/refresh/**",
                    "/api/user/create/account",
                    "/api/user/forget/password"
                ).permitAll()
                .requestMatchers(
                    "/api/banner/list",
                    "/api/product/hot",
                    "/api/product/brief/list",
                    "/api/product/detail",
                    "/api/product/categroy/list",
                    "/api/product/search",
                    "/api/product/related",
                    "/api/product/spec/price",
                    "/api/product/scroll/query/list",
                    "/api/product/user/keyword/list",
                    "/api/category/**",
                    "/api/notice/**",
                    "/api/about/us/introduce"
                ).permitAll()
                // Swagger 文档
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll()
                // 静态资源
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                // 评论查看接口（可选认证，在 Filter 中处理）
                .requestMatchers("/api/user/product/comment/**/show").permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            // 添加 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

}
