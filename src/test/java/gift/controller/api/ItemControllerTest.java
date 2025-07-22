package gift.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.ItemRequest;
import gift.dto.LoginResponse;
import gift.dto.MemberLoginRequest;
import gift.dto.OptionRequest;
import gift.entity.Item;
import gift.repository.ItemRepository;
import gift.service.MemberService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ItemRepository itemRepository;

    private String adminToken;
    private String userToken;
    private Item testItem;

    @BeforeEach
    void setUp() {
        LoginResponse adminLogin = memberService.login(new MemberLoginRequest("admin@example.com", "admin1234"));
        adminToken = adminLogin.token();
        LoginResponse userLogin = memberService.login(new MemberLoginRequest("user@example.com", "user1234"));
        userToken = userLogin.token();
        testItem = itemRepository.save(new Item(null, "테스트 상품", 1000, "test.jpg"));
    }

    @Test
    @DisplayName("API - 전체 상품 목록 조회 (페이지네이션 적용)")
    void getAllItems() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("API - ID로 상품 조회 성공")
    void getItemById_Success() throws Exception {
        mockMvc.perform(get("/api/products/" + testItem.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testItem.getId()));
    }

    @Test
    @DisplayName("ADMIN 권한으로 상품 등록 성공")
    void createItem_By_Admin_Succeeds() throws Exception {
        List<OptionRequest> options = List.of(new OptionRequest("기본 옵션", 100));
        ItemRequest request = new ItemRequest("관리자 등록 상품", 5000, "admin_item.jpg", options);

        mockMvc.perform(post("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("USER 권한으로 '카카오' 미포함 상품 등록 성공")
    void createItem_By_User_Succeeds() throws Exception {
        List<OptionRequest> options = List.of(new OptionRequest("기본", 10));
        ItemRequest request = new ItemRequest("유저 등록 상품", 2000, "user_item.jpg", options);

        mockMvc.perform(post("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("인증 토큰 없이 상품 등록 시 401 에러 발생")
    void createItem_Without_Token_Fails() throws Exception {
        List<OptionRequest> options = List.of(new OptionRequest("기본", 10));
        ItemRequest request = new ItemRequest("인증 없는 상품", 100, "no-auth.jpg", options);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("ADMIN 권한으로 '카카오' 포함 상품 등록 성공")
    void createKakaoItem_By_Admin_Succeeds() throws Exception {
        List<OptionRequest> options = List.of(new OptionRequest("기본", 10));
        ItemRequest request = new ItemRequest("카카오프렌즈 인형", 30000, "kakao_doll.jpg", options);

        mockMvc.perform(post("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }
}