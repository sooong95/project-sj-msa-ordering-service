package song.sj.dto.external_dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemVerificationDto {

    private Long itemId;
    private int quantity;
}
