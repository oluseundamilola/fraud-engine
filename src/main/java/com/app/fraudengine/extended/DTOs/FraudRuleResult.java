package com.app.fraudengine.extended.DTOs;

import lombok.Data;

@Data
public class FraudRuleResult {

    private int score;

    private String reason;

    private boolean triggered;
}
