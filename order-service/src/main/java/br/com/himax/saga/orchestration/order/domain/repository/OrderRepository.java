package br.com.himax.saga.orchestration.order.domain.repository;

import br.com.himax.saga.orchestration.order.domain.document.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

}
