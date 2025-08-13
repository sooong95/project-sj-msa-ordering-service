package song.sj.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import song.sj.dto.external_dto.item.ItemInfoDto;
import song.sj.dto.external_dto.item.ItemVerificationDto;
import song.sj.dto.order.OrderItemDto;
import song.sj.dto.order.OrderSaveDto;
import song.sj.dto.order.OrderShopDto;
import song.sj.dto.external_dto.shop.ShopInfoDto;
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

        List<Long> shopIdList = new ArrayList<>();
        Order order = Order.builder().build();
        OrderShop orderShop = OrderShop.builder().build();

        for (OrderShopDto orderShopDto : orderSaveDto.getOrderShopList()) {
            shopIdList.add(orderShopDto.getShopId());
        }

        List<ShopInfoDto> shopInfoList = shopServiceFeignClient.getShopInfo(shopIdList).getData();

        for (ShopInfoDto shopInfoDto : shopInfoList) {
            orderShop.addOrderShop(shopInfoDto.getShopId(), order);
            orderShopRepository.save(orderShop);
        }

        for (OrderShopDto orderShopDto : orderSaveDto.getOrderShopList()) {
            List<ItemVerificationDto> verificationList = orderShopDto.getOrderItemList().stream()
                    .map(item -> new ItemVerificationDto(item.getItemId(), item.getQuantity()))
                    .toList();

            List<ItemInfoDto> itemInfo = itemServiceFeignClient.getItemInfo(verificationList).getData();

            for (ItemInfoDto f : itemInfo) {
                OrderItem orderItem = OrderItem.builder().build();
                orderItem.addOrderItem(f.getItemId(), orderShop,
                        f.getItemName(), f.getQuantity(), f.getItemImagesUrl());
                orderItemRepository.save(orderItem);
                log.info("오더 아이템 확인하기 = {}", orderItem);
            }
        }

        order.addMemberId(userId);
        order.changeOrderStatus(OrderStatus.ORDER);
        orderRepository.save(order);

        // 주문이 생성되면 kafka 비동기 통신으로 shop 에게 주문이 들어왔다고 알림.
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
