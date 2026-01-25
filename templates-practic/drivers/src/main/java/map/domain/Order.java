package map.domain;

import java.time.LocalDateTime;

public class Order extends Entity<Integer>{
    Driver driver;
    Status status;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String pickUpAddress;
    String destinationAddress;
    String clientName;

    public Order(Driver driver, Status status, LocalDateTime startDate, LocalDateTime endDate, String pickUpAddress, String destinationAddress, String clientName) {
        this.driver = driver;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pickUpAddress = pickUpAddress;
        this.destinationAddress = destinationAddress;
        this.clientName = clientName;
    }
    public Driver getDriver() {
        return driver;
    }
    public Status getStatus() {
        return status;
    }
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public LocalDateTime getEndDate() {
        return endDate;
    }
    public String getPickUpAddress() {
        return pickUpAddress;
    }
    public String getDestinationAddress() {
        return destinationAddress;
    }
    public String getClientName() {
        return clientName;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setDriver(Integer driverId) {
        Driver driver = new Driver(driverId.toString());
        driver.setId(driverId);
        this.driver = driver;
    }

    public void setStartDate(LocalDateTime now) {
        this.startDate = now;
    }
}
