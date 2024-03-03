package br.com.himax.saga.orchestration.orchestrator.domain.service;

import br.com.himax.saga.orchestration.orchestrator.api.model.Event;
import br.com.himax.saga.orchestration.orchestrator.api.model.History;
import br.com.himax.saga.orchestration.orchestrator.api.producer.SagaOrchestratorProducer;
import br.com.himax.saga.orchestration.orchestrator.core.enums.ETopics;
import br.com.himax.saga.orchestration.orchestrator.core.mapper.JsonHelper;
import br.com.himax.saga.orchestration.orchestrator.core.saga.SagaExecutionController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import static br.com.himax.saga.orchestration.orchestrator.core.enums.EEventSource.ORCHESTRATOR;
import static br.com.himax.saga.orchestration.orchestrator.core.enums.ESagaStatus.FAIL;
import static br.com.himax.saga.orchestration.orchestrator.core.enums.ESagaStatus.SUCCESS;
import static br.com.himax.saga.orchestration.orchestrator.core.enums.ETopics.NOTIFY_ENDING;

@Slf4j
@AllArgsConstructor
@Service
public class OrchestratorService {
    private final JsonHelper helper;
    private final SagaExecutionController controller;
    private final SagaOrchestratorProducer producer;

    public void startSaga(Event event){
        event.setStatus(SUCCESS);
        event.setSource(ORCHESTRATOR);
        ETopics topic = getTopic(event);

        log.info("SAGA STARTED FOR EVENT {}!",event.getId());
        addHistory(event, "Saga started");

        sendToProducerWithTopic(event, topic);
    }

    public void finishSagaSuccess(Event event){
        event.setStatus(SUCCESS);
        event.setSource(ORCHESTRATOR);

        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}!",event.getId());
        addHistory(event, "Saga finished");

        notifyFinishedSaga(event);
    }

    public void finishSagaFail(Event event){
        event.setStatus(FAIL);
        event.setSource(ORCHESTRATOR);

        log.info("SAGA FINISHED WITH ERRORS FOR EVENT {}!",event.getId());
        addHistory(event, "Saga finished with errors");

        notifyFinishedSaga(event);
    }

    public void continueSaga(Event event){
        ETopics topic = getTopic(event);
        log.info("SAGA CONTINUING FOR EVENT {}", event.getId());

        sendToProducerWithTopic(event, topic);
    }

    private ETopics getTopic(Event event) {
        return controller.getNextTopic(event);
    }

    private void addHistory(Event event, String message) {
        History history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addHistory(history);
    }

    private void sendToProducerWithTopic(Event event, ETopics topic) {
        producer.sendEvent(helper.toJson(event), topic.getTopic());
    }

    private void notifyFinishedSaga(Event event){
        sendToProducerWithTopic(event, NOTIFY_ENDING);
    }
}
