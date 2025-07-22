package gift.controller.web;

import gift.dto.LoginResponse;
import gift.dto.MemberLoginRequest;
import gift.dto.MemberRegisterRequest;
import gift.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.Cookie;

@Controller
@RequestMapping("/members")
public class WebMemberController {

    private final MemberService memberService;

    public WebMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "members/registerForm";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "members/loginForm";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute MemberRegisterRequest request) {
        memberService.register(request);
        return "redirect:/members/login";
    }

    @PostMapping("/login")
    public String handleLogin(
        @ModelAttribute MemberLoginRequest request,
        HttpServletResponse response
    ) {
        LoginResponse loginResponse = memberService.login(request);
        String token = loginResponse.token();

        Cookie cookie = new Cookie("jwt-token", token);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return "redirect:/admin/items";
    }
}