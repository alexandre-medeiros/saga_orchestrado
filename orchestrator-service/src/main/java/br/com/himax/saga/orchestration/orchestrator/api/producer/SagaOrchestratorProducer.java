package br.com.himax.saga.orchestration.orchestrator.api.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class SagaOrchestratorProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;

    public void sendEvent(String payload, String topic){
        try{
            log.info("Send event to topic {} with data {}", topic, payload);
            kafkaTemplate.send(topic,payload);
        }catch (Exception ex){
            log.error("Error trying to send data to topic {} with data {}", topic, payload, ex);
        }
    }
}
