package com.anilaltunkan.security.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "car")
public class Car {

    private Long id;
    private String name;
    private Integer price;
    private Integer numberOfSeats;
    private Integer salePrice;
    private String saleDate;
    private String buyerName;
    private Boolean isAvailable;
    private Long version;

    public Car() {

    }

    public Car(String name, Integer price, Integer numberOfSeats, Integer salePrice, String saleDate, String buyerName, Boolean isAvailable) {
        this.name = name;
        this.price = price;
        this.numberOfSeats = numberOfSeats;
        this.salePrice = salePrice;
        this.saleDate = saleDate;
        this.buyerName = buyerName;
        this.isAvailable = isAvailable;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "price", nullable = false)
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }

    @Column(name = "number_of_seats", nullable = false)
    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }
    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @Column(name = "sale_date", nullable = true)
    public String getSaleDate() {
        return saleDate;
    }
    public void setSaleDate(String saleDate){
        this.saleDate = saleDate;
    }

    @Column(name = "sale_price", nullable = true)
    public Integer getSalePrice() {
        return salePrice;
    }
    public void setSalePrice(Integer salePrice) {
        this.salePrice = salePrice;
    }

    @Column(name = "buyer_name", nullable = true)
    public String getBuyerName() {
        return buyerName;
    }
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    @Column(name = "is_available", nullable = true)
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Version
    @Column(name="version")
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
