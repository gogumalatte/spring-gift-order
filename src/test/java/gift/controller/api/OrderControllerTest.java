package gift.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.MemberLoginRequest;
import gift.dto.OrderRequest;
import gift.entity.Item;
import gift.entity.Option;
import gift.repository.ItemRepository;
import gift.repository.OptionRepository;
import gift.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
    "jwt.secret.key=test-secret-key-for-order-controller-test-1234567890",
    "kakao.client.id=test-client-id",
    "kakao.redirect.uri=http://localhost:8080/oauth/kakao/callback"
})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OptionRepository optionRepository;

    private String userAccessToken;
    private Option testOption;

    @BeforeEach
    void setUp() {
        userAccessToken = memberService.login(new MemberLoginRequest("user@example.com", "user1234")).accessToken();
        Item testItem = itemRepository.save(new Item(null, "테스트 상품", 10000, "item.jpg"));
        testOption = optionRepository.save(new Option("테스트 옵션", 10, testItem));
    }

    @Test
    @DisplayName("상품 주문 성공")
    void placeOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest(testOption.getId(), 5, "감사합니다!");

        mockMvc.perform(post("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.optionId").value(testOption.getId()))
            .andExpect(jsonPath("$.quantity").value(5));

        Option updatedOption = optionRepository.findById(testOption.getId()).get();
        assertThat(updatedOption.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("재고 부족으로 인한 상품 주문 실패")
    void placeOrder_Fail_Insufficient_Stock() throws Exception {
        OrderRequest request = new OrderRequest(testOption.getId(), 15, "재고 부족 테스트");

        mockMvc.perform(post("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Option notUpdatedOption = optionRepository.findById(testOption.getId()).get();
        assertThat(notUpdatedOption.getQuantity()).isEqualTo(10);
    }
}