package gift.service;

import gift.dto.KakaoUserInfoResponse;
import gift.dto.LoginResponse;
import gift.dto.MemberLoginRequest;
import gift.dto.MemberRegisterRequest;
import gift.entity.Member;
import gift.entity.Role;
import gift.exception.LoginException;
import gift.repository.MemberRepository;
import gift.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public MemberService(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public LoginResponse register(MemberRegisterRequest request) {
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        Member newMember = new Member(null, request.email(), hashedPassword, Role.USER, null, null);
        memberRepository.save(newMember);

        String accessToken = jwtUtil.createToken(newMember.getEmail(), newMember.getRole().name());
        return new LoginResponse(accessToken);
    }

    public LoginResponse login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
            .orElseThrow(() -> new LoginException("가입되지 않은 이메일입니다."));
        if (!BCrypt.checkpw(request.password(), member.getPassword())) {
            throw new LoginException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.createToken(member.getEmail(), member.getRole().name());
        return new LoginResponse(accessToken);
    }

    @Transactional
    public Member loginOrRegister(KakaoUserInfoResponse userInfo, String accessToken) {
        String email = userInfo.getEmail();
        if (email == null) {
            email = userInfo.id() + "@kakao.temp.email";
        }
        String nickname = userInfo.getNickname();
        String profileImageUrl = userInfo.getProfileImageUrl();
        final String finalEmail = email;

        Member member = memberRepository.findByEmail(finalEmail)
            .map(m -> {
                m.updateProfile(nickname, profileImageUrl);
                return m;
            })
            .orElseGet(() -> {
                String tempPassword = "kakao_temp_password";
                String hashedPassword = BCrypt.hashpw(tempPassword, BCrypt.gensalt());
                Member newMember = new Member(null, finalEmail, hashedPassword, Role.USER, nickname, profileImageUrl);
                return memberRepository.save(newMember);
            });
        member.updateKakaoAccessToken(accessToken);
        return member;
    }
}