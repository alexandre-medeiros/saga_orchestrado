package br.com.himax.saga.orchestration.inventory.api.consumer;

import br.com.himax.saga.orchestration.inventory.api.model.Event;
import br.com.himax.saga.orchestration.inventory.core.mapper.JsonHelper;
import br.com.himax.saga.orchestration.inventory.domain.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class InventoryConsumer {
    public static final String SUCCESS = "inventory-success";
    public static final String FAIL = "inventory-fail";
    private final JsonHelper jsonHelper;

    private InventoryService service;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = SUCCESS)
    public void consumeSuccessEvent(String payload){
        log.info("Receiving success event {} from {} topic",payload, SUCCESS);
        Event event =  jsonHelper.toEvent(payload);
        service.updateInventory(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = FAIL)
    public void consumeFailEvent(String payload){
        log.info("Receiving rollback event {} from {} topic",payload, FAIL);
        Event event =  jsonHelper.toEvent(payload);
        service.rollbackInventory(event);
    }
}
