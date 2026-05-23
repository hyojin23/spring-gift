# Quickstart: Service 트랜잭션 경계 일관화 리팩토링

## 구현 순서

1. service의 현재 트랜잭션 상태를 확인합니다.

```powershell
rg "@Transactional|class .*Service" src/main/java/gift -g "*Service.java"
```

2. DB 접근 service에 기본 readOnly transaction을 추가합니다.

```java
@Service
@Transactional(readOnly = true)
public class ProductService {
}
```

3. 쓰기 메서드에 `@Transactional`을 추가합니다.

```java
@Transactional
public ProductResponse createProduct(ProductRequest request) {
    ...
}
```

4. 외부 API 호출 중심 service는 제외합니다.

5. 전체 테스트를 실행합니다.

```powershell
.\gradlew.bat test
```

## 확인 포인트

- 쓰기 메서드에 `@Transactional`을 빠뜨리지 않아야 합니다.
- 기존 `OrderService`의 트랜잭션 의도를 깨지 않아야 합니다.
- 응답 status/code/message/redirect가 바뀌지 않아야 합니다.
