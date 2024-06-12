package banzzac.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import banzzac.mapper.MemberMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtTokenProvider {
	
	@Resource
	private MemberMapper memberMapper;
	
	private final Key secretKey;
	private final long tokenValidMillSecond = 1000L * 5;
	private final long tokenValidRefresh = 1000L * 60 * 60 * 24 * 3;
	
	
	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
	     byte[] keyBytes = Decoders.BASE64.decode(secretKey);
	     this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	 // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {
    	//캐스팅
    	MemberDetail details =(MemberDetail)authentication.getPrincipal(); 
    	
    	
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
       
        long now = (new Date()).getTime();
        
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + tokenValidMillSecond);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .claim("nickname", details.getNickname())
                .claim("img", details.getImg())
                .claim("id", details.getId())
                .claim("no", details.getNo())
                .setExpiration(accessTokenExpiresIn)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + tokenValidRefresh))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
	
    
    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);
        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        
        // 클레임에서 권한 정보 가져오기
        // 클레임에서 권한 정보를 추출합니다.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 객체를 생성합니다.
        MemberDetail principal = new MemberDetail(claims.getSubject(), "", authorities);
        principal.setId(claims.get("id",String.class));
        principal.setNickname(claims.get("nickname",String.class));
        principal.setImg(claims.get("img",String.class));
        principal.setNo(claims.get("no",Integer.class));
        
        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }
    
    
    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token, HttpServletResponse response) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 토큰 서명입니다", e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다", e);
            
            response.setStatus(401);
            log.info("만료된 토큰의 Response", response);
        } catch (UnsupportedJwtException e) {
            log.info("잘못된 토큰입니다", e);
        } catch (IllegalArgumentException e) {
            log.info("잘못된 토큰입니다", e);
        }
        
        
        
        return false;
    }
    
    
    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    
	
}
