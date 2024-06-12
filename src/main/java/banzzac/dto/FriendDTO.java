package banzzac.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.Data;
@Component
@Data
public class FriendDTO {

	private String id,friendId,dogImg, dogName,mImg,mNickname,mStatusMesaage;
	private int block;
	
	
}
