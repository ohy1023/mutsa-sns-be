package com.likelionsns.final_project.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    @GetMapping("/sendTest")
    public String sendTest(@RequestParam Integer roomId) {
        return "chat/sendTest";
    }

    @GetMapping("/myChat")
    public String myChat() {
        return "chat/myChatRoom";
    }

}