package com.example.space.service;

import com.example.space.dto.request.AddressRequest;
import com.example.space.dto.request.SpaceCreateRequest;
import com.example.space.dto.request.SpaceUpdateRequest;
import com.example.space.dto.response.SpaceMatchingContextResponse;
import com.example.space.entity.Space;
import com.example.space.entity.SpaceCategory;
import com.example.space.global.exception.BadRequestException;
import com.example.space.repository.AddressRepository;
import com.example.space.repository.SpaceImageRepository;
import com.example.space.repository.SpaceRepository;
import com.example.space.repository.SpaceScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpaceServiceTest {

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private SpaceImageRepository spaceImageRepository;

    @Mock
    private SpaceScheduleRepository spaceScheduleRepository;

    @Mock
    private AddressRepository addressRepository;

    private SpaceService spaceService;

    @BeforeEach
    void setUp() {
        spaceService = new SpaceService(
                spaceRepository,
                spaceImageRepository,
                spaceScheduleRepository,
                addressRepository
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
                1L,
                "thumbnail",
                10000,
                SpaceCategory.OTHER,
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
    @Test
    void updateSpace_shouldUpdatePhone_whenRequestContainsPhone() {
        Long spaceId = 1L;
        Space space = Space.create(
                "host-1", "space", "description", null, 1L,
                "thumbnail", 10000, SpaceCategory.OTHER, null
                        );
        SpaceUpdateRequest request = new SpaceUpdateRequest(
                null, null, null, null, null, null, null, null, "01012345678"
                );
            when(spaceRepository.findByIdAndDeletedAtIsNull(spaceId))
                .thenReturn(Optional.of(space));
        spaceService.updateSpace("host-1", spaceId, request);
        assertThat(space.getPhone()).isEqualTo("01012345678");
    }

    @Test
    void createSpace_shouldRejectMissingRequiredAddressField() {
        SpaceCreateRequest request = new SpaceCreateRequest(
                "space",
                "description",
                new AddressRequest(null, "마포구", null, "서울특별시 마포구 월드컵북로 1", null, null, null, null, null),
                "thumbnail",
                List.of(),
                10000,
                SpaceCategory.OTHER,
                null
        );

        assertThatThrownBy(() -> spaceService.createSpace("host-1", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("시/도는 필수입니다.");
    }
}
