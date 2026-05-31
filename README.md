# spring-gift

## spec-kit 기반 SDD 리팩토링 기록

이 프로젝트는 기능 구현 이후 유지보수성을 높이기 위해 `spec-kit`을 활용한 SDD(Spec Driven Development) 방식으로 리팩토링을 진행했습니다.

단순히 코드를 바로 수정하는 대신, 리팩토링할 주제를 먼저 정하고 `spec.md`, `plan.md`, `tasks.md` 등 관련 문서를 작성한 뒤 해당 명세를 기준으로 구현과 테스트를 진행했습니다.

### 주요 작업 방식

1. AI와 대화하며 리팩토링할 부분이 있는지 확인했습니다.
2. 리팩토링 주제별로 `spec.md` 등 관련 문서를 생성했습니다.
3. `/speckit.implement` 명령어를 사용해 명세 기반으로 구현했습니다.
4. 구현 완료 후 AI에게 브랜치 생성, 커밋, push를 요청했습니다.
5. PR을 생성하고 merge했습니다.

커밋과 push는 주로 다음 프롬프트를 사용해 진행했습니다.

```text
해당 작업 내용 커밋하려 해, 먼저 main 브랜치에서 원격 main을 pull 해서 최신화하고,
적절한 이름의 브랜치 만들고, 적절한 커밋 메시지(한글) 작성해서 커밋해 줘.
커밋 메시지는 AngularJS Git Commit Message Conventions을 참고해 줘.
작업이 잘 완료되었으면 해당 브랜치를 원격에 push까지 해줘
```

### SDD 문서 구조

spec-kit 관련 문서는 생성 파일이 많기 때문에, 리뷰 시에는 먼저 [`specs/README.md`](specs/README.md)와 [`.specify/README.md`](.specify/README.md)를 참고해 주세요.
두 문서에는 사람이 우선 확인할 리뷰 문서와 AI/도구 보조용 문서의 구분 기준을 정리했습니다.

리팩토링 작업은 `specs/` 디렉터리 아래에 주제별로 분리해 관리했습니다.

예시:

```text
specs/
├── 010-product-domain-validation-refactor/
├── 017-order-notification-service-refactor/
├── 023-authentication-resolver-refactor/
├── 039-product-usecase-service-refactor/
├── 040-service-transaction-boundary-refactor/
└── 041-order-created-event-refactor/
```

각 spec 디렉터리는 보통 다음 문서로 구성했습니다.

```text
spec.md        # 리팩토링 목표와 요구사항
plan.md        # 구현 계획
research.md    # 설계 판단과 대안
data-model.md  # 관련 모델 또는 구조 변화
quickstart.md  # 검증 방법
tasks.md       # 작업 체크리스트
```

이 방식을 통해 구현 전에 변경 목적과 범위를 먼저 정리하고, 구현 후에는 `tasks.md` 체크리스트를 통해 완료 여부를 확인했습니다.

### 주요 리팩토링 내용

#### 예외 처리 일관화

`IllegalArgumentException`, `NoSuchElementException` 등 범용 예외를 도메인별 예외로 교체하고, `GlobalExceptionHandler`에서 일관된 응답을 반환하도록 정리했습니다.

대상 예시:

- option 예외
- product 예외
- member 예외
- order 예외
- wish 예외
- category 예외
- auth 예외

#### 도메인 검증 강화

도메인 객체가 스스로 지켜야 하는 불변 조건을 생성자와 변경 메서드에서 검증하도록 개선했습니다.

대상 예시:

- `Option`
- `Product`
- `Member`
- `Order`
- `Wish`
- `Category`

이를 통해 서비스 계층에 흩어져 있던 일부 검증 책임을 도메인 내부로 이동했습니다.

#### 서비스 책임 분리

컨트롤러나 서비스에 몰려 있던 책임을 별도 서비스로 분리했습니다.

예시:

- `AdminProductService`
- `AdminMemberService`
- `ProductUseCaseService`
- `OrderNotificationService`
- `KakaoAuthService`
- `AuthenticatedMemberResolver`

컨트롤러는 요청과 응답 흐름에 집중하고, 비즈니스 로직은 서비스 계층에서 처리하도록 구조를 정리했습니다.

#### 인증 처리 개선

Authorization 헤더에서 Bearer 토큰을 추출하는 로직을 정리하고, 인증된 회원을 가져오는 책임을 공통 컴포넌트로 분리했습니다.

관련 작업:

- `AuthenticationResolver` 토큰 파싱 정리
- `JwtProvider` 테스트 추가
- `JwtTokenException` 도입
- `AuthenticatedMemberResolver` 도입

#### 주문 흐름 개선

주문 생성 과정에서 여러 책임이 한곳에 모여 있던 구조를 점진적으로 정리했습니다.

주요 개선:

- 주문 예외 정리
- 주문 도메인 검증 강화
- 카카오 메시지 발송 책임 분리
- 위시리스트 정리 정책 명확화
- 주문 총 가격 계산 책임 분리
- 주문 생성 이벤트 도입

특히 `OrderService`가 카카오 알림을 직접 호출하지 않고, 주문 생성 후 `OrderCreatedEvent`를 발행하도록 변경했습니다. 카카오 알림은 `@TransactionalEventListener(phase = AFTER_COMMIT)`를 사용하는 리스너에서 처리하도록 분리했습니다.

#### 트랜잭션 경계 정리

서비스 계층의 트랜잭션 선언을 일관화했습니다.

- 조회 메서드: `@Transactional(readOnly = true)`
- 생성/수정/삭제 메서드: `@Transactional`

이를 통해 서비스별 트랜잭션 경계를 명확히 했습니다.

### 커밋 메시지 규칙

커밋 메시지는 AngularJS Git Commit Message Conventions를 참고하되, scope는 생략하고 한글 메시지를 사용했습니다.

예시:

```text
refactor: 옵션 삭제 검증에 count 쿼리 사용
test: 권한 관련 JwtProvider 토큰 검증 테스트 추가
refactor: 주문 생성 이벤트로 알림 발송 분리
```

### 테스트

리팩토링 후 전체 테스트를 실행해 기존 기능이 깨지지 않는지 확인했습니다.

```bash
./gradlew test
```

Windows 환경에서는 다음 명령어를 사용했습니다.

```powershell
.\gradlew.bat test
```

### 정리

이번 리팩토링은 기능 추가보다는 기존 구조를 더 명확하고 유지보수하기 좋게 만드는 데 집중했습니다.

spec-kit을 활용한 SDD 방식은 리팩토링 목적과 범위를 문서로 먼저 정리하고, 구현과 테스트를 작은 PR 단위로 관리하는 데 도움이 되었습니다. 특히 AI를 단순 코드 생성 도구가 아니라 리팩토링 후보 탐색, 설계 문서 작성, 구현, 커밋 자동화까지 함께 수행하는 협업 도구로 활용할 수 있었습니다.
