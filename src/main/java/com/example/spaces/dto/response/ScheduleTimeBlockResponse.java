package com.example.spaces.dto.response;
import com.example.spaces.entity.SpaceSchedule;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ScheduleTimeBlockResponse(
        @JsonProperty("start_time")
        LocalDateTime startTime,

        @JsonProperty("end_time")
        LocalDateTime endTime,

        String status
) {

    public static ScheduleTimeBlockResponse from(SpaceSchedule schedule) {
        return new ScheduleTimeBlockResponse(
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getIsBookable() ? "AVAILABLE" : "BLOCKED"
        );
    }
}