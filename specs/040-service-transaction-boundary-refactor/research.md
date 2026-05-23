# Research: Service 트랜잭션 경계 일관화 리팩토링

## 결정 1: 클래스 기본값은 `readOnly = true`

**결정**: DB 접근 service는 클래스 레벨 `@Transactional(readOnly = true)`를 기본으로 둔다.

**이유**:

- 조회 메서드가 더 많은 service에서 기본 의도를 간결하게 표현할 수 있다.
- 쓰기 메서드만 명시적으로 `@Transactional`을 붙이면 read/write 차이가 드러난다.
- JPA flush 동작 최적화에 도움이 된다.

**대안**:

- 모든 메서드에 개별 annotation 부여: 가장 명확하지만 반복이 많다.
- 클래스 레벨 `@Transactional`만 사용: 조회 readOnly 의도가 드러나지 않는다.

## 결정 2: 외부 API client service는 제외

**결정**: `KakaoAuthService`, `OrderNotificationService`처럼 외부 API 호출 중심 service에는 트랜잭션을 추가하지 않는다.

**이유**:

- DB 트랜잭션과 외부 네트워크 호출을 불필요하게 묶으면 트랜잭션 시간이 길어진다.
- 이번 작업 목적은 DB 유스케이스 경계 명시다.

## 결정 3: 기능 변경 없이 annotation만 조정

**결정**: 메서드 로직, 예외, 응답 DTO, redirect 경로를 변경하지 않는다.

**이유**:

- 트랜잭션 경계 리팩토링은 동작 보존이 핵심이다.
- 테스트가 기존 계약을 그대로 검증해야 한다.

## 결정 4: 기존 `OrderService` 정책은 존중

**결정**: `OrderService`는 이미 조회/생성 메서드에 transaction annotation이 있으므로 필요 이상으로 바꾸지 않는다.

**이유**:

- 주문 생성은 옵션 수량 차감, 포인트 차감, 주문 저장, 위시 정리, 알림 발송이 묶인 중요한 흐름이다.
- 별도 작업 없이 annotation 의도를 유지하는 편이 안전하다.
