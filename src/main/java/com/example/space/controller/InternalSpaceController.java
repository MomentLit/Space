package com.example.space.controller;

import com.example.space.dto.response.SpaceMatchingContextResponse;
import com.example.space.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/spaces")
public class InternalSpaceController {

    private final SpaceService spaceService;

    @GetMapping("/{space-id}/matching-context")
    public ResponseEntity<SpaceMatchingContextResponse> getMatchingContext(
            @PathVariable("space-id") Long spaceId,
            @RequestParam("start-time")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            @RequestParam("end-time")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime
    ) {
        return ResponseEntity.ok(
                spaceService.getMatchingContext(spaceId, startTime, endTime)
        );
    }
}
