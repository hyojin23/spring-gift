# Quickstart: 인증 Member Argument Resolver 리팩토링

## 구현 순서

1. 현재 중복 인증 처리 위치를 확인합니다.

```powershell
Select-String -Path src\main\java\gift\**\*.java -Pattern 'RequestHeader\(value = "Authorization"','AuthenticatedMemberResolver'
```

2. `gift.auth.Authenticated` 애노테이션을 추가합니다.

```java
package gift.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {
}
```

3. `gift.auth.AuthenticatedMemberArgumentResolver`를 추가합니다.

```java
@Component
public class AuthenticatedMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTHORIZATION = "Authorization";

    private final AuthenticatedMemberResolver authenticatedMemberResolver;

    public AuthenticatedMemberArgumentResolver(AuthenticatedMemberResolver authenticatedMemberResolver) {
        this.authenticatedMemberResolver = authenticatedMemberResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticated.class)
            && Member.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        String authorization = webRequest.getHeader(AUTHORIZATION);
        return authenticatedMemberResolver.resolve(authorization);
    }
}
```

4. MVC 설정에 resolver를 등록합니다.

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver;

    public WebMvcConfig(AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver) {
        this.authenticatedMemberArgumentResolver = authenticatedMemberArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedMemberArgumentResolver);
    }
}
```

5. `OrderController`와 `WishController`에서 직접 header를 제거하고 `@Authenticated Member member`를 사용합니다.

```java
@GetMapping
public ResponseEntity<Page<OrderResponse>> getOrders(
    @Authenticated Member member,
    Pageable pageable
) {
    return ResponseEntity.ok(orderService.getOrders(member.getId(), pageable));
}
```

6. 테스트를 추가/수정합니다.

```powershell
.\gradlew.bat test --tests *AuthenticatedMemberArgumentResolver*
.\gradlew.bat test --tests *OrderController*
.\gradlew.bat test --tests *WishController*
.\gradlew.bat test
```

## 확인 포인트

- `WishController`와 `OrderController`에 `@RequestHeader(value = "Authorization", required = false)`가 남아 있지 않아야 합니다.
- 인증 실패 응답은 401 `AUTH.UNAUTHORIZED`여야 합니다.
- 인증 성공 시 기존 service 호출 인자와 응답 body가 유지되어야 합니다.
- `supportsParameter()`는 `@Authenticated Member`만 true를 반환해야 합니다.
- 애노테이션 없는 `Member` 파라미터는 처리하지 않아야 합니다.
