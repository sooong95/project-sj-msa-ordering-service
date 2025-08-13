package song.sj.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
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
    @Builder.Default
    private List<String> itemImagesUrl = new ArrayList<>();

    public void addOrderItem(Long itemId, OrderShop orderShop, String itemName,
                             int quantity, List<String> itemImagesUrl) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.orderShop = orderShop;
        this.itemImagesUrl = itemImagesUrl;
        orderShop.getOrderItemsList().add(this);
    }
}
