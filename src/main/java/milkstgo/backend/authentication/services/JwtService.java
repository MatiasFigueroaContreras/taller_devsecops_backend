package milkstgo.backend.authentication.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret_key}")
    private String SECRET_KEY;

    private long TOKEN_EXPIRATION_DAYS = 5;
    private long REFRESH_TOKEN_EXPIRATION_DAYS = 15;

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsFunction) {
        Claims claims = getAllClaims(token);
        return claimsFunction.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "ACCESS");
        return generateToken(claims, userDetails, TOKEN_EXPIRATION_DAYS);
    }

    public String generateRefreshToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");
        return generateToken(claims, userDetails, REFRESH_TOKEN_EXPIRATION_DAYS);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationDays){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expirationDays))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidToken(String token, UserDetails userDetails){
        String username = getUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)) && userDetails.isAccountNonLocked();
    }

    public boolean isValidAccessToken(String token, UserDetails userDetails) {
        Claims claims = getAllClaims(token);
        String type = (String) claims.get("type");
        return isValidToken(token, userDetails) && type.equals("ACCESS");
    }

    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}
