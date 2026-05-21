# Research: 카카오 로그인 URL 구성 분리 리팩토링

## Decision 1: KakaoLoginUrlProvider 추가

**Decision**: 카카오 authorization URL 생성 책임을 `KakaoLoginUrlProvider`로 분리한다.

**Rationale**: 로그인 URL 생성은 카카오 OAuth 정책입니다. controller 안에 URI와 query parameter가 직접 있으면 HTTP 응답 처리와 OAuth 설정 조립 책임이 섞입니다.

**Alternatives considered**:

- **KakaoAuthService에 추가**: callback 로그인 use case와 authorize URL 생성이 같은 service에 섞입니다.
- **controller에 유지**: 파일 수는 적지만 controller가 카카오 URL 정책을 계속 소유합니다.

## Decision 2: provider는 String URL 반환

**Decision**: `KakaoLoginUrlProvider`는 완성된 URL 문자열을 반환한다.

**Rationale**: controller는 `Location` header에 문자열 URL을 넣으면 충분합니다. `URI` 타입을 써도 되지만 기존 controller가 문자열을 사용하므로 동작 변화가 적습니다.

**Alternatives considered**:

- **URI 반환**: 타입 의미는 명확하지만 현재 ResponseEntity header 구성에는 문자열이 더 단순합니다.

## Decision 3: authorize URI와 scope는 provider 내부 상수로 유지

**Decision**: authorize URI, response_type, scope는 provider 내부 상수로 둔다.

**Rationale**: 현재 환경별로 바꿀 요구가 없고, application properties로 옮기면 설정 범위가 불필요하게 넓어집니다.

**Alternatives considered**:

- **properties로 이동**: 운영 유연성은 늘지만 이번 책임 분리 범위를 넘어섭니다.

## Decision 4: controller 테스트는 provider mock 중심으로 변경

**Decision**: controller login 테스트는 URL 조립 세부사항 대신 provider가 반환한 URL이 Location header에 들어가는지 검증한다.

**Rationale**: URL 조립은 provider 테스트가 담당하고, controller 테스트는 HTTP status/header 변환에 집중합니다.

**Alternatives considered**:

- **controller 테스트에서 URL query까지 계속 검증**: 책임 분리 후에도 controller 테스트가 provider 내부 구현에 묶입니다.
