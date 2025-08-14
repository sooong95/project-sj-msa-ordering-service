package song.sj.dto.order;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderInfoDto {

    private int count;
    private Long orderId;
    private Long shopId;
    private List<OrderItemInfoDto> orderItemDtoList;
}
