package song.sj.service.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import song.sj.dto.item.ItemVerificationDto;

import java.util.List;

@FeignClient(value = "sj-item-service", path = "/external")
public interface ItemServiceFeignClient {

    @PostMapping("/item-verification")
    void verificationItem(@RequestBody List<ItemVerificationDto> itemVerificationDtoList);
}
