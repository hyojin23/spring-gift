# Quickstart: Product 도메인 검증 강화 리팩토링

## 목표

`Product` 생성자와 `update()`에서 공통 도메인 불변 조건을 검증해, Product가 항상 유효한 상태로 생성/수정되도록 합니다.

## 구현 순서

1. Product 단위 테스트 추가
   - 빈 상품명 생성 실패
   - 0 이하 가격 생성 실패
   - 빈 이미지 URL 생성 실패
   - null 카테고리 생성 실패
   - update 검증 실패
   - update 실패 시 기존 상태 유지

2. `Product`에 validation 메서드 추가
   - `validate(name, price, imageUrl, category)`
   - 실패 시 `ProductValidationException` 발생

3. 생성자와 `update()`에 validation 적용
   - 필드 할당 전에 검증
   - 생성자와 update가 같은 검증 메서드 사용

4. 경로별 상품명 정책 유지 확인
   - API: `ProductNameValidator.validate(name)`
   - Admin: `ProductNameValidator.validate(name, true)`

5. 테스트 실행

## 검증 명령

```powershell
.\gradlew.bat test --tests *Product* --tests *AdminProduct*
```

## 완료 조건

- Product 생성자와 `update()`가 공통 불변 조건을 검증합니다.
- 검증 실패 시 `ProductValidationException`이 발생합니다.
- `ProductNameValidator`의 API/Admin 호출 정책은 변경되지 않습니다.
- Product 및 Admin Product 테스트가 통과합니다.
