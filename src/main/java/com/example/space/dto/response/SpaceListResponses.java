package com.example.space.dto.response;

import com.example.space.entity.Space;

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