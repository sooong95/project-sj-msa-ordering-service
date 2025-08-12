/*
package song.sj.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import song.sj.dto.order.OrderHistoryDto;
import song.sj.dto.order.OrderHistoryItemDto;
import song.sj.dto.order.OrderHistoryShopDto;
import song.sj.entity.Order;
import song.sj.entity.OrderItem;
import song.sj.entity.OrderShop;
import song.sj.repository.OrderRepository;
import song.sj.service.image.ImageFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopOrderQueryService {

    private final OrderRepository orderRepository;
    private final ItemImageRepository itemImageRepository;
    private final ImageFile imageFile;

    public OrderHistoryDto findOneOrder(Long shopId) {

        List<OrderHistoryShopDto> historyShop = orderRepository.findOneShopOrder(shopId).getOrderShopList()
                .stream().map(orderShop -> {
                            List<OrderHistoryItemDto> historyItem = orderShop.getOrderItemsList().stream().map(
                                    this::convertToOrderHistoryItemDto
                            ).toList();
                            return convertToOrderHistoryShopDto(orderShop, historyItem);
                        }
                ).toList();

        return convertToOrderHistoryDto(orderRepository.findById(shopId).orElseThrow(), historyShop);
    }

    private OrderHistoryItemDto convertToOrderHistoryItemDto(OrderItem orderItem) {

        return new OrderHistoryItemDto(
                orderItem.getItemId().getItemName(),
                orderItem.getQuantity(),
                orderItem.getOrderShop().getOrder().getOrderStatus(),
                itemImageRepository.findByItemId(orderItem.getItemId().getId()).stream().map(
                        image -> imageFile.getFullPath(image.getImageName())
                ).toList()
        );
    }

    private OrderHistoryShopDto convertToOrderHistoryShopDto(OrderShop orderShop, List<OrderHistoryItemDto> orderHistoryItemDtoList) {

        return new OrderHistoryShopDto(
                orderShop.getShop().getShopName(),
                orderHistoryItemDtoList.size(),
                orderHistoryItemDtoList
                );
    }

    private OrderHistoryDto convertToOrderHistoryDto(Order order, List<OrderHistoryShopDto> orderHistoryShopDtoList) {

        return new OrderHistoryDto(
                order.getCreatedDate(),
                orderHistoryShopDtoList
        );
    }
}
*/
