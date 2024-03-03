package br.com.himax.saga.orchestration.order.api.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static br.com.himax.saga.orchestration.order.core.enums.ETopics.START_SAGA;

@Slf4j
@RequiredArgsConstructor
@Component
public class StartProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;

    public void sendEvent(String payload){
        try{
            log.info("Send event to topic {} with data {}", START_SAGA.getTopic(), payload);
            kafkaTemplate.send(START_SAGA.getTopic(),payload);
        }catch (Exception ex){
            log.error("Error trying to send data to topic {} with data {}", START_SAGA.getTopic(), payload, ex);
        }
    }
}
