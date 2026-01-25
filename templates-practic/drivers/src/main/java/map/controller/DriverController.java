package map.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import map.domain.Driver;
import map.domain.Order;
import map.service.Service;
import map.utils.events.ChangeEventType;
import map.utils.events.EntityChangeEvent;
import map.utils.observer.Observer;

import java.util.Objects;
import java.util.Optional;

public class DriverController implements Observer<EntityChangeEvent> {
    public TableView tableView;
    public TableColumn idColumn;
    public TableColumn pickupAddressColumn;
    public TableColumn destinationAddressColumn;
    public TableColumn clientNameColumn;
    public TableColumn markColumn;
    public TableColumn statusColumn;
    public TableColumn startDateColumn;
    public TableColumn endDateColumn;
    Service service;
    Driver driver;

    ObservableList<Order> modelOrders = FXCollections.observableArrayList();

    public void initModel(){
        modelOrders.setAll(service.getOrdersByDriver(driver.getId()));
    }

    @FXML
    public void initialize(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        pickupAddressColumn.setCellValueFactory(new PropertyValueFactory<>("pickUpAddress"));
        destinationAddressColumn.setCellValueFactory(new PropertyValueFactory<>("destinationAddress"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        markColumn.setCellFactory(tc -> {
            TableCell<Order, Void> cell = new TableCell<>() {
                private final Button btn = new Button("Finished");
                {
                    btn.setOnAction(event -> {
                        Order order = getTableView().getItems().get(getIndex());
                        service.finishOrder(order);

                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            };
            return cell;
        });
        tableView.setItems(modelOrders);
    }


    public void setService(Service service){
        this.service = service;
        service.addObserver(this);
        initModel();
    }

    public void setDriver(Driver driver){
        this.driver = driver;
    }

    @Override
    public void update(EntityChangeEvent event) {
        if(event.getType() == ChangeEventType.UPDATE)
            initModel();

        if(service.getWhatDriverToBeNotified() == -1)
            return;

        if(event.getType() == ChangeEventType.ADD || event.getType() == ChangeEventType.INCREMENT){
            if(Objects.equals(service.amITheOne().get(service.getWhatDriverToBeNotified()).getId(), driver.getId())) {
//                new Thread(() -> {
//                    try {
//                        Thread.sleep(5000);
//
//                        Platform.runLater(() -> {
//                            if(service.getWhatDriverToBeNotified() + 1 > service.amITheOne().size() - 1)
//                    {
//                        Alert al = new Alert(Alert.AlertType.INFORMATION);
//                        al.setHeaderText("No one accepted the order");
//                        al.show();
//                        service.setWhatDriverToBeNotified(-1);
//                        return;
//                    }
//                    service.incrementWhatDriverToBeNotified(event.getData());
//                        });
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }).start();


                ButtonType acceptButton = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
                ButtonType declineButton = new ButtonType("Decline", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to accept or decline?", acceptButton, declineButton);
                alert.setTitle("Confirmation: " + driver.getName());
                alert.setHeaderText("New order: " + event.getData().getPickUpAddress() + " -> " + event.getData().getDestinationAddress());

                Optional<ButtonType> result = alert.showAndWait();
                
                if (result.isPresent() && result.get() == acceptButton) {
                    service.AcceptOrder(event.getData(), driver.getId());
                    service.setWhatDriverToBeNotified(-1);
                } else if (result.isPresent() && result.get() == declineButton) {
                    if(service.getWhatDriverToBeNotified() + 1 > service.amITheOne().size() - 1)
                    {
                        Alert al = new Alert(Alert.AlertType.INFORMATION);
                        al.setHeaderText("No one accepted the order");
                        al.show();
                        service.setWhatDriverToBeNotified(-1);
                        return;
                    }
                    service.incrementWhatDriverToBeNotified(event.getData());
                }
            }
        }
    }
}
