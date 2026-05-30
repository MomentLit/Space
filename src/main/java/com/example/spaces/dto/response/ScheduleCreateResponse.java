package com.example.spaces.dto.response;

import com.example.spaces.entity.SpaceSchedule;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ScheduleCreateResponse(
        @JsonProperty("schedule_id")
        Long scheduleId
) {

    public static ScheduleCreateResponse from(SpaceSchedule schedule) {
        return new ScheduleCreateResponse(schedule.getId());
    }
}