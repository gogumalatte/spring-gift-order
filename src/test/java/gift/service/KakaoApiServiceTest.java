package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import gift.client.KakaoClient;
import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import gift.dto.KakaoUserInfoResponse.KakaoAccount;
import gift.dto.KakaoUserInfoResponse.Properties;
import gift.entity.Item;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class KakaoApiServiceTest {

    @Mock
    private KakaoClient kakaoClient;

    @InjectMocks
    private KakaoApiService kakaoApiService;

    @BeforeEach
    void setUp() {
        kakaoApiService = new KakaoApiService(kakaoClient, "test-client-id", "http://localhost/callback");
    }

    @Test
    @DisplayName("액세스 토큰 받기 테스트")
    void getAccessToken() {
        var mockResponse = new KakaoTokenResponse("bearer", "test-access-token", 3600, null, null, null);
        given(kakaoClient.fetchAccessToken(anyString(), anyString(), anyString()))
            .willReturn(mockResponse);

        String accessToken = kakaoApiService.getAccessToken("test-auth-code");

        assertThat(accessToken).isEqualTo("test-access-token");
    }

    @Test
    @DisplayName("사용자 정보 받기 테스트")
    void getUserInfo() {
        Properties properties = new Properties("테스트유저", "test.jpg", "thumb.jpg");
        KakaoAccount.Profile profile = new KakaoAccount.Profile("테스트유저", "thumb.jpg", "test.jpg", false);
        KakaoAccount kakaoAccount = new KakaoAccount(false, false, false, profile, false, true, true, "test@example.com");
        var mockResponse = new KakaoUserInfoResponse(1L, kakaoAccount, properties);
        given(kakaoClient.fetchUserInfo(anyString()))
            .willReturn(mockResponse);

        KakaoUserInfoResponse userInfo = kakaoApiService.getUserInfo("test-access-token");

        assertThat(userInfo.id()).isEqualTo(1L);
        assertThat(userInfo.getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("나에게 메시지 보내기 테스트")
    void sendMessageToMe_Success() throws Exception {
        String accessToken = "test-access-token";
        Item item = new Item(1L, "테스트 상품", 10000, "item.jpg");
        Option option = new Option("테스트 옵션", 10, item);
        Member member = new Member(1L, "test@mail.com", "pw", Role.USER, "nick", "profile.jpg");
        Order order = new Order(member, option, 2, "생일 축하해!");

        kakaoApiService.sendMessageToMe(accessToken, order);

        ArgumentCaptor<MultiValueMap<String, String>> bodyCaptor = ArgumentCaptor.forClass(
            MultiValueMap.class);
        verify(kakaoClient).sendKakaoTalkMessage(eq(accessToken), bodyCaptor.capture());

        String templateJson = bodyCaptor.getValue().getFirst("template_object");
        assertThat(templateJson).contains("주문이 완료되었습니다!");
        assertThat(templateJson).contains("테스트 상품");
        assertThat(templateJson).contains("테스트 옵션");
        assertThat(templateJson).contains("생일 축하해!");
    }
}