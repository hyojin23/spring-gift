# Research: 인증 Member Argument Resolver 리팩토링

## 결정 1: `HandlerMethodArgumentResolver` 도입

**결정**: 인증 필수 controller 파라미터를 처리하기 위해 Spring MVC `HandlerMethodArgumentResolver`를 도입한다.

**이유**:

- 현재 controller마다 `Authorization` header를 받고 인증 member를 직접 resolve한다.
- 인증 필수 API의 공통 정책을 MVC argument 해석 단계로 이동하면 controller는 비즈니스 요청 처리에 집중할 수 있다.
- 향후 인증 필수 API가 늘어나도 메서드 파라미터 선언만으로 재사용할 수 있다.

**대안**:

- `AuthenticatedMemberResolver`를 계속 controller에서 직접 호출: 구현은 단순하지만 `@RequestHeader`와 resolve 호출 중복이 남는다.
- Servlet filter/interceptor 사용: 인증 실패 선처리에는 적합하지만 controller 파라미터에 `Member`를 직접 주입하려면 추가 저장소나 request attribute 규칙이 필요하다.
- Spring Security 도입: 장기적으로 가능하지만 이번 리팩토링 범위를 넘어선다.

## 결정 2: 명시적 `@Authenticated` 애노테이션 사용

**결정**: resolver는 `Member` 타입만 보지 않고 `@Authenticated Member` 파라미터만 지원한다.

**이유**:

- 단순 타입 기반 resolver는 향후 다른 목적의 `Member` 파라미터와 충돌할 수 있다.
- 컨트롤러 시그니처만 봐도 해당 API가 인증 회원을 요구한다는 의도가 드러난다.
- 테스트에서 지원 대상과 비지원 대상을 명확히 구분할 수 있다.

**대안**:

- `Member` 타입 전체 지원: 코드가 짧지만 암묵적이고 충돌 가능성이 있다.
- `AuthenticatedMember` wrapper 타입 도입: 타입 안정성은 좋지만 서비스 호출 시 `member.member()` 같은 래핑 해제가 반복된다.

## 결정 3: 기존 `AuthenticatedMemberResolver` 재사용

**결정**: 새 argument resolver는 JWT 파싱을 직접 하지 않고 기존 `AuthenticatedMemberResolver.resolve()`에 위임한다.

**이유**:

- `AuthenticatedMemberResolver`가 이미 "member가 없으면 `AuthenticationException`" 정책을 캡슐화하고 있다.
- JWT 파싱과 회원 조회는 `AuthenticationResolver`가 계속 담당한다.
- 인증 실패 응답 계약을 변경하지 않고 구조만 개선할 수 있다.

**대안**:

- argument resolver에서 `AuthenticationResolver.extractMember()`를 직접 호출: 한 단계 줄어들지만 null 검사와 예외 변환 정책이 중복된다.
- `AuthenticationResolver.extractMember()`가 바로 예외를 던지도록 변경: 기존 null 반환 계약이 깨진다.

## 결정 4: Web MVC 설정으로 resolver 등록

**결정**: `WebMvcConfigurer#addArgumentResolvers()`를 구현하는 설정 클래스를 추가해 resolver를 등록한다.

**이유**:

- Spring MVC의 표준 확장 지점이다.
- controller 코드와 인증 파라미터 해석 등록 책임을 분리할 수 있다.
- 테스트에서 Bean wiring을 검증하기 쉽다.

**대안**:

- `RequestMappingHandlerAdapter` 직접 커스터마이징: 필요 이상으로 범위가 넓고 설정 복잡도가 높다.

## 결정 5: API 계약은 변경하지 않음

**결정**: request header 형식, endpoint path, request body, response body, error response는 변경하지 않는다.

**이유**:

- 이번 작업은 내부 구조 리팩토링이다.
- 클라이언트 관점의 동작이 바뀌면 리팩토링 이상의 변경이 된다.
- 기존 controller 테스트를 회귀 테스트로 활용할 수 있다.

## 후속 후보

- `AuthenticationException`을 `gift.auth.exception`으로 이동한다.
- 인증 실패 예외와 JWT 예외를 공통 `AuthException` 계층으로 정리한다.
- 인증 필수 API가 많아지면 Spring Security 전환 여부를 별도 spec에서 검토한다.
