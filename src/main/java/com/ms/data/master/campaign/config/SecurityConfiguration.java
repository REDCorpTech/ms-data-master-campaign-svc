package com.ms.data.master.campaign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // semua endpoint bebas diakses
                )
                .build();
    }

        @Bean
        public CorsFilter corsFilter() {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.addAllowedOriginPattern("*://localhost:*"); // Allows all origins with "localhost" and any port
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.addAllowedMethod("*");
            corsConfiguration.addAllowedOriginPattern("https://brand.sequrra.com"); // Allows all origins with "ugkslimousine.com"
            corsConfiguration.addAllowedOriginPattern("https://brand-dev.sequrra.com"); // Allows all origins with "ugkslimousine.com"
            corsConfiguration.addAllowedOriginPattern("https://admin.sequrra.com"); // Allows all origins with "ugkslimousine.com"
            corsConfiguration.addAllowedOriginPattern("https://admin.sequrra.com"); // Allows all origins with "ugkslimousine.com"
            corsConfiguration.addAllowedOriginPattern("https://sqr.sequrra.com");
            corsConfiguration.addAllowedOriginPattern("https://api.sequrra.com/customer/webscan/sqr/authenticate");
            // Allows all origins with "ugkslimousine.com"

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfiguration);

            return new CorsFilter(source);
        }
}
