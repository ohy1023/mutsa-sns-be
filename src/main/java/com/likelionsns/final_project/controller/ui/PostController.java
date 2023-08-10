package com.likelionsns.final_project.controller.ui;

import com.likelionsns.final_project.domain.dto.PostDto;
import com.likelionsns.final_project.domain.request.PostCreateRequest;
import com.likelionsns.final_project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("post-list")
    public String viewPosts(Model model, @PageableDefault(size = 5) Pageable pageable) {
        Page<PostDto> postDtos = postService.getAllItems(pageable);

        model.addAttribute("postList", postDtos.getContent());
        model.addAttribute("totalPages", postDtos.getTotalPages());
        model.addAttribute("number", postDtos.getNumber());
        return "post/postList";
    }

    @GetMapping("post-detail")
    public String viewDetail(@RequestParam Integer id) {
        return "post/postDetail";
    }

    // 글 작성 페이지로 이동
    @GetMapping("/createPost")
    public String showCreatePostPage() {
        return "post/createPost"; // createPost.jsp 파일을 렌더링
    }

}
