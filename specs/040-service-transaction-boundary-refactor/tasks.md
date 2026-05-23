# Tasks: Service 트랜잭션 경계 일관화 리팩토링

**Input**: Design documents from `/specs/040-service-transaction-boundary-refactor/`  
**Prerequisites**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`

## Phase 1: Setup

- [x] T001 모든 service의 현재 `@Transactional` 선언 상태를 확인한다.
- [x] T002 DB 접근 service와 외부 API client service를 분류한다.
- [x] T003 각 DB 접근 service의 read/write 메서드를 분류한다.

## Phase 2: Implementation

- [x] T004 `CategoryService` 트랜잭션 경계를 read/write 의도에 맞게 정리한다.
- [x] T005 `ProductService` 트랜잭션 경계를 추가한다.
- [x] T006 `ProductUseCaseService` 트랜잭션 경계를 추가한다.
- [x] T007 `AdminProductService` 트랜잭션 경계를 추가한다.
- [x] T008 `OptionService` 트랜잭션 경계를 추가한다.
- [x] T009 `WishService` 트랜잭션 경계를 추가한다.
- [x] T010 `MemberService` 트랜잭션 경계를 추가한다.
- [x] T011 `AdminMemberService` 트랜잭션 경계를 추가한다.
- [x] T012 `OrderService` 기존 트랜잭션 경계를 확인하고 필요 시 일관화한다.
- [x] T013 외부 API client service에는 트랜잭션을 추가하지 않는다.

## Phase 3: Validation

- [x] T014 `./gradlew.bat test`를 실행한다.

## Dependencies

- T001-T003 before T004-T013
- T004-T013 before T014

## Parallel Example

```text
T005-T011 can be applied independently by service file.
```
