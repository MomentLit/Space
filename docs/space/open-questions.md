# Space Service Open Questions

이 문서는 코드만으로 확정할 수 없는 정책과 설계 질문을 모은다.

## 도메인 정책 확인 필요

- `ApprovalStatus.PENDING` 상태 공간이 일반 목록/상세 조회에 노출되어도 되는가?
- `ApprovalStatus.APPROVED`만 공개 조회되어야 하는가?
- `ApprovalStatus.REJECTED` 상태 공간은 호스트에게 어떻게 노출되어야 하는가?
- `isActive=false`와 `deletedAt != null`의 정책적 차이는 무엇인가?
- `isActive=false`이지만 `deletedAt == null`인 상태가 존재할 수 있는가?
- `aiSummary`는 누가 생성하고 누가 수정할 수 있는가?
- `pricePerHour`의 통화, 단위, 최소값, 최대값은 무엇인가?
- 일정 중복은 허용되는가?
- 예약된 일정은 수정 또는 삭제할 수 있는가?

## MSA 책임 경계 확인 필요

- `hostId`가 실제 회원/호스트인지 검증하는 책임은 어느 서비스에 있는가?
- 공간 생성 가능 사용자인지 검증하는 책임은 Space Service인가, Member/Auth Service인가?
- 예약 생성 시 Space Schedule을 검증하거나 잠그는 책임은 어느 서비스에 있는가?
- 이미지 업로드와 이미지 URL 생성은 Space Service 책임인가, 별도 파일/미디어 서비스 책임인가?
- 공간 삭제 시 Reservation, Review, Chat 등 관련 데이터에 어떤 영향을 줘야 하는가?

## 권한 / 인증 확인 필요

- 공간 생성은 모든 인증 사용자가 가능한가, 특정 role만 가능한가?
- 공간 수정/삭제는 `hostId` 검증만으로 충분한가?
- 관리자 승인/반려 권한은 어떤 role에 있는가?
- JWT `role` claim이 없거나 null이면 어떻게 처리해야 하는가?
- 인증 실패와 권한 실패의 응답 형식은 어떻게 구분하는가?

## 상태값 / 상태 전이 확인 필요

- `ApprovalStatus.PENDING -> APPROVED -> REJECTED` 전이는 어떤 API에서 발생하는가?
- 반려된 공간을 다시 수정 후 재심사할 수 있는가?
- 삭제된 공간을 복구할 수 있는가?
- `SpaceSchedule.isBookable`은 예약 상태와 어떻게 동기화되는가?
- 일정 응답 상태가 `AVAILABLE`, `BLOCKED` 외에 더 필요한가?

## API 스펙 확인 필요

- 각 Request DTO 필드의 필수 여부는 무엇인가?
- Bean Validation 제약을 추가할 계획이 있는가?
- 실패 응답 공통 포맷은 무엇인가?
- 공간 목록 조회에 pagination이 필요한가?
- 공간 목록 조회 정렬 기준은 무엇인가?
- `category` query parameter의 허용 값 오류는 어떤 응답으로 처리하는가?
- 날짜/시간 요청과 응답의 timezone 기준은 무엇인가?

## 데이터 구조 확인 필요

- 실제 운영 DB DDL은 어디에서 관리하는가?
- `space_images.space_id`, `space_schedules.space_id`에 DB FK가 존재하는가?
- `host_id`, `category`, `deleted_at`, `admin_status`, `is_active`에 index가 필요한가?
- `price_per_hour`가 `Integer`인 것이 장기적으로 충분한가?

## 외부 의존성 확인 필요

- JWT 발급 주체는 어느 서비스인가?
- JWT subject와 role claim 계약은 어디에 정의되어 있는가?
- 이미지 저장소로 S3 또는 다른 스토리지를 사용하는가?
- Reservation Service는 Space Service API를 호출하는가, DB를 참조하는가, 이벤트를 구독하는가?
- Kafka 또는 다른 메시징 기반 이벤트 연동 계획이 있는가?

## 보안 이슈 확인 필요

- `GET /spaces/me`가 SecurityConfig matcher 순서 문제로 공개 접근될 가능성이 있는가?
- `GET /spaces/*` permitAll matcher가 `/spaces/me`보다 먼저 선언된 현재 순서를 변경해야 하는가?
- 인증이 필요한 Controller에서 `principal == null` 가능성을 방어해야 하는가?
- `GET /spaces/{space-id}`가 문자열 `me`와 충돌하지 않도록 path pattern을 제한해야 하는가?
