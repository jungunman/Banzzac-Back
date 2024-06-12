package banzzac.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import banzzac.dto.ChatDTO;
import banzzac.utill.MessageType;


@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    
    
    /*
     @EventListener - Spring 4.2부터는 이벤트 리스너가 ApplicationListener 인터페이스를 구현하는 Bean 일 필요가 없어졌습니다. @EventListener 주석을 통해 관리되는 Bean의 모든 public 메소드에 등록 할 수 있습니다.

해당 어노테이션은 Bean으로 등록된 Class의 메서드에서 사용할 수 있습니다.

해당 어노테이션이 적용되어 있는 메서드의 인수로 현재 SessionConnectedEvent와 SessionDisconnectEvent가

있습니다. 해당 클래스들의 상속관계를 거슬로 올라가다 보면 ApplicationEvent를 상속 받는것을 알 수 있습니다.(Spring 4.2 부터는 ApplicationEvent를 상속받지 않는 POJO클래스로도 이벤트로 사용가능하다고 합니다.)
     * */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    	
    	///// 새로운 접속 시도
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        ////브라우저를 새로고침하여 통신이 끊어짐
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            logger.info("User Disconnected : " + username);

            //// 다른 사용자에게 현재 사용자가  연결 종료 되었다는 것을 알리기 작업\
            
            //1. 메세지 객체 생성
            ChatDTO chatMessage = new ChatDTO();
            
            //2. 메세지 타입 : 떠남
            chatMessage.setType(MessageType.leave);
            
            // 3. 보내는 사람 : 현재 유저
            chatMessage.setSenderId(username);

            //4. 전송 ->  Controller 의 /topic/public 으로 메세지 전달
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }

}
