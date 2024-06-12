package banzzac.rest;

import java.util.ArrayList;
import java.util.Enumeration;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import banzzac.dto.ChatDTO;
import banzzac.dto.ChatroomDTO;
import banzzac.dto.MemberDTO;
import banzzac.dto.ReportDTO;
import banzzac.mapper.ChatMapper;
import banzzac.mapper.MemberMapper;
import jakarta.annotation.Resource;
import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

/*
위에서 작성한 WebSocketConfig에서

"/app"로 시작하는 대상이 있는 클라이언트에서 보낸 모든 메시지는 @MessageMapping 어노테이션이 달린 

메서드로 라우팅 됩니다.

예를 들어

"/app/chat.sendMessage" 인 메세지는 sendMessage()로 라우팅 되며

"/app/chat.addUser" 인 메시지는 addUser()로 라우팅됩니다.
*/
	
	@Resource
	ChatMapper mapper;
	
	@Resource
	MemberMapper memMapper;
	
//	// "/app/chat/rooms/{roomId}/send"로 메시지가 전송되면 이 메소드가 호출됩니다.
//    @MessageMapping("/chat/rooms/{roomId}/send")
//    // "/topic/public/rooms/{roomId}"로 메시지를 broadcast합니다.
//    @SendTo("/topic/public/rooms/{roomId}")
//    public ChatMessageResponse sendMessage(@Payload ChatMessageRequest message) {
//        // 메시지 처리 로직 (예: 데이터베이스 저장)
//        // 이 예제에서는 받은 메시지를 그대로 반환합니다.
//
//        ChatMessageResponse response = new ChatMessageResponse();
//        response.setId(System.currentTimeMillis()); // 예제로 ID를 현재 시간으로 설정
//        response.setContent(message.getText());
//        response.setReceiver(message.getFrom());
//        response.setSender("");
//        
//        return response;
//    }
	
	@MessageMapping("/chat/rooms/{roomId}/send")
    @SendTo("/topic/public")
    public ChatDTO sendMessage(@Payload ChatDTO dto, @PathVariable String roomId) {
        mapper.insertChat(dto); // 메시지 저장
        mapper.changeLastMessage(dto);	// 채팅방 최신 상태 변경
        System.out.println("받은거냐?"+dto);
        return dto;
    }

	
	@GetMapping("/{userId}/{chatroomNo}")
	ArrayList<ChatDTO> chatList(@PathVariable String userId, @PathVariable int chatroomNo) {
		System.out.println("채팅내용 뿌려주기"+chatroomNo);
		mapper.changeIsRead(userId, chatroomNo);
		ArrayList<ChatDTO> res = mapper.getChatList(chatroomNo); 
		System.out.println("리스트 "+res);
		return res;
	}
	
	@GetMapping("/{userId}")
	ArrayList<ChatroomDTO> chatroomList(@PathVariable String userId){	//HttpSession session 멤버변수로 받아서 session.getAttribute("user").getId
		//MemberDTO sessionDTO = (MemberDTO)session.getAttribute("member");
		ArrayList<ChatroomDTO> res = mapper.getChatroomList(userId);
		System.out.println("채팅방 목록 뿌려주기");
		
		return res;
	}
	
	@PostMapping("makeChatroom")
	public ChatroomDTO insertChatroom(@RequestBody ChatroomDTO dto) {
		System.out.println("채팅방 만들기 : "+dto);
		mapper.insertChatroom(dto);
	    
		return dto;
	}
	
	@GetMapping("outChatroom/{userId}/{no}")
	public void getOutChatroom(@PathVariable String userId, @PathVariable int no) {
		System.out.println("채팅방 나가기"+no);
		mapper.outChatroom(userId, no);
		
	}
	
	@PostMapping("report/{chatroomNo}")
	public void reportUser(@RequestBody ReportDTO dto, @PathVariable int chatroomNo) {
		System.out.println("신고하기" + chatroomNo);
		memMapper.reportMember(dto);
		mapper.outChatroom(dto.getMemberId(), chatroomNo);
	}
	
	@GetMapping("block/{oppId}/{chatroomNo}")
	public void blockUser(@PathVariable String oppId,@PathVariable int chatroomNo) {
		ChatroomDTO dto = mapper.getChatroomMember(chatroomNo);
		String memberId = "";
		if(dto.getRoomMember1().equals(oppId)) {
			memberId = dto.getRoomMember2();
		}else {
			memberId = dto.getRoomMember1();
		}
		mapper.blockUser(memberId,oppId);
		mapper.outChatroom(memberId, chatroomNo);
		
	}

	
	
	
}
