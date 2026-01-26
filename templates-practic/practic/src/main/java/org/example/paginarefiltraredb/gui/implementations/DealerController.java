package org.example.paginarefiltraredb.gui.implementations;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.paginarefiltraredb.domain.dtos.implementation.CarDTO;
import org.example.paginarefiltraredb.domain.entities.Car;
import org.example.paginarefiltraredb.domain.entities.CarStatus;
import org.example.paginarefiltraredb.domain.entities.User;
import org.example.paginarefiltraredb.service.CarService;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;
import org.example.paginarefiltraredb.service.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class DealerController implements Observer<Car> {

    @FXML private TableView<CarDTO> tableView;
    @FXML private TableColumn<CarDTO, String> nameColumn;
    @FXML private TableColumn<CarDTO, String> descriptionColumn;
    @FXML private TableColumn<CarDTO, Double> priceColumn;
    @FXML private TableColumn<CarDTO, String> statusColumn;
    @FXML private Label notificationLabel;

    private final ObservableList<CarDTO> model = FXCollections.observableArrayList();
    private CarService carService;
    private User currentUser;

    public void setCarService(CarService carService) {
        this.carService = carService;
        this.carService.addObserver(this);
        loadData();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusDisplay"));

        tableView.setItems(model);

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                CarDTO selectedCar = tableView.getSelectionModel().getSelectedItem();
                if (selectedCar != null) {
                    openCarDetails(selectedCar);
                }
            }
        });
    }

    private void loadData() {
        if (carService == null) return;

        List<CarDTO> dtos = StreamSupport.stream(carService.findAll().spliterator(), false)
                .map(CarDTO::fromEntity)
                .collect(Collectors.toList());
        model.setAll(dtos);
    }

    private void openCarDetails(CarDTO carDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/paginarefiltraredb/car_details_view.fxml"));
            Parent root = loader.load();

            CarDetailsController controller = loader.getController();
            controller.setCarService(carService);
            controller.setCarDTO(carDTO);
            controller.setDealerMode(true); // Show dealer buttons

            Stage stage = new Stage();
            stage.setTitle("Car Details - " + carDTO.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(EntityChangeEvent<Car> event) {
        Platform.runLater(() -> {
            loadData();

            Car car = event.getData();
            if (car.getStatus() == CarStatus.APPROVED) {
                showNotification("Car '" + car.getName() + "' has been APPROVED!", "#27ae60", "#d5f5e3");
            } else if (car.getStatus() == CarStatus.REJECTED) {
                showNotification("Car '" + car.getName() + "' has been REJECTED. Reason: " + car.getRejectionReason(),
                        "#e74c3c", "#fadbd8");
            } else if (car.getStatus() == CarStatus.NEEDS_APPROVAL) {
                showNotification("Car '" + car.getName() + "' submitted for approval successfully!", "#3498db", "#d6eaf8");
            }
        });
    }

    private void showNotification(String message, String textColor, String bgColor) {
        notificationLabel.setText(message);
        notificationLabel.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 10; -fx-background-radius: 5;");
        notificationLabel.setTextFill(javafx.scene.paint.Color.web(textColor));
        notificationLabel.setVisible(true);
    }
}

