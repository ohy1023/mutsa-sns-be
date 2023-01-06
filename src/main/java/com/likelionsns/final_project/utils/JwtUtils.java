package com.likelionsns.final_project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

public class JwtUtils {
    public static String createToken(String userName, String key, Long expiredTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(HS256, key)
                .compact();
    }

    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration(); // expire timestamp를 return함
        return expiredDate.before(new Date()); // 현재보다 전인지 check를 합니다.
    }

    private static Claims extractClaims(String token, String key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    public static String getUserName(String token, String key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token)
                .getBody().get("userName", String.class);
    }
}
