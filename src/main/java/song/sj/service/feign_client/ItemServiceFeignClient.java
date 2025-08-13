package song.sj.service.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import song.sj.dto.Result;
import song.sj.dto.external_dto.item.ItemInfoDto;
import song.sj.dto.external_dto.item.ItemVerificationDto;

import java.util.List;

@FeignClient(value = "sj-item-service", path = "/external")
public interface ItemServiceFeignClient {

    @PostMapping("/item-verification")
    void verificationItem(@RequestBody List<ItemVerificationDto> itemVerificationDtoList);

    @PostMapping("/item-info")
    Result<List<ItemInfoDto>> getItemInfo(@RequestBody List<ItemVerificationDto> itemVerificationDtoList);
}
