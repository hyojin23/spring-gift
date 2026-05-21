# Research: JwtProvider 테스트 보강 리팩토링

## 결정 1: production code는 변경하지 않는다

**Decision**: 이번 작업은 `JwtProviderTest` 추가에 한정합니다.

**Rationale**: `JwtProvider`는 인증 핵심 컴포넌트입니다. 예외 wrapping이나 API 변경 전에 현재 동작을 테스트로 고정하는 것이 안전합니다.

**Alternatives Considered**:

- `InvalidTokenException` 즉시 도입: resolver와 예외 계약이 함께 바뀌어 범위가 커집니다.
- `getEmail()` 반환 타입 변경: 호출부 변경이 필요합니다.

## 결정 2: Spring context 없는 단위 테스트로 작성한다

**Decision**: `new JwtProvider(secret, expiration)`으로 직접 생성해 테스트합니다.

**Rationale**: `JwtProvider`는 생성자에서 설정값을 받고 외부 bean 의존성이 없습니다. Spring context를 띄우지 않는 단위 테스트가 빠르고 안정적입니다.

**Alternatives Considered**:

- `@SpringBootTest`: 설정 파일 의존성이 생기고 테스트가 느려집니다.
- `@Value` 주입 테스트: provider 자체 동작 검증에는 불필요합니다.

## 결정 3: 만료 token은 음수 expiration으로 만든다

**Decision**: expired provider를 `expiration = -1000` 같은 값으로 만들어 이미 만료된 token을 생성합니다.

**Rationale**: sleep을 사용하는 테스트보다 빠르고 안정적입니다.

**Alternatives Considered**:

- 짧은 expiration 후 sleep: 테스트 시간이 늘고 flaky해질 수 있습니다.
- JJWT builder를 테스트에서 직접 사용: production `createToken()` 동작을 검증하지 못합니다.

## 결정 4: 실패 예외는 JJWT 계층 중심으로 검증한다

**Decision**: malformed/expired/wrong secret 실패는 `JwtException` 또는 `IllegalArgumentException` 범위로 검증합니다.

**Rationale**: 현재 provider는 라이브러리 예외를 그대로 노출합니다. 구체 예외 타입을 지나치게 고정하면 JJWT 버전 변경에 취약해질 수 있습니다.

**Alternatives Considered**:

- 정확한 예외 class 고정: 테스트가 너무 구현 세부사항에 민감해집니다.
- 예외 발생 여부만 검증: 실패 원인의 범위가 너무 넓습니다.
