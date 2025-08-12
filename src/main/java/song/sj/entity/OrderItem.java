package song.sj.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @JoinColumn(name = "order_shop_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderShop orderShop;

    private Long itemId;

    private String itemName;
    private int quantity;

    public void addOrderItem(Long itemId, OrderShop orderShop, String itemName, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.orderShop = orderShop;
        orderShop.getOrderItemsList().add(this);
    }
}
