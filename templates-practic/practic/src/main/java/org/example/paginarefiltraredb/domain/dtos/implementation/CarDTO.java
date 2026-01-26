package org.example.paginarefiltraredb.domain.dtos.implementation;

import org.example.paginarefiltraredb.domain.entities.Car;
import org.example.paginarefiltraredb.domain.entities.CarStatus;

public class CarDTO {
    private Long id;
    private String name;
    private String description;
    private Double basePrice;
    private CarStatus status;
    private String comments;
    private String rejectionReason;

    public CarDTO() {}

    public CarDTO(Long id, String name, String description, Double basePrice, CarStatus status,
                  String comments, String rejectionReason) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.status = status;
        this.comments = comments;
        this.rejectionReason = rejectionReason;
    }

    public static CarDTO fromEntity(Car car) {
        return new CarDTO(
            car.getId(),
            car.getName(),
            car.getDescription(),
            car.getBasePrice(),
            car.getStatus(),
            car.getComments(),
            car.getRejectionReason()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getStatusDisplay() {
        return status != null ? status.name() : "";
    }
}


