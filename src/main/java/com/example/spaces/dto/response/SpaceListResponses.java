package com.example.spaces.dto.response;

import com.example.spaces.entity.Space;

import java.util.List;

public record SpaceListResponses(
        List<SpaceListResponse> spaces
) {

    public static SpaceListResponses from(List<Space> spaces) {
        return new SpaceListResponses(
                spaces.stream()
                        .map(SpaceListResponse::from)
                        .toList()
        );
    }
}