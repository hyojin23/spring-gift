# Quickstart: Admin 예외 처리 분리 리팩토링

## 구현 순서

1. admin controller 내부 handler를 확인합니다.

```powershell
rg "@ExceptionHandler" src/main/java/gift/product src/main/java/gift/member
```

2. `AdminProductExceptionHandler`를 추가합니다.

```java
@ControllerAdvice(assignableTypes = AdminProductController.class)
public class AdminProductExceptionHandler {

    @ExceptionHandler(AdminProductException.class)
    public String handle(AdminProductException exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/admin/products";
    }
}
```

3. `AdminMemberExceptionHandler`를 추가합니다.

4. 기존 controller 내부 `@ExceptionHandler` method를 제거합니다.

5. 불필요한 import를 제거합니다.

6. 테스트를 실행합니다.

```powershell
.\gradlew.bat test --tests *AdminProductController*
.\gradlew.bat test --tests *AdminMemberController*
.\gradlew.bat test
```

## 확인 포인트

- product admin 예외는 `/admin/products`로 redirect되어야 합니다.
- member admin 예외는 `/admin/members`로 redirect되어야 합니다.
- flash attribute key는 `"error"`로 유지되어야 합니다.
- REST API `GlobalExceptionHandler`는 변경하지 않아야 합니다.
