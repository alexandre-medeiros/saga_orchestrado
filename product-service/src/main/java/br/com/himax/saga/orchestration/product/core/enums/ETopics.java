package br.com.himax.saga.orchestration.product.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ETopics {
    BASE_ORCHESTRATOR("orchestrator"),
    PRODUCT_SUCCESS("product-success"),
    PRODUCT_FAIL("product-fail");

    private String topic;
}
