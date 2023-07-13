import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;

@Service
public class NotUse_TokenProvider {

    private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private int tokenExpriationMsec = 10000;

    //토큰 생성
    public String createToken(String id){
        //set Expiration 매개변수가 Date 로 되어 있어 LocalDateTime 을 사용 하지 못함.
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, tokenExpriationMsec);
        Date expiryDate = calendar.getTime();

        Claims claims = Jwts.claims();
        claims.put("id", id);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    //토큰 검증
    public boolean validateToken(String authToken) throws JwtException{
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            e.printStackTrace();
            return false;
        }
    }



}

