package br.com.himax.saga.orchestration.order.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFilter {
    private String orderId;
    private String transactionId;
}
