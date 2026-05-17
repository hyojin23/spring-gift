# Quickstart: Admin Product 서비스 분리 리팩토링

## 1. 현재 브랜치 확인

```powershell
git branch --show-current
```

예상 브랜치:

```text
008-admin-product-service-refactor
```

## 2. 구현 범위

- `AdminProductService`를 추가합니다.
- `AdminProductController`에서 repository 직접 접근을 제거합니다.
- Admin 상품 목록/등록/수정/삭제 view와 redirect 계약을 유지합니다.
- 상품명 검증 실패 시 기존 form view와 model attribute를 유지합니다.
- `ProductNameValidator.validate(name, true)`를 admin 검증 규칙으로 유지합니다.
- HTML template 파일은 변경하지 않습니다.
- Product JSON API와 global error response contract는 변경하지 않습니다.

## 3. 검증 명령

```powershell
.\gradlew.bat test --tests *Product*
```

필요 시 더 좁게 실행:

```powershell
.\gradlew.bat test --tests *AdminProductControllerTest*
.\gradlew.bat test --tests *ProductControllerTest* --tests *ProductServiceTest*
```

## 4. 수동 확인 포인트

- `/admin/products`는 `product/list` view를 반환합니다.
- `/admin/products/new`는 `product/new` view와 카테고리 목록을 반환합니다.
- 등록/수정 검증 실패 시 기존 입력값과 오류 메시지가 유지됩니다.
- 등록/수정/삭제 성공 시 `/admin/products`로 redirect합니다.
- `AdminProductController`는 `AdminProductService`만 의존합니다.
