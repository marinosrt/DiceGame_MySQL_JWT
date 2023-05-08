package S05T2N1RoyoTerolMarina.model.service.auth;

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
public class JwtServiceImpl implements JwtService {

    private static final String SECRET_KEY = "5A7134743777217A25432A462D4A404E635266556A586E3272357538782F413F";

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    /* TO PASS ANY INFORMATION I WANT TO STORE IN MY TOKEN.
     * this only generates token with not only userDetals with with extraClaims*/
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) //spring always call it "username" even if it is an email
                .setIssuedAt(new Date(System.currentTimeMillis())) //when claim created. this info will help check expiration date or if token is valid still
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) //estem dient quant de temps dura en expirar
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) //which key you want to sign this token
                .compact(); //this generates and return the token
    }

    // todo
    /*  HEM CREAT UN ALTRE MÈTODE, que només crea token amb els user details que enviem */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    //todo
    /* method to validate the token
     * we send token + user details to check if THIS TOKEN BELONGS TO THE USERDETAILS*/
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
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
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
