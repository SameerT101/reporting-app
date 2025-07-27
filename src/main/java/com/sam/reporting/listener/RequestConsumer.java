package com.sam.reporting.listener;

import com.sam.reporting.config.RabbitConfig;
import com.sam.reporting.dto.ReportRequest;
import com.sam.reporting.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestConsumer {
    private final ReportService service;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void on(ReportRequest req) {
        service.generateAndMail(req);
    }
}
