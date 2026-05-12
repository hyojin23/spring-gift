<!--
Sync Impact Report
Version change: none → 1.0.0
Modified principles: added all principles
Added sections: Project Constraints, Development Workflow
Removed sections: none
Templates requiring updates: ✅ .specify/templates/plan-template.md (no action needed), ✅ .specify/templates/spec-template.md (no action needed), ✅ .specify/templates/tasks-template.md (no action needed)
Follow-up TODOs: none
-->

# Spring-Gift Constitution

## 1. Domain-First Architecture

- Controller는 요청과 응답 처리에만 집중해야 한다.
- 비즈니스 로직은 Service 또는 Domain 계층에서 처리해야 한다.
- 도메인 객체는 자신의 상태와 규칙에 대한 책임을 가져야 한다.
- DTO와 Persistence Entity는 반드시 분리해야 한다.
- 구조 변경 시 기존 비즈니스 동작은 유지되어야 한다.

---

## 2. Test-Driven Stability

- 모든 변경 이후 전체 테스트가 통과해야 한다.
- 테스트를 우회하거나 비활성화하는 변경은 허용하지 않는다.
- 가능한 경우 Red-Green-Refactor 사이클을 유지한다.
- 비즈니스 규칙은 테스트 가능한 구조로 작성해야 한다.
- 작동 변경은 반드시 테스트 또는 검증 가능한 결과로 증명해야 한다.

---

## 3. Structural and Behavioral Separation

- 구조 변경과 작동 변경은 반드시 분리해야 한다.
- 하나의 커밋에는 하나의 목적만 포함해야 한다.
- 구조 개선 커밋에는 작동 변경을 포함하지 않는다.
- 작동 변경 시 의도하지 않은 리팩토링을 함께 수행하지 않는다.
- 변경 범위는 항상 "다음 변경 한 가지" 수준으로 제한한다.

---

## 4. Consistent API and Error Handling

- API 응답 형식은 일관성을 유지해야 한다.
- 예외는 GlobalExceptionHandler를 통해 공통 처리해야 한다.
- 클라이언트가 이해 가능한 오류 메시지를 제공해야 한다.
- Validation 로직은 중복되지 않게 관리해야 한다.
- HTTP 상태 코드는 의미에 맞게 사용해야 한다.

---

## 5. Maintainable Simplicity

- 불필요한 추상화와 과도한 설계를 지양해야 한다.
- 누구나 동일하게 실행 가능한 환경을 유지해야 한다.
- 개인 로컬 설정에 의존하지 않는 구조를 유지해야 한다.
- 반복 실행 가능한 테스트 및 개발 환경을 유지해야 한다.
- 불필요한 코드와 사용되지 않는 코드는 제거해야 한다.

---

## 6. Small Scoped Changes

- 작업 시작 전 README 또는 계획 문서에 작업 범위를 정의해야 한다.
- Git 커밋은 기능 또는 작업 단위로 구성해야 한다.
- git diff 기준으로 변경 목적을 빠르게 설명할 수 있어야 한다.
- 커밋 메시지는 AngularJS Git Commit Message Convention을 따른다.
- 큰 변경은 작은 단계로 분리하여 점진적으로 수행해야 한다.

---

## 7. Responsible AI Collaboration

- AI 산출물을 그대로 신뢰하지 않는다.
- 설계와 검증 책임은 개발자에게 있다.
- AI가 생성한 코드는 반드시 직접 이해하고 수정해야 한다.
- AI 사용 내역과 학습한 내용은 README에 기록해야 한다.
- AI가 의도하지 않은 변경이나 과도한 구현을 수행하면 즉시 범위를 재검토해야 한다.

---

## 8. Transparent Verification

- 단순히 예외가 발생하지 않는 것만으로 정상 동작으로 판단하지 않는다.
- 상태 재조회 또는 관찰 가능한 결과를 통해 작동을 검증해야 한다.
- 중간 결과를 자주 확인하고 의도하지 않은 변경은 제거해야 한다.
- 반복적으로 적용되는 중요한 결정은 ADR로 기록할 수 있다.
- 테스트 전략과 검증 방식은 설계의 일부로 관리해야 한다.

---

## Governance

- 본 Constitution은 spring-gift 프로젝트의 모든 설계, 구현, 리팩토링 작업의 기준이 된다.
- 모든 변경 사항은 본 문서의 원칙을 우선적으로 따라야 한다.
- Constitution 수정 시 변경 이유와 영향을 함께 기록해야 한다.
- 주요 원칙 변경 시 버전 정보를 함께 갱신해야 한다.

**Version**: 1.0.0 | **Ratified**: 2026-05-13 | **Last Amended**: 2026-05-13
