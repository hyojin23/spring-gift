# Data Model: 인증 Member Argument Resolver 리팩토링

## Authenticated Annotation

### 책임

- controller method parameter가 현재 인증된 `Member`를 요구한다는 사실을 표시합니다.
- `AuthenticatedMemberArgumentResolver`의 지원 대상 조건으로 사용됩니다.

### 예상 형태

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {
}
```

### 제약

- method parameter에만 사용할 수 있습니다.
- 단독으로 인증을 수행하지 않고 argument resolver의 marker 역할만 합니다.

## AuthenticatedMemberArgumentResolver

### 책임

- `@Authenticated Member` 파라미터를 지원합니다.
- 현재 HTTP 요청의 `Authorization` header를 읽습니다.
- 기존 `AuthenticatedMemberResolver`에 인증 회원 조회를 위임합니다.
- 인증 실패 시 기존 `AuthenticationException` 흐름을 유지합니다.

### 주요 메서드

```java
public boolean supportsParameter(MethodParameter parameter)

public Object resolveArgument(
    MethodParameter parameter,
    ModelAndViewContainer mavContainer,
    NativeWebRequest webRequest,
    WebDataBinderFactory binderFactory
)
```

### 입력

- controller method parameter metadata
- current request `Authorization` header

### 출력

- 인증된 `Member`

### 예외

- `AuthenticationException`: header가 없거나 유효하지 않아 `Member`를 resolve할 수 없을 때 발생합니다.

## WebMvcConfig

### 책임

- Spring MVC argument resolver 목록에 `AuthenticatedMemberArgumentResolver`를 등록합니다.

### 예상 형태

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedMemberArgumentResolver);
    }
}
```

## AuthenticatedMemberResolver

### 책임

- 기존 인증 필수 member 조회 정책을 유지합니다.
- `AuthenticationResolver.extractMember()` 결과가 null이면 `AuthenticationException`을 던집니다.

### 변경 여부

- 이번 작업에서는 재사용 대상입니다.
- 필요할 경우 테스트 보강만 수행합니다.

## OrderController

### 변경 전

```java
public ResponseEntity<Page<OrderResponse>> getOrders(
    @RequestHeader(value = "Authorization", required = false) String authorization,
    Pageable pageable
)
```

### 변경 후

```java
public ResponseEntity<Page<OrderResponse>> getOrders(
    @Authenticated Member member,
    Pageable pageable
)
```

### 영향

- controller가 header parsing을 알 필요가 없습니다.
- service 호출에는 기존처럼 `member.getId()` 또는 `member`를 사용합니다.

## WishController

### 변경 전

```java
public ResponseEntity<Page<WishResponse>> getWishes(
    @RequestHeader(value = "Authorization", required = false) String authorization,
    Pageable pageable
)
```

### 변경 후

```java
public ResponseEntity<Page<WishResponse>> getWishes(
    @Authenticated Member member,
    Pageable pageable
)
```

### 영향

- 목록/추가/삭제 API 모두 동일한 인증 파라미터 주입 방식을 사용합니다.

## 관계

```text
OrderController / WishController
  -> @Authenticated Member parameter
  -> AuthenticatedMemberArgumentResolver
  -> AuthenticatedMemberResolver.resolve(authorization)
  -> AuthenticationResolver.extractMember(authorization)
  -> Member or AuthenticationException
```
