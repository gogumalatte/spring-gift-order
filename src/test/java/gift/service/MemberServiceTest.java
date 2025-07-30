package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import gift.dto.KakaoUserInfoResponse;
import gift.dto.KakaoUserInfoResponse.KakaoAccount;
import gift.dto.KakaoUserInfoResponse.Properties;
import gift.entity.Member;
import gift.entity.Role;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("기존 회원이 카카오 로그인 시 액세스 토큰이 갱신된다")
    void loginOrRegister_updates_accessToken_for_existing_user() {
        String kakaoAccessToken = "test-kakao-access-token";
        Properties properties = new Properties("테스트유저", null, null);
        KakaoAccount kakaoAccount = new KakaoAccount(false, false, false, null, false, true, true, "test@kakao.com");
        KakaoUserInfoResponse userInfo = new KakaoUserInfoResponse(123L, kakaoAccount, properties);

        Member existingMember = new Member(1L, "test@kakao.com", "hashedPw", Role.USER, "테스트유저", null);
        given(memberRepository.findByEmail(userInfo.getEmail())).willReturn(Optional.of(existingMember));

        Member resultMember = memberService.loginOrRegister(userInfo, kakaoAccessToken);

        assertThat(resultMember.getKakaoAccessToken()).isEqualTo(kakaoAccessToken);
    }

    @Test
    @DisplayName("신규 회원이 카카오 로그인 시 액세스 토큰이 저장된다")
    void loginOrRegister_saves_accessToken_for_new_user() {
        String kakaoAccessToken = "test-kakao-access-token";
        Properties properties = new Properties("테스트유저", null, null);
        KakaoAccount kakaoAccount = new KakaoAccount(false, false, false, null, false, true, true, "test@kakao.com");
        KakaoUserInfoResponse userInfo = new KakaoUserInfoResponse(123L, kakaoAccount, properties);

        given(memberRepository.findByEmail(userInfo.getEmail())).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));

        Member resultMember = memberService.loginOrRegister(userInfo, kakaoAccessToken);

        assertThat(resultMember).isNotNull();
        assertThat(resultMember.getEmail()).isEqualTo(userInfo.getEmail());
        assertThat(resultMember.getKakaoAccessToken()).isEqualTo(kakaoAccessToken);
    }
}