package com.example.spaces.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.spaces.entity.Space;

public record SpaceListResponse(
        @JsonProperty("space_id")
        Long spaceId,

        String name,

        String address,

        @JsonProperty("thumbnail_url")
        String thumbnailUrl,

        @JsonProperty("price_per_hour")
        Integer pricePerHour,

        String category
) {

    public static SpaceListResponse from(Space space) {
        return new SpaceListResponse(
                space.getId(),
                space.getName(),
                space.getAddress(),
                space.getThumbnailUrl(),
                space.getPricePerHour(),
                space.getCategory().name()
        );
    }
}