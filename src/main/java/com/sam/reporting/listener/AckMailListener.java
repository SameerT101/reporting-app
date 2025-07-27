package com.sam.reporting.listener;

import com.sam.reporting.event.ReportRequestEvent;
import com.sam.reporting.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
