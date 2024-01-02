package com.api.localportfogram.Image.dto;

import com.api.localportfogram.Image.entity.ImageEntity;
import com.api.localportfogram.portfolio.entity.PortfolioEntity;
import com.api.localportfogram.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class Images {
    private List<Image> images;

    public List<ImageEntity> toEntity(Images images, UserEntity user, PortfolioEntity post) {
        return images.getImages().stream()
                .map(image -> ImageEntity.builder()
                        .imageId(image.getImageId())
                        .fileName(image.getFileName())
                        .endPoint(image.getEndPoint())
                        .fileSize(image.getFileSize())
                        .originalFileName(image.getOriginalFileName())
                        .createdAt(image.getCreatedAt())
                        .deletedAt(image.getDeletedAt())
                        .deletedYn(image.getDeletedYn())
                        .build())
                .collect(Collectors.toList());
    }

}