package com.api.localportfogram.Image.controller;

import com.api.localportfogram.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/image")
    public void imageUpload(@RequestParam("file") MultipartFile files) {
       imageService.upload(files);
    }
}
