package gift.controller.api;

import gift.service.KakaoOAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/kakao")
public class OAuthApiController {

    private final KakaoOAuthService kakaoOAuthService;

    public OAuthApiController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @GetMapping("/callback")
    public String kakaoCallback(@RequestParam("code") String code) {
        String accessToken = kakaoOAuthService.getAccessToken(code);
        return "카카오 액세스 토큰: " + accessToken;
    }
}