package song.sj;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableFeignClients(basePackages = "song.sj.service.feign_client")
@EnableJpaAuditing // Auditing 활성화
@SpringBootApplication(/*exclude = SecurityAutoConfiguration.class*/)
public class SjOrderingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SjOrderingServiceApplication.class, args);
	}

	@Bean
	JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}
}
