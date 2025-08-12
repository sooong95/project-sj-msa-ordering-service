/*
package song.sj.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import song.sj.entity.*;

import java.util.List;

import static song.sj.entity.QItemImages.itemImages;
import static song.sj.entity.QOrder.order;
import static song.sj.entity.QOrderItem.orderItem;
import static song.sj.entity.QOrderShop.orderShop;
import static song.sj.entity.item.QItem.item;

@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Order> findAllOrder(Long memberId) {
        return jpaQueryFactory
                .selectFrom(order)
                .leftJoin(order.orderShopList, orderShop)
                .leftJoin(orderShop.orderItemsList, orderItem)
                .leftJoin(orderItem.item, item)
                .leftJoin(item.itemImages, itemImages)
                .where(order.member.id.eq(memberId))
                .distinct()
                .fetch();
    }

    public Order findOneShopOrder(Long shopId) {

        return jpaQueryFactory
                .selectFrom(order)
                .leftJoin(order.orderShopList, orderShop)
                .leftJoin(orderShop.orderItemsList, orderItem)
                .leftJoin(orderItem.item, item)
                .leftJoin(item.itemImages, itemImages)
                .where(orderShop.shop.id.eq(shopId))
                .fetchOne();
    }
}
*/
