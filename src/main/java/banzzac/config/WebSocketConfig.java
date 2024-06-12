package banzzac.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker	//@EnableWebSocketMessageBroker is used to enable our WebSocket server
								//WebSocket 서버를 활성화하는 데 사용됩니다.

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { 
								//웹 소켓 연결을 구성하기 위한 메서드를 구현하고 제공

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		/*
		클라이언트가 웹 소켓 서버에 연결하는 데 사용할 웹 소켓 엔드 포인트를 등록합니다.
  		엔드 포인트 구성에 withSockJS ()를 사용합니다.
  		SockJS는 웹 소켓을 지원하지 않는 브라우저에 폴백 옵션을 활성화하는 데 사용됩니다.
  		Fallback 이란?  어떤 기능이 약해지거나 제대로 동작하지 않을 때, 이에 대처하는 기능 또는 동작
  		
  		메소드 이름에 STOMP라는 단어가 있을 수 있습니다. 이러한 메서드는 Spring 프레임 워크 STOMP 구현에서 제공됩니다.
STOMP는 Simple Text Oriented Messaging Protocol의 약어입니다. 데이터 교환의 형식과 규칙을 정의하는 메시징 프로토콜입니다.
STOM를 사용하는 이유?  
WebSocket은 통신 프로토콜 일뿐입니다. 
특정 주제를 구독한 사용자에게만 메시지를 보내는 방법 또는 특정 사용자에게 메시지를 보내는 방법과 같은 내용은 정의하지 않습니다. 
이러한 기능을 위해서는 STOMP가 필요합니다.
		*/
		
		registry.addEndpoint("/chat").setAllowedOriginPatterns("*");
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		//한 클라이언트에서 다른 클라이언트로 메시지를 라우팅 하는 데 사용될 메시지 브로커를 구성하고 있습니다.
		
		registry.setApplicationDestinationPrefixes("/app");
		//- "/app" 시작되는 메시지가 message-handling methods으로 라우팅 되어야 한다는 것을 명시합니다.
		//-->메세지를  보내기  :: ChatController 로 라우팅
		
        registry.enableSimpleBroker("/topic");
        //- "/topic" 시작되는 메시지가 메시지 브로커로 라우팅 되도록 정의합니다.
        //  메시지 브로커는 특정 주제를 구독 한 연결된 모든 클라이언트에게 메시지를 broadcast 합니다.
        
	}
}
