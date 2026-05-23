package gift.auth;

import gift.member.Member;
import gift.wish.exception.AuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticatedMemberArgumentResolverTest {

    private final AuthenticatedMemberResolver authenticatedMemberResolver = mock(AuthenticatedMemberResolver.class);
    private final AuthenticatedMemberArgumentResolver resolver = new AuthenticatedMemberArgumentResolver(
        authenticatedMemberResolver
    );

    @Test
    @DisplayName("@Authenticated Member 파라미터를 지원한다")
    void supportsAuthenticatedMemberParameter() throws Exception {
        MethodParameter parameter = parameter("authenticatedMember", 0);

        assertThat(resolver.supportsParameter(parameter)).isTrue();
    }

    @Test
    @DisplayName("애노테이션이 없는 Member 파라미터는 지원하지 않는다")
    void doesNotSupportMemberWithoutAnnotation() throws Exception {
        MethodParameter parameter = parameter("memberWithoutAnnotation", 0);

        assertThat(resolver.supportsParameter(parameter)).isFalse();
    }

    @Test
    @DisplayName("@Authenticated가 붙어도 Member 타입이 아니면 지원하지 않는다")
    void doesNotSupportAuthenticatedNonMemberParameter() throws Exception {
        MethodParameter parameter = parameter("authenticatedString", 0);

        assertThat(resolver.supportsParameter(parameter)).isFalse();
    }

    @Test
    @DisplayName("Authorization header에서 인증 회원을 반환한다")
    void resolveArgument() throws Exception {
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        Member member = new Member("member@example.com", "password");
        when(webRequest.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(authenticatedMemberResolver.resolve("Bearer valid-token")).thenReturn(member);

        Object result = resolver.resolveArgument(parameter("authenticatedMember", 0), null, webRequest, null);

        assertThat(result).isEqualTo(member);
        verify(authenticatedMemberResolver).resolve("Bearer valid-token");
    }

    @Test
    @DisplayName("인증 회원을 찾을 수 없으면 인증 예외를 전파한다")
    void resolveArgumentWithInvalidAuthorization() throws Exception {
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getHeader("Authorization")).thenReturn(null);
        when(authenticatedMemberResolver.resolve(null)).thenThrow(new AuthenticationException());

        assertThatThrownBy(() -> resolver.resolveArgument(parameter("authenticatedMember", 0), null, webRequest, null))
            .isInstanceOf(AuthenticationException.class);
    }

    private MethodParameter parameter(String methodName, int parameterIndex) throws NoSuchMethodException {
        Method method = TestController.class.getDeclaredMethod(methodName, parameterTypes(methodName));
        return new MethodParameter(method, parameterIndex);
    }

    private Class<?>[] parameterTypes(String methodName) {
        return switch (methodName) {
            case "authenticatedMember", "memberWithoutAnnotation" -> new Class<?>[]{Member.class};
            case "authenticatedString" -> new Class<?>[]{String.class};
            default -> throw new IllegalArgumentException("Unknown method: " + methodName);
        };
    }

    private static class TestController {
        void authenticatedMember(@Authenticated Member member) {
        }

        void memberWithoutAnnotation(Member member) {
        }

        void authenticatedString(@Authenticated String value) {
        }
    }
}
