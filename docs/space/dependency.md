# Space Service Dependency

이 문서는 Space Service가 코드에서 직접 의존하는 외부 요소와 확인되지 않은 연동을 정리한다.

## 의존성 목록

| 의존 대상 | 의존 방식 | 사용 위치 | 역할 | 확인 필요 사항 |
|---|---|---|---|---|
| PostgreSQL | JDBC datasource | `application.yaml`, `build.gradle` | Space 데이터 저장소 | 운영 DB, schema, DDL, FK, index |
| Spring Data JPA | Repository | `SpaceRepository`, `SpaceImageRepository`, `SpaceScheduleRepository` | Entity 영속화와 조회 | 쿼리 성능, index |
| Spring Security | Security filter chain | `SecurityConfig` | API 인증 정책 설정 | matcher 순서, role 정책 |
| JWT | Bearer token | `JwtFilter`, `JwtProvider` | 사용자 식별, role 추출 | 발급 주체, claim 계약 |
| 공통 응답 포맷 | `ApiResponse`, `ResponseUtil` | `SpaceController` | 성공 응답 body 생성 | 실패 응답 포맷 |
| Validation | `@Valid`, dependency | `SpaceController`, `build.gradle` | 요청 검증 기반 | DTO 제약 어노테이션 없음 |
| Actuator | dependency | `build.gradle` | 운영 endpoint 후보 | 실제 노출 정책 |

## PostgreSQL

확인됨:

- `runtimeOnly 'org.postgresql:postgresql'` dependency가 존재한다.
- datasource URL은 `jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:momentlit}?currentSchema=${DB_SCHEMA:spaces}`다.
- 기본 schema는 `spaces`다.
- JPA `ddl-auto` 기본값은 `update`다.

확인 필요:

- 운영 환경의 `ddl-auto` 값
- 실제 schema와 migration 관리 방식

## Spring Data JPA

확인됨:

- `SpaceRepository`, `SpaceImageRepository`, `SpaceScheduleRepository`가 `JpaRepository`를 상속한다.
- 조회 로직은 대부분 Spring Data JPA 파생 쿼리 메서드로 구성되어 있다.

확인 필요:

- 목록 조회 pagination 필요 여부
- 검색 조건별 DB index

## Spring Security

확인됨:

- `SecurityConfig`에서 CSRF, formLogin, httpBasic을 disable한다.
- session policy는 `STATELESS`다.
- `JwtFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 추가한다.

주의:

- `GET /spaces/*` permitAll matcher가 `GET /spaces/me` authenticated matcher보다 먼저 선언되어 있다.
- `GET /spaces/me`는 인증이 필요하다는 의도와 달리 공개 접근될 위험이 있다.

## JWT

확인됨:

- Authorization header에서 `Bearer ` prefix를 제거해 토큰을 읽는다.
- JWT subject를 `userId`로 사용한다.
- `role` claim에서 `ROLE_` prefix를 제거해 role로 사용한다.
- 인증 성공 시 `UserPrincipal(userId, role)`이 SecurityContext에 저장된다.

확인 필요:

- JWT 발급 서비스
- subject와 role claim 계약
- role이 null일 때 정책
- 토큰 만료/invalid 응답 포맷

## 공통 응답 포맷

확인됨:

- 성공 응답은 `ApiResponse<T>(message, data)` 형태다.
- `ResponseUtil.success`가 성공 응답 생성을 담당한다.

확인 필요:

- 실패 응답 포맷
- 에러 코드 체계

## Validation

확인됨:

- `spring-boot-starter-validation` dependency가 존재한다.
- 일부 Controller method에서 `@Valid @RequestBody`를 사용한다.

주의:

- 현재 Request DTO에는 `@NotNull`, `@NotBlank`, `@Positive` 같은 Bean Validation 제약이 확인되지 않는다.
- 필수값 정책은 확정하지 않는다.

## Actuator

확인됨:

- `spring-boot-starter-actuator` dependency가 존재한다.

확인 필요:

- actuator endpoint 노출 설정
- 운영 모니터링 사용 여부

## 확인되지 않은 외부 연동

현재 코드에서 직접 확인되지 않음:

- FeignClient
- WebClient
- RestTemplate
- Kafka Producer
- Kafka Consumer
- Event Publisher
- S3 또는 이미지 저장소 SDK

이미지 관련 확인됨:

- 현재 Space Service는 이미지 파일을 업로드하지 않고 이미지 URL 문자열만 저장한다.

확인 필요:

- 이미지 업로드/삭제 책임 서비스
- 예약 서비스가 Space Schedule을 참조하는 방식
- Member/Auth Service와의 인증/권한 계약
