package gift.wish;

import gift.auth.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("인증 정보가 유효하지 않으면 401 에러 응답을 반환한다")
    void getWishesUnauthorized() throws Exception {
        mockMvc.perform(get("/api/wishes")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTH.UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").value("인증 정보가 없거나 유효하지 않습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("존재하지 않는 위시를 삭제하면 404 에러 응답을 반환한다")
    void deleteWishNotFound() throws Exception {
        mockMvc.perform(delete("/api/wishes/999999")
                .header("Authorization", bearerToken("user1@example.com")))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("WISH.NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("요청한 위시 항목을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("다른 사용자의 위시를 삭제하면 403 에러 응답을 반환한다")
    void deleteWishForbidden() throws Exception {
        mockMvc.perform(delete("/api/wishes/3")
                .header("Authorization", bearerToken("user1@example.com")))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("WISH.ACCESS_DENIED"))
            .andExpect(jsonPath("$.message").value("이 위시 항목에 접근할 권한이 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("위시 목록을 조회한다")
    void getWishes() throws Exception {
        mockMvc.perform(get("/api/wishes")
                .header("Authorization", bearerToken("user1@example.com")))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("새 위시를 추가하면 201과 Location header를 반환한다")
    void addNewWish() throws Exception {
        String request = """
            {
              "productId": 2
            }
            """;

        mockMvc.perform(post("/api/wishes")
                .header("Authorization", bearerToken("user1@example.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.productId").value(2));
    }

    @Test
    @DisplayName("이미 존재하는 위시를 추가하면 200을 반환한다")
    void addExistingWish() throws Exception {
        String request = """
            {
              "productId": 1
            }
            """;

        mockMvc.perform(post("/api/wishes")
                .header("Authorization", bearerToken("user1@example.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(1));
    }

    @Test
    @DisplayName("존재하지 않는 상품을 위시에 추가하면 404 에러 응답을 반환한다")
    void addWishProductNotFound() throws Exception {
        String request = """
            {
              "productId": 999999
            }
            """;

        mockMvc.perform(post("/api/wishes")
                .header("Authorization", bearerToken("user1@example.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("WISH.PRODUCT_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("위시에 추가할 상품을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("본인 위시를 삭제하면 204를 반환한다")
    void deleteOwnWish() throws Exception {
        mockMvc.perform(delete("/api/wishes/1")
                .header("Authorization", bearerToken("user1@example.com")))
            .andExpect(status().isNoContent());
    }

    private String bearerToken(String email) {
        return "Bearer " + jwtProvider.createToken(email);
    }
}
