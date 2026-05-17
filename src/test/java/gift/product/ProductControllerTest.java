package gift.product;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("상품 목록을 조회한다")
    void getProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("맥북 프로 16인치"));
    }

    @Test
    @DisplayName("상품을 조회한다")
    void getProduct() throws Exception {
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("맥북 프로 16인치"));
    }

    @Test
    @DisplayName("존재하지 않는 상품을 조회하면 404 에러 응답을 반환한다")
    void getProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PRODUCT.NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("요청한 상품을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("상품을 생성한다")
    void createProduct() throws Exception {
        String request = """
            {
              "name": "새상품",
              "price": 1000,
              "imageUrl": "https://example.com/new-product.jpg",
              "categoryId": 1
            }
            """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.name").value("새상품"))
            .andExpect(jsonPath("$.categoryId").value(1));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 상품을 생성하면 404 에러 응답을 반환한다")
    void createProductCategoryNotFound() throws Exception {
        String request = """
            {
              "name": "새상품",
              "price": 1000,
              "imageUrl": "https://example.com/new-product.jpg",
              "categoryId": 999999
            }
            """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PRODUCT.CATEGORY_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("요청한 카테고리를 찾을 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("상품명 검증에 실패하면 400 에러 응답을 반환한다")
    void createProductInvalidName() throws Exception {
        String request = """
            {
              "name": "카카오 상품",
              "price": 1000,
              "imageUrl": "https://example.com/new-product.jpg",
              "categoryId": 1
            }
            """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("PRODUCT.INVALID_NAME"))
            .andExpect(jsonPath("$.message").value("\"카카오\"가 포함된 상품명은 담당 MD와 협의한 경우에만 사용할 수 있습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("상품을 수정한다")
    void updateProduct() throws Exception {
        String request = """
            {
              "name": "수정상품",
              "price": 2000,
              "imageUrl": "https://example.com/updated-product.jpg",
              "categoryId": 1
            }
            """;

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("수정상품"))
            .andExpect(jsonPath("$.price").value(2000));
    }

    @Test
    @DisplayName("존재하지 않는 상품을 수정하면 404 에러 응답을 반환한다")
    void updateProductNotFound() throws Exception {
        String request = """
            {
              "name": "수정상품",
              "price": 2000,
              "imageUrl": "https://example.com/updated-product.jpg",
              "categoryId": 1
            }
            """;

        mockMvc.perform(put("/api/products/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PRODUCT.NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("요청한 상품을 찾을 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 상품을 수정하면 404 에러 응답을 반환한다")
    void updateProductCategoryNotFound() throws Exception {
        String request = """
            {
              "name": "수정상품",
              "price": 2000,
              "imageUrl": "https://example.com/updated-product.jpg",
              "categoryId": 999999
            }
            """;

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PRODUCT.CATEGORY_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("요청한 카테고리를 찾을 수 없습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("상품을 삭제한다")
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/4"))
            .andExpect(status().isNoContent());
    }
}
