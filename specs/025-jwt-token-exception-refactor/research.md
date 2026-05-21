# Research: JWT 토큰 예외 리팩토링

## Decision 1: JJWT 예외를 auth 도메인 예외로 변환

**Decision**: `JwtProvider.getEmail()`은 JJWT의 `JwtException`과 잘못된 token 입력 예외를 `JwtTokenException`으로 변환한다.

**Rationale**: JWT 파싱 실패는 인증 도메인의 실패입니다. service/resolver 계층이 JJWT 구현 예외를 직접 알 필요가 없고, 후속 라이브러리 교체나 예외 메시지 변경에도 테스트가 흔들리지 않습니다.

**Alternatives considered**:

- **JJWT 예외를 그대로 노출**: 현재 구현은 단순하지만 인증 실패 기준이 외부 라이브러리에 묶입니다.
- **만료/서명/형식별 예외 세분화**: 지금 controller 동작은 모두 인증 실패로 동일하므로 과한 분기입니다.

## Decision 2: resolver의 null 반환 계약 유지

**Decision**: `AuthenticationResolver`는 `JwtTokenException`을 잡아 기존처럼 `null`을 반환한다.

**Rationale**: 현재 controller들은 `member == null`일 때 401을 반환합니다. 이번 작업의 목적은 내부 예외 정리이며 외부 인증 실패 응답 구조 변경이 아닙니다.

**Alternatives considered**:

- **resolver가 예외를 던지도록 변경**: global handler 기반으로 더 일관될 수 있지만 controller 영향 범위가 커집니다.
- **controller마다 토큰 예외 처리**: 인증 실패 처리 기준이 흩어집니다.

## Decision 3: 단일 JwtTokenException으로 시작

**Decision**: auth 토큰 실패는 우선 `JwtTokenException` 하나로 표현한다.

**Rationale**: 현재 요구사항은 token이 유효하지 않다는 사실만 필요합니다. 만료 여부를 별도 응답으로 구분하지 않으므로 예외 타입을 늘릴 필요가 없습니다.

**Alternatives considered**:

- **ExpiredTokenException, MalformedTokenException, InvalidSignatureException 분리**: 도메인 표현은 더 세밀하지만 현재 사용처에서는 모두 인증 실패로 동일하게 처리됩니다.
- **InvalidTokenException 이름 사용**: 의미는 충분하지만 JWT 대상임을 드러내는 `JwtTokenException`이 현재 범위에는 더 명확합니다.
