package gift.controller.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.dto.MemberLoginRequest;
import gift.entity.Item;
import gift.repository.ItemRepository;
import gift.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberService memberService;

    private Item testItem;
    private Cookie adminCookie;

    @BeforeEach
    void setUp() {
        String token = memberService.login(new MemberLoginRequest("admin@example.com", "admin1234")).token();
        adminCookie = new Cookie("accessToken", token);
        adminCookie.setPath("/");
        testItem = itemRepository.save(new Item(null, "사전 등록 상품", 20000, "before.jpg"));
    }

    @Test
    @DisplayName("관리자 페이지 - 상품 등록 성공")
    void createItem_Success() throws Exception {
        mockMvc.perform(post("/admin/items")
                .cookie(adminCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "테스트 성공 상품")
                .param("price", "12000")
                .param("imageUrl", "success.jpg")
                .param("options[0].name", "기본 옵션")
                .param("options[0].quantity", "100"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/items"));
    }

    @Test
    @DisplayName("관리자 페이지 - 상품 수정 성공")
    void updateItem_Success() throws Exception {
        mockMvc.perform(post("/admin/items/" + testItem.getId() + "/update")
                .cookie(adminCookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "수정된 상품")
                .param("price", "9999")
                .param("imageUrl", "updated.jpg")
                .param("options[0].name", "수정된 옵션")
                .param("options[0].quantity", "50"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/items/" + testItem.getId()));
    }

    @Test
    @DisplayName("관리자 페이지 - 로그인 없이 상품 등록 시도 시 로그인 페이지로 리다이렉트")
    void createItem_Fail_Without_Login() throws Exception {
        mockMvc.perform(post("/admin/items")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "인증실패 테스트 상품")
                .param("price", "100")
                .param("options[0].name", "옵션")
                .param("options[0].quantity", "10"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/members/login"));
    }

    @Test
    @DisplayName("관리자 페이지 - 상품 삭제 성공")
    void deleteItem_Success() throws Exception {
        mockMvc.perform(post("/admin/items/" + testItem.getId() + "/delete")
                .cookie(adminCookie))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/items"));
    }
}