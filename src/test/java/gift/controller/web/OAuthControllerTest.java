package gift.controller.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import gift.config.WebMvcConfig;
import gift.dto.KakaoUserInfoResponse;
import gift.dto.KakaoUserInfoResponse.KakaoAccount;
import gift.dto.KakaoUserInfoResponse.Properties;
import gift.entity.Member;
import gift.entity.Role;
import gift.interceptor.AuthenticationInterceptor;
import gift.login.LoggedInMemberArgumentResolver;
import gift.repository.MemberRepository;
import gift.service.KakaoApiService;
import gift.service.MemberService;
import gift.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OAuthController.class)
@Import(WebMvcConfig.class)
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KakaoApiService kakaoApiService;
    @MockitoBean
    private MemberService memberService;
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthenticationInterceptor authenticationInterceptor;
    @MockitoBean
    private LoggedInMemberArgumentResolver loggedInMemberArgumentResolver;

    @MockitoBean
    private MemberRepository memberRepository;

    private Member testMember;
    private KakaoUserInfoResponse testUserInfo;
    private String testAccessToken;

    @BeforeEach
    void setUp() {
        Properties properties = new Properties("테스트유저", "test.jpg", "thumb.jpg");
        KakaoAccount.Profile profile = new KakaoAccount.Profile("테스트유저", "thumb.jpg", "test.jpg", false);
        KakaoAccount kakaoAccount = new KakaoAccount(false, false, false, profile, false, true, true, "test@kakao.com");
        testUserInfo = new KakaoUserInfoResponse(12345L, kakaoAccount, properties);

        testMember = new Member(1L, "test@kakao.com", "password", Role.USER, "테스트유저", "test.jpg");
        testAccessToken = "test_access_token_for_my_service";
    }

    @Test
    @DisplayName("카카오 로그인 콜백 성공 테스트")
    void kakaoCallback() throws Exception {
        String testAuthorizationCode = "test_code";
        String testKakaoAccessToken = "test-kakao-access-token";

        given(kakaoApiService.getAccessToken(testAuthorizationCode)).willReturn(testKakaoAccessToken);
        given(kakaoApiService.getUserInfo(testKakaoAccessToken)).willReturn(testUserInfo);
        given(memberService.loginOrRegister(testUserInfo, testKakaoAccessToken)).willReturn(testMember);
        given(jwtUtil.createToken(any(String.class), any(String.class))).willReturn(testAccessToken);

        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);

        mockMvc.perform(get("/oauth/kakao/callback").param("code", testAuthorizationCode))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/items"))
            .andExpect(cookie().exists("accessToken"))
            .andExpect(cookie().value("accessToken", testAccessToken));
    }
}