/*
package song.sj.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import song.sj.dto.Result;
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
public class MemberOrderQueryService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final ItemImageRepository itemImageRepository;
    private final ImageFile imageFile;

    public Result<List<OrderHistoryDto>> memberOrderHistory() {

        List<OrderHistoryDto> orderHistoryList = orderRepository.findAllOrder(memberService.getMemberFromJwt().getId()).stream().map(
                order -> {
                    List<OrderHistoryShopDto> historyShop = order.getOrderShopList().stream().map(
                            orderShop -> {
                                List<OrderHistoryItemDto> historyItem = orderShop.getOrderItemsList().stream().map(
                                        this::convertToOrderHistoryItemDto
                                ).toList();
                                return convertToOrderHistoryShopDto(orderShop, historyItem);
                            }
                    ).toList();
                    return convertToOrderHistoryDto(order, historyShop);
                }
        ).toList();

        return new Result<>(orderHistoryList.size(), orderHistoryList);
    }

    public OrderHistoryDto findOneShopOrder(Long orderId) {

        List<OrderHistoryShopDto> historyShop = orderRepository.findOneShopOrder(orderId).getOrderShopList()
                .stream().map(orderShop -> {
                            List<OrderHistoryItemDto> historyItem = orderShop.getOrderItemsList().stream().map(
                                    this::convertToOrderHistoryItemDto
                            ).toList();
                            return convertToOrderHistoryShopDto(orderShop, historyItem);
                        }
                ).toList();

        return convertToOrderHistoryDto(orderRepository.findById(orderId).orElseThrow(), historyShop);
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
