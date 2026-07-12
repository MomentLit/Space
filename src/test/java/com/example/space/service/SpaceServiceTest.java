package com.example.space.service;

import com.example.space.dto.request.AddressRequest;
import com.example.space.dto.request.SpaceAdminStatusUpdateRequest;
import com.example.space.dto.request.SpaceCreateRequest;
import com.example.space.dto.request.SpaceUpdateRequest;
import com.example.space.dto.response.AdminSpaceDetailResponse;
import com.example.space.dto.response.AdminSpaceListResponses;
import com.example.space.dto.response.SpaceAdminStatusResponse;
import com.example.space.dto.response.SpaceMatchingContextResponse;
import com.example.space.entity.Address;
import com.example.space.entity.ApprovalStatus;
import com.example.space.entity.Space;
import com.example.space.entity.SpaceCategory;
import com.example.space.entity.SpaceImage;
import com.example.space.global.exception.BadRequestException;
import com.example.space.global.exception.ForbiddenException;
import com.example.space.global.exception.SpaceNotFoundException;
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
import static org.mockito.Mockito.mock;
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

    @Test
    void getAdminStatus_shouldReturnAdminStatus_whenRequesterIsAdmin() {
        Space space = Space.create(
                "host-1", "space", "description", null, 1L,
                "thumbnail", 10000, SpaceCategory.OTHER, null
        );

        when(spaceRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(space));

        SpaceAdminStatusResponse response = spaceService.getAdminStatus("ADMIN", 1L);

        assertThat(response.adminStatus()).isEqualTo(ApprovalStatus.PENDING);
    }

    @Test
    void getAdminStatus_shouldThrowForbidden_whenRequesterIsNotAdmin() {
        assertThatThrownBy(() -> spaceService.getAdminStatus("USER", 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("관리자 권한이 없습니다.");
    }

    @Test
    void getAdminStatus_shouldThrowForbidden_whenRoleIsNull() {
        assertThatThrownBy(() -> spaceService.getAdminStatus(null, 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("관리자 권한이 없습니다.");
    }

    @Test
    void updateAdminStatus_shouldThrowForbidden_whenRoleIsNull() {
        assertThatThrownBy(() -> spaceService.updateAdminStatus(
                null,
                1L,
                new SpaceAdminStatusUpdateRequest(ApprovalStatus.APPROVED)
        ))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("관리자 권한이 없습니다.");
    }

    @Test
    void updateAdminStatus_shouldChangeStatus_whenSpaceIsPending() {
        Space space = Space.create(
                "host-1", "space", "description", null, 1L,
                "thumbnail", 10000, SpaceCategory.OTHER, null
        );

        when(spaceRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(space));

        spaceService.updateAdminStatus(
                "ADMIN",
                1L,
                new SpaceAdminStatusUpdateRequest(ApprovalStatus.APPROVED)
        );

        assertThat(space.getAdminStatus()).isEqualTo(ApprovalStatus.APPROVED);
    }

    @Test
    void updateAdminStatus_shouldReject_whenSpaceIsNotPending() {
        Space space = Space.create(
                "host-1", "space", "description", null, 1L,
                "thumbnail", 10000, SpaceCategory.OTHER, null
        );
        space.updateAdminStatus(ApprovalStatus.APPROVED);

        when(spaceRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(space));

        assertThatThrownBy(() -> spaceService.updateAdminStatus(
                "ADMIN",
                1L,
                new SpaceAdminStatusUpdateRequest(ApprovalStatus.REJECTED)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("승인 대기 상태의 공간만 승인 상태를 변경할 수 있습니다.");
    }

    @Test
    void getAdminSpaces_shouldReturnAllActiveSpaces_whenRequesterIsAdmin() {
        Space space = Space.create(
                "host-1", "space", "description", null, 1L,
                "thumbnail", 10000, SpaceCategory.OTHER, null
        );
        Address address = mock(Address.class);
        when(address.getId()).thenReturn(1L);

        when(spaceRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of(space));
        when(addressRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(address));

        AdminSpaceListResponses response = spaceService.getAdminSpaces("ADMIN");

        assertThat(response.spaces()).hasSize(1);
        assertThat(response.spaces().get(0).hostId()).isEqualTo("host-1");
        assertThat(response.spaces().get(0).adminStatus()).isEqualTo(ApprovalStatus.PENDING.name());
        assertThat(response.spaces().get(0).isActive()).isTrue();
    }

    @Test
    void getAdminSpaces_shouldThrowForbidden_whenRequesterIsNotAdmin() {
        assertThatThrownBy(() -> spaceService.getAdminSpaces("USER"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("관리자 권한이 없습니다.");
    }

    @Test
    void getAdminSpace_shouldReturnSpaceDetail_whenRequesterIsAdmin() {
        Space space = Space.create(
                "host-1", "space", "description", null, 1L,
                "thumbnail", 10000, SpaceCategory.OTHER, null
        );
        Address address = mock(Address.class);

        when(spaceRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(space));
        when(addressRepository.findById(1L))
                .thenReturn(Optional.of(address));
        when(spaceImageRepository.findAllBySpaceId(1L))
                .thenReturn(List.of(SpaceImage.create(1L, "image-1")));

        AdminSpaceDetailResponse response = spaceService.getAdminSpace("ADMIN", 1L);

        assertThat(response.hostId()).isEqualTo("host-1");
        assertThat(response.adminStatus()).isEqualTo(ApprovalStatus.PENDING.name());
        assertThat(response.imageUrls()).containsExactly("image-1");
    }

    @Test
    void getAdminSpace_shouldThrowForbidden_whenRoleIsNull() {
        assertThatThrownBy(() -> spaceService.getAdminSpace(null, 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("관리자 권한이 없습니다.");
    }

    @Test
    void getAdminSpace_shouldThrowNotFound_whenSpaceDoesNotExist() {
        when(spaceRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> spaceService.getAdminSpace("ADMIN", 1L))
                .isInstanceOf(SpaceNotFoundException.class)
                .hasMessage("공간을 찾을 수 없습니다.");
    }

    @Test
    void updateAdminStatus_shouldReject_whenAdminStatusIsNull() {
        assertThatThrownBy(() -> spaceService.updateAdminStatus(
                "ADMIN",
                1L,
                new SpaceAdminStatusUpdateRequest(null)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("승인 상태는 필수입니다.");
    }
}
