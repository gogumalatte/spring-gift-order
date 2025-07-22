package gift.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wishes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "product_id"})
})
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Item product;

    @Column(nullable = false)
    private int quantity;

    protected Wish() {
    }

    public Wish(Member member, Item product, int quantity) {
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public Item getProduct() { return product; }
    public int getQuantity() { return quantity; }
}