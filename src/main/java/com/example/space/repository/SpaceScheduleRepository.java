package com.example.space.repository;

import com.example.space.entity.SpaceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceScheduleRepository extends JpaRepository<SpaceSchedule, Long> {

    List<SpaceSchedule> findAllBySpaceIdOrderByStartTimeAsc(Long spaceId);

    Optional<SpaceSchedule> findByIdAndSpaceId(
            Long id,
            Long spaceId
    );

    void deleteAllBySpaceId(Long spaceId);
}