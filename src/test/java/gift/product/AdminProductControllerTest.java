package gift.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("관리자 상품 목록 화면을 조회한다")
    void list() throws Exception {
        mockMvc.perform(get("/admin/products"))
            .andExpect(status().isOk())
            .andExpect(view().name("product/list"))
            .andExpect(model().attributeExists("products"));
    }

    @Test
    @DisplayName("관리자 상품 목록 화면은 flash 오류 메시지를 표시할 수 있다")
    void listWithError() throws Exception {
        mockMvc.perform(get("/admin/products")
                .flashAttr("error", "상품이 존재하지 않습니다. id=999999"))
            .andExpect(status().isOk())
            .andExpect(view().name("product/list"))
            .andExpect(model().attribute("error", "상품이 존재하지 않습니다. id=999999"))
            .andExpect(model().attributeExists("products"));
    }

    @Test
    @DisplayName("관리자 상품 등록 화면을 조회한다")
    void newForm() throws Exception {
        mockMvc.perform(get("/admin/products/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("product/new"))
            .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("관리자 상품을 등록하면 상품 목록으로 redirect한다")
    void create() throws Exception {
        mockMvc.perform(post("/admin/products")
                .param("name", "관리상품")
                .param("price", "1000")
                .param("imageUrl", "https://example.com/admin-product.jpg")
                .param("categoryId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    @DisplayName("관리자 상품 등록 검증에 실패하면 등록 화면을 다시 반환한다")
    void createInvalidName() throws Exception {
        mockMvc.perform(post("/admin/products")
                .param("name", "관리상품@")
                .param("price", "1000")
                .param("imageUrl", "https://example.com/admin-product.jpg")
                .param("categoryId", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("product/new"))
            .andExpect(model().attribute("errors", hasSize(1)))
            .andExpect(model().attribute("name", "관리상품@"))
            .andExpect(model().attribute("price", 1000))
            .andExpect(model().attribute("imageUrl", "https://example.com/admin-product.jpg"))
            .andExpect(model().attribute("categoryId", 1L))
            .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("관리자 상품 등록에서는 카카오 포함 상품명을 허용한다")
    void createKakaoName() throws Exception {
        mockMvc.perform(post("/admin/products")
                .param("name", "카카오상품")
                .param("price", "1000")
                .param("imageUrl", "https://example.com/kakao-product.jpg")
                .param("categoryId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    @DisplayName("관리자 상품 수정 화면을 조회한다")
    void editForm() throws Exception {
        mockMvc.perform(get("/admin/products/1/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("product/edit"))
            .andExpect(model().attributeExists("product"))
            .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("존재하지 않는 상품 수정 화면에 접근하면 오류 메시지와 함께 상품 목록으로 redirect한다")
    void editFormProductNotFound() throws Exception {
        mockMvc.perform(get("/admin/products/999999/edit"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"))
            .andExpect(flash().attribute("error", "상품이 존재하지 않습니다. id=999999"));
    }

    @Test
    @DisplayName("관리자 상품을 수정하면 상품 목록으로 redirect한다")
    void update() throws Exception {
        mockMvc.perform(post("/admin/products/1/edit")
                .param("name", "수정상품")
                .param("price", "2000")
                .param("imageUrl", "https://example.com/updated-product.jpg")
                .param("categoryId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    @DisplayName("존재하지 않는 상품 수정 요청은 오류 메시지와 함께 상품 목록으로 redirect한다")
    void updateProductNotFound() throws Exception {
        mockMvc.perform(post("/admin/products/999999/edit")
                .param("name", "수정상품")
                .param("price", "2000")
                .param("imageUrl", "https://example.com/updated-product.jpg")
                .param("categoryId", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"))
            .andExpect(flash().attribute("error", "상품이 존재하지 않습니다. id=999999"));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 상품을 등록하면 오류 메시지와 함께 상품 목록으로 redirect한다")
    void createCategoryNotFound() throws Exception {
        mockMvc.perform(post("/admin/products")
                .param("name", "관리상품")
                .param("price", "1000")
                .param("imageUrl", "https://example.com/admin-product.jpg")
                .param("categoryId", "999999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"))
            .andExpect(flash().attribute("error", "카테고리가 존재하지 않습니다. id=999999"));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 상품을 수정하면 오류 메시지와 함께 상품 목록으로 redirect한다")
    void updateCategoryNotFound() throws Exception {
        mockMvc.perform(post("/admin/products/1/edit")
                .param("name", "수정상품")
                .param("price", "2000")
                .param("imageUrl", "https://example.com/updated-product.jpg")
                .param("categoryId", "999999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"))
            .andExpect(flash().attribute("error", "카테고리가 존재하지 않습니다. id=999999"));
    }

    @Test
    @DisplayName("관리자 상품 수정 검증에 실패하면 수정 화면을 다시 반환한다")
    void updateInvalidName() throws Exception {
        mockMvc.perform(post("/admin/products/1/edit")
                .param("name", "수정상품@")
                .param("price", "2000")
                .param("imageUrl", "https://example.com/updated-product.jpg")
                .param("categoryId", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("product/edit"))
            .andExpect(model().attribute("errors", hasSize(1)))
            .andExpect(model().attributeExists("product"))
            .andExpect(model().attribute("name", "수정상품@"))
            .andExpect(model().attribute("price", 2000))
            .andExpect(model().attribute("imageUrl", "https://example.com/updated-product.jpg"))
            .andExpect(model().attribute("categoryId", 1L))
            .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("관리자 상품을 삭제하면 상품 목록으로 redirect한다")
    void delete() throws Exception {
        mockMvc.perform(post("/admin/products/4/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));
    }
}
