package banzzac.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor

public class ChatroomDTO {

	private int ChatroomNo, unreadMessagesCount;
	private String roomMember1, roomMember2, lastMessage, 
				opponentId, memberNickname, memberImg,
				dogName, dogImg;
	private Date lastMessageSendtime;
	private Set<WebSocketSession> sessions = new HashSet<>();
	
//	   public String getLastMessageSendtimeStr() {
//		   SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//	      return sdf.format(lastMessageSendtime);
//	   }
	   
//	   public void setLastMessageSendtimeStr(String lastMessageSendtimeStr) {
//	      try {
//	         this.lastMessageSendtime = sdf.parse(lastMessageSendtimeStr);
//	      } catch (Exception e) {
//	         // TODO Auto-generated catch block
//	         e.printStackTrace();
//	      }
//	   }
	
}
