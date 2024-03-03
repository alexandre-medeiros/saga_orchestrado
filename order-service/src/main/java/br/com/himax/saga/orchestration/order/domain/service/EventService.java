package br.com.himax.saga.orchestration.order.domain.service;

import br.com.himax.saga.orchestration.order.api.dto.EventFilter;
import br.com.himax.saga.orchestration.order.domain.document.Event;
import br.com.himax.saga.orchestration.order.domain.exception.ValidationException;
import br.com.himax.saga.orchestration.order.domain.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {
    private EventRepository repository;

    public List<Event> findAll(){
        return repository.findAll();
    }

    public Event findByFilter(EventFilter filter){
        if(filter.getOrderId() != null){
            return repository.findTop1ByOrderId(filter.getOrderId())
                    .orElseThrow(()-> new ValidationException("Event Not found"));
        }else{
            return repository.findTop1ByTransactionId(filter.getOrderId())
                    .orElseThrow(()-> new ValidationException("Event Not found"));
        }
    }

    public void notifyEnding(Event event){
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        create(event);
        log.info("Order {} with saga notified! TransactionId: {}", event.getOrderId(), event.getTransactionId());
    }

    public Event create(Event event){
        return repository.save(event);
    }
}
