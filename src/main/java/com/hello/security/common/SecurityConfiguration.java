package com.hello.security.common;

import com.hello.security.auth.CustomOAuth2UserService;
import com.hello.security.auth.OAuth2AuthenticationSuccessHandler;
import com.hello.security.jwt.TokenProvider;
import com.hello.security.jwt.config.*;
import com.hello.security.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RedisService redisService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;


    //AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //시큐리티 필터 Bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()
                .cors().configurationSource(configurationSource())
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), tokenProvider),
                        UsernamePasswordAuthenticationFilter.class)               //인가 필터
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), redisService, tokenProvider)) //인가 필터
                .authorizeHttpRequests()
                .antMatchers("/", "/create", "/login", "/board/**", "/boards/**","/auth/**",
                             "/api*", "/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()

                .and()
                    .oauth2Login()
                    .redirectionEndpoint(redirectionEndpointConfig -> redirectionEndpointConfig.baseUri("/*/oauth2/code/*"))
                    .defaultSuccessUrl("/login-success")
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .userInfoEndpoint().userService(customOAuth2UserService)
                .and()

                .and()
                    .csrf().disable()
                    .formLogin().disable()
                    .logout()
                    .logoutSuccessHandler(new JwtLogoutSuccessHandler(redisService, tokenProvider))
                    .deleteCookies("refreshToken") //로그아웃 처리 후 쿠키 삭제
                .and()

                    .exceptionHandling()
//                .accessDeniedHandler(jwtAccessDeniedHandler) // 인가 실패
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint(tokenProvider, redisService)) //인증 실패


                ;

        return httpSecurity.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Set-Cookie");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

