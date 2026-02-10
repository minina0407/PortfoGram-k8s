package com.api.localportfogram.comment.controller;

import com.api.localportfogram.comment.dto.Comment;
import com.api.localportfogram.comment.service.CommentService;
import com.api.localportfogram.reply.dto.Reply;
import com.api.localportfogram.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "댓글 API", description = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;
    private final ReplyService replyService;

    public CommentController(CommentService commentService, ReplyService replyService) {
        this.commentService = commentService;
        this.replyService = replyService;
    }

    @PostMapping
    @Operation(summary = "댓글 생성", description = "새로운 댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) {
        Comment createdComment = commentService.createComment(comment);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping("/{commentId}/replies")
    @Operation(summary = "댓글의 답변 조회", description = "특정 댓글에 대한 답변을 조회합니다!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답변 조회 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.") })
    public ResponseEntity<Page<Reply>> getRepliesByCommentId(
            @PathVariable("commentId") Long commentId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Reply> replies = replyService.getRepliesByCommentId(commentId, pageable);
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 댓글이 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "댓글이 입력되지 않았습니다."),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.")
    })
    public ResponseEntity<Comment> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(commentId, comment);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.")
    })
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
