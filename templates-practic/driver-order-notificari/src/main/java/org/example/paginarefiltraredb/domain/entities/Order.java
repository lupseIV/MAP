package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbManyToOne;
import org.example.paginarefiltraredb.domain.entities.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.NavigableMap;

public class Order extends Entity<Integer>{

    @DbManyToOne
    @DbColumn(name = "driver_id")
    private Integer driverId;

    @DbColumn(name = "status", nullable = false)
    private OrderStatus status;

    @DbColumn(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @DbColumn(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @DbColumn(name = "pick_up_address", nullable = false)
    private String pickUpAddress;

    @DbColumn(name = "destination_address", nullable = false)
    private String destinationAddress;

    @DbColumn(name = "client_name", nullable = false)
    private String clientName;

    public Order(Integer driverId, OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, String pickUpAddress, String destinationAddress, String clientName) {
        this.driverId = driverId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pickUpAddress = pickUpAddress;
        this.destinationAddress = destinationAddress;
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "driverId=" + driverId +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", pickUpAddress='" + pickUpAddress + '\'' +
                ", clientName='" + clientName + '\'' +
                '}';
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
}
