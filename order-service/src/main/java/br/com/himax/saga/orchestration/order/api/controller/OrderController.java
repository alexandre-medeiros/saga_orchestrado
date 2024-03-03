package br.com.himax.saga.orchestration.order.api.controller;

import br.com.himax.saga.orchestration.order.api.dto.OrderRequest;
import br.com.himax.saga.orchestration.order.domain.document.Order;
import br.com.himax.saga.orchestration.order.domain.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private static final String ID_PATTERN = "%s_%s";
    private final OrderService service;
    @PostMapping
    public Order create(@RequestBody OrderRequest request) {
        Order order = Order.builder()
                .products(request.getProducts())
                .createdAt(LocalDateTime.now())
                .transactionId(String.format(ID_PATTERN, OffsetDateTime.now().toInstant().toEpochMilli(), UUID.randomUUID()))
                .build();

        return service.create(order);
    }
}
