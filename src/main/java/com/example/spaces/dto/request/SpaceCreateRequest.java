package com.example.spaces.dto.request;

import com.example.spaces.entity.SpaceCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record SpaceCreateRequest(
        String name,

        String description,

        String address,

        @JsonProperty("thumbnail_url")
        String thumbnailUrl,

        @JsonProperty("image_urls")
        List<String> imageUrls,

        @JsonProperty("price_per_hour")
        Integer pricePerHour,

        SpaceCategory category,

        BigDecimal lat,

        BigDecimal lng
) {
}