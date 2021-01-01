package com.anilaltunkan.security.dto;

import javax.persistence.*;

public class ParametersDTO {
    private int numberOfSeats;
    private String profitRatio;

    public ParametersDTO() {

    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }
    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public String getProfitRatio() { return profitRatio; }
    public void setProfitRatio(String profitRatio) {
        this.profitRatio = profitRatio;
    }
}
