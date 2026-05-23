# Quickstart: Product 유스케이스 서비스 공통화 리팩토링

## 구현 순서

1. 생성/수정 중복을 확인합니다.

```powershell
rg "createProduct|updateProduct|findCategory|getCategory|ProductNameValidator" src/main/java/gift/product
```

2. API와 admin의 상품명 검증 정책 차이를 확인합니다.

```java
ProductNameValidator.validate(name);       // API
ProductNameValidator.validate(name, true); // Admin
```

3. 공통 생성/수정 유스케이스 입력 형태를 정합니다.

4. `ProductService` 또는 별도 package-private service에 생성/수정 저장 흐름을 모읍니다.

5. `ProductService` API 메서드는 기존처럼 `ProductResponse`를 반환하도록 유지합니다.

6. `AdminProductService` 생성/수정 메서드는 공통 유스케이스를 호출하도록 변경합니다.

7. 테스트를 실행합니다.

```powershell
.\gradlew.bat test --tests *Product*
.\gradlew.bat test --tests *AdminProductController*
.\gradlew.bat test
```

## 확인 포인트

- API `"카카오"` 상품명 제한이 유지되어야 합니다.
- 관리자 `"카카오"` 상품명 허용이 유지되어야 합니다.
- 관리자 상품/카테고리 미존재 redirect + flash error가 유지되어야 합니다.
- API 상품/카테고리 미존재 JSON error response가 유지되어야 합니다.
