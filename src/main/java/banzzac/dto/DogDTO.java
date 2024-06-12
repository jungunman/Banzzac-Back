package banzzac.dto;


import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.stereotype.Component;


import lombok.Data;


@Data
@Component
public class DogDTO {

	private String id,name,img,gender,neutrification,size,kind,personality,activity;
	private int age,weight;
	private ArrayList<String> personalityArr;
	
	public void setPersonality(String personality) {
		this.personality = personality;
		this.personalityArr = new ArrayList<>(Arrays.asList(personality.split(",")));
	}
	
	public void setPersonalityArr(ArrayList<String> personalityArr) {
		this.personalityArr = personalityArr;
		this.personality = String.join(",",personalityArr);
	}
}
