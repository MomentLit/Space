package com.example.spaces.dto.response;


import com.example.spaces.entity.Space;

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