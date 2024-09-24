package com.example.AdrianoCoffee.Service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "IyB6EITnPO8+7Y5A5GbGaay2i31nkP+GV0wrTfhw2OrekgTQhXgZH4NHTMAXqeX1M9i7VZtwUekXR4UHeg79Efp4R7ePNr5Ep05IlQYIuKpbO9i/YSTBX5v/oFRBN1TFKtcA6JSY2eEjNUMV0XcgxMHwu5hKoCvsyuSboppHc5S2UD0sHX9ZTMIPtyRwXMhWncqve4/6O0z9L7+J9Edts65iLr5gxupVMT6yRDms/Z1lTu3q8xDg9UfVSA0JXOPU72kskH777GVdpHVfcZIVtXjVHsr3ScV8pXe0VOZP3cSHCaAQqNNsjXAcHuVPfK1OVHdA3YDfn6WOHizObKt0min0lHLSsCLniz7Ip82VxYUIbr0355IXFVAPwpeo2ycIcTk5BdhBaUh/DqDHMnpuV6sjVL40OcOMdWlvzr6UbIvZ03Y5mNRLxqZG/D66EuiQe03fcMgSFnp7xXNpmRaqnhUzyJTIPBag7fTrESGDMebZlGsUxcePkfGmQwuxfeuuTIfQxBdGV1HlYGPgBrEkg3lOzQ+j2tRq/waFLZN4VBbgkosXi7g7yi8DO4GD6LQTMyF3sTIq5to+OxINX6BIuT5Wu6CYl8j6fM8lNeHA3UPPP8eRv5lHmntUuqM2c9uG5eAiUkgkd42J+4F/T90zqKtYcNoaW8TsXF1RifrgxUvi4rKQLKw+Qd5whbqb/fMG";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims,
                                UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}