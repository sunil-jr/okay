package com.paat.NNS.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paat.NNS.dto.Message;
import com.paat.NNS.entity.user.Authority;
import com.paat.NNS.entity.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final TokenFiler tokenFiler;
    private final AuthErrorHandling authErrorHandling;

    private static final String[] PERMIT_URL = {"/register", "/login"};

    @Bean
    public AuthenticationManager authenticationManagerBean() {
        return authentication -> {
            throw new AuthenticationServiceException("Authentication is disabled");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((e) -> e.authenticationEntryPoint(authErrorHandling))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(PERMIT_URL).permitAll().anyRequest().authenticated())
                .addFilterBefore(tokenFiler, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CorsFilter(corsConfiguration()), TokenFiler.class)
                .build();
    }

    private UrlBasedCorsConfigurationSource corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:1234"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST"));
        corsConfiguration.setMaxAge(TimeUnit.HOURS.toSeconds(1));
        corsConfiguration.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource corsSourceConfiguration = new UrlBasedCorsConfigurationSource();
        corsSourceConfiguration.registerCorsConfiguration("/**", corsConfiguration);
        return corsSourceConfiguration;
    }

    @Component
    public static class AuthErrorHandling implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Message message = new Message();
            message.setMessage("Unauthorized!!!");
            try (ServletOutputStream stream = response.getOutputStream()) {
                stream.print(new ObjectMapper().writeValueAsString(message));
                stream.flush();
            }
        }
    }

    @Component
    public static class TokenFiler extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                authorization = authorization.replace("Bearer ", "");
                // TODO: validate token
                // TODO: Get user from db
                User user = new User();

                List<GrantedAuthority> authorities = new ArrayList<>();
                for (Authority authority : user.getGroup().getAuthorities()) {
                    authorities.add(new SimpleGrantedAuthority(authority.getName()));
                }
                UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(user.getId(), user.getEmail(), authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }
    }
}
