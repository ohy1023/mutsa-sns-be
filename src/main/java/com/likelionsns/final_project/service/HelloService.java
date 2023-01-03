package com.likelionsns.final_project.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public Integer sumOfDigit(Integer num) {
        int res = 0;

        while (num != 0) {
            res += num % 10;
            num /= 10;
        }
        return res;
    }

}

