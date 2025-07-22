package gift.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OptionTest {

    @Test
    @DisplayName("옵션 수량 감소 성공 테스트")
    void subtractQuantity_Success() {
        Option option = new Option("테스트 옵션", 10, null);
        option.subtractQuantity(3);
        assertThat(option.getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("옵션 수량 부족 시 예외 발생 테스트")
    void subtractQuantity_Fail_If_Not_Enough() {
        Option option = new Option("테스트 옵션", 5, null);

        assertThatThrownBy(() -> option.subtractQuantity(10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 부족합니다.");
    }
}