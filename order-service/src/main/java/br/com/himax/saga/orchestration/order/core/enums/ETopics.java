package br.com.himax.saga.orchestration.order.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ETopics {
    START_SAGA("start-saga"),
    NOTIFY_ENDING("notify-ending");

    private String topic;
}
