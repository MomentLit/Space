package com.example.spaces.dto.response;

import com.example.spaces.entity.SpaceSchedule;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public record ScheduleListResponse(
        @JsonProperty("schedule_id")
        Long scheduleId,

        LocalDate date,

        @JsonProperty("time_blocks")
        List<ScheduleTimeBlockResponse> timeBlocks
) {

    public static ScheduleListResponse from(
            LocalDate date,
            List<SpaceSchedule> schedules
    ) {
        List<SpaceSchedule> sortedSchedules = schedules.stream()
                .sorted(Comparator.comparing(SpaceSchedule::getStartTime))
                .toList();

        return new ScheduleListResponse(
                sortedSchedules.get(0).getId(),
                date,
                sortedSchedules.stream()
                        .map(ScheduleTimeBlockResponse::from)
                        .toList()
        );
    }
}