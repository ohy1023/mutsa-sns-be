package com.likelionsns.final_project.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP WHERE post_id = ?")
@FilterDef(
        name = "deletedPostFilter",
        parameters = @ParamDef(name = "isDeleted", type = "boolean")
)
@Filter(
        name = "deletedPostFilter",
        condition = "(deleted_at IS NOT NULL) = :isDeleted"
)
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

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
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

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.addPost(this);
    }
}
