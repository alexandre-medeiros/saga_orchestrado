package br.com.himax.saga.orchestration.order.domain.repository;

import br.com.himax.saga.orchestration.order.domain.document.Event;
import br.com.himax.saga.orchestration.order.domain.document.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findAll();
//    List<Event> findAllOrderByCreatedAtDesc();

    Optional<Event> findTop1ByOrderId(String orderId);
//    Optional<Event> findTop1ByOrderIdOrderByCreatedAtDesc(String orderId);

    Optional<Event> findTop1ByTransactionId(String transactionId);
//    Optional<Event> findTop1ByTransactionIdOrderByCreatedAtDesc(String transactionId);
}
