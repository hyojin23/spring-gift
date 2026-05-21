# Research: KakaoLoginClient 요청 구성 리팩토링

## Decision 1: private method로 요청 구성 분리

**Decision**: token form parameter와 user info Bearer header 생성을 `KakaoLoginClient`의 private method로 분리한다.

**Rationale**: 요청 구성은 client 내부 구현 세부사항입니다. 외부 재사용 요구가 없으므로 새 builder class를 만들면 오히려 파일 수와 이동 비용만 늘어납니다.

**Alternatives considered**:

- **별도 KakaoRequestFactory 추가**: 테스트는 쉬워질 수 있지만 현재 구성 값이 작고 재사용처가 없습니다.
- **현재 method에 유지**: 단순하지만 requestAccessToken이 요청 구성과 전송을 동시에 설명합니다.

## Decision 2: URI는 상수로 분리

**Decision**: 카카오 token URI와 user info URI는 `private static final` 상수로 분리한다.

**Rationale**: 외부 API endpoint는 client의 중요한 계약입니다. 문자열을 method 안에 직접 두기보다 상수 이름으로 의미를 드러내는 편이 읽기 쉽습니다.

**Alternatives considered**:

- **properties로 이동**: 운영 환경별로 바꿀 요구가 아직 없고, 이번 작업 범위를 넘어섭니다.

## Decision 3: Content-Type은 MediaType 사용

**Decision**: token 요청 content type은 문자열 `"application/x-www-form-urlencoded"` 대신 Spring `MediaType.APPLICATION_FORM_URLENCODED`를 사용한다.

**Rationale**: 오타를 줄이고 HTTP header 의미를 명확히 드러낼 수 있습니다.

**Alternatives considered**:

- **문자열 유지**: 동작은 동일하지만 header 의미가 덜 명확합니다.

## Decision 4: MockRestServiceServer로 요청 구성 검증

**Decision**: 가능하면 `MockRestServiceServer`를 `RestClient.Builder`에 bind하여 실제 URI, method, header, body를 검증한다.

**Rationale**: private method를 직접 테스트하지 않고 public method를 호출해 외부 요청 구성이 동일한지 확인할 수 있습니다.

**Alternatives considered**:

- **private method 직접 테스트**: 불가능하고 바람직하지 않습니다.
- **RestClient chain mock**: mock 설정이 길어져 테스트가 구현 세부사항에 더 강하게 묶입니다.
