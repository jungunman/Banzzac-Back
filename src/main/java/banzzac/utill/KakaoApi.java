package banzzac.utill;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class KakaoApi {

	
	
//	private final String kakaoApiKey = "e09ad0168c41380f4ce7f8565f79f0cd";//운만
	private final String kakaoApiKey = "d1c29661caeadbbfdf7dfb03de5a298a";//성재
	private final String kakaoRedirectUri = "http://localhost/api/login/oauth2/code/kakao";

	
	
	public String getAccessToken(String code) {
		 String accessToken = "";
		 String refreshToken = "";
		 String reqUrl = "https://kauth.kakao.com/oauth/token";
		 
		 try{
		        URL url = new URL(reqUrl);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        
		        //필수 헤더 세팅
		        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		        conn.setDoOutput(true); //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
		        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		        StringBuilder sb = new StringBuilder();
		       
		        //필수 쿼리 파라미터 세팅
		        sb.append("grant_type=authorization_code");
		        sb.append("&client_id=").append(kakaoApiKey);
		        sb.append("&redirect_uri=").append(kakaoRedirectUri);
		        sb.append("&code=").append(code);
		        bw.write(sb.toString());
		        bw.flush();
		        int responseCode = conn.getResponseCode();
		        log.info("[KakaoApi.getAccessToken] responseCode = {}", responseCode);

		        BufferedReader br;
		        if (responseCode >= 200 && responseCode <= 300) {
		            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        } else {
		            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		        }

		        String line = "";
		        StringBuilder responseSb = new StringBuilder();
		        while((line = br.readLine()) != null){
		            responseSb.append(line);
		        }
		        String result = responseSb.toString();
		        JsonParser parser = new JsonParser();
		        JsonElement element = parser.parse(result);
		        accessToken = element.getAsJsonObject().get("access_token").getAsString();
		        refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
		        
		        System.out.println(accessToken);
		        br.close();
		        bw.close(); 
		    }catch (Exception e){
		        e.printStackTrace();
		    }

		 
		 return accessToken;
	}
	
	
	public KakaoProfile getUserInfo(String accessToken) {
	    String reqUrl = "https://kapi.kakao.com/v2/user/me";

	    RestTemplate rt = new RestTemplate();

	    //HttpHeader 오브젝트
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Authorization", "Bearer " + accessToken);
	    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

	    //http 헤더(headers)를 가진 엔티티
	    HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
	            new HttpEntity<>(headers);

	    //reqUrl로 Http 요청 , POST 방식
	    ResponseEntity<String> response =
	            rt.exchange(reqUrl, HttpMethod.POST, kakaoProfileRequest, String.class);

	    KakaoProfile kakaoProfile = new KakaoProfile(response.getBody());

	    return kakaoProfile;
	}

	
	
}
