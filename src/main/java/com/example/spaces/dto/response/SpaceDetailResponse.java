package com.example.spaces.dto.response;

import com.example.spaces.entity.Space;
import com.example.spaces.entity.SpaceImage;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpaceDetailResponse(
        @JsonProperty("space_id")
        Long spaceId,

        String name,

        String description,

        @JsonProperty("ai_summary")
        String aiSummary,

        String address,

        @JsonProperty("thumbnail_url")
        String thumbnailUrl,

        @JsonProperty("image_urls")
        List<String> imageUrls,

        @JsonProperty("price_per_hour")
        Integer pricePerHour,

        String category
) {

    public static SpaceDetailResponse from(
            Space space,
            List<SpaceImage> images
    ) {
        return new SpaceDetailResponse(
                space.getId(),
                space.getName(),
                space.getDescription(),
                space.getAiSummary(),
                space.getAddress(),
                space.getThumbnailUrl(),
                images.stream()
                        .map(SpaceImage::getImageUrl)
                        .toList(),
                space.getPricePerHour(),
                space.getCategory().name()
        );
    }
}
