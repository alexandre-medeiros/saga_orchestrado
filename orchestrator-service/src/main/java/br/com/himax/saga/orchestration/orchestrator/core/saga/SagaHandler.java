package br.com.himax.saga.orchestration.orchestrator.core.saga;

import static br.com.himax.saga.orchestration.orchestrator.core.enums.EEventSource.*;
import static br.com.himax.saga.orchestration.orchestrator.core.enums.ESagaStatus.*;
import static br.com.himax.saga.orchestration.orchestrator.core.enums.ETopics.*;
public final class SagaHandler {
    private SagaHandler(){}

    public static final Object[][] SAGA_HANDLER = {
            {ORCHESTRATOR, SUCCESS, PRODUCT_SUCCESS},
            {ORCHESTRATOR, FAIL, FINISH_FAIL},

            {PRODUCT_SERVICE, ROLLBACK_PENDING, PRODUCT_FAIL},
            {PRODUCT_SERVICE, FAIL, FINISH_FAIL},
            {PRODUCT_SERVICE, SUCCESS, PAYMENT_SUCCESS},

            {PAYMENT_SERVICE, ROLLBACK_PENDING, PAYMENT_FAIL},
            {PAYMENT_SERVICE, FAIL, PRODUCT_FAIL},
            {PAYMENT_SERVICE, SUCCESS, INVENTORY_SUCCESS},

            {INVENTORY_SERVICE, ROLLBACK_PENDING, INVENTORY_FAIL},
            {INVENTORY_SERVICE, FAIL, PAYMENT_FAIL},
            {INVENTORY_SERVICE, SUCCESS, FINISH_SUCCESS}
    };

    public static final int EVENT_SOURCE_INDEX = 0;
    public static final int SAGA_STATUS_INDEX = 1;
    public static final int TOPIC_INDEX = 2;

}
