package com.api.localportfogram.portfolio.controller;


import com.api.localportfogram.portfolio.dto.Portfolio;
import com.api.localportfogram.portfolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "포트폴리오 API", description = "포트폴리오 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
@Tag(name = "포트폴리오 API", description = "포트폴리오 관련 API")
public class PortfolioController {
    private final PortfolioService portfolioService;


    @GetMapping
    @Operation(summary = "모든 포트폴리오 조회", description = "모든 포트폴리오를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    public ResponseEntity<Page<Portfolio>> getAllPortfolios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Portfolio> portfolios = portfolioService.getAllPortfolios(  pageable);

        return new ResponseEntity<>(portfolios, HttpStatus.OK);

    }

}
