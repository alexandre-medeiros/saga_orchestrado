package br.com.himax.saga.orchestration.inventory.domain.repository;

import br.com.himax.saga.orchestration.inventory.domain.model.OrderInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderInventoryRepository extends JpaRepository<OrderInventory, Integer> {
    List<OrderInventory> findByOrderIdAndTransactionId(String orderId, String transactionId);
    Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
}