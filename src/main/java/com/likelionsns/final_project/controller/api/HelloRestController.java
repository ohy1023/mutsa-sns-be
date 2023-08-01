package com.likelionsns.final_project.controller.api;

import com.likelionsns.final_project.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HelloRestController {

    private final HelloService helloService;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("hello");
    }

    @GetMapping("/hello/{num}")
    public ResponseEntity<Integer> sumOfDigit(@PathVariable Integer num) {
        Integer result = helloService.sumOfDigit(num);
        return ResponseEntity.ok().body(result);
    }

}
