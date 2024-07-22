package com.likelionsns.final_project.controller.ui;

import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final PostService postService;


    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @GetMapping("/my-posts")
    public String getMyPosts(Model model, @PageableDefault(size = 5) Pageable pageable, Authentication authentication) {
        Page<PostDto> myPost = postService.getMyPost(pageable, "test");

        model.addAttribute("postList", myPost.getContent());
        model.addAttribute("totalPages", myPost.getTotalPages());
        model.addAttribute("number", myPost.getNumber());

        return "user/myPosts";
    }
}
