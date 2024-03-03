package br.com.himax.saga.orchestration.product.api.model;

import br.com.himax.saga.orchestration.product.core.enums.ESagaStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @NotEmpty
    private String id;
    @NotEmpty
    private String transactionId;
    @NotEmpty
    private String orderId;
    @NotNull
    @Valid
    private Order payload;
    private String source;
    private ESagaStatus status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

    public void addHistory(History history) {
        if (isEmpty(eventHistory)) {
            eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);
    }

    public List<OrderItem> getProducts(){
        return payload.getProducts();
    }

    public String getPayloadOrderId(){
        return payload.getId();
    }

    public String getPayloadTransactionId(){
        return payload.getTransactionId();
    }
}