package banzzac.utill;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Data;

@Data
public class KakaoProfile {
	
	private Integer id;
    private LocalDateTime connectedAt;
    private String email;
    private String nickname;
    private String gender;
    private String phoneNumber;
    private String ageRange;

    public KakaoProfile(String jsonResponseBody){
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonResponseBody);
        System.out.println("프로필1111111111111111111111111111");
        this.id = element.getAsJsonObject().get("id").getAsInt();

        System.out.println("프로필222222222222222222222222222222");
        String connected_at = element.getAsJsonObject().get("connected_at").getAsString();
        connected_at = connected_at.substring(0, connected_at.length() - 1);
        LocalDateTime connectDateTime = LocalDateTime.parse(connected_at, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        this.connectedAt = connectDateTime;
        System.out.println("프로필33333333333333333");

        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
        this.nickname = properties.getAsJsonObject().get("nickname").getAsString();
        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
        
        System.out.println("프로필444444444444444444444444444444");
        this.email = kakaoAccount.getAsJsonObject().get("email").getAsString();
        System.out.println("이메일통과");
        
        this.phoneNumber = kakaoAccount.getAsJsonObject().get("phone_number").getAsString();
        System.out.println("폰넘버통과");
        this.ageRange = kakaoAccount.getAsJsonObject().get("age_range").getAsString();
        System.out.println("나이통과");
        this.gender = kakaoAccount.getAsJsonObject().get("gender").getAsString();
        System.out.println("성별통과ㅡㅡㅡ전체끝");
    }
}
