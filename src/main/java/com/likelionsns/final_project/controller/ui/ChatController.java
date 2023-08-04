package com.likelionsns.final_project.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/createChatRoom")
    public String createChatRoom() {
        return "chat/createChatRoom";
    }

    @GetMapping("/sendMessage")
    public String sendMessage() {
        return "chat/sendMessage";
    }

    @GetMapping("/sendTest")
    public String sendTest() {
        return "chat/sendTest";
    }

    @GetMapping("/chats")
    public String chats() {
        return "chat/chats";
    }
}