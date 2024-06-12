package banzzac.jwt;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;

@Data
public class MemberDetail extends User{

	private static final long serialVersionUID = 1L;
	
	private int no,isGrant;
	private String id,pwd,img,nickname;
	
	public MemberDetail(String username,
			String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username,password,authorities);
	}
	
	
}
