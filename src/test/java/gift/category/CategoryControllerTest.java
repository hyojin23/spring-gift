package gift.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("카테고리 목록을 조회한다")
    void getCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("카테고리를 생성한다")
    void createCategory() throws Exception {
        String request = """
                {
                  "name": "커피",
                  "color": "#6F4E37",
                  "imageUrl": "https://example.com/coffee.png",
                  "description": "커피 상품"
                }
                """;

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("커피"))
                .andExpect(jsonPath("$.color").value("#6F4E37"));
    }

    @Test
    @DisplayName("카테고리를 수정한다")
    void updateCategory() throws Exception {
        String createRequest = """
                {
                  "name": "커피",
                  "color": "#6F4E37",
                  "imageUrl": "https://example.com/coffee.png",
                  "description": "커피 상품"
                }
                """;

        String location = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String updateRequest = """
                {
                  "name": "디저트",
                  "color": "#F5CBA7",
                  "imageUrl": "https://example.com/dessert.png",
                  "description": "디저트 상품"
                }
                """;

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("디저트"))
                .andExpect(jsonPath("$.color").value("#F5CBA7"));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 수정하면 404를 반환한다")
    void updateNotFoundCategory() throws Exception {
        String request = """
                {
                  "name": "디저트",
                  "color": "#F5CBA7",
                  "imageUrl": "https://example.com/dessert.png",
                  "description": "디저트 상품"
                }
                """;

        mockMvc.perform(put("/api/categories/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("카테고리를 삭제한다")
    void deleteCategory() throws Exception {
        String createRequest = """
                {
                  "name": "커피",
                  "color": "#6F4E37",
                  "imageUrl": "https://example.com/coffee.png",
                  "description": "커피 상품"
                }
                """;

        String location = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
    }
}
