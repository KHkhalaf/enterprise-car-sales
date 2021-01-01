package com.anilaltunkan.security.model;

import javax.persistence.*;

@Entity
@Table(name = "parameters")
public class Parameters {
    private Integer id;
    private Integer numberOfSeats;
    private Float profitRatio;

    public Parameters() {

    }

    public Parameters(Integer numberOfSeats, Float profitRatio) {
        this.numberOfSeats = numberOfSeats;
        this.profitRatio = profitRatio;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "number_of_seats", nullable = false)
    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }
    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @Column(name = "profit_ratio", nullable = false)
    public Float getProfitRatio() { return profitRatio; }
    public void setProfitRatio(Float profitRatio) {
        this.profitRatio = profitRatio;
    }
}
