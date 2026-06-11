package com.example.spaces.dto.response;

import com.example.spaces.entity.Space;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SpaceCreateResponse(
        @JsonProperty("space_id")
        Long spaceId
) {

    public static SpaceCreateResponse from(Space space) {
        return new SpaceCreateResponse(space.getId());
    }
}