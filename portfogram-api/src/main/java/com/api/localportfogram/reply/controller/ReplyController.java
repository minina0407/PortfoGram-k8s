package com.api.localportfogram.reply.controller;

import com.api.localportfogram.reply.dto.Reply;
import com.api.localportfogram.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replies")
@Tag(name = "답글 API", description = "답글 관련 API")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping
    @Operation(summary = "답글 생성", description = "답글을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "답글 생성 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.")
    })
    public ResponseEntity<Reply> createReply(
            @RequestBody Reply reply
    ) {
        Reply createdReply = replyService.createReply(reply);
        return new ResponseEntity<>(createdReply, HttpStatus.CREATED);
    }

    @PutMapping("/{replyId}")
    @Operation(summary = "답글 수정", description = "특정 답글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답글 수정 성공"),
            @ApiResponse(responseCode = "404", description = "답글을 찾을 수 없습니다.")
    })
    public ResponseEntity<Reply> updateReply(
            @PathVariable("replyId") Long replyId,
            @RequestBody Reply reply
    ) {
        Reply updatedReply = replyService.updateReply(replyId, reply);
        return new ResponseEntity<>(updatedReply, HttpStatus.OK);
    }

    @DeleteMapping("/{replyId}")
    @Operation(summary = "답글 삭제", description = "특정 답글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "답글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "답글을 찾을 수 없습니다.")
    })
    public ResponseEntity<Void> deleteReply(
            @PathVariable("replyId") Long replyId
    ) {
        replyService.deleteReply(replyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
