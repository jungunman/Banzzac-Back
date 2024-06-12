package banzzac.rest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("api/oauth2/kakao")
public class KakaoController {
	
    //redirect uri에 전달된 코드 값을 가지고 Access Token을 요청한다.
    @GetMapping
    public String getAccessToken(@RequestParam("code") String code) {
        System.out.println("code = " + code);

        // 1. header 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // 2. body 생성
        //MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); //고정값
        params.add("client_id", "d1c29661caeadbbfdf7dfb03de5a298a");
        params.add("redirect_uri", "http://localhost/api/oauth2/kakao"); //등록한 redirect uri
        params.add("code", code); 
       

        // 3. header + body
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, httpHeaders);
    
        // 4. http 요청하기
        RestTemplate restTemplate = new RestTemplate();
       
        ResponseEntity<Object> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                httpEntity,
                Object.class
        );

        

        return "kakao";
    }


}