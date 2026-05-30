package com.example.spaces.dto.request;

import com.example.spaces.entity.SpaceCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpaceUpdateRequest(
        String name,

        String description,

        @JsonProperty("ai_summary")
        String aiSummary,

        @JsonProperty("thumbnail_url")
        String thumbnailUrl,

        @JsonProperty("image_urls")
        List<String> imageUrls,

        @JsonProperty("price_per_hour")
        Integer pricePerHour,

        SpaceCategory category
) {
}