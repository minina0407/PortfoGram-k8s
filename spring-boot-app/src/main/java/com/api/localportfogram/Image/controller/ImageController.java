package com.api.localportfogram.Image.controller;

import com.api.localportfogram.Image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "이미지 API", description = "이미지 관련 API")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/image")
    @Operation(summary = "이미지 업로드", description = "이미지를 서버에 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "이미지 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<String> imageUpload(@RequestParam("file") MultipartFile file) {
        String fileName = imageService.upload(file);
        // 업로드된 이미지에 접근할 수 있는 URI 생성
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(fileName)
                .build()
                .toUri();
        return ResponseEntity.created(location).body("Image uploaded successfully: " + location.toString());
    }
}
