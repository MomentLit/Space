# Space Service 문서

이 디렉터리는 Space Service의 실제 코드 기준 문서를 담는다.

문서의 기준 코드는 `src/main/java/com/example/space` 하위 패키지다. 코드에서 직접 확인된 내용은 "확인됨"으로, 코드만으로 확정할 수 없는 정책은 "확인 필요"로 구분한다.

## Space Service의 책임

- 확인됨: 공간 생성, 목록 조회, 단건 조회, 수정, 삭제를 처리한다.
- 확인됨: 로그인 사용자의 내 공간 목록 조회를 처리한다.
- 확인됨: 공간 이미지 URL 목록을 저장하고 교체/삭제한다.
- 확인됨: 공간 일정 생성, 조회, 수정, 삭제를 처리한다.
- 확인됨: 공간 수정/삭제와 일정 생성/수정/삭제 시 `hostId` 기반 소유자 검증을 수행한다.
- 확인됨: 삭제된 공간은 `deletedAt` 기준으로 대부분의 조회에서 제외한다.

## 주요 기능

- 공간 생성: `POST /spaces`
- 공간 목록 조회: `GET /spaces`
- 공간 상세 조회: `GET /spaces/{space-id}`
- 공간 수정: `PATCH /spaces/{space-id}`
- 공간 삭제: `DELETE /spaces/{space-id}`
- 내 공간 조회: `GET /spaces/me`
- 일정 생성: `POST /spaces/{space-id}/schedule`
- 일정 조회: `GET /spaces/{space-id}/schedule`
- 일정 수정: `PATCH /spaces/{space-id}/schedule/{schedule-id}`
- 일정 삭제: `DELETE /spaces/{space-id}/schedule/{schedule-id}`

## 패키지 구조

| 패키지 | 역할 |
|---|---|
| `com.example.space.controller` | REST API Controller |
| `com.example.space.service` | Application Service, 유스케이스 처리 |
| `com.example.space.entity` | JPA Entity와 Enum |
| `com.example.space.repository` | Spring Data JPA Repository |
| `com.example.space.dto.request` | 요청 DTO |
| `com.example.space.dto.response` | 응답 DTO |
| `com.example.space.global.config` | Security 설정 |
| `com.example.space.global.security` | JWT 인증 필터와 Principal |
| `com.example.space.global.dto` | 공통 응답 DTO |
| `com.example.space.global.util` | 응답 생성 유틸 |

## 주요 문서 링크

- [API 문서](./api.md)
- [도메인 문서](./domain.md)
- [Entity / Repository 문서](./entity.md)
- [주요 흐름 문서](./flow.md)
- [예외 문서](./exception.md)
- [외부 의존성 문서](./dependency.md)
- [Open Questions](./open-questions.md)

## 문서 읽는 순서

1. `README.md`
2. `domain.md`
3. `entity.md`
4. `api.md`
5. `flow.md`
6. `exception.md`
7. `dependency.md`
8. `open-questions.md`

## 현재 확인된 주의사항

- 확인됨: `ApprovalStatus`는 존재하고 공간 생성 시 `PENDING`으로 설정된다.
- 확인됨: 현재 일반 공간 목록/상세 조회 로직에서는 `ApprovalStatus`가 필터 조건으로 사용되지 않는다.
- 확인됨: `isActive`는 생성 시 `true`, 삭제 시 `false`로 변경된다.
- 확인됨: 대부분의 조회 로직은 `isActive`가 아니라 `deletedAt` 기준으로 삭제 여부를 판단한다.
- 확인됨: `GET /spaces/me`는 Controller와 `SecurityConfig` 의도상 인증이 필요하다.
- 확인됨: `SecurityConfig`에서 `GET /spaces/*` permitAll matcher가 `GET /spaces/me` authenticated matcher보다 먼저 선언되어 있어 인증 정책 위험이 있다.
- 확인 필요: 전역 예외 처리기와 실패 응답 공통 포맷은 현재 코드에서 확인되지 않는다.
- 확인 필요: DTO 필드의 필수값 validation 정책은 현재 명확하지 않다.
- 확인 필요: Reservation, Payment, Member, Review, Chat 등의 내부 책임은 Space Service 문서에 포함하지 않는다.
