package com.exchange.portal.exchangeportal.common;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class MDCTaskDecorator implements TaskDecorator {
    public static final MDCTaskDecorator INSTANCE = new MDCTaskDecorator();

    private MDCTaskDecorator() {
    }

    public static MDCTaskDecorator getInstance() {
        return INSTANCE;
    }

    public Runnable decorate(Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(contextMap);
                runnable.run();
            } finally {
                MDC.clear();
            }

        };
    }
}