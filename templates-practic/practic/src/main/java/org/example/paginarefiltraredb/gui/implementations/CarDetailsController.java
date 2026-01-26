package org.example.paginarefiltraredb.gui.implementations;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.paginarefiltraredb.domain.dtos.implementation.CarDTO;
import org.example.paginarefiltraredb.domain.entities.CarStatus;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.service.CarService;

import java.util.Optional;

/**
 * Controller for Car Details dialog
 * Used by both Dealer and Admin with different functionality
 */
public class CarDetailsController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private Label statusLabel;
    @FXML private VBox rejectionReasonBox;
    @FXML private TextArea rejectionReasonArea;
    @FXML private TextArea commentsArea;
    @FXML private HBox dealerButtonsBox;
    @FXML private HBox adminButtonsBox;
    @FXML private Button submitForApprovalBtn;
    @FXML private Button approveBtn;
    @FXML private Button rejectBtn;
    @FXML private Label processingLabel;

    private CarService carService;
    private CarDTO carDTO;
    private boolean isDealerMode = false;
    private boolean isAdminMode = false;

    public void setCarService(CarService carService) {
        this.carService = carService;
    }

    public void setCarDTO(CarDTO carDTO) {
        this.carDTO = carDTO;
        populateFields();
    }

    public void setDealerMode(boolean dealerMode) {
        this.isDealerMode = dealerMode;
        updateButtonVisibility();
    }

    public void setAdminMode(boolean adminMode) {
        this.isAdminMode = adminMode;
        updateButtonVisibility();
    }

    private void populateFields() {
        if (carDTO == null) return;

        nameField.setText(carDTO.getName());
        descriptionArea.setText(carDTO.getDescription());
        priceField.setText(String.format("%.2f", carDTO.getBasePrice()));
        commentsArea.setText(carDTO.getComments() != null ? carDTO.getComments() : "");

        // Set status with color
        CarStatus status = carDTO.getStatus();
        statusLabel.setText(status.name());

        switch (status) {
            case NEW -> {
                statusLabel.setTextFill(Color.BLUE);
                statusLabel.setStyle("-fx-background-color: lightblue;");
            }
            case NEEDS_APPROVAL -> {
                statusLabel.setTextFill(Color.ORANGE);
                statusLabel.setStyle("-fx-background-color: lightyellow;");
            }
            case APPROVED -> {
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setStyle("-fx-background-color: lightgreen;");
            }
            case REJECTED -> {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setStyle("-fx-background-color: mistyrose;");
            }
        }

        if (status == CarStatus.REJECTED && carDTO.getRejectionReason() != null) {
            rejectionReasonBox.setVisible(true);
            rejectionReasonBox.setManaged(true);
            rejectionReasonArea.setText(carDTO.getRejectionReason());
        }
    }

    private void updateButtonVisibility() {
        if (isDealerMode) {
            showDealerButtons();
        }
        if (isAdminMode) {
            showAdminButtons();
        }
    }

    private void showDealerButtons() {
        dealerButtonsBox.setVisible(true);
        dealerButtonsBox.setManaged(true);
        adminButtonsBox.setVisible(false);
        adminButtonsBox.setManaged(false);

        boolean canSubmit = canDealerSubmit();
        submitForApprovalBtn.setDisable(!canSubmit);
        commentsArea.setEditable(canSubmit);
    }

    private void showAdminButtons() {
        adminButtonsBox.setVisible(true);
        adminButtonsBox.setManaged(true);
        dealerButtonsBox.setVisible(false);
        dealerButtonsBox.setManaged(false);

        boolean canProcess = canAdminProcess();
        approveBtn.setDisable(!canProcess);
        rejectBtn.setDisable(!canProcess);
        commentsArea.setEditable(false);
    }

    private boolean canDealerSubmit() {
        if (carDTO == null) return false;
        CarStatus status = carDTO.getStatus();
        return status == CarStatus.NEW || status == CarStatus.REJECTED;
    }

    private boolean canAdminProcess() {
        if (carDTO == null) return false;
        return carDTO.getStatus() == CarStatus.NEEDS_APPROVAL;
    }

    @FXML
    public void handleSubmitForApproval() {
        if (carDTO == null || carService == null) return;

        String comments = commentsArea.getText().trim();

        setProcessingState(true);

        carService.submitForApproval(carDTO.getId(), comments)
                .thenAccept(success -> Platform.runLater(() -> {
                    setProcessingState(false);
                    if (success) {
                        WindowManager.showMessage("Success", "Car submitted for approval successfully!");
                        closeDialog();
                    } else {
                        WindowManager.showError("Error", "Failed to submit car for approval. Invalid status transition.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        setProcessingState(false);
                        WindowManager.showError("Error", "An error occurred: " + ex.getMessage());
                    });
                    return null;
                });
    }

    @FXML
    public void handleApprove() {
        if (carDTO == null || carService == null) return;

        // Show processing state
        setProcessingState(true);

        carService.approveCar(carDTO.getId())
                .thenAccept(success -> Platform.runLater(() -> {
                    setProcessingState(false);
                    if (success) {
                        WindowManager.showMessage("Success", "Car approved successfully!");
                        closeDialog();
                    } else {
                        WindowManager.showError("Error", "Failed to approve car. Invalid status transition.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        setProcessingState(false);
                        WindowManager.showError("Error", "An error occurred: " + ex.getMessage());
                    });
                    return null;
                });
    }

    @FXML
    public void handleReject() {
        if (carDTO == null || carService == null) return;

        // Ask for rejection reason
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rejection Reason");
        dialog.setHeaderText("Please provide a reason for rejecting this car:");
        dialog.setContentText("Reason:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) {
            WindowManager.showError("Error", "You must provide a rejection reason.");
            return;
        }

        String rejectionReason = result.get().trim();

        setProcessingState(true);

        carService.rejectCar(carDTO.getId(), rejectionReason)
                .thenAccept(success -> Platform.runLater(() -> {
                    setProcessingState(false);
                    if (success) {
                        WindowManager.showMessage("Success", "Car rejected successfully!");
                        closeDialog();
                    } else {
                        WindowManager.showError("Error", "Failed to reject car. Invalid status transition.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        setProcessingState(false);
                        WindowManager.showError("Error", "An error occurred: " + ex.getMessage());
                    });
                    return null;
                });
    }

    @FXML
    public void handleCancel() {
        closeDialog();
    }

    private void setProcessingState(boolean processing) {
        processingLabel.setVisible(processing);
        submitForApprovalBtn.setDisable(processing);
        approveBtn.setDisable(processing);
        rejectBtn.setDisable(processing);
    }

    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
