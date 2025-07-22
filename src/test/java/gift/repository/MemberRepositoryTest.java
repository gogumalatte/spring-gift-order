package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.entity.Member;
import gift.entity.Role;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 회원 조회 테스트")
    void findByEmail() {
        Member member = new Member(null, "test@example.com", "password123", Role.USER);
        memberRepository.save(member);

        Optional<Member> foundMemberOptional = memberRepository.findByEmail("test@example.com");

        assertThat(foundMemberOptional).isPresent();
        assertThat(foundMemberOptional.get().getEmail()).isEqualTo("test@example.com");
    }
}