# Quickstart: Product 서비스 및 예외 처리 리팩토링

## 1. 현재 브랜치 확인

```powershell
git branch --show-current
```

예상 브랜치:

```text
007-product-service-exception-refactor
```

## 2. 구현 범위

- `ProductService`를 추가해 Product API 비즈니스 로직을 이동합니다.
- `gift.product.exception` 패키지에 Product 도메인 예외를 추가합니다.
- `GlobalExceptionHandler`에 Product 예외 매핑을 추가합니다.
- `ProductController`는 `ProductService`만 주입받고 성공 응답만 조립하도록 변경합니다.
- `ProductController`의 repository 직접 접근, 직접 404 분기, 개별 `@ExceptionHandler`를 제거합니다.
- `ProductControllerTest`, `ProductServiceTest`, `GlobalExceptionHandlerTest`를 추가 또는 갱신합니다.

## 3. 검증 명령

```powershell
.\gradlew.bat test --tests *Product* --tests *GlobalExceptionHandlerTest*
```

필요 시 더 좁게 실행:

```powershell
.\gradlew.bat test --tests *ProductServiceTest*
.\gradlew.bat test --tests *ProductControllerTest*
.\gradlew.bat test --tests *GlobalExceptionHandlerTest*
```

## 4. 수동 확인 포인트

- 존재하지 않는 상품 조회/수정은 404와 `PRODUCT.NOT_FOUND`를 반환합니다.
- 존재하지 않는 카테고리로 생성/수정하면 404와 `PRODUCT.CATEGORY_NOT_FOUND`를 반환합니다.
- 상품명 검증 실패는 400과 `PRODUCT.INVALID_NAME`을 반환합니다.
- 정상 상품 목록/조회/생성/수정/삭제 응답 status는 기존과 같습니다.
- AdminProductController는 변경하지 않습니다.
