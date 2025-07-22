package gift.controller.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.LoginResponse;
import gift.dto.MemberLoginRequest;
import gift.dto.MemberRegisterRequest;
import gift.dto.WishRequest;
import gift.dto.WishUpdateRequest;
import gift.entity.Item;
import gift.entity.Member;
import gift.entity.Wish;
import gift.repository.ItemRepository;
import gift.repository.MemberRepository;
import gift.repository.WishRepository;
import gift.service.MemberService;
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
class WishControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WishRepository wishRepository;

    private String userToken;
    private Member loginMember;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    void setUp() {
        memberService.register(new MemberRegisterRequest("wish@example.com", "password"));
        LoginResponse loginResponse = memberService.login(new MemberLoginRequest("wish@example.com", "password"));
        userToken = loginResponse.token();
        loginMember = memberRepository.findByEmail("wish@example.com").get();

        testItem1 = itemRepository.save(new Item(null, "테스트 상품 1", 10000, "test1.jpg"));
        testItem2 = itemRepository.save(new Item(null, "테스트 상품 2", 20000, "test2.jpg"));
    }

    @Test
    @DisplayName("위시리스트에 새로운 상품 추가 성공")
    void addWish_Success() throws Exception {
        WishRequest wishRequest = new WishRequest(testItem1.getId(), 1);

        mockMvc.perform(post("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wishRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.product.id").value(testItem1.getId()));
    }

    @Test
    @DisplayName("위시리스트 목록 조회 성공")
    void getWishes_Success() throws Exception {
        wishRepository.save(new Wish(loginMember, testItem1, 1));

        mockMvc.perform(get("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].product.id").value(testItem1.getId()));
    }

    @Test
    @DisplayName("중복된 상품 추가 시 409 에러 발생")
    void addWish_Fail_When_Duplicate() throws Exception {
        wishRepository.save(new Wish(loginMember, testItem1, 1));
        WishRequest wishRequest = new WishRequest(testItem1.getId(), 1);

        mockMvc.perform(post("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wishRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("위시리스트 상품 수량 변경 성공")
    void updateWishQuantity_Success() throws Exception {
        Wish savedWish = wishRepository.save(new Wish(loginMember, testItem1, 1));
        Long wishId = savedWish.getId();
        WishUpdateRequest updateRequest = new WishUpdateRequest(10);

        mockMvc.perform(patch("/api/wishes/" + wishId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
            .andExpect(jsonPath("$.content[0].quantity").value(10));
    }

    @Test
    @DisplayName("위시리스트 상품 삭제 성공")
    void deleteWish_Success() throws Exception {
        Wish savedWish = wishRepository.save(new Wish(loginMember, testItem1, 1));
        Long wishId = savedWish.getId();

        mockMvc.perform(delete("/api/wishes/" + wishId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
            .andExpect(jsonPath("$.content", hasSize(0)));
    }
}