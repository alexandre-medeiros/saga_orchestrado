package br.com.himax.saga.orchestration.orchestrator.api.model;

import br.com.himax.saga.orchestration.orchestrator.core.enums.EEventSource;
import br.com.himax.saga.orchestration.orchestrator.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
    private EEventSource source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}