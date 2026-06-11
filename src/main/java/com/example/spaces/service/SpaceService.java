package com.example.spaces.service;

import com.example.spaces.dto.request.ScheduleCreateRequest;
import com.example.spaces.dto.request.ScheduleUpdateRequest;
import com.example.spaces.dto.request.SpaceCreateRequest;
import com.example.spaces.dto.request.SpaceUpdateRequest;
import com.example.spaces.dto.response.MySpaceListResponses;
import com.example.spaces.dto.response.ScheduleCreateResponse;
import com.example.spaces.dto.response.ScheduleListResponses;
import com.example.spaces.dto.response.SpaceCreateResponse;
import com.example.spaces.dto.response.SpaceDetailResponse;
import com.example.spaces.dto.response.SpaceListResponses;
import com.example.spaces.entity.Space;
import com.example.spaces.entity.SpaceCategory;
import com.example.spaces.entity.SpaceImage;
import com.example.spaces.entity.SpaceSchedule;
import com.example.spaces.repository.SpaceImageRepository;
import com.example.spaces.repository.SpaceRepository;
import com.example.spaces.repository.SpaceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceImageRepository spaceImageRepository;
    private final SpaceScheduleRepository spaceScheduleRepository;

    @Transactional
    public SpaceCreateResponse createSpace(
            String hostId,
            SpaceCreateRequest request
    ) {
        Space space = Space.create(
                hostId,
                request.name(),
                request.description(),
                null,
                request.address(),
                request.thumbnailUrl(),
                request.pricePerHour(),
                request.category(),
                request.lat(),
                request.lng()
        );

        Space savedSpace = spaceRepository.save(space);

        saveImages(savedSpace.getId(), request.imageUrls());

        return SpaceCreateResponse.from(savedSpace);
    }

    public SpaceListResponses getSpaces(
            String name,
            SpaceCategory category
    ) {
        List<Space> spaces = findSpaces(name, category);

        return SpaceListResponses.from(spaces);
    }

    public SpaceDetailResponse getSpace(
            Long spaceId
    ) {
        Space space = getActiveSpace(spaceId);
        List<SpaceImage> images = spaceImageRepository.findAllBySpaceId(spaceId);

        return SpaceDetailResponse.from(space, images);
    }

    @Transactional
    public void updateSpace(
            String userId,
            Long spaceId,
            SpaceUpdateRequest request
    ) {
        Space space = getActiveSpace(spaceId);
        validateOwner(space, userId);

        space.update(
                request.name(),
                request.description(),
                request.aiSummary(),
                request.thumbnailUrl(),
                request.pricePerHour(),
                request.category()
        );

        if (request.imageUrls() != null) {
            spaceImageRepository.deleteAllBySpaceId(spaceId);
            saveImages(spaceId, request.imageUrls());
        }
    }

    @Transactional
    public void deleteSpace(
            String userId,
            Long spaceId
    ) {
        Space space = getActiveSpace(spaceId);
        validateOwner(space, userId);

        space.delete();

        spaceImageRepository.deleteAllBySpaceId(spaceId);
        spaceScheduleRepository.deleteAllBySpaceId(spaceId);
    }

    public MySpaceListResponses getMySpaces(
            String hostId,
            String name,
            SpaceCategory category
    ) {
        List<Space> spaces = findMySpaces(hostId, name, category);

        return MySpaceListResponses.from(spaces);
    }

    @Transactional
    public ScheduleCreateResponse createSchedule(
            String userId,
            Long spaceId,
            ScheduleCreateRequest request
    ) {
        Space space = getActiveSpace(spaceId);
        validateOwner(space, userId);

        SpaceSchedule schedule = SpaceSchedule.create(
                spaceId,
                request.startTime(),
                request.endTime(),
                true
        );

        SpaceSchedule savedSchedule = spaceScheduleRepository.save(schedule);

        return ScheduleCreateResponse.from(savedSchedule);
    }

    public ScheduleListResponses getSchedules(
            Long spaceId
    ) {
        getActiveSpace(spaceId);

        List<SpaceSchedule> schedules =
                spaceScheduleRepository.findAllBySpaceIdOrderByStartTimeAsc(spaceId);

        return ScheduleListResponses.from(schedules);
    }

    @Transactional
    public void updateSchedule(
            String userId,
            Long spaceId,
            Long scheduleId,
            ScheduleUpdateRequest request
    ) {
        Space space = getActiveSpace(spaceId);
        validateOwner(space, userId);

        SpaceSchedule schedule = spaceScheduleRepository.findByIdAndSpaceId(scheduleId, spaceId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        LocalDateTime startTime = request.startTime() != null
                ? request.startTime()
                : schedule.getStartTime();

        LocalDateTime endTime = request.endTime() != null
                ? request.endTime()
                : schedule.getEndTime();

        Boolean isBookable = request.isBookable() != null
                ? request.isBookable()
                : schedule.getIsBookable();

        schedule.update(
                startTime,
                endTime,
                isBookable
        );
    }

    @Transactional
    public void deleteSchedule(
            String userId,
            Long spaceId,
            Long scheduleId
    ) {
        Space space = getActiveSpace(spaceId);
        validateOwner(space, userId);

        SpaceSchedule schedule = spaceScheduleRepository.findByIdAndSpaceId(scheduleId, spaceId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        spaceScheduleRepository.delete(schedule);
    }

    private Space getActiveSpace(Long spaceId) {
        return spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));
    }

    private void validateOwner(
            Space space,
            String userId
    ) {
        if (!space.isOwner(userId)) {
            throw new SecurityException("해당 공간에 대한 권한이 없습니다.");
        }
    }

    private void saveImages(
            Long spaceId,
            List<String> imageUrls
    ) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        List<SpaceImage> images = imageUrls.stream()
                .map(imageUrl -> SpaceImage.create(spaceId, imageUrl))
                .toList();

        spaceImageRepository.saveAll(images);
    }

    private List<Space> findSpaces(
            String name,
            SpaceCategory category
    ) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasCategory = category != null;

        if (hasName && hasCategory) {
            return spaceRepository.findAllByNameContainingAndCategoryAndDeletedAtIsNull(
                    name,
                    category
            );
        }

        if (hasName) {
            return spaceRepository.findAllByNameContainingAndDeletedAtIsNull(name);
        }

        if (hasCategory) {
            return spaceRepository.findAllByCategoryAndDeletedAtIsNull(category);
        }

        return spaceRepository.findAllByDeletedAtIsNull();
    }

    private List<Space> findMySpaces(
            String hostId,
            String name,
            SpaceCategory category
    ) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasCategory = category != null;

        if (hasName && hasCategory) {
            return spaceRepository.findAllByHostIdAndNameContainingAndCategoryAndDeletedAtIsNull(
                    hostId,
                    name,
                    category
            );
        }

        if (hasName) {
            return spaceRepository.findAllByHostIdAndNameContainingAndDeletedAtIsNull(
                    hostId,
                    name
            );
        }

        if (hasCategory) {
            return spaceRepository.findAllByHostIdAndCategoryAndDeletedAtIsNull(
                    hostId,
                    category
            );
        }

        return spaceRepository.findAllByHostIdAndDeletedAtIsNull(hostId);
    }
}