package org.zipli.socknet.ws;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class WsMessageController {

//    private final IMessageService messageService;
//
//    public WsMessageController(IMessageService messageService) {
//        this.messageService = messageService;
//    }
//
//    @MessageMapping("/sendMessage")
//    @SendTo("/topic")
//    public Message sendMessage(@Payload WsMessage message) {
//       return messageService.sendMessage(message);
//    }
//
//    @MessageMapping("/createChat")
//    @SendTo("/topic")
//    public void createChat(@Payload WsMessage message) {
//        messageService.createChat(message);
//    }
//
//    @MessageMapping("/removeChat")
//    @SendTo("/topic")
//    public void removeChat(@Payload WsMessage message) {
//        messageService.removeChat(message);
//    }
//
//    @MessageMapping("/updateChat")
//    @SendTo("/topic")
//    public void updateChat(@Payload WsMessage message) {
//        chatService.updateChat(message);
//    }
//
//    @MessageMapping("/joinToChat")
//    @SendTo("/topic")
//    public void joinToChat(@Payload WsMessage message) {
//        messageService.joinGlobalChat(message);
//    }
//
//    @MessageMapping("/showAllChats")
//    @SendTo("/topic")
//    public void showAllChats(@Payload WsMessage message) {
//        messageService.showAllPrivateChat(message);
//    }
//
//    @MessageMapping("/showAllMessages")
//    @SendTo("/topic")
//    public void showAllMessages(@Payload WsMessage message) {
//        messageService.showAllGlobalChat(message);
//    }

}
