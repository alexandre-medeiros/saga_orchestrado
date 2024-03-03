package br.com.himax.saga.orchestration.orchestrator.api.consumer;

import br.com.himax.saga.orchestration.orchestrator.core.mapper.JsonHelper;
import br.com.himax.saga.orchestration.orchestrator.api.model.Event;
import br.com.himax.saga.orchestration.orchestrator.domain.service.OrchestratorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class SagaOrchestratorConsumer {
    public static final String START_SAGA = "start-saga";
    public static final String ORCHESTRATOR = "orchestrator";
    public static final String FINISH_SUCCESS = "finish-success";
    public static final String FINISH_FAIL = "finish-fail";
    private final JsonHelper jsonHelper;

    private final OrchestratorService service;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = START_SAGA)
    public void consumeStartSagaEvent(String payload){
        log.info("Receiving event {} from start-saga topic",payload);
        Event event =  jsonHelper.toEvent(payload);
        service.startSaga(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = ORCHESTRATOR)
    public void consumeOrchestratorEvent(String payload){
        log.info("Receiving event {} from orchestrator topic",payload);
        Event event =  jsonHelper.toEvent(payload);
        service.continueSaga(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = FINISH_SUCCESS)
    public void consumeFinishSuccessEvent(String payload){
        log.info("Receiving event {} from finish-success topic",payload);
        Event event =  jsonHelper.toEvent(payload);
        service.finishSagaSuccess(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = FINISH_FAIL)
    public void consumeFinishFailEvent(String payload){
        log.info("Receiving event {} from finish-fail topic",payload);
        Event event =  jsonHelper.toEvent(payload);
        service.finishSagaFail(event);
    }
}
