package me.jack.lat.lmsbackendmongo.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

public class JwtUtil {

    private static final Dotenv dotenv = Dotenv.configure().load();

    private static final String ACCESS_TOKEN_SECRET_KEY = dotenv.get("ACCESS_TOKEN_SECRET_KEY");
    private static final String REFRESH_TOKEN_SECRET_KEY = dotenv.get("REFRESH_TOKEN_SECRET_KEY");
    private static final int ACCESS_TOKEN_EXPIRATION_TIME = Integer.parseInt(dotenv.get("ACCESS_TOKEN_EXPIRATION_TIME"));
    private static final int REFRESH_TOKEN_EXPIRATION_TIME = Integer.parseInt(dotenv.get("REFRESH_TOKEN_EXPIRATION_TIME"));

    public static String generateAccessToken(String userId, String role) {
        return Jwts.builder().subject(userId)
                .claim("role", role).expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_KEY.getBytes()))
                .compact();
    }

    public static String generateRefreshToken(String userId) {
        return Jwts.builder().subject(userId).expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET_KEY.getBytes()))
                .compact();
    }

    public static Claims decodeAccessToken(String jwt) {
        return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_KEY.getBytes())).build().parseSignedClaims(jwt).getPayload();
    }

    public static Claims decodeRefreshToken(String jwt) {
        return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET_KEY.getBytes())).build().parseSignedClaims(jwt).getPayload();
    }

    public static Date getExpirationDateFromAccessToken(String token) {
        return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_KEY.getBytes())).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public static Date getExpirationDateFromRefreshToken(String token) {
        return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET_KEY.getBytes())).build().parseSignedClaims(token).getPayload().getExpiration();
    }
}