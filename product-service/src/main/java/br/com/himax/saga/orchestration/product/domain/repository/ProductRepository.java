package br.com.himax.saga.orchestration.product.domain.repository;

import br.com.himax.saga.orchestration.product.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    boolean existsByCode(String code);
}
