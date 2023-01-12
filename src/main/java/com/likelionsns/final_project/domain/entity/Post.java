package com.likelionsns.final_project.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP WHERE post_id = ?")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer id;

    private String title;

    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "comment")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Post(Integer id, String title, String body, User user, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
        this.comments = comments;
    }

    public void updatePost(String updatedTitle, String updatedBody) {
        this.title = updatedTitle;
        this.body = updatedBody;
    }
}
