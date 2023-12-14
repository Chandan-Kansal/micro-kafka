package com.example.productservice.dto;

import java.math.BigDecimal;

public class ProductResponse {
    private String id;
    private String name;
    private String Description;
    private BigDecimal price;

    private Integer quantity;

    public ProductResponse() {
    }

    public ProductResponse(String id, String name, String description, BigDecimal price,Integer quantity) {
        this.id = id;
        this.name = name;
        Description = description;
        this.price = price;
        this.quantity=quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
