# Quickstart: Option 상품 범위 조회 리팩토링

## 1. 현재 브랜치 확인

```powershell
git branch --show-current
```

예상 브랜치:

```text
004-option-product-scoped-lookup-refactor
```

## 2. 구현 범위

- `OptionRepository`에 `findByIdAndProductId(Long optionId, Long productId)`를 추가합니다.
- `OptionService.deleteOption`이 `findById(optionId)` 후 product id를 filter로 비교하는 대신 product-scoped lookup method를 사용하도록 변경합니다.
- `OptionServiceTest`의 삭제 관련 mock stubbing을 새 repository method 기준으로 갱신합니다.
- 다른 상품에 속한 옵션 삭제가 기존처럼 `OptionNotFoundException`으로 처리되는지 확인합니다.
- 기존 Option API 응답 계약은 변경하지 않습니다.

## 3. 검증 명령

```powershell
.\gradlew.bat test --tests *Option*
```

필요 시 더 좁게 실행:

```powershell
.\gradlew.bat test --tests *OptionServiceTest*
.\gradlew.bat test --tests *OptionControllerTest*
```

## 4. 수동 확인 포인트

- 존재하지 않는 옵션 삭제는 기존처럼 옵션 미존재 예외로 처리됩니다.
- 다른 상품에 속한 옵션 삭제도 기존처럼 옵션 미존재 예외로 처리됩니다.
- 정상 옵션 삭제는 기존처럼 204 응답을 반환합니다.
- 마지막 옵션 삭제 제한은 기존처럼 삭제 대상 옵션 조회 전에 적용됩니다.
