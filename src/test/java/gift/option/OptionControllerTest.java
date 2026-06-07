package gift.option;

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
class OptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("존재하지 않는 상품의 옵션 목록을 조회하면 404 에러 응답을 반환한다")
    void getOptionsProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/999999/options"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("OPTION.PRODUCT_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("요청한 상품을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("존재하지 않는 상품에 옵션을 생성하면 404 에러 응답을 반환한다")
    void createOptionProductNotFound() throws Exception {
        String request = """
            {
              "name": "골드 / 1TB",
              "quantity": 10
            }
            """;

        mockMvc.perform(post("/api/products/999999/options")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("OPTION.PRODUCT_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("요청한 상품을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("존재하지 않는 옵션을 삭제하면 404 에러 응답을 반환한다")
    void deleteOptionNotFound() throws Exception {
        mockMvc.perform(delete("/api/products/1/options/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("OPTION.NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("요청한 옵션을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("중복 옵션명으로 생성하면 400 에러 응답을 반환한다")
    void createOptionDuplicateName() throws Exception {
        String request = """
            {
              "name": "스페이스 블랙 / M1 Pro",
              "quantity": 10
            }
            """;

        mockMvc.perform(post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("OPTION.DUPLICATE_NAME"))
            .andExpect(jsonPath("$.message").value("이미 존재하는 옵션명입니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("마지막 옵션을 삭제하면 400 에러 응답을 반환한다")
    void deleteLastOption() throws Exception {
        mockMvc.perform(delete("/api/products/3/options/5"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("OPTION.DELETE_NOT_ALLOWED"))
            .andExpect(jsonPath("$.message").value("옵션이 1개인 상품은 옵션을 삭제할 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("주문 이력이 있는 옵션을 삭제하면 409 에러 응답을 반환한다")
    void deleteOrderedOption() throws Exception {
        mockMvc.perform(delete("/api/products/2/options/3"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("OPTION.ORDERED_DELETE_NOT_ALLOWED"))
            .andExpect(jsonPath("$.message").value("주문 이력이 있는 옵션은 삭제할 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("옵션명 검증에 실패하면 400 에러 응답을 반환한다")
    void createOptionInvalidName() throws Exception {
        String request = """
            {
              "name": "골드@1TB",
              "quantity": 10
            }
            """;

        mockMvc.perform(post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("OPTION.INVALID_NAME"))
            .andExpect(jsonPath("$.message").value("옵션 이름에 허용되지 않는 특수 문자가 포함되어 있습니다. 사용 가능: ( ), [ ], +, -, &, /, _"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("옵션 목록을 조회한다")
    void getOptions() throws Exception {
        mockMvc.perform(get("/api/products/1/options"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("스페이스 블랙 / M1 Pro"));
    }

    @Test
    @DisplayName("새 옵션을 생성하면 201과 Location header를 반환한다")
    void createOption() throws Exception {
        String request = """
            {
              "name": "골드 / 1TB",
              "quantity": 10
            }
            """;

        mockMvc.perform(post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.name").value("골드 / 1TB"))
            .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @DisplayName("옵션을 삭제하면 204를 반환한다")
    void deleteOption() throws Exception {
        mockMvc.perform(delete("/api/products/1/options/1"))
            .andExpect(status().isNoContent());
    }
}
