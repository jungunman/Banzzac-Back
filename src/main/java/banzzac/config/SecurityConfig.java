package banzzac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import banzzac.jwt.JwtAuthenticationFilter;
import banzzac.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity  //Spring Security를 활성화
@RequiredArgsConstructor
public class SecurityConfig{
	
	/** JWT Provider를 제공하는 JwtAuthenticationFilter에 등록하기 위하여 멤버 변수로 추가한다.
	 * JwtAuthenticationFilter는 JWT 코드 내에서 해당 코드가 유효한지 판단한다. */
	private final JwtTokenProvider jwtTokenProvider;
	//private final LogoutHandler logoutHandler;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    	
        return httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(basic ->basic.disable())
                .csrf(csrf ->csrf.disable())
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(management ->management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests ->requests
                // 해당 API에 대해서는 모든 요청을 허가한다, 회원가입 API와 Login API를 추가했다. 추후에는 직접 회원 가입하는 URL도 추가해야할 듯 싶다.
                .requestMatchers("/api/login","/api/member/createMember","/api/member/createDog","/api/login/oauth2/code/kakao").permitAll()
                // USER 권한이 있어야 요청할 수 있음,
                .requestMatchers("/api/**").hasAnyRole("USER","ADMIN")
                .requestMatchers("/dashboard").hasAnyRole("ADMIN")
                .requestMatchers("/management/**").hasAnyRole("ADMIN")
                .requestMatchers("/sales/**").hasAnyRole("ADMIN")
                // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                .anyRequest().authenticated())
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class).build();
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
