package com.sam.reporting.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Fired immediately after a report request is accepted.
 * Carries the user's e‑mail so listeners can acknowledge it.
 */
@Getter
public class ReportRequestEvent extends ApplicationEvent {

    private final String email;

    /**
     * Spring needs the source object; we add email as payload
     */
    public ReportRequestEvent(Object source, String email) {
        super(source);
        this.email = email;
    }
}
