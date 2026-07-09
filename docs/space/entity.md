# Space Service Entity / Repository

이 문서는 Entity, Enum, Repository 구조를 실제 코드 기준으로 정리한다.

## Entity

### Space

- Class: `com.example.space.entity.Space`
- Table: `spaces`

| 필드 | DB 컬럼 | 타입 | 의미 |
|---|---|---|---|
| `id` | `id` | `Long` | 공간 ID |
| `hostId` | `host_id` | `String` | 공간 소유자 ID |
| `name` | `name` | `String` | 공간 이름 |
| `description` | `description` | `String` | 공간 설명 |
| `aiSummary` | `ai_summary` | `String` | AI 요약 |
| `addressId` | `address_id` | `Long` | 주소 ID |
| `thumbnailUrl` | `thumbnail_url` | `String` | 대표 이미지 URL |
| `pricePerHour` | `price_per_hour` | `Integer` | 시간당 가격 |
| `adminStatus` | `admin_status` | `ApprovalStatus` | 관리자 승인 상태 |
| `isActive` | `is_active` | `Boolean` | 활성 여부 |
| `category` | `category` | `SpaceCategory` | 공간 카테고리 |
| `createdAt` | `created_at` | `LocalDateTime` | 생성 시각 |
| `updatedAt` | `updated_at` | `LocalDateTime` | 수정 시각 |
| `deletedAt` | `deleted_at` | `LocalDateTime` | 삭제 시각 |

확인됨:

- `adminStatus`와 `category`는 `EnumType.STRING`으로 저장된다.
- `createdAt`은 `@CreationTimestamp`, `updatedAt`은 `@UpdateTimestamp`를 사용한다.
- 삭제는 `deletedAt`과 `isActive`를 변경하는 소프트 삭제 방식이다.

### SpaceImage

- Class: `com.example.space.entity.SpaceImage`
- Table: `space_images`

| 필드 | DB 컬럼 | 타입 | 의미 |
|---|---|---|---|
| `id` | `id` | `Long` | 이미지 ID |
| `spaceId` | `space_id` | `Long` | 공간 ID |
| `imageUrl` | `image_url` | `String` | 이미지 URL |

확인됨:

- `Space`와 객체 연관관계 없이 `spaceId` 값으로 연결된다.

### SpaceSchedule

- Class: `com.example.space.entity.SpaceSchedule`
- Table: `space_schedules`

| 필드 | DB 컬럼 | 타입 | 의미 |
|---|---|---|---|
| `id` | `id` | `Long` | 일정 ID |
| `spaceId` | `space_id` | `Long` | 공간 ID |
| `startTime` | `start_time` | `LocalDateTime` | 시작 시간 |
| `endTime` | `end_time` | `LocalDateTime` | 종료 시간 |
| `isBookable` | `is_bookable` | `Boolean` | 예약 가능 여부 |

확인됨:

- `Space`와 객체 연관관계 없이 `spaceId` 값으로 연결된다.
- 생성/수정 시 시간 유효성 검증을 수행한다.

## Enum

### ApprovalStatus

값:

- `PENDING`
- `APPROVED`
- `REJECTED`

확인됨:

- 공간 생성 시 `PENDING`으로 설정된다.
- 현재 Service 조회 로직에서는 일반 목록/상세 필터로 사용되지 않는다.

### SpaceCategory

값:

- `PRACTICE_ROOM`
- `STUDIO`
- `MEETING_ROOM`
- `PARTY_ROOM`
- `CLASSROOM`
- `POPUP_STORE`
- `OFFICE`
- `HALL`
- `CAFE`
- `OTHER`

## Entity 간 관계

확인됨:

- `SpaceImage.spaceId`가 `Space.id`를 참조하는 값으로 사용된다.
- `SpaceSchedule.spaceId`가 `Space.id`를 참조하는 값으로 사용된다.
- JPA 객체 연관관계인 `@OneToMany`, `@ManyToOne`은 현재 코드에서 확인되지 않는다.

확인 필요:

- 실제 DB 외래키 존재 여부
- 인덱스 존재 여부
- 운영 DDL 또는 migration 파일

## Repository

### SpaceRepository

- Interface: `com.example.space.repository.SpaceRepository`
- Extends: `JpaRepository<Space, Long>`

| 메서드 | 역할 |
|---|---|
| `findByIdAndDeletedAtIsNull` | 삭제되지 않은 공간 단건 조회 |
| `findAllByDeletedAtIsNull` | 삭제되지 않은 전체 공간 조회 |
| `findAllByHostIdAndDeletedAtIsNull` | 특정 host의 삭제되지 않은 공간 조회 |
| `findAllByNameContainingAndDeletedAtIsNull` | 이름 검색 |
| `findAllByCategoryAndDeletedAtIsNull` | 카테고리 검색 |
| `findAllByNameContainingAndCategoryAndDeletedAtIsNull` | 이름 + 카테고리 검색 |
| `findAllByAdminStatusAndIsActiveTrueAndDeletedAtIsNull` | 승인 상태 + 활성 + 삭제 제외 조회 |
| `findAllByHostIdAndNameContainingAndDeletedAtIsNull` | 내 공간 이름 검색 |
| `findAllByHostIdAndCategoryAndDeletedAtIsNull` | 내 공간 카테고리 검색 |
| `findAllByHostIdAndNameContainingAndCategoryAndDeletedAtIsNull` | 내 공간 이름 + 카테고리 검색 |

주의:

- `findAllByAdminStatusAndIsActiveTrueAndDeletedAtIsNull`는 존재하지만 현재 `SpaceService`에서 사용되지 않는다.

### SpaceImageRepository

- Interface: `com.example.space.repository.SpaceImageRepository`
- Extends: `JpaRepository<SpaceImage, Long>`

| 메서드 | 역할 |
|---|---|
| `findAllBySpaceId` | 공간 ID로 이미지 목록 조회 |
| `deleteAllBySpaceId` | 공간 ID로 이미지 전체 삭제 |

### SpaceScheduleRepository

- Interface: `com.example.space.repository.SpaceScheduleRepository`
- Extends: `JpaRepository<SpaceSchedule, Long>`

| 메서드 | 역할 |
|---|---|
| `findAllBySpaceIdOrderByStartTimeAsc` | 공간 ID로 일정 목록을 시작 시간 오름차순 조회 |
| `findByIdAndSpaceId` | 일정 ID와 공간 ID로 일정 단건 조회 |
| `deleteAllBySpaceId` | 공간 ID로 일정 전체 삭제 |
