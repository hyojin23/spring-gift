package gift.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("회원가입에 성공하면 201 Created와 토큰을 반환한다")
    void register() throws Exception {
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "new-member@example.com",
                        "password": "password"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token", not(blankOrNullString())));
    }

    @Test
    @DisplayName("로그인에 성공하면 200 OK와 토큰을 반환한다")
    void login() throws Exception {
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "login-member@example.com",
                        "password": "password"
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "login-member@example.com",
                        "password": "password"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", not(blankOrNullString())));
    }

    @Test
    @DisplayName("중복 이메일로 회원가입하면 400 에러 응답을 반환한다")
    void registerDuplicateEmail() throws Exception {
        String body = """
            {
                "email": "duplicate-member@example.com",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("MEMBER.DUPLICATE_EMAIL"));
    }

    @Test
    @DisplayName("등록되지 않은 이메일로 로그인하면 401 에러 응답을 반환한다")
    void loginEmailNotFound() throws Exception {
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "missing-member@example.com",
                        "password": "password"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("MEMBER.INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인하면 401 에러 응답을 반환한다")
    void loginWrongPassword() throws Exception {
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "wrong-password-member@example.com",
                        "password": "password"
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "wrong-password-member@example.com",
                        "password": "wrong-password"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("MEMBER.INVALID_CREDENTIALS"));
    }
}
