package song.sj.dto.external_dto.shop;

import lombok.Data;

@Data
public class ShopInfoDto {

    private Long shopId;
    private String shopName;
    private String shopDescription;

    private String city;
    private String street;
    private String zipcode;
}
