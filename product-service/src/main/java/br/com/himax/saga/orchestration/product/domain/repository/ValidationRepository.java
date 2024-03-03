package br.com.himax.saga.orchestration.product.domain.repository;

import br.com.himax.saga.orchestration.product.domain.model.ValidationProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ValidationRepository extends JpaRepository<ValidationProduct,Integer> {
    boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
    Optional<ValidationProduct> findByOrderIdAndTransactionId(String orderId, String transactionId);
}
