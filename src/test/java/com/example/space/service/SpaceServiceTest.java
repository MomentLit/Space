package com.example.space.service;

import com.example.space.dto.response.SpaceMatchingContextResponse;
import com.example.space.entity.Space;
import com.example.space.entity.SpaceCategory;
import com.example.space.repository.SpaceImageRepository;
import com.example.space.repository.SpaceRepository;
import com.example.space.repository.SpaceScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpaceServiceTest {

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private SpaceImageRepository spaceImageRepository;

    @Mock
    private SpaceScheduleRepository spaceScheduleRepository;

    private SpaceService spaceService;

    @BeforeEach
    void setUp() {
        spaceService = new SpaceService(
                spaceRepository,
                spaceImageRepository,
                spaceScheduleRepository
        );
    }

    @Test
    void matchingContextIncludesOwnerStatusAndScheduleAvailability() {
        LocalDateTime startTime = LocalDateTime.of(2026, 6, 10, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 6, 10, 12, 0);
        Space space = Space.create(
                "host-1",
                "space",
                "description",
                null,
                "address",
                "thumbnail",
                10000,
                SpaceCategory.OTHER,
                null,
                null
        );

        when(spaceRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(space));
        when(spaceScheduleRepository
                .existsBySpaceIdAndIsBookableTrueAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        1L,
                        startTime,
                        endTime
                ))
                .thenReturn(true);

        SpaceMatchingContextResponse response =
                spaceService.getMatchingContext(1L, startTime, endTime);

        assertThat(response.hostId()).isEqualTo("host-1");
        assertThat(response.active()).isTrue();
        assertThat(response.approved()).isFalse();
        assertThat(response.available()).isTrue();
    }
}
