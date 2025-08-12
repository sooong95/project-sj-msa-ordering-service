package song.sj.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import song.sj.dto.item.ItemVerificationDto;
import song.sj.dto.order.OrderItemDto;
import song.sj.dto.order.OrderSaveDto;
import song.sj.dto.order.OrderShopDto;
import song.sj.dto.shop.ShopInfoDto;
import song.sj.entity.Order;
import song.sj.entity.OrderItem;
import song.sj.entity.OrderShop;
import song.sj.enums.OrderStatus;
import song.sj.repository.*;
import song.sj.service.feign_client.ItemServiceFeignClient;
import song.sj.service.feign_client.ShopServiceFeignClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShopServiceFeignClient shopServiceFeignClient;
    private final OrderShopRepository orderShopRepository;
    private final ItemServiceFeignClient itemServiceFeignClient;

    public void orderSave(Long userId, OrderSaveDto orderSaveDto) {

        // feign client 로 shop 에 대해 조회.
        // 주문이 생성되면 kafka 비동기 통신으로 shop 에게 주문이 들어왔다고 알림.

        /*for (OrderShopDto shop : orderSaveDto.getOrderShopList()) {
            Shop findShop = shopRepository.findById(shop.getShopId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 shop 입니다."));
            Order.addOrderShop(findShop);
            OrderShop orderShop = OrderShop.createOrderShop(findShop);
            orderShopRepository.save(orderShop);

            for (OrderItemDto item : shop.getOrderItemList()) {
                Item findItem = itemRepository.findById(item.getItemId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 아이템 입니다."));
                if (item.getQuantity() > findItem.getQuantity()) {
                    throw new RuntimeException("등록한 수량보다 요청 수량이 많습니다. 수량을 다시 확인해 주세요.");
                }
                orderItemRepository.save(OrderItem.addOrderItem(findItem, orderShop, item.getQuantity()));

            }
            Order order = Order.setMemberAndOrderStatus(memberService.getMemberFromJwt());
            orderShop.addOrder(order);
            orderRepository.save(order);
        }*/

        List<Long> shopIdList = new ArrayList<>();
        Order order = Order.builder().build();
        OrderShop orderShop = OrderShop.builder().build();

        for (OrderShopDto orderShopDto : orderSaveDto.getOrderShopList()) {
            shopIdList.add(orderShopDto.getShopId());
            List<ShopInfoDto> shopInfoList = shopServiceFeignClient.getShopInfo(shopIdList).getData();

            for (ShopInfoDto shopInfoDto : shopInfoList) {
                orderShop.addOrderShop(shopInfoDto.getShopId(), order);
                orderShopRepository.save(orderShop);
            }

            for (OrderItemDto orderItemDto : orderShopDto.getOrderItemList()) {
                OrderItem orderItem = OrderItem.builder().build();
                // 여기에 로직 추가
                List<ItemVerificationDto> verificationList = orderShopDto.getOrderItemList().stream()
                        .map(item -> new ItemVerificationDto(item.getItemId(), item.getQuantity()))
                        .toList();
                itemServiceFeignClient.verificationItem(verificationList);
                // ----------
                orderItem.addOrderItem(orderItemDto.getItemId(), orderShop, orderItem.getItemName(), orderItemDto.getQuantity());
            }
        }

        order.addMemberId(userId);
        order.changeOrderStatus(OrderStatus.ORDER);
        orderRepository.save(order);
    }

    public void orderCancel(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));

        for (OrderShop orderShop : order.getOrderShopList()) {
            OrderShop findOrderShop = orderShopRepository.findById(orderShop.getId()).orElseThrow();

            for (OrderItem orderItem : findOrderShop.getOrderItemsList()) {
                orderItemRepository.delete(orderItemRepository.findById(orderItem.getId()).orElseThrow());
            }
            orderShopRepository.delete(findOrderShop);
        }
        orderRepository.delete(order);
    }
}
