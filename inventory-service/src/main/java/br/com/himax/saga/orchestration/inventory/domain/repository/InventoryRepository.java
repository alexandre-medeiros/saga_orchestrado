package br.com.himax.saga.orchestration.inventory.domain.repository;

import br.com.himax.saga.orchestration.inventory.domain.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByProductCode(String productCode);
}