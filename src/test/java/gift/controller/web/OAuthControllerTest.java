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

    private Member testMember;
    private KakaoUserInfoResponse testUserInfo;
    private String testJwtToken;

    @BeforeEach
    void setUp() {
        Properties properties = new Properties("테스트유저", "test.jpg", "thumb.jpg");
        KakaoAccount.Profile profile = new KakaoAccount.Profile("테스트유저", "thumb.jpg", "test.jpg", false);
        KakaoAccount kakaoAccount = new KakaoAccount(false, false, false, profile, false, true, true, "test@kakao.com");
        testUserInfo = new KakaoUserInfoResponse(12345L, kakaoAccount, properties);

        testMember = new Member(1L, "test@kakao.com", "password", Role.USER, "테스트유저", "test.jpg");
        testJwtToken = "test_jwt_token";
    }

    @Test
    @DisplayName("카카오 로그인 콜백 성공 테스트")
    void kakaoCallback() throws Exception {
        String testAuthorizationCode = "test_code";

        given(kakaoApiService.processKakaoLogin(testAuthorizationCode)).willReturn(testUserInfo);
        given(memberService.loginOrRegister(testUserInfo)).willReturn(testMember);
        given(jwtUtil.createToken(any(), any())).willReturn(testJwtToken);
        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);

        mockMvc.perform(get("/oauth/kakao/callback").param("code", testAuthorizationCode))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/items"))
            .andExpect(cookie().exists("accessToken"))
            .andExpect(cookie().value("accessToken", testJwtToken));
    }
}