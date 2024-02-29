package com.pinterest.config;

import com.pinterest.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations()
                        ).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/members/signup"),
                                new AntPathRequestMatcher("/members/login"),
                                new AntPathRequestMatcher("/boards"),
                                new AntPathRequestMatcher("/articles"),
                                new AntPathRequestMatcher("/image/**"),
                                new AntPathRequestMatcher("/"))
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .and()
                .oauth2Login()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .userInfoEndpoint().userService(customOAuth2UserService);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
