# Quickstart: Category 예외 패키지 정리 리팩토링

## 구현 순서

1. category 예외 사용처를 검색합니다.

```powershell
rg "Category(NotFound|Validation)Exception" src
```

2. `src/main/java/gift/category/exception` 패키지로 예외 클래스를 이동합니다.

3. package 선언을 변경합니다.

```java
package gift.category.exception;
```

4. main/test import를 새 패키지로 수정합니다.

5. 관련 테스트를 실행합니다.

```powershell
.\gradlew.bat test --tests *Category*
.\gradlew.bat test --tests *GlobalExceptionHandler*
```

6. 전체 테스트를 실행합니다.

```powershell
.\gradlew.bat test
```

## 확인 포인트

- category 미존재 응답 code가 `CATEGORY.NOT_FOUND`로 유지되는지 확인합니다.
- category 검증 실패 응답 code가 `CATEGORY.INVALID`로 유지되는지 확인합니다.
- 예외 메시지가 기존과 동일한지 확인합니다.
- category 예외 클래스가 루트 패키지에 남아 있지 않은지 확인합니다.
