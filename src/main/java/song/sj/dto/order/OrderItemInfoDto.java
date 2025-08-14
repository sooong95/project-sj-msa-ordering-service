package song.sj.dto.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemInfoDto {

    private Long itemId;
    private String itemName;
    private String description;
    private String itemImageUrl;
    private int itemQuantity;
}
