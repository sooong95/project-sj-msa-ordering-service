package song.sj.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import song.sj.dto.external_dto.item.ItemInfoDto;
import song.sj.dto.external_dto.item.ItemVerificationDto;
import song.sj.dto.order.*;
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
import java.util.stream.Stream;

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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void orderSave(Long userId, OrderSaveDto orderSaveDto) {

        // feign client 로 shop 에 대해 조회.

        List<Long> shopIdList = new ArrayList<>();
        Order order = Order.builder().build();


        for (OrderShopDto orderShopDto : orderSaveDto.getOrderShopList()) {
            shopIdList.add(orderShopDto.getShopId());
        }

        List<ShopInfoDto> shopInfoList = getShopInfoWithCircuitBreaker(shopIdList);

        for (ShopInfoDto shopInfoDto : shopInfoList) {
            OrderShop orderShop = OrderShop.builder().build();
            orderShop.addOrderShop(shopInfoDto.getShopId(), order);
            orderShopRepository.save(orderShop);
        }

        for (OrderShopDto orderShopDto : orderSaveDto.getOrderShopList()) {

            OrderShop orderShop = order.getOrderShopList().stream()
                    .filter(os -> os.getShopId().equals(orderShopDto.getShopId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 상점이 존재하지 않습니다."));

            List<ItemVerificationDto> verificationList = orderShopDto.getOrderItemList().stream()
                    .map(item -> new ItemVerificationDto(item.getItemId(), item.getQuantity()))
                    .toList();

            List<ItemInfoDto> itemInfo = getItemInfoWithCircuitBreaker(verificationList);

            for (ItemInfoDto f : itemInfo) {
                OrderItem orderItem = OrderItem.builder().build();
                orderItem.addOrderItem(f.getItemId(), orderShop,
                        f.getItemName(), f.getDescription(), f.getQuantity(),f.getItemImagesUrl());
                orderItemRepository.save(orderItem);
                log.info("오더 아이템 확인하기 = {}", orderItem);
            }
        }

        order.addMemberId(userId);
        order.changeOrderStatus(OrderStatus.ORDER);
        Order findOrder = orderRepository.save(order);

        // 주문이 생성되면 kafka 비동기 통신으로 shop 에게 주문이 들어왔다고 알림.
        sendOrderCreatedNotificationToShop(findOrder.getId());
    }

    private void sendOrderCreatedNotificationToShop(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        for (OrderShop orderShop : order.getOrderShopList()) {
            Long shopId = orderShop.getShopId();

            List<OrderItemInfoDto> orderItemInfoDtoList = orderShop.getOrderItemsList().stream().map(item -> OrderItemInfoDto.builder()
                    .itemId(item.getItemId())
                    .itemName(item.getItemName())
                    .description(item.getDescription())
                    .build()).toList();

            kafkaTemplate.send("confirm-order-topics", OrderInfoDto.builder()
                    .count(orderItemInfoDtoList.size())
                    .orderId(orderId)
                    .shopId(shopId)
                    .orderItemDtoList(orderItemInfoDtoList)
                    .build());
        }
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

    @CircuitBreaker(name = "itemService", fallbackMethod = "fallbackItemService")
    private List<ItemInfoDto> getItemInfoWithCircuitBreaker(List<ItemVerificationDto> verificationList) {
        return itemServiceFeignClient.getItemInfo(verificationList).getData();
    }

    @CircuitBreaker(name = "shopService", fallbackMethod = "fallbackShopService")
    private List<ShopInfoDto> getShopInfoWithCircuitBreaker(List<Long> shopIdList) {
        return shopServiceFeignClient.getShopInfo(shopIdList).getData();
    }

    public List<ItemInfoDto> fallbackItemService(List<ItemVerificationDto> verificationList, Throwable t) {
        log.error("Item Service 호출 실패, fall back 동작: {}", t.getMessage());
        throw new RuntimeException("상품 서비스가 응답이 없어 에러가 발생 했습니다. 나중에 다시 시도해 주세요.");
    }

    public List<ShopInfoDto> fallbackShopService(List<Long> shopIdList, Throwable t) {
        log.error("Shop Service 호출 실패, fall back 동작: {}", t.getMessage());
        throw new RuntimeException("샵 서비스가 응답이 없어 에러가 발생 했습니다. 나중에 다시 시도해 주세요.");
    }
}
