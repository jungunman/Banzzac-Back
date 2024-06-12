package banzzac.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import banzzac.utill.MessageType;
import lombok.Data;

@Data
@Component
public class ChatDTO {

	private MessageType type;
	private String senderId, senderNickname , receiverId , receiverNickname , message , sendImg;
	private int isRead , chatroomNo;
	private Date sendTime;

	
	public String getSendTimeStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(sendTime);
	}
	
	public void setOrderDateStr(String sendTime) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			this.sendTime = sdf.parse(sendTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
