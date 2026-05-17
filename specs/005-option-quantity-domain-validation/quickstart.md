# Quickstart: Option 수량 도메인 검증 강화

## 1. 현재 브랜치 확인

```powershell
git branch --show-current
```

예상 브랜치:

```text
005-option-quantity-domain-validation
```

## 2. 구현 범위

- `Option` 생성자가 수량이 1 이상 99,999,999 이하인지 검증하도록 변경합니다.
- `Option.subtractQuantity`가 차감 수량이 1 이상인지 검증하도록 변경합니다.
- 현재 재고보다 큰 수량 차감은 기존처럼 예외로 막습니다.
- `OptionRequest`의 Bean Validation은 유지합니다.
- 옵션명 검증은 이번 작업에서 변경하지 않습니다.
- `OptionTest`를 추가해 도메인 수량 검증을 직접 테스트합니다.

## 3. 검증 명령

```powershell
.\gradlew.bat test --tests *Option*
```

필요 시 더 좁게 실행:

```powershell
.\gradlew.bat test --tests *OptionTest*
.\gradlew.bat test --tests *OptionServiceTest*
.\gradlew.bat test --tests *OptionControllerTest*
```

## 4. 수동 확인 포인트

- 잘못된 수량으로 `Option`을 생성할 수 없습니다.
- 0 이하 수량 차감으로 재고가 증가하지 않습니다.
- 현재 재고보다 큰 수량 차감은 기존처럼 실패합니다.
- 정상 수량 차감은 기존처럼 재고를 감소시킵니다.
- Option API 응답 계약은 변경되지 않습니다.
