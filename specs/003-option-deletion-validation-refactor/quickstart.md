# Quickstart: Option 삭제 검증 조회 최적화

## 1. 현재 브랜치 확인

```powershell
git branch --show-current
```

예상 브랜치:

```text
003-option-deletion-validation-refactor
```

## 2. 구현 범위

- `OptionRepository`에 `countByProductId(Long productId)`를 추가합니다.
- `OptionService.validateCanDelete`가 옵션 목록 전체 조회 대신 count query를 사용하도록 변경합니다.
- `OptionServiceTest`의 삭제 관련 mock stubbing을 count query 기준으로 갱신합니다.
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

- 마지막 옵션 삭제 시도는 기존처럼 삭제 제한 예외로 처리됩니다.
- 옵션이 2개 이상이면 마지막 옵션 삭제 제한 검증을 통과합니다.
- 존재하지 않는 옵션 삭제는 기존처럼 옵션 미존재 예외로 처리됩니다.
- 정상 옵션 삭제는 기존처럼 204 응답을 반환합니다.
