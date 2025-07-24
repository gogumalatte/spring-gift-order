package gift.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/kakao")
public class OAuthApiController {

    @GetMapping("/callback")
    public String kakaoCallback(@RequestParam("code") String code) {
        return "카카오 인가 코드: " + code;
    }
}