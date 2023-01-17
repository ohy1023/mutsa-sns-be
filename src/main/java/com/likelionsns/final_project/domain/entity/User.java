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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP WHERE user_id = ?")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    private String userName;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, columnDefinition = "varchar(10) default 'USER'")
    private UserRole userRole;

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        this.userRole = this.userRole == null ? USER : this.userRole;
    }

    public User promoteRole(User user) {
        user.userRole = ADMIN;
        return user;
    }

    public User demoteRole(User user) {
        user.userRole = USER;
        return user;
    }

    @Builder
    public User(Integer id, String userName, String password, UserRole userRole) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.userRole = userRole;
    }
}
