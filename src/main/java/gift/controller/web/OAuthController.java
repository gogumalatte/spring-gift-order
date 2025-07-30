package gift.controller.web;

import gift.dto.KakaoUserInfoResponse;
import gift.entity.Member;
import gift.service.KakaoApiService;
import gift.service.MemberService;
import gift.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oauth")
public class OAuthController {

    private final KakaoApiService kakaoApiService;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    public OAuthController(KakaoApiService kakaoApiService, MemberService memberService, JwtUtil jwtUtil) {
        this.kakaoApiService = kakaoApiService;
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/kakao")
    public String kakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId
            + "&redirect_uri=" + kakaoRedirectUri
            + "&response_type=code";
        return "redirect:" + kakaoAuthUrl;
    }

    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) {
        String accessToken = kakaoApiService.getAccessToken(code);
        KakaoUserInfoResponse userInfo = kakaoApiService.getUserInfo(accessToken);
        Member member = memberService.loginOrRegister(userInfo, accessToken);

        String token = jwtUtil.createToken(member.getEmail(), member.getRole().name());
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        return "redirect:/admin/items";
    }

}