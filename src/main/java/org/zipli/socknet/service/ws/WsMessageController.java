package org.zipli.socknet.service.ws;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.zipli.socknet.dto.WsMessage;


@Controller
public class WsMessageController {

    private final IMessageService messageService;
//    private final IChatService chatService;

    public WsMessageController(IMessageService messageService/*, IChatService chatService*/) {
        this.messageService = messageService;
//        this.chatService = chatService;
    }

    @MessageMapping("/globalChat/sendMessage")
    @SendTo("/topic/chat")
    public void sendMessageGlobal(@Payload WsMessage message) {
        messageService.sendMessageGlobal(message);
    }

    @MessageMapping("/privateChat/sendMessage")
    @SendTo("/topic/chat")
    public void sendMessagePrivate(@Payload WsMessage message) {
        messageService.sendMessagePrivate(message);
    }

    @MessageMapping("/createChat")
    @SendTo("/topic/chat")
    public void createChat(@Payload WsMessage message) {
         messageService.createChat(message);
        // chatService.createChat(message);
    }

    @MessageMapping("/removeChat")
    @SendTo("/topic/chat")
    public void removeChat(@Payload WsMessage message) {
        messageService.removeChat(message);
        //chatService.removeChat(message);
    }

    @MessageMapping("/updateChat")
    @SendTo("/topic/chat")
    public void updateChat(@Payload WsMessage message) {
        messageService.updateChat(message);
       // chatService.updateChat(message);
    }

    @MessageMapping("/globalChat/join")
    @SendTo("/globalChat")
    public void joinGlobalChat(@Payload WsMessage message) {
        messageService.joinGlobalChat(message);
       // chatService.joinGlobalChat(message);
    }

    @MessageMapping("/privateChat/showAll")
    @SendTo("/privateChat")
    public void showAllPrivateChat(@Payload WsMessage message) {
        messageService.showAllPrivateChat(message);
       // chatService.showAllPrivateChat(message);
    }

    @MessageMapping("/showAllGlobalChat")
    @SendTo("/globalChat")
    public void showAllGlobalChat(@Payload WsMessage message) {
        messageService.showAllGlobalChat(message);
       // chatService.showAllGlobalChat(message);
    }
}
