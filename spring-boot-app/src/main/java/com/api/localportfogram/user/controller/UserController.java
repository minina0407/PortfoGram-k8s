package com.api.localportfogram.user.controller;

import com.api.localportfogram.user.dto.Profile;
import com.api.localportfogram.user.dto.User;
import com.api.localportfogram.user.entity.UserEntity;
import com.api.localportfogram.user.service.FollowService;
import com.api.localportfogram.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserController is a REST controller class that handles user-related API requests and responses.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "사용자 API", description = "사용자 관련 API")
public class UserController {

   private final UserService userService;
    private final FollowService followService;

    @GetMapping("/profile")
    @Operation(summary = "프로필 조회", description = "사용자의 프로필을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없습니다.")
    })
    public ResponseEntity<Profile> getProfile(
            @RequestParam("userId") Long userId
    ) {
        Profile profile = userService.searchProfileById(userId);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }
    @PostMapping("/follow")
    @Operation(summary = "유저 팔로우", description = "다른 사용자를 팔로우합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 팔로우 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.")
    })
    public ResponseEntity<Void> followUser(@RequestParam("followingId") Long followingId) {

        followService.followUser(followingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/unfollow")
    @Operation(summary = "유저 언팔로우", description = "다른 사용자의 팔로우를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 언팔로우 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.")
    })
    public ResponseEntity<Void> unfollowUser(@RequestParam("followingId") Long followingId) {
        UserEntity user = userService.getMyUserWithAuthorities();
        followService.unfollowUser(user.getId(), followingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "회원 가입", description = "새로운 사용자를 회원으로 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 이메일입니다."),
            @ApiResponse(responseCode = "400", description = "중복된 닉네임입니다.")
    })
    public ResponseEntity<User> signUp(
            @Valid @RequestBody User user
    ) {
        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/withdrawal")
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다.")
})
    public void withdrawMembership(
            @RequestParam("userId") Long userId
    ) {
        userService.deleteMember(userId);
    }
}