# Research: AuthenticationResolver 토큰 파싱 리팩토링

## 결정 1: null 반환 계약을 유지한다

**Decision**: `AuthenticationResolver.extractMember()`는 인증 실패 시 기존처럼 null을 반환합니다.

**Rationale**: 현재 여러 controller가 `member == null`이면 401을 반환합니다. 이번 작업에서 예외 기반 인증 실패로 바꾸면 변경 범위가 커지므로, 내부 구조만 정리합니다.

**Alternatives Considered**:

- 인증 실패 예외 throw: global handler와 모든 controller 흐름을 함께 바꿔야 합니다.
- Optional<Member> 반환: 호출부 전체 signature 변경이 필요합니다.

## 결정 2: Bearer token 추출은 private method로 분리한다

**Decision**: `extractBearerToken(String authorization)`을 추가합니다.

**Rationale**: Bearer prefix 검사는 인증 header 형식 정책입니다. `replace()`보다 `startsWith()`와 `substring()`이 의도를 더 명확히 표현합니다.

**Alternatives Considered**:

- `replace()` 유지: non-Bearer header도 JWT 파싱 단계까지 흘러갑니다.
- 정규식 사용: 현재 형식이 단순해 과합니다.

## 결정 3: broad `catch (Exception)`은 제거한다

**Decision**: JWT 파싱 실패에 해당하는 예외만 인증 실패로 처리합니다.

**Rationale**: 모든 예외를 null로 바꾸면 DB 장애나 버그도 인증 실패처럼 보입니다. 예상 가능한 token 파싱 실패만 삼키는 편이 안전합니다.

**Alternatives Considered**:

- 모든 예외 catch 유지: 장애 분석이 어려워집니다.
- 아무 예외도 catch하지 않음: 잘못된 JWT가 500으로 이어질 수 있습니다.

## 결정 4: Bearer prefix는 `"Bearer "`로 유지한다

**Decision**: 기존 header 형식과 동일하게 대문자 B와 공백이 포함된 `"Bearer "` prefix를 기준으로 합니다.

**Rationale**: 기존 controller/test helper들은 `"Bearer " + token` 형식을 사용합니다. 대소문자 허용 범위를 넓히는 것은 정책 변경이므로 이번 작업에서 제외합니다.

**Alternatives Considered**:

- case-insensitive 처리: 호환성은 좋아질 수 있지만 기존 정책보다 넓어집니다.
- trim 후 처리: header 공백 정책을 바꾸는 일이므로 후속 작업에서 다룹니다.
