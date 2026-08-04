package com.api.localportfogram.user.entity;

import com.api.localportfogram.auth.enums.AuthEnums;
import lombok.*;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthEnums.ROLE role;

    @OneToMany(mappedBy = "follower")
    private List<FollowEntity> followers;

    @OneToMany(mappedBy = "following")
    private List<FollowEntity> followings;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private ProfileImageEntity profileImage;

    @Builder
    public UserEntity(Long id, String nickname, String password, String email, String name, AuthEnums.ROLE role,
                      List<FollowEntity> followers, List<FollowEntity> followings, ProfileImageEntity profileImage) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
        this.followers = followers;
        this.followings = followings;
        this.profileImage = profileImage;
    }
}
