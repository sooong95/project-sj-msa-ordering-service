package song.sj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import song.sj.entity.Order;
/*import song.sj.repository.query.OrderQueryRepository;*/

public interface OrderRepository extends JpaRepository<Order, Long>/*, OrderQueryRepository*/ {
}
