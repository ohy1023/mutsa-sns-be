package com.likelionsns.final_project.domain.entity;

import com.likelionsns.final_project.domain.enums.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static com.likelionsns.final_project.domain.enums.UserRole.*;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE users_id = ?")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Integer id;

    private String userName; // id

    private String nickName; // 이름

    private String userImg;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, columnDefinition = "varchar(10) default 'USER'")
    private UserRole userRole;

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        this.userRole = this.userRole == null ? USER : this.userRole;
        this.userImg = this.userImg == null ? "https://ficket-event-content.s3.ap-northeast-2.amazonaws.com/mutsa-sns/basic_profile.png" : this.userImg;
    }

    public void promoteRole(User user) {
        user.userRole = ADMIN;
    }

    public void demoteRole(User user) {
        user.userRole = USER;
    }

    @Builder
    public User(Integer id, String userName, String nickName, String password, UserRole userRole) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.userRole = userRole;
    }
}
