package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.domain.events.RaceEvent;
import org.service.RaceEventService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DistancesDialogController {

    @FXML private TextArea currentDistancesArea;
    @FXML private CheckBox autoDistancesCheckbox;
    @FXML private javafx.scene.layout.HBox manualBox;
    @FXML private javafx.scene.layout.HBox autoBox;
    @FXML private TextArea manualInput;
    @FXML private Spinner<Integer> countSpinner;

    private Stage stage;
    private RaceEvent event;
    private RaceEventService service;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3);
        countSpinner.setValueFactory(valueFactory);

        autoDistancesCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            manualBox.setVisible(!newVal);
            manualBox.setManaged(!newVal);
            autoBox.setVisible(newVal);
            autoBox.setManaged(newVal);
        });
    }

    public void setService(RaceEventService service, RaceEvent event, Stage stage) {
        this.service = service;
        this.event = event;
        this.stage = stage;
        updateCurrentDistances();
    }

    private void updateCurrentDistances() {
        if (event.getDistances() != null) {
            currentDistancesArea.setText(event.getDistances().toString());
        }
    }

    @FXML
    private void handleSave() {
        List<Integer> newDistances = new ArrayList<>();

        if (autoDistancesCheckbox.isSelected()) {
            int count = countSpinner.getValue();
        } else {
            String text = manualInput.getText();
            if (text != null && !text.trim().isEmpty()) {
                try {
                    newDistances = Arrays.stream(text.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .sorted()
                            .collect(Collectors.toList());

                    for (int i = 0; i < newDistances.size() - 1; i++) {
                        if (newDistances.get(i) >= newDistances.get(i+1)) {
                            showAlert("Error", "Distances must be strictly ascending!");
                            return;
                        }
                    }
                    event.setDistances(newDistances);
                    service.save(event); // Persist
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid number format. Use comma separated integers.");
                    return;
                }
            }
        }

        saveClicked = true;
        stage.close();
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}