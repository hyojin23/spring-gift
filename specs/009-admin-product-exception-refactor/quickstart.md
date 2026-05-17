# Quickstart: Admin Product 예외 처리 리팩토링

## 목표

관리자 상품 화면에서 상품/카테고리 미존재 상황을 범용 `NoSuchElementException`이 아닌 관리자 상품 도메인 예외로 표현하고, ADR-0001에 따라 `/admin/products` redirect + flash attribute `error`로 처리합니다.

## 구현 순서

1. 관리자 상품 예외 클래스 추가
   - `AdminProductException`
   - `AdminProductNotFoundException`
   - `AdminProductCategoryNotFoundException`

2. `AdminProductService` 예외 교체
   - 상품 조회 실패: `AdminProductNotFoundException`
   - 카테고리 조회 실패: `AdminProductCategoryNotFoundException`
   - `NoSuchElementException` import 제거

3. 관리자 예외 처리 추가
   - `@ExceptionHandler(AdminProductException.class)` 또는 admin 전용 `@ControllerAdvice`
   - `RedirectAttributes.addFlashAttribute("error", exception.getMessage())`
   - `"redirect:/admin/products"` 반환

4. 상품 목록 template 수정
   - flash attribute `error`가 있으면 오류 메시지 표시

5. 테스트 추가
   - 존재하지 않는 상품 수정 화면 접근 redirect
   - 존재하지 않는 상품 수정 요청 redirect
   - 존재하지 않는 카테고리로 등록 요청 redirect
   - 존재하지 않는 카테고리로 수정 요청 redirect
   - flash attribute `error` 검증

## 검증 명령

```powershell
.\gradlew.bat test --tests *AdminProduct* --tests *Product*
```

## 완료 조건

- `AdminProductService`에 `NoSuchElementException`이 남지 않습니다.
- 관리자 미존재 예외는 `/admin/products`로 redirect됩니다.
- redirect 응답은 flash attribute `error`를 포함합니다.
- Product JSON API 테스트는 기존처럼 통과합니다.
