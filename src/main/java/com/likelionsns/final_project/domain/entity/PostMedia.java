package com.likelionsns.final_project.domain.entity;

import com.likelionsns.final_project.domain.enums.MediaType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostMedia extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String mediaUrl; // 이미지 또는 동영상 URL

    @Column(nullable = false)
    private MediaType mediaType; // "image" 또는 "video"
}
