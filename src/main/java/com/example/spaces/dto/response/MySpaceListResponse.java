package com.example.spaces.dto.response;

import com.example.spaces.entity.Space;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MySpaceListResponse(
        @JsonProperty("space_id")
        Long spaceId,

        String name,

        String address,

        @JsonProperty("thumbnail_url")
        String thumbnailUrl,

        @JsonProperty("price_per_hour")
        Integer pricePerHour,

        @JsonProperty("admin_status")
        String adminStatus,

        @JsonProperty("is_active")
        Boolean isActive,

        String category
) {

    public static MySpaceListResponse from(Space space) {
        return new MySpaceListResponse(
                space.getId(),
                space.getName(),
                space.getAddress(),
                space.getThumbnailUrl(),
                space.getPricePerHour(),
                space.getAdminStatus().name(),
                space.getIsActive(),
                space.getCategory().name()
        );
    }
}