package br.com.himax.saga.orchestration.order.api.consumer;


import br.com.himax.saga.orchestration.order.core.mapper.JsonHelper;
import br.com.himax.saga.orchestration.order.domain.document.Event;
import br.com.himax.saga.orchestration.order.domain.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class NotifyEndConsumer {
    public static final String NOTIFY_ENDING = "notify-ending";
    private final JsonHelper jsonHelper;
    private final EventService service;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = NOTIFY_ENDING)
    public void consumeNotifyEndingEvent(String payload){
        log.info("Receiving ending notification event {} from notify-ending topic",payload);
        Event event =  jsonHelper.toEvent(payload);
        service.notifyEnding(event);
    }
}
