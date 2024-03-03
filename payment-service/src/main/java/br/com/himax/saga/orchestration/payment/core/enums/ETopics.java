package br.com.himax.saga.orchestration.payment.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ETopics {
    BASE_ORCHESTRATOR("orchestrator"),
    PAYMENT_SUCCESS("payment-success"),
    PAYMENT_FAIL("payment-fail");

    private String topic;
}
