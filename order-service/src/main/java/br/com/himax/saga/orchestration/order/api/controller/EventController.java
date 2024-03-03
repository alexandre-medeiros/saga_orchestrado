package br.com.himax.saga.orchestration.order.api.controller;

import br.com.himax.saga.orchestration.order.api.dto.EventFilter;
import br.com.himax.saga.orchestration.order.api.dto.OrderRequest;
import br.com.himax.saga.orchestration.order.domain.document.Event;
import br.com.himax.saga.orchestration.order.domain.document.Order;
import br.com.himax.saga.orchestration.order.domain.service.EventService;
import br.com.himax.saga.orchestration.order.domain.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventController {
    private final EventService service;

    @GetMapping("all")
    public List<Event> findAll() {
        return service.findAll();
    }

    @GetMapping
    public Event findByFilter(@RequestBody EventFilter filter) {
        return service.findByFilter(filter);
    }
}
