package com.sam.reporting.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import com.sam.reporting.service.MailService;
import com.sam.reporting.event.ReportRequestEvent;

@Component
@RequiredArgsConstructor
public class AckMailListener {

    private final MailService mail;

    @EventListener
    public void on(ReportRequestEvent e) {
        mail.sendPlain(
                e.getEmail(),
                "Report request received",
                "We’re on it! You’ll get your PDF soon.");
    }
}
