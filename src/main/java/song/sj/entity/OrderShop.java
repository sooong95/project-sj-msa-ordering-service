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
public class OrderShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_shop_id")
    private Long id;

    @JoinColumn(name = "order_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    private Long shopId;


    @OneToMany(mappedBy = "orderShop")
    private List<OrderItem> orderItemsList = new ArrayList<>();

    /*@OneToMany(mappedBy = "orderShop")
    private List<Review> reviewList = new ArrayList<>();*/

    /*public OrderShop createOrderShop(Long shopId) {
        OrderShop orderShop = new OrderShop();
        orderShop.shopId = shopId;
        order.getOrderShopList().add(this);

        return orderShop;
    }

    public void addOrder(Order order) {
        this.order = order;
        order.getOrderShopList().add(this);
    }*/

    public void addOrderShop(Long shopId, Order order) {
        this.shopId = shopId;
        this.order = order;
        order.getOrderShopList().add(this);
    }
}
