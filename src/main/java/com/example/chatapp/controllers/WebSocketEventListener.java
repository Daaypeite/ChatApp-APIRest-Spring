package com.example.chatapp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.chatapp.model.ChatMessage;

@Component
public class WebSocketEventListener {

	// criar uma propriedade para "manitorar" aas ocorrencias eventuais deste
	// componente
	private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

	// injecao de dependencia
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	// "escutador" de nova conexao
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		logger.info("Uma nova conexao - via web socket - acaba de ocorrer");
	}

	// "escutador" de desconexao
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String username = (String) headerAccessor.getSessionAttributes().get("username");
	
		// verificar se o nome do usuario existe.
		if (username != null) {
			logger.info("User Disconnected : " + username);
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setType(ChatMessage.MessageType.leave);
			chatMessage.setSender(username);
			messagingTemplate.convertAndSend("/topic/public", chatMessage);

		}
	}
}
