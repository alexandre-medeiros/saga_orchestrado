package br.com.himax.saga.orchestration.inventory.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ETopics {
    BASE_ORCHESTRATOR("orchestrator"),
    INVENTORY_SUCCESS("inventory-success"),
    INVENTORY_FAIL("inventory-fail");

    private String topic;
}
