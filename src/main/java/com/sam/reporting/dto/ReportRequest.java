package com.sam.reporting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * Payload sent by the REST client and passed around via RabbitMQ.
 */
@Data
public class ReportRequest implements Serializable {

    @NotBlank
    private String reportType;   // e.g. "product_of_all_categories"

    @Email
    @NotBlank
    private String userEmail;    // recipient for the ack + final PDF

}
