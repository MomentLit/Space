# Space Service API

기준 Controller는 `src/main/java/com/example/space/controller/SpaceController.java`다.

실패 응답 포맷과 HTTP 상태 매핑은 `@ControllerAdvice`, `@ExceptionHandler`가 확인되지 않아 확정하지 않는다. `@Valid`는 일부 API에 사용되지만 Request DTO에 Bean Validation 제약 어노테이션이 확인되지 않아 필수값 정책도 확정하지 않는다.

## API 목록

| Method | Path | Handler Method | 인증 필요 여부 | Request DTO | Response DTO |
|---|---|---|---|---|---|
| POST | `/spaces` | `createSpace` | 확인됨: 필요 | `SpaceCreateRequest` | `ApiResponse<SpaceCreateResponse>` |
| GET | `/spaces` | `getSpaces` | 확인됨: 불필요 | query: `name`, `category` | `ApiResponse<SpaceListResponses>` |
| GET | `/spaces/{space-id}` | `getSpace` | 확인됨: 불필요 | path: `space-id` | `ApiResponse<SpaceDetailResponse>` |
| PATCH | `/spaces/{space-id}` | `updateSpace` | 확인됨: 필요 | `SpaceUpdateRequest` | 없음, 204 |
| DELETE | `/spaces/{space-id}` | `deleteSpace` | 확인됨: 필요 | path: `space-id` | 없음, 204 |
| GET | `/spaces/me` | `getMySpaces` | 확인됨: 의도상 필요 | query: `name`, `category` | `ApiResponse<MySpaceListResponses>` |
| POST | `/spaces/{space-id}/schedule` | `createSchedule` | 확인됨: 필요 | `ScheduleCreateRequest` | `ApiResponse<ScheduleCreateResponse>` |
| GET | `/spaces/{space-id}/schedule` | `getSchedules` | 확인됨: 불필요 | path: `space-id` | `ApiResponse<ScheduleListResponses>` |
| PATCH | `/spaces/{space-id}/schedule/{schedule-id}` | `updateSchedule` | 확인됨: 필요 | `ScheduleUpdateRequest` | 없음, 204 |
| DELETE | `/spaces/{space-id}/schedule/{schedule-id}` | `deleteSchedule` | 확인됨: 필요 | path: `space-id`, `schedule-id` | 없음, 204 |

## POST `/spaces`

- Handler: `SpaceController.createSpace`
- Service: `SpaceService.createSpace`
- 인증: 확인됨. `@AuthenticationPrincipal UserPrincipal principal`에서 `userId`를 사용한다.
- Request DTO: `SpaceCreateRequest`
- Response DTO: `SpaceCreateResponse`
- 성공 응답: Controller에서 201 Created를 반환한다.

주요 처리 흐름:

1. JWT 인증 principal에서 `userId`를 조회한다.
2. `Space.create`로 공간을 생성한다.
3. 생성 시 `adminStatus=PENDING`, `isActive=true`가 설정된다.
4. `SpaceRepository.save`로 저장한다.
5. `imageUrls`가 있으면 `SpaceImage`를 생성해 저장한다.

확인 필요:

- 요청 필드 필수 여부
- 이미지 URL 생성 주체
- 공간 생성 가능 role

## GET `/spaces`

- Handler: `SpaceController.getSpaces`
- Service: `SpaceService.getSpaces`
- 인증: 확인됨. `SecurityConfig`에서 permitAll로 설정되어 있다.
- Request: query parameter `name`, `category`
- Response DTO: `SpaceListResponses`

주요 처리 흐름:

1. `name`, `category` 존재 여부에 따라 Repository 파생 쿼리를 선택한다.
2. 모든 조회는 `DeletedAtIsNull` 조건을 사용한다.
3. 응답에는 `spaceId`, `name`, `address`, `thumbnailUrl`, `pricePerHour`, `category`가 포함된다.

확인 필요:

- `ApprovalStatus.APPROVED`만 공개 조회해야 하는지
- `isActive=true` 조건도 필요해야 하는지
- pagination, 정렬 기준

## GET `/spaces/{space-id}`

- Handler: `SpaceController.getSpace`
- Service: `SpaceService.getSpace`
- 인증: 확인됨. `SecurityConfig`에서 permitAll로 설정되어 있다.
- Response DTO: `SpaceDetailResponse`

주요 처리 흐름:

1. `SpaceRepository.findByIdAndDeletedAtIsNull`로 공간을 조회한다.
2. `SpaceImageRepository.findAllBySpaceId`로 이미지 URL 목록을 조회한다.
3. 상세 응답을 생성한다.

확인 필요:

- 승인되지 않은 공간 상세 조회 허용 여부
- 비활성 공간 조회 정책

## PATCH `/spaces/{space-id}`

- Handler: `SpaceController.updateSpace`
- Service: `SpaceService.updateSpace`
- 인증: 확인됨. 필요.
- Request DTO: `SpaceUpdateRequest`
- Response: 204 No Content

주요 처리 흐름:

1. 삭제되지 않은 공간을 조회한다.
2. 요청 사용자와 공간의 `hostId`를 비교한다.
3. 소유자가 아니면 `SecurityException`을 발생시킨다.
4. null이 아닌 필드만 수정한다.
5. `imageUrls`가 null이 아니면 기존 이미지를 삭제하고 새 이미지 URL 목록을 저장한다.

확인 필요:

- 주소와 좌표가 수정 대상에서 제외된 것이 의도인지
- 이미지 목록 빈 배열 요청 시 전체 삭제가 의도인지

## DELETE `/spaces/{space-id}`

- Handler: `SpaceController.deleteSpace`
- Service: `SpaceService.deleteSpace`
- 인증: 확인됨. 필요.
- Response: 204 No Content

주요 처리 흐름:

1. 삭제되지 않은 공간을 조회한다.
2. 소유자 검증을 수행한다.
3. `Space.delete`로 `deletedAt`을 설정하고 `isActive=false`로 변경한다.
4. 공간 이미지와 일정을 삭제한다.

확인 필요:

- 예약이 존재하는 공간 삭제 정책
- 이미지 저장소가 별도로 있을 경우 실제 파일 삭제 책임

## GET `/spaces/me`

- Handler: `SpaceController.getMySpaces`
- Service: `SpaceService.getMySpaces`
- 인증: 확인됨. Controller와 `SecurityConfig` 의도상 필요.
- Response DTO: `MySpaceListResponses`

주요 처리 흐름:

1. JWT 인증 principal에서 `userId`를 조회한다.
2. `hostId`, `name`, `category` 조건으로 내 공간을 조회한다.
3. 응답에는 `adminStatus`, `isActive`가 포함된다.

주의사항:

- 확인됨: `SecurityConfig`에서 `GET /spaces/*` permitAll이 `GET /spaces/me` authenticated보다 먼저 선언되어 있다.
- 확인 필요: 실제 Spring Security matcher 동작에서 `/spaces/me`가 공개 접근되는지 테스트가 필요하다.

## POST `/spaces/{space-id}/schedule`

- Handler: `SpaceController.createSchedule`
- Service: `SpaceService.createSchedule`
- 인증: 확인됨. 필요.
- Request DTO: `ScheduleCreateRequest`
- Response DTO: `ScheduleCreateResponse`

주요 처리 흐름:

1. 삭제되지 않은 공간을 조회한다.
2. 소유자 검증을 수행한다.
3. `SpaceSchedule.create`로 일정을 생성한다.
4. `startTime`은 `endTime`보다 빨라야 한다.
5. `isBookable`은 `true`로 저장된다.

확인 필요:

- 일정 중복 검증
- 예약 충돌 검증
- 시간대 정책

## GET `/spaces/{space-id}/schedule`

- Handler: `SpaceController.getSchedules`
- Service: `SpaceService.getSchedules`
- 인증: 확인됨. 불필요.
- Response DTO: `ScheduleListResponses`

주요 처리 흐름:

1. 삭제되지 않은 공간을 조회한다.
2. `SpaceScheduleRepository.findAllBySpaceIdOrderByStartTimeAsc`로 일정을 조회한다.
3. 응답 생성 시 시작 일자 기준으로 grouping한다.
4. `isBookable=true`는 `AVAILABLE`, `false`는 `BLOCKED`로 응답한다.

확인 필요:

- 날짜 grouping 기준 timezone
- 예약된 시간대 표현 방식

## PATCH `/spaces/{space-id}/schedule/{schedule-id}`

- Handler: `SpaceController.updateSchedule`
- Service: `SpaceService.updateSchedule`
- 인증: 확인됨. 필요.
- Request DTO: `ScheduleUpdateRequest`
- Response: 204 No Content

주요 처리 흐름:

1. 삭제되지 않은 공간을 조회한다.
2. 소유자 검증을 수행한다.
3. `scheduleId`와 `spaceId`로 일정을 조회한다.
4. 요청 값이 null이면 기존 값을 유지한다.
5. 시간 유효성을 검증한 뒤 수정한다.

확인 필요:

- 예약된 일정 수정 제한
- `isBookable` 변경 정책

## DELETE `/spaces/{space-id}/schedule/{schedule-id}`

- Handler: `SpaceController.deleteSchedule`
- Service: `SpaceService.deleteSchedule`
- 인증: 확인됨. 필요.
- Response: 204 No Content

주요 처리 흐름:

1. 삭제되지 않은 공간을 조회한다.
2. 소유자 검증을 수행한다.
3. `scheduleId`와 `spaceId`로 일정을 조회한다.
4. 일정을 삭제한다.

확인 필요:

- 예약된 일정 삭제 제한
- 예약 서비스와의 연동 정책
