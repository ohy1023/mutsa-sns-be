package com.likelionsns.final_project.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostMediaDto {

    private String mediaUrl;
    private Integer mediaOrder;

}
