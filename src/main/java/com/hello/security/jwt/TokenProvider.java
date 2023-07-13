package com.hello.security.jwt;

import com.hello.security.auth.domain.KakaoAccount;
import com.hello.security.jwt.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final JwtRepository jwtRepository;

    private final String accessSecret;
    private final String refreshSecret;

    private final long tokenAccessValidityInMilliseconds;
    private final long tokenRefreshValidityInMilliSeconds;

    private Key accessKey;
    private Key refreshKey;

    public TokenProvider(
            JwtRepository jwtRepository,
            @Value("${jwt.accessSecret}") String accessSecret,
            @Value("${jwt.refreshSecret}") String refreshSecret,
            @Value("${jwt.token.access-validity-in-seconds}") long tokenAccessValidityInMilliseconds,
            @Value("${jwt.token.refresh-validity-in-seconds}") long tokenRefreshValidityInMilliseconds) {
        this.jwtRepository = jwtRepository;
        this.accessSecret = accessSecret;
        this.refreshSecret = refreshSecret;
        this.tokenAccessValidityInMilliseconds = tokenAccessValidityInMilliseconds * 1000; //30분
        this.tokenRefreshValidityInMilliSeconds = (tokenRefreshValidityInMilliseconds * 1000 * 60 * 24 * 7); //7일
    }

    //객체 초기화, secretKey를 Base64로 디코딩
    @Override
    public void afterPropertiesSet() {
        byte[] access = Decoders.BASE64.decode(accessSecret);
        byte[] refresh = Decoders.BASE64.decode(refreshSecret);
        this.accessKey = Keys.hmacShaKeyFor(access);
        this.refreshKey = Keys.hmacShaKeyFor(refresh);
    }

    //최초 로그인 시 토큰 생성
    public TokenDto createToken(Authentication authentication) {
        //만료 시간 long --> Date
        Date now = new Date();

        //인가된 사용자의 권한 획득
        String authorities = getAuthorities(authentication);

        //Access Token
        String accessToken = getAccessToken(authentication);

        //Refresh Token
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(new Date(now.getTime() + tokenRefreshValidityInMilliSeconds))
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .compact();

        jwtRepository.insertRefreshToken(
                authentication.getName(),
                refreshToken,
                Duration.ofMillis(tokenRefreshValidityInMilliSeconds));

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    public TokenDto createToken(OAuth2User oAuth2User) {
        //만료 시간 long --> Date
        Date now = new Date();
        String authorities = getAuthorities(oAuth2User);

        Map<String, Object> kakao_account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        String email = (String) kakao_account.get("email");
        //인가된 사용자의 권한 획득

        //Access Token
        String accessToken = Jwts.builder()
                .setSubject(oAuth2User.getName()) // 제목
                .claim(AUTHORITIES_KEY, authorities) // 정보 저장
                .setAudience(email)
                .signWith(accessKey, SignatureAlgorithm.HS512) //시크릿키 + HS512 인코딩 (서명)
                .setExpiration(new Date(now.getTime() + tokenAccessValidityInMilliseconds)) // 만료시간
                .compact();

        //Refresh Token
        String refreshToken = Jwts.builder()
                .setSubject(oAuth2User.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setAudience(email)
                .setExpiration(new Date(now.getTime() + tokenRefreshValidityInMilliSeconds))
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .compact();

        jwtRepository.insertRefreshToken(
                email,
                refreshToken,
                Duration.ofMillis(tokenRefreshValidityInMilliSeconds));

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //액세스 토큰 발급
    public String getAccessToken(Authentication authentication) {
        Date now = new Date();

        String authorities = getAuthorities(authentication);

        return Jwts.builder()
                .setSubject(authentication.getName()) // 제목
                .claim(AUTHORITIES_KEY, authorities) // 정보 저장
                .signWith(accessKey, SignatureAlgorithm.HS512) //시크릿키 + HS512 인코딩 (서명)
                .setExpiration(new Date(now.getTime() + tokenAccessValidityInMilliseconds)) // 만료시간
                .compact();
    }

    //토큰 정보 파싱(인증 객체 확인)
    public Authentication getAuthentication(String accessToken) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(accessKey) //액세스 키 검증
                .build()
                .parseClaimsJws(accessToken) //Request된 토큰 검증
                .getBody();

        //JWT Body 파싱 하여 정보 획득
        Collection<? extends GrantedAuthority> authorities = getAuthorities(claims); //권한

        User principal = new User(claims.getSubject(), "", authorities); //유저 id,password 획득
        logger.debug("principal: {}", principal);

        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities); //유저 정보, JWT, 권한 리턴
    }

    //권한 획득(JWT 바디로)
    private static Collection<? extends GrantedAuthority> getAuthorities(Claims claims) {
        return Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(",")) //JWT
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    //권한 획득(인증 객체로)
    private static String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
    private static String getAuthorities(OAuth2User oAuth2User) {
        return oAuth2User.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    //AccessToken 검증
    public boolean validateToken(String accessToken) {
        boolean result = false;
        try {
            Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(accessToken);
            result = true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return result;
    }

    //Refresh 토큰 검증
    public Authentication validateRefresh(HttpServletRequest request) {
        logger.info(">>>> RefreshToken 검증 시작");
        String refreshToken = null;

        //쿠키속 refreshToken 꺼내기
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }
        try {
            //Jwt 파싱
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(refreshKey) // refreshKey 입력
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            if (jwtRepository.getRefreshToken(claims.getSubject(), refreshToken)) {
                //권한 획득
                Collection<? extends GrantedAuthority> authorities = getAuthorities(claims);

                //유저 id,password 획득
                User principal = new User(claims.getSubject(), "", authorities);
                logger.debug("principal: {}", principal);

                return new UsernamePasswordAuthenticationToken(principal, refreshToken, authorities); //유저 정보, JWT, 권한 리턴
            }
        } catch (Exception e) {
            logger.info(" >>> RefreshToken is expired");
        }
        return null;
    }

    //만료 시간 획득
    public Long getExpiration(String jwt) {
        Date expiration
                = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    //사용자 정보 획득
    public String getAccountInfo(String jwt) {
        Claims accountInfo
                = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        return accountInfo.getSubject();
    }

}