# Space Service Exception

현재 코드에서 커스텀 예외 클래스와 전역 예외 처리기(`@ControllerAdvice`, `@ExceptionHandler`)는 확인되지 않는다. 따라서 HTTP 상태 코드와 실패 응답 포맷은 확정하지 않는다.

## 예외 목록

| Exception | 발생 위치 | 발생 조건 | 메시지 | HTTP 응답 매핑 |
|---|---|---|---|---|
| `IllegalArgumentException` | `SpaceService.getActiveSpace` | `findByIdAndDeletedAtIsNull` 결과가 없을 때 | `공간을 찾을 수 없습니다.` | 확인 필요 |
| `SecurityException` | `SpaceService.validateOwner` | 요청 사용자 ID가 `Space.hostId`와 다를 때 | `해당 공간에 대한 권한이 없습니다.` | 확인 필요 |
| `IllegalArgumentException` | `SpaceService.updateSchedule` | `findByIdAndSpaceId` 결과가 없을 때 | `일정을 찾을 수 없습니다.` | 확인 필요 |
| `IllegalArgumentException` | `SpaceService.deleteSchedule` | `findByIdAndSpaceId` 결과가 없을 때 | `일정을 찾을 수 없습니다.` | 확인 필요 |
| `IllegalArgumentException` | `SpaceSchedule.validateTime` | `startTime` 또는 `endTime`이 null일 때 | `일정 시작 시간과 종료 시간은 필수입니다.` | 확인 필요 |
| `IllegalArgumentException` | `SpaceSchedule.validateTime` | `startTime`이 `endTime`보다 빠르지 않을 때 | `일정 시작 시간은 종료 시간보다 빨라야 합니다.` | 확인 필요 |
| JWT 관련 인증 실패 가능성 | `JwtFilter`, `JwtProvider` | 토큰 파싱 실패, 유효하지 않은 토큰, claim 문제 | 별도 메시지 없음 | 확인 필요 |

## 공간 조회 실패

확인됨:

- `SpaceService.getActiveSpace`는 `spaceRepository.findByIdAndDeletedAtIsNull(spaceId)`를 사용한다.
- 결과가 없으면 `IllegalArgumentException("공간을 찾을 수 없습니다.")`를 발생시킨다.

적용 흐름:

- 공간 단건 조회
- 공간 수정
- 공간 삭제
- 일정 생성
- 일정 조회
- 일정 수정
- 일정 삭제

확인 필요:

- 404 Not Found로 매핑할지 여부
- 실패 응답 body 형식

## 권한 검증 실패

확인됨:

- `SpaceService.validateOwner`에서 소유자 검증을 수행한다.
- `Space.isOwner(userId)`가 false면 `SecurityException("해당 공간에 대한 권한이 없습니다.")`를 발생시킨다.

적용 흐름:

- 공간 수정
- 공간 삭제
- 일정 생성
- 일정 수정
- 일정 삭제

확인 필요:

- 403 Forbidden으로 매핑할지 여부
- 인증 실패와 인가 실패의 응답 구분

## 일정 조회 실패

확인됨:

- 일정 수정/삭제 시 `spaceScheduleRepository.findByIdAndSpaceId(scheduleId, spaceId)`를 사용한다.
- 결과가 없으면 `IllegalArgumentException("일정을 찾을 수 없습니다.")`를 발생시킨다.

확인 필요:

- 404 Not Found로 매핑할지 여부

## 일정 시간 검증 실패

확인됨:

- `SpaceSchedule.create`와 `SpaceSchedule.update`에서 `validateTime`을 호출한다.
- 시작/종료 시간이 null이면 예외가 발생한다.
- 시작 시간이 종료 시간보다 빠르지 않으면 예외가 발생한다.

확인 필요:

- 400 Bad Request로 매핑할지 여부
- Request DTO Bean Validation으로 처리할지 Entity 검증으로 유지할지

## JWT 관련 인증 실패 가능성

확인됨:

- `JwtFilter`는 `Authorization` header가 `Bearer `로 시작하면 토큰을 검증한다.
- `JwtProvider.validateToken`은 `JwtException` 또는 `IllegalArgumentException` 발생 시 false를 반환한다.
- `JwtFilter` 내부 예외는 catch 후 `SecurityContextHolder.clearContext()`를 호출한다.

확인 필요:

- 토큰 없음, 토큰 만료, 토큰 invalid 상황의 최종 HTTP 응답
- JWT 발급 주체와 claim 규격
- `role` claim이 없을 때 처리 정책

## 실패 응답 포맷

확인 필요:

- 성공 응답은 `ApiResponse<T>`와 `ResponseUtil.success` 사용이 확인된다.
- 실패 응답 공통 포맷은 현재 코드에서 확인되지 않는다.
