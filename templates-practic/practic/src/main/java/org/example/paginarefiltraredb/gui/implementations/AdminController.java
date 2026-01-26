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

/**
 * Controller for Admin view - shows only cars with NEEDS_APPROVAL status
 * Admin can approve or reject cars
 */
public class AdminController implements Observer<Car> {

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

        List<CarDTO> dtos = carService.findCarsNeedingApproval().stream()
                .map(CarDTO::fromEntity)
                .collect(Collectors.toList());
        model.setAll(dtos);

        updateNotificationLabel();
    }

    private void updateNotificationLabel() {
        int pendingCount = model.size();
        if (pendingCount > 0) {
            notificationLabel.setText("You have " + pendingCount + " car(s) pending approval!");
            notificationLabel.setVisible(true);
        } else {
            notificationLabel.setText("No pending approvals");
            notificationLabel.setStyle("-fx-background-color: #d5f5e3; -fx-padding: 10; -fx-background-radius: 5;");
            notificationLabel.setTextFill(javafx.scene.paint.Color.web("green"));
            notificationLabel.setVisible(true);
        }
    }

    private void openCarDetails(CarDTO carDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/paginarefiltraredb/car_details_view.fxml"));
            Parent root = loader.load();

            CarDetailsController controller = loader.getController();
            controller.setCarService(carService);
            controller.setCarDTO(carDTO);
            controller.setAdminMode(true);

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
            // Show notification when a new car needs approval
            if (car.getStatus() == CarStatus.NEEDS_APPROVAL) {
                showNotification("New approval request: '" + car.getName() + "'", "#e67e22", "#fdebd0");
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

