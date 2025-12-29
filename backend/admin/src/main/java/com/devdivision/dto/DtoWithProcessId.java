package com.devdivision.dto;

import java.io.Serializable;

public class DtoWithProcessId implements Serializable {
    private String correlationId;

    public DtoWithProcessId() {}

    public DtoWithProcessId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
