package gift.order;

import gift.auth.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("주문 목록을 조회한다")
    void getOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                .header("Authorization", bearerToken("user1@example.com")))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 목록 조회 시 인증 정보가 유효하지 않으면 401 응답을 반환한다")
    void getOrdersUnauthorized() throws Exception {
        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("주문을 생성하면 201 Created와 주문 응답을 반환한다")
    void createOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                .header("Authorization", bearerToken("user1@example.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "optionId": 4,
                        "quantity": 1,
                        "message": "선물 메시지"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.optionId").value(4))
            .andExpect(jsonPath("$.quantity").value(1))
            .andExpect(jsonPath("$.message").value("선물 메시지"));
    }

    @Test
    @DisplayName("주문 생성 시 인증 정보가 유효하지 않으면 401 응답을 반환한다")
    void createOrderUnauthorized() throws Exception {
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "optionId": 4,
                        "quantity": 1,
                        "message": "선물 메시지"
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("존재하지 않는 옵션으로 주문하면 404 응답을 반환한다")
    void createOrderOptionNotFound() throws Exception {
        mockMvc.perform(post("/api/orders")
                .header("Authorization", bearerToken("user1@example.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "optionId": 999999,
                        "quantity": 1,
                        "message": "선물 메시지"
                    }
                    """))
            .andExpect(status().isNotFound());
    }

    private String bearerToken(String email) {
        return "Bearer " + jwtProvider.createToken(email);
    }
}
