package song.sj.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import song.sj.dto.order.OrderSaveDto;
import song.sj.service.MemberOrderService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class MemberOrderController {

    private final MemberOrderService memberOrderService;
    /*private final MemberOrderQueryService memberOrderQueryService;*/

    @Timed(value = "ordering.save", longTask = true)
    @PostMapping
    public ResponseEntity<String> orderSave(@RequestHeader("X-User-Id") Long userId, @RequestBody OrderSaveDto orderSaveDto) {

        memberOrderService.orderSave(userId, orderSaveDto);
        return new ResponseEntity<>("주문이 완료 되었습니다.", HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> orderCancel(@PathVariable("orderId") Long orderId) {

        memberOrderService.orderCancel(orderId);
        return new ResponseEntity<>("주문이 취소 되었습니다.", HttpStatus.OK);
    }

    /*@GetMapping
    public ResponseEntity<Result<List<OrderHistoryDto>>> memberOrderHistory() {

        return new ResponseEntity<>(memberOrderQueryService.memberOrderHistory(), HttpStatus.OK);
    }

    @GetMapping("/shop/{orderId}")
    public ResponseEntity<OrderHistoryDto> shopFindOneOrder(@PathVariable("orderId") Long orderId) {

        return new ResponseEntity<>(memberOrderQueryService.findOneShopOrder(orderId), HttpStatus.OK);
    }*/
}
