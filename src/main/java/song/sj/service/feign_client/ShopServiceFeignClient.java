package song.sj.service.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import song.sj.dto.Result;
import song.sj.dto.external_dto.shop.ShopInfoDto;

import java.util.List;

@FeignClient(name = "sj-shop-service", path = "/external")
public interface ShopServiceFeignClient {

    @PostMapping("/shop-info")
    Result<List<ShopInfoDto>> getShopInfo(@RequestBody List<Long> shopIdList);
}
