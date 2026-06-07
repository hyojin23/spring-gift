package gift.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("관리자 회원 목록 화면을 조회한다")
    void list() throws Exception {
        mockMvc.perform(get("/admin/members"))
            .andExpect(status().isOk())
            .andExpect(view().name("member/list"))
            .andExpect(model().attributeExists("members"));
    }

    @Test
    @DisplayName("관리자 회원 목록 화면은 flash 오류 메시지를 표시할 수 있다")
    void listWithError() throws Exception {
        mockMvc.perform(get("/admin/members")
                .flashAttr("error", "존재하지 않는 회원입니다."))
            .andExpect(status().isOk())
            .andExpect(view().name("member/list"))
            .andExpect(model().attribute("error", "존재하지 않는 회원입니다."))
            .andExpect(model().attributeExists("members"));
    }

    @Test
    @DisplayName("관리자 회원 등록 화면을 조회한다")
    void newForm() throws Exception {
        mockMvc.perform(get("/admin/members/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("member/new"));
    }

    @Test
    @DisplayName("관리자 회원을 등록하면 회원 목록으로 redirect한다")
    void create() throws Exception {
        mockMvc.perform(post("/admin/members")
                .param("email", "admin-new-member@example.com")
                .param("password", "password"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/members"));

        Member member = memberRepository.findByEmail("admin-new-member@example.com").orElseThrow();
        assertThat(member.getPassword()).isNotEqualTo("password");
        Member authenticated = memberService.authenticate(new MemberRequest("admin-new-member@example.com", "password"));
        assertThat(authenticated.getEmail()).isEqualTo("admin-new-member@example.com");
    }

    @Test
    @DisplayName("중복 이메일로 관리자 회원 등록에 실패하면 등록 화면을 다시 반환한다")
    void createDuplicateEmail() throws Exception {
        mockMvc.perform(post("/admin/members")
                .param("email", "admin@example.com")
                .param("password", "password"))
            .andExpect(status().isOk())
            .andExpect(view().name("member/new"))
            .andExpect(model().attribute("error", "이미 등록된 이메일입니다."))
            .andExpect(model().attribute("email", "admin@example.com"));
    }

    @Test
    @DisplayName("관리자 회원 수정 화면을 조회한다")
    void editForm() throws Exception {
        mockMvc.perform(get("/admin/members/1/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("member/edit"))
            .andExpect(model().attributeExists("member"));
    }

    @Test
    @DisplayName("존재하지 않는 회원 수정 화면에 접근하면 오류 메시지와 함께 회원 목록으로 redirect한다")
    void editFormMemberNotFound() throws Exception {
        mockMvc.perform(get("/admin/members/999999/edit"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/members"))
            .andExpect(flash().attribute("error", "존재하지 않는 회원입니다."));
    }

    @Test
    @DisplayName("관리자 회원을 수정하면 회원 목록으로 redirect한다")
    void update() throws Exception {
        mockMvc.perform(post("/admin/members/1/edit")
                .param("email", "updated-admin@example.com")
                .param("password", "updated-password"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/members"));

        Member member = memberRepository.findByEmail("updated-admin@example.com").orElseThrow();
        assertThat(member.getPassword()).isNotEqualTo("updated-password");
        Member authenticated = memberService.authenticate(
            new MemberRequest("updated-admin@example.com", "updated-password")
        );
        assertThat(authenticated.getEmail()).isEqualTo("updated-admin@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 회원 수정 요청은 오류 메시지와 함께 회원 목록으로 redirect한다")
    void updateMemberNotFound() throws Exception {
        mockMvc.perform(post("/admin/members/999999/edit")
                .param("email", "updated-admin@example.com")
                .param("password", "updated-password"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/members"))
            .andExpect(flash().attribute("error", "존재하지 않는 회원입니다."));
    }

    @Test
    @DisplayName("관리자 회원 포인트를 충전하면 회원 목록으로 redirect한다")
    void chargePoint() throws Exception {
        mockMvc.perform(post("/admin/members/1/charge-point")
                .param("amount", "1000"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/members"));
    }

    @Test
    @DisplayName("존재하지 않는 회원의 포인트 충전 요청은 오류 메시지와 함께 회원 목록으로 redirect한다")
    void chargePointMemberNotFound() throws Exception {
        mockMvc.perform(post("/admin/members/999999/charge-point")
                .param("amount", "1000"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/members"))
            .andExpect(flash().attribute("error", "존재하지 않는 회원입니다."));
    }

    @Test
    @DisplayName("유효하지 않은 금액으로 포인트 충전에 실패하면 오류 메시지와 함께 회원 목록으로 redirect한다")
    void chargePointInvalidAmount() throws Exception {
        mockMvc.perform(post("/admin/members/1/charge-point")
                .param("amount", "0"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/members"))
            .andExpect(flash().attribute("error", "포인트 금액은 1 이상이어야 합니다."));
    }

    @Test
    @DisplayName("관리자 회원을 삭제하면 회원 목록으로 redirect한다")
    void delete() throws Exception {
        Member member = memberRepository.save(new Member("delete-admin-member@example.com", "password"));

        mockMvc.perform(post("/admin/members/{id}/delete", member.getId()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/members"));
    }

    @Test
    @DisplayName("주문 이력이 있는 회원을 삭제하면 오류 메시지와 함께 회원 목록으로 redirect한다")
    void deleteMemberWithOrders() throws Exception {
        mockMvc.perform(post("/admin/members/{id}/delete", 2L))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/members"))
            .andExpect(flash().attribute("error", "주문 이력이 있는 회원은 삭제할 수 없습니다. id=2"));
    }
}
