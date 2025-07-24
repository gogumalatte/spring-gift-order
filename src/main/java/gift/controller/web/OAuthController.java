package gift.controller.web;

import gift.dto.KakaoUserInfoResponse;
import gift.service.KakaoApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oauth")
public class OAuthController {

    private final KakaoApiService kakaoApiService;

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    public OAuthController(KakaoApiService kakaoApiService) { // 생성자 수정
        this.kakaoApiService = kakaoApiService;
    }

    @GetMapping("/kakao")
    public String kakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId
            + "&redirect_uri=" + kakaoRedirectUri
            + "&response_type=code";
        return "redirect:" + kakaoAuthUrl;
    }

    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code) {
        String accessToken = kakaoApiService.getAccessToken(code);
        KakaoUserInfoResponse userInfo = kakaoApiService.getUserInfo(accessToken);

        return "redirect:/some-success-page?email=" + userInfo.getEmail();
    }
}