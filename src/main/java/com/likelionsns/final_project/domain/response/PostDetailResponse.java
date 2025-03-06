package com.likelionsns.final_project.domain.response;

import com.likelionsns.final_project.domain.dto.PostMediaDto;
import com.likelionsns.final_project.domain.entity.Post;
import com.likelionsns.final_project.domain.entity.PostMedia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Integer postId;
    private String body;
    private String userName;
    private String nickName;
    private String userImg;
    private String registeredAt;
    private Long likeCnt;
    private Long commentCnt;
    private Boolean isLiked;
    private Boolean isOwner;
    private List<PostMediaDto> postMediaDtoList;

    public static PostDetailResponse toResponse(Post post, Boolean isOwner, Boolean isLiked, List<PostMedia> postMediaList, Long likeCnt, Long commentCnt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");

        List<PostMediaDto> postMediaDtoList = postMediaList.stream().map(
                        postMedia -> PostMediaDto.builder()
                                .mediaUrl(postMedia.getMediaUrl())
                                .mediaOrder(postMedia.getMediaOrder())
                                .build()
                )
                .collect(Collectors.toList());

        return PostDetailResponse.builder()
                .postId(post.getId())
                .userName(post.getUser().getUserName())
                .userImg(post.getUser().getUserImg())
                .body(post.getBody())
                .isOwner(isOwner)
                .isLiked(isLiked)
                .registeredAt(post.getRegisteredAt().format(formatter))
                .likeCnt(likeCnt)
                .commentCnt(commentCnt)
                .postMediaDtoList(postMediaDtoList)
                .build();
    }
}
