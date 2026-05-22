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
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTH.UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").value("인증 정보가 없거나 유효하지 않습니다."));
    }

    @Test
    @DisplayName("주문 목록 조회 시 인증 정보가 없으면 401 에러 응답을 반환한다")
    void getOrdersWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTH.UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").value("인증 정보가 없거나 유효하지 않습니다."));
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
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTH.UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").value("인증 정보가 없거나 유효하지 않습니다."));
    }

    @Test
    @DisplayName("주문 생성 시 인증 정보가 없으면 401 에러 응답을 반환한다")
    void createOrderWithoutAuthorization() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "optionId": 4,
                        "quantity": 1,
                        "message": "선물 메시지"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTH.UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").value("인증 정보가 없거나 유효하지 않습니다."));
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
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("ORDER.OPTION_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("주문할 옵션을 찾을 수 없습니다. optionId=999999"));
    }

    @Test
    @DisplayName("포인트가 부족하면 400 에러 응답을 반환한다")
    void createOrderInsufficientPoint() throws Exception {
        mockMvc.perform(post("/api/orders")
                .header("Authorization", bearerToken("user2@example.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "optionId": 1,
                        "quantity": 1,
                        "message": "선물 메시지"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("MEMBER.INSUFFICIENT_POINT"))
            .andExpect(jsonPath("$.message").value("포인트가 부족합니다."));
    }

    @Test
    @DisplayName("재고가 부족하면 기존 옵션 수량 에러 응답을 반환한다")
    void createOrderInsufficientQuantity() throws Exception {
        mockMvc.perform(post("/api/orders")
                .header("Authorization", bearerToken("user1@example.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "optionId": 8,
                        "quantity": 9,
                        "message": "선물 메시지"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("OPTION.INVALID_QUANTITY"))
            .andExpect(jsonPath("$.message").value("차감할 수량이 현재 재고보다 많습니다."));
    }

    private String bearerToken(String email) {
        return "Bearer " + jwtProvider.createToken(email);
    }
}
