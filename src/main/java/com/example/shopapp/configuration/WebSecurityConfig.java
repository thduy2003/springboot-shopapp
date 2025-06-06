package com.example.shopapp.configuration;

import com.example.shopapp.filters.JwtTokenFilter;
import com.example.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

@Configuration

@EnableWebSecurity
@EnableWebMvc
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests ->  {
                    requests.requestMatchers(
                                     String.format("%s/users/register",apiPrefix),
                                    String.format("%s/users/login",apiPrefix),String.format("%s/categories**",apiPrefix)
                                    ,String.format("%s/roles",apiPrefix), String.format("%s/products/images/*",apiPrefix),
                                    "/v2/api-docs"
                                    ,"/v3/api-docs"
                                    ,"/v3/api-docs/**"
                                    ,"/swagger-resources"
                                    ,"/swagger-resources/**"
                            ,"/configuration/security"
                                    ,"/configuration/ui"
                                    ,"/swagger-ui.html"
                                    ,"/swagger-ui/**"
                                    , "/webjars/**"
                            )
                            .permitAll()
                            .requestMatchers(
                                    HttpMethod.POST, String.format("%s/orders/**",apiPrefix))
                            .hasRole(Role.USER)
                            .requestMatchers(
                                    HttpMethod.GET, String.format("%s/orders/**",apiPrefix))
                            .permitAll()
                            .requestMatchers(
                                    HttpMethod.PUT, String.format("%s/orders/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE, String.format("%s/orders/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.POST, String.format("%s/categories/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.PUT, String.format("%s/categories/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE, String.format("%s/categories/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.POST, String.format("%s/products/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.GET, String.format("%s/products/**",apiPrefix))
                            .permitAll()

                            .requestMatchers(
                                    HttpMethod.PUT, String.format("%s/products/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE, String.format("%s/products/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.POST, String.format("%s/order_details/**",apiPrefix))
                            .hasRole(Role.USER)
                            .requestMatchers(
                                    HttpMethod.GET, String.format("%s/order_details/**",apiPrefix))
                            .hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.PUT, String.format("%s/order_details/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE, String.format("%s/order_details/**",apiPrefix))
                            .hasRole(Role.ADMIN)
                            .anyRequest().authenticated();
                    });
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }
}
