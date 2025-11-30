package com.ms.data.master.campaign.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JwtUtil {

    public String getBrandId(String token) {
        return Optional.ofNullable(JWT.decode(token.startsWith("Bearer ") ? token.substring(7) : token)
                        .getClaim("brandId")
                        .asString())
                .filter(brandId -> !brandId.trim().isEmpty())
                .orElseThrow(() -> new RuntimeException("brandId claim not found in token"));
    }
}