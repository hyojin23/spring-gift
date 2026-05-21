# Research: KakaoAuthController 서비스 분리 리팩토링

## Decision 1: callback use case를 KakaoAuthService로 이동

**Decision**: `/callback`의 카카오 token 교환, 사용자 정보 조회, 회원 조회/생성, access token 저장, JWT 발급을 `KakaoAuthService`로 이동한다.

**Rationale**: controller가 외부 client, repository, JWT provider를 모두 직접 조합하면 HTTP 레이어 테스트가 비대해집니다. 인증 use case를 service로 분리하면 controller는 요청/응답 변환에 집중하고, 핵심 흐름은 단위 테스트로 검증할 수 있습니다.

**Alternatives considered**:

- **controller 유지**: 파일 수는 적지만 controller가 인증 정책을 계속 소유합니다.
- **client 안에 회원 저장까지 포함**: 외부 API client가 persistence를 알게 되어 책임이 섞입니다.

## Decision 2: service는 token 문자열을 반환

**Decision**: `KakaoAuthService`는 `String` JWT token을 반환하고, `KakaoAuthController`가 `TokenResponse`를 생성한다.

**Rationale**: `TokenResponse`는 HTTP 응답 DTO입니다. service가 HTTP DTO를 직접 반환하지 않으면 service 테스트가 더 작아지고, 응답 표현 변경도 controller에 머뭅니다.

**Alternatives considered**:

- **service가 TokenResponse 반환**: 구현은 단순하지만 service가 web DTO에 의존합니다.
- **별도 result DTO 추가**: 현재 반환값이 token 하나뿐이라 과합니다.

## Decision 3: login redirect URL 생성은 controller에 유지

**Decision**: `/login` redirect URL 생성은 우선 controller에 유지한다.

**Rationale**: redirect URL 생성은 HTTP 응답의 Location header와 강하게 연결됩니다. 현재 리팩토링의 핵심은 callback 인증 흐름 분리이므로 범위를 좁힙니다.

**Alternatives considered**:

- **redirect URL builder service 분리**: 가능하지만 이번 작업의 주된 복잡도인 callback use case와 별도 관심사입니다.

## Decision 4: 카카오 실패 예외 정책은 변경하지 않음

**Decision**: `KakaoLoginClient` 호출 실패나 응답 누락에 대한 새 예외 정책은 이번 작업에서 정의하지 않는다.

**Rationale**: 이번 작업은 책임 분리입니다. 외부 API 실패를 별도 도메인 예외로 정리하는 작업은 응답 정책과 global handler까지 함께 다뤄야 하므로 후속 spec으로 분리하는 편이 안전합니다.
