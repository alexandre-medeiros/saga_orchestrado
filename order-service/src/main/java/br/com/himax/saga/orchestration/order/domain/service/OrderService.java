package br.com.himax.saga.orchestration.order.domain.service;

import br.com.himax.saga.orchestration.order.api.producer.StartProducer;
import br.com.himax.saga.orchestration.order.core.mapper.JsonHelper;
import br.com.himax.saga.orchestration.order.domain.document.Event;
import br.com.himax.saga.orchestration.order.domain.document.Order;
import br.com.himax.saga.orchestration.order.domain.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class OrderService {
    private StartProducer producer;
    private EventService eventService;
    private final JsonHelper jsonHelper;
    private final OrderRepository repository;

    public Order create(Order order) {
        order = repository.save(order);
        producer.sendEvent(jsonHelper.toJson(createPayload(order)));
        return order;
    }

    private Event createPayload(Order order) {
        Event event = Event.builder()
                .orderId(order.getId())
                .transactionId(order.getTransactionId())
                .payload(order)
                .createdAt(LocalDateTime.now())
                .build();

        return eventService.create(event);
    }
}
