package banzzac.jwt;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import banzzac.dto.MemberDTO;
import banzzac.mapper.LoginMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements UserDetailsService {
	
	
	private final LoginMapper loginMapper;
	
	/** 해당 유저가 있다면 그 유저에 대한 정보를 반환합니다.
	 * Security 에서 제공하는 UserDetails 인터페이스를 구현한 MemberDetail을 반환하게 만들었습니다.
	 * 해당 권한은 숫자로 제공되나 String으로 변환하여 List에 추가하였습니다.
	 * 이 값은 Security Config에서 권한 확인 용도로 사용됩니다.*/
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MemberDTO member = loginMapper.loginId(username);
		 if (member == null) {
	            // user가 null인 경우 예외 발생
	            throw new UsernameNotFoundException("회원을 찾을 수 없습니다.");
	     }
		String grant = "";
	
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		if(member.getIsGrant() > 3) {
			grant = "ROLE_ADMIN";
		}else if(member.getIsGrant() == 3) {
			grant = "ROLE_USER";
		}else if(member.getIsGrant() == 2) {
			grant = "ROLE_WAITER";
		}else if(member.getIsGrant() == 1) {
			grant = "ROLE_RETIRE";
		}else if(member.getIsGrant() == 0) {
			grant = "ROLE_BLACKLIST";
		}
		
		authorities.add(new SimpleGrantedAuthority(grant));
		
		MemberDetail user = new MemberDetail(member.getId(),member.getPwd(),authorities);
		
		user.setImg(member.getImg());
		user.setId(member.getId());
		user.setIsGrant(member.getIsGrant());
		user.setIsGrant(member.getIsGrant());
		user.setNickname(member.getNickname());
		user.setNo(member.getNo());
		
		return user;
	}

	public MemberDTO checkLoin(String username, String password) throws LoginException {
        MemberDTO user = loginMapper.loginId(username);

        if (user == null) {
            // user가 null인 경우 예외 발생
            throw new LoginException("유저를 찾을 수 없습니다.");
        }
        
        // password check
        if(!password.equals(user.getPwd()))
            throw new LoginException("password error");

        return user;
    }

	
	public void handleJwtToken(String userId ,JwtTokenProvider jwtTokenProvider, HttpServletResponse  response) {
		
		UserDetails userDetails  = loadUserByUsername(userId);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	
		SecurityContextHolder.getContext().setAuthentication(authentication);
		JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
		
		response.setHeader("Authorization", "Bearer " + jwtToken.getAccessToken());
		response.setStatus(HttpServletResponse.SC_OK);
		Cookie cookie = new Cookie("refreshToken", jwtToken.getRefreshToken());
        cookie.setPath("/");
        cookie.setHttpOnly(true); // HTTP Only로 설정
        cookie.setMaxAge(1000 * 60 * 60 * 24 * 3); // 쿠키의 유효 기간 설정 (초 단위)
        response.addCookie(cookie);
        
	}
	
	
}
