package com.ms.data.master.campaign.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JwtUtil {

    public String getBrandId(String token) {
        return JWT.decode(token.startsWith("Bearer ") ? token.substring(7) : token)
                .getClaim("brandId")
                .asString();
    }
}