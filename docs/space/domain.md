# Space Service Domain

이 문서는 `src/main/java/com/example/space/entity`와 `SpaceService`에서 확인된 도메인 개념과 규칙을 정리한다.

## Space

`Space`는 공간을 나타내는 Entity다.

확인됨:

- 테이블명은 `spaces`다.
- 생성 시 `hostId`, `name`, `description`, `aiSummary`, `addressId`, `thumbnailUrl`, `pricePerHour`, `category`, `phone`을 받을 수 있다.
- `SpaceService.createSpace`는 `aiSummary`를 `null`로 전달한다.
- 생성 시 `adminStatus`는 `ApprovalStatus.PENDING`으로 설정된다.
- 생성 시 `isActive`는 `true`로 설정된다.
- 삭제 시 `deletedAt`에 현재 시간이 설정되고 `isActive=false`가 된다.
- `isOwner(String userId)`는 `hostId.equals(userId)`로 소유자를 판단한다.

확인 필요:

- `hostId`가 실제 회원 또는 호스트인지 검증하는 책임
- `address`의 필수 여부와 수정 가능 여부
- `pricePerHour`의 단위와 허용 범위

## SpaceImage

`SpaceImage`는 공간 이미지 URL을 저장하는 Entity다.

확인됨:

- 테이블명은 `space_images`다.
- `spaceId`와 `imageUrl`을 가진다.
- 이미지 파일 자체가 아니라 URL 문자열만 저장한다.
- 공간 생성 시 `imageUrls`가 있으면 여러 `SpaceImage`가 저장된다.
- 공간 수정 시 `imageUrls`가 null이 아니면 기존 이미지를 모두 삭제한 뒤 다시 저장한다.
- 공간 삭제 시 해당 공간의 이미지 row를 삭제한다.

확인 필요:

- 이미지 업로드 저장소가 별도로 존재하는지
- 이미지 URL 유효성 검증 정책
- 이미지 row 삭제와 실제 파일 삭제의 책임 경계

## SpaceSchedule

`SpaceSchedule`은 공간의 예약 가능 시간 블록을 나타내는 Entity다.

확인됨:

- 테이블명은 `space_schedules`다.
- `spaceId`, `startTime`, `endTime`, `isBookable`을 가진다.
- 생성 시 `SpaceService.createSchedule`은 `isBookable=true`를 전달한다.
- `startTime` 또는 `endTime`이 null이면 `IllegalArgumentException`이 발생한다.
- `startTime`은 `endTime`보다 빨라야 한다.
- 수정 시 요청 값이 null이면 기존 값을 유지한다.
- 응답에서는 `isBookable=true`가 `AVAILABLE`, `false`가 `BLOCKED`로 변환된다.

확인 필요:

- 일정 중복 허용 여부
- 예약 충돌 검증 위치
- 예약된 일정 수정/삭제 제한
- timezone 정책

## ApprovalStatus

`ApprovalStatus`는 공간의 관리자 승인 상태로 보이는 Enum이다.

확인됨:

- 값은 `PENDING`, `APPROVED`, `REJECTED`다.
- `Space` 생성 시 기본값은 `PENDING`이다.
- `SpaceRepository.findAllByAdminStatusAndIsActiveTrueAndDeletedAtIsNull` 메서드는 존재한다.
- 현재 `SpaceService`의 일반 목록/상세 조회 로직에서는 `ApprovalStatus`가 필터 조건으로 사용되지 않는다.

추정:

- 관리자 승인 정책을 표현하기 위한 상태일 가능성이 있다.

확인 필요:

- 승인/반려 API 또는 관리 기능의 위치
- `APPROVED` 상태만 공개 조회되어야 하는지
- `REJECTED` 상태 공간의 표시 정책

## SpaceCategory

`SpaceCategory`는 공간 카테고리 Enum이다.

확인됨:

- 값은 `PRACTICE_ROOM`, `STUDIO`, `MEETING_ROOM`, `PARTY_ROOM`, `CLASSROOM`, `POPUP_STORE`, `OFFICE`, `HALL`, `CAFE`, `OTHER`다.
- 공간 생성/수정 요청과 공간 목록 검색 조건에 사용된다.

확인 필요:

- 카테고리 추가/변경 정책
- 사용자에게 노출되는 한글명 또는 표시명 관리 위치

## hostId

확인됨:

- JWT principal의 `userId`가 공간 생성 시 `hostId`로 저장된다.
- 공간 수정/삭제와 일정 생성/수정/삭제 시 `hostId`와 요청 사용자 ID를 비교한다.

확인 필요:

- `hostId`가 Member Service의 회원 ID인지, Host ID인지
- 특정 role만 공간을 생성할 수 있는지

## isActive

확인됨:

- 생성 시 `true`다.
- 삭제 시 `false`다.
- 내 공간 목록 응답에는 포함된다.
- 현재 대부분의 조회 로직은 `isActive`가 아니라 `deletedAt` 기준으로 삭제 여부를 판단한다.

확인 필요:

- `isActive=false`와 `deletedAt != null`의 정책적 차이
- `isActive`만 false인 공간이 존재할 수 있는지

## deletedAt

확인됨:

- 삭제 시 현재 시간으로 설정된다.
- `findByIdAndDeletedAtIsNull`, `findAllByDeletedAtIsNull` 등 삭제 제외 조회에 사용된다.
- 공간 삭제 시 이미지와 일정은 row 삭제되고, 공간 자체는 소프트 삭제된다.

확인 필요:

- 삭제 공간 복구 가능 여부
- 보관 기간 또는 물리 삭제 정책

## aiSummary

확인됨:

- `Space` 필드로 존재한다.
- 공간 생성 시 현재 서비스에서는 `null`로 저장된다.
- 공간 수정 요청에서는 변경 가능하다.
- 상세 응답에 포함된다.

확인 필요:

- AI summary 생성 주체
- 사용자가 직접 수정할 수 있는 값인지

## pricePerHour

확인됨:

- `Space.pricePerHour`는 `Integer`다.
- 생성/수정 요청과 목록/상세 응답에 포함된다.

확인 필요:

- 통화 단위
- 시간 단위
- 최소/최대 가격
- 음수 또는 0 허용 여부
