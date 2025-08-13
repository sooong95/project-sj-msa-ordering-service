package song.sj.dto.external_dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemInfoDto {

    private Long itemId;
    private String itemName;
    private int quantity;
    private List<String> itemImagesUrl;
}
