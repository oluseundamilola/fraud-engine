package com.app.fraudengine.extended.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionResultDTO {

    private String transactionReference;

    private boolean blocked;

    private String fraudColor;

    private int fraudScore;

    private String reason;
}
