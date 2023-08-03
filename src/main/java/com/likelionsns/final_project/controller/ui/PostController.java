package com.likelionsns.final_project.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {

    @GetMapping("post-list")
    public String viewPosts() {
        return "post/postList";
    }

    @GetMapping("post-detail")
    public String viewDetail(@RequestParam Integer id) {
        return "post/postDetail";
    }
}
