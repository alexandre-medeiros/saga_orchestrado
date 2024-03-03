package br.com.himax.saga.orchestration.payment.api.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static br.com.himax.saga.orchestration.payment.core.enums.ETopics.BASE_ORCHESTRATOR;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final String topic = BASE_ORCHESTRATOR.getTopic();

    public void sendEvent(String payload){
        try{
            log.info("Send event to topic {} with data {}", topic, payload);
            kafkaTemplate.send(topic,payload);
        }catch (Exception ex){
            log.error("Error trying to send data to topic {} with data {}", topic, payload, ex);
        }
    }
}
