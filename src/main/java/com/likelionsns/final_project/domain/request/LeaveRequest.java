package com.likelionsns.final_project.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
    private int chatNo;
    private String userName;
}
