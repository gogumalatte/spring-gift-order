package gift.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    @DisplayName("Item의 정보가 올바르게 수정되는지 테스트")
    void itemUpdateTest() {

        Item item = new Item(1L, "기존 상품", 1000, "old.jpg");

        item.updateInfo("새로운 상품", 2000, "new.jpg");

        assertThat(item.getName()).isEqualTo("새로운 상품");
        assertThat(item.getPrice()).isEqualTo(2000);
        assertThat(item.getImageUrl()).isEqualTo("new.jpg");
    }
}