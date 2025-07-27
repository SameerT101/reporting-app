package com.sam.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportProductRow {
    private final String category;
    private final String productName;
    private final Double price;
}
