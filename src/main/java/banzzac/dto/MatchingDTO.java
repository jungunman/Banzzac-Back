package banzzac.dto;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class MatchingDTO {
	
	//화면에서 받아온 조건들 ==> 디비에서 결과값 추출용
	private Integer no;
	private String memberId;
	private String walkingStyleStr;
	private ArrayList<String> walkingStyle;
	private Integer ageRangeStart;
	private Integer ageRangeEnd;
	private Integer gender;
	private String size;
	private String dogNatureStr;
	private ArrayList<String> dogNature;
	private String amountOfActivity;
	private boolean wantMatching;
	
	//디비에서 받아온 데이터들 ==> 화면으로 반환용
	private MemberDTO memberDTO;
	private ArrayList<DogDTO> dogDTOs;
	
	
	public void setWalkingStyle(ArrayList<String> walkingStyle) {
		System.out.println("walkStyle = "+walkingStyle);
		this.walkingStyle = walkingStyle;
		this.walkingStyleStr = String.join(",", walkingStyle);
		System.out.println("walkingStyleStr 2 = "+this.walkingStyleStr);
	}
	public void setWalkingStyleStr(String walkingStyleStr) {
		if(walkingStyleStr != null && !walkingStyleStr.equals("")) {
			System.out.println("walkingStyleStr = "+walkingStyleStr);
			this.walkingStyleStr = walkingStyleStr;
			this.walkingStyle = new ArrayList<>(Arrays.asList(walkingStyleStr.split(",")));
		}
	}
	
	
	public void setDogNatureStr(String dogNatureStr) {
		this.dogNatureStr = dogNatureStr;
		this.dogNature = new ArrayList<>(Arrays.asList(dogNatureStr.split(",")));
	}
	public void setDogNature(ArrayList<String> dogNature) {
		this.dogNature = dogNature;
		this.dogNatureStr = String.join(",", dogNature);
	}
	
	
}
