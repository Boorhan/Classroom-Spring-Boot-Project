package com.demo.classroom.Security.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.demo.classroom.Utility.Constants.ExpirationTime;;

@Service
public class JwtService {

    private final String JWT_SECRET_KEY;
    private final long ACCESS_TOKEN_EXPIRATION_TIME;
    private final long REFRESH_TOKEN_EXPIRATION_TIME;
    private long currentTimeMillis = System.currentTimeMillis();

    private Set<String> blacklistedTokens = new HashSet<>();

    @Autowired
    public JwtService(Environment env) {
        this.JWT_SECRET_KEY = env.getProperty("JWT_SECRET_KEY");
        this.ACCESS_TOKEN_EXPIRATION_TIME = ExpirationTime.ACCESS_TOKEN.getValue(); 
        this.REFRESH_TOKEN_EXPIRATION_TIME= ExpirationTime.REFRESH_TOKEN.getValue();
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);  
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails, Long... userID) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
    
        if (userID.length > 0 && userID[0] != null) {
            claims.put("userId", userID[0]);
        }
        return buildToken(claims, userDetails, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String rotateRefreshToken(String refreshToken, UserDetails userDetails) {
        if (isTokenValid(refreshToken, userDetails)) {
            return generateRefreshToken(userDetails);
        }
        return null;
    }

    public String refreshAccessToken(String refreshToken, UserDetails userDetails) {
        if (isTokenValid(refreshToken, userDetails)) {
            return generateToken(userDetails);
        }
        return null;
    }

    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, userDetails, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (isTokenBlacklisted(token)) {
            return false;
        }
        return isUsernameMatching(token, userDetails) && !isTokenExpired(token);
    }

    private boolean isUsernameMatching(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
