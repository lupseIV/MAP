package org.example.paginarefiltraredb.domain.entities;

public class Car extends Entity<Long> {
    private String name;
    private String description;
    private Double basePrice;
    private CarStatus status;
    private String comments;
    private String rejectionReason;

    public Car() {
        this.status = CarStatus.NEW;
    }

    public Car(String name, String description, Double basePrice) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.status = CarStatus.NEW;
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

    @Override
    public String toString() {
        return "Car{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", status=" + status +
                '}';
    }
}

