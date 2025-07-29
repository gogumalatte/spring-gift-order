package gift.controller.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.dto.KakaoUserInfoResponse;
import gift.entity.Member;
import gift.entity.Role;
import gift.repository.MemberRepository;
import gift.service.KakaoApiService;
import gift.service.MemberService;
import gift.util.JwtUtil;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OAuthController.class)
@Import(OAuthControllerTest.TestConfig.class)
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KakaoApiService kakaoApiService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MemberRepository memberRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public KakaoApiService kakaoApiService() {
            return org.mockito.Mockito.mock(KakaoApiService.class);
        }

        @Bean
        public MemberService memberService() {
            return org.mockito.Mockito.mock(MemberService.class);
        }

        @Bean
        public JwtUtil jwtUtil() {
            return org.mockito.Mockito.mock(JwtUtil.class);
        }

        @Bean
        public MemberRepository memberRepository() {
            return org.mockito.Mockito.mock(MemberRepository.class);
        }
    }

    @Test
    @DisplayName("카카오 로그인 콜백 성공 테스트")
    void kakaoCallback() throws Exception {
        String testAuthorizationCode = "test_code";
        String testAccessToken = "test_access_token";

        KakaoUserInfoResponse testUserInfo = new KakaoUserInfoResponse(12345L,
            Map.of("email", "test@kakao.com"),
            Map.of("nickname", "테스트유저", "profile_image", "test.jpg"));

        Member testMember = new Member(1L, "test@kakao.com", "password", Role.USER, "테스트유저", "test.jpg");

        String testJwtToken = "test_jwt_token";

        given(kakaoApiService.processKakaoLogin(testAuthorizationCode)).willReturn(testUserInfo);
        given(memberService.loginOrRegister(testUserInfo)).willReturn(testMember);
        given(jwtUtil.createToken(testMember.getEmail(), testMember.getRole().name())).willReturn(testJwtToken);

        given(memberRepository.findByEmail("test@kakao.com")).willReturn(Optional.of(testMember));

        mockMvc.perform(get("/oauth/kakao/callback").param("code", testAuthorizationCode))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/items"))
            .andExpect(cookie().value("jwt-token", testJwtToken));
    }
}