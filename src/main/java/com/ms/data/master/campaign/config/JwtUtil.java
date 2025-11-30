package com.ms.data.master.campaign.config;

import com.auth0.jwt.JWT;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public String getSub(String token) {
        try {
            // Remove "Bearer " if exists
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            return JWT.decode(token).getSubject(); // "sub" claim
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
}