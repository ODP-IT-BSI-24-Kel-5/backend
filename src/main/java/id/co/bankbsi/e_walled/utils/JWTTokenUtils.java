package id.co.bankbsi.e_walled.utils;

import id.co.bankbsi.e_walled.models.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JWTTokenUtils {
    @Value("${jwt.secret}")
    private String jwtKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key hmacKey;

    @PostConstruct
    public void init() {
        this.hmacKey = new SecretKeySpec(jwtKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setAudience("bankbsi.co.id")
                .claim("tokenVersion", user.getTokenVersion())
                .claim("sessionId", user.getSessionId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + getExpirationTime()))
                .signWith(hmacKey)
                .compact();
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    public boolean isTokenValid(String token, Users user) {
        try {
            UUID tokenUserId = UUID.fromString(extractSubject(token));
            Integer tokenVersion = extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));
            UUID sessionId = UUID.fromString(extractClaim(token, claims -> claims.get("sessionId", String.class)));

            return tokenUserId.equals(user.getId())
                    && tokenVersion.equals(user.getTokenVersion())
                    && sessionId.equals(user.getSessionId())
                    && !isTokenExpired(token);

        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
