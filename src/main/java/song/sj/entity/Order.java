package song.sj.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import song.sj.TimeStamp;
import song.sj.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Entity
@Getter
@Table(name = "ordering")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private Long memberId;
    private Long deliveryId;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Builder.Default
    @OneToMany(mappedBy = "order")
    private List<OrderShop> orderShopList = new ArrayList<>();


    /*private List<Long> billIdList = new ArrayList<>();*/

    /*public static void addOrderShop(Long shopId) {
        Order order = new Order();
        OrderShop orderShop = new OrderShop();
        orderShop.addOrder(order);
    }*/

    /*public static Order setMemberAndOrderStatus(Long memberId) {

        Order order = new Order();
        order.memberId = memberId;

        order.orderStatus = OrderStatus.ORDER;

        return order;
    }*/

    public void addMemberId(Long userId) {
        this.memberId = userId;
    }

    public void setDelivery(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public void changeOrderStatus(OrderStatus orderStatus) {
        if (Objects.nonNull(orderStatus)) this.orderStatus = orderStatus;
    }
}
