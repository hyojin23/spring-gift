# Implementation Plan: Kakao 메시지 템플릿 분리 리팩토링

**Branch**: `020-kakao-message-template-refactor` | **Date**: 2026-05-20 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/020-kakao-message-template-refactor/spec.md`

**Note**: 이 문서는 `KakaoMessageClient`에서 메시지 템플릿 문자열 생성 책임을 별도 builder로 분리하는 리팩토링 계획입니다.

## Summary

`KakaoMessageClient`는 현재 카카오 API HTTP 호출과 템플릿 JSON 문자열 생성을 함께 담당합니다. `KakaoMessageTemplateBuilder`를 추가해 상품명, 옵션명, 수량, 금액, 주문 메시지를 포함한 템플릿 생성을 위임하고, client는 HTTP 요청 전송에 집중하게 합니다. 기존 `sendToMe()` public API와 카카오 API 호출 방식은 유지합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, RestClient  
**Storage**: 해당 없음  
**Testing**: JUnit 5 / Mockito / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API + 외부 카카오 API client  
**Performance Goals**: 성능 목표 없음, 책임 분리와 템플릿 테스트 가능성 개선 우선  
**Constraints**: public API 유지, endpoint/header/form key 유지, best-effort 정책 유지  
**Scale/Scope**: `KakaoMessageClient`, `KakaoMessageTemplateBuilder`, order tests

## Constitution Check

- Domain-First Architecture: 주문 완료 알림의 표현 생성 책임을 명확한 객체로 분리합니다.
- Test-Driven Stability: 메시지 포함/생략과 금액 포맷을 테스트로 고정합니다.
- Structural and Behavioral Separation: HTTP 전송은 client, 템플릿 생성은 builder가 담당합니다.
- Consistent API and Error Handling: `OrderNotificationService`의 best-effort 정책과 client public API를 유지합니다.
- Maintainable Simplicity: 문자열 템플릿 방식은 유지하고 JSON builder 도입은 제외합니다.
- Small Scoped Changes: 카카오 메시지 템플릿 생성 책임 분리에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 외부 API 호출 계약을 유지합니다.

## Project Structure

### Documentation (this feature)

```text
specs/020-kakao-message-template-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/order/
├── KakaoMessageClient.java
└── KakaoMessageTemplateBuilder.java
```

```text
src/test/java/gift/order/
├── KakaoMessageTemplateBuilderTest.java
└── OrderNotificationServiceTest.java
```

**Structure Decision**: builder는 현재 카카오 메시지가 order 알림 전용이므로 `gift.order` 패키지에 둡니다. 추후 알림 도메인이 커지면 notification 패키지로 이동할 수 있습니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `KakaoMessageClient.buildTemplate()`의 현재 출력 구조를 확인합니다.
2. 주문 메시지 null/blank 처리 정책을 확인합니다.
3. 금액 포맷 정책을 확인합니다.
4. `KakaoMessageClient.sendToMe()` public API와 HTTP request 구조를 확인합니다.

## Phase 1: Design & Contracts

1. `KakaoMessageTemplateBuilder.build(order, product)`를 설계합니다.
2. 기존 template 문자열 생성 코드를 builder로 이동합니다.
3. `KakaoMessageClient`는 builder를 주입받아 `template_object` 값을 생성합니다.
4. builder 단위 테스트를 추가합니다.
5. 기존 order 알림 테스트가 통과하는지 확인합니다.

## Phase 2: Task Planning Approach

1. builder 테스트 추가
2. builder 클래스 추가
3. client 의존성 변경
4. private buildTemplate 제거
5. order 테스트 실행과 검색 검증

## Risk Assessment

- **템플릿 출력 회귀 위험**: 상품명/옵션명/수량/금액/메시지 포함 여부를 테스트로 고정합니다.
- **외부 API 호출 계약 변경 위험**: `sendToMe()` signature와 `template_object` key를 유지합니다.
- **구조 과확장 위험**: JSON builder나 notification 패키지 분리는 이번 범위에서 제외합니다.
- **문자 escaping 위험**: 기존 문자열 생성 방식을 그대로 이동해 behavior-preserving refactor로 유지합니다.
