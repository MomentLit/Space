package com.example.space.dto.response;


import com.example.space.entity.Space;

import java.util.List;

public record MySpaceListResponses(
        List<MySpaceListResponse> spaces
) {

    public static MySpaceListResponses from(List<Space> spaces) {
        return new MySpaceListResponses(
                spaces.stream()
                        .map(MySpaceListResponse::from)
                        .toList()
        );
    }
}