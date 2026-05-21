# Research: 카카오 로그인 예외 처리 리팩토링

## Decision 1: KakaoLoginException 추가

**Decision**: 카카오 로그인 실패를 표현하는 `KakaoLoginException`을 `gift.auth.exception` 패키지에 추가한다.

**Rationale**: 카카오 API 호출 실패와 응답 이상은 인증 도메인의 외부 연동 실패입니다. Spring `RestClientException`이나 NPE가 service/controller로 직접 새지 않도록 도메인 예외로 변환합니다.

**Alternatives considered**:

- **RestClientException 그대로 전파**: 구현은 간단하지만 service가 외부 HTTP client 구현에 묶입니다.
- **Token/UserInfo 예외 세분화**: 응답 정책이 아직 동일하므로 예외 타입을 늘릴 필요가 없습니다.

## Decision 2: HTTP 호출 실패는 client에서 변환

**Decision**: `KakaoLoginClient`가 `RestClientException` 계열을 잡아 `KakaoLoginException`으로 감싼다.

**Rationale**: 외부 API client 경계에서 구현 예외를 도메인 예외로 번역하는 것이 책임 위치상 자연스럽습니다. service는 카카오 로그인 실패라는 의미만 알면 됩니다.

**Alternatives considered**:

- **KakaoAuthService에서 catch**: service가 client 내부 구현 예외를 알아야 합니다.
- **controller advice에서 RestClientException 처리**: 카카오 외 다른 RestClient 예외까지 같은 정책으로 묶일 수 있습니다.

## Decision 3: 응답 body null은 client에서 검증

**Decision**: `requestAccessToken()`과 `requestUserInfo()`의 response body null은 `KakaoLoginClient`에서 검증한다.

**Rationale**: API 응답 body 존재 여부는 client가 가장 잘 압니다. null을 그대로 반환하면 service에서 NPE가 발생해 실패 의미가 흐려집니다.

**Alternatives considered**:

- **service에서 null 검증**: token/user info client method가 null을 반환할 수 있다는 계약이 생깁니다.

## Decision 4: 필수 값 blank 검증은 service 또는 response accessor 경계에서 처리

**Decision**: access token과 email 같은 필수 값은 auth 흐름에서 사용하기 전에 `KakaoLoginException`으로 검증한다.

**Rationale**: 카카오 API가 성공 status를 반환해도 필수 값이 없으면 로그인 흐름은 진행할 수 없습니다. `Member` 생성자나 뒤쪽 로직의 검증 예외에 의존하지 않고 카카오 로그인 실패로 표현합니다.

**Alternatives considered**:

- **MemberValidationException에 맡김**: email 누락 원인이 카카오 응답 이상이라는 의미가 사라집니다.
