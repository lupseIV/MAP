package org.ui.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.domain.Observer;
import org.domain.dtos.filters.EventGUIFilter;
import org.domain.dtos.guiDTOS.EventGuiDTO;
import org.domain.events.Event;
import org.domain.events.RaceEvent;
import org.domain.observer_events.RaceObserverEvent;
import org.domain.users.duck.SwimmingDuck;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.AuthService;
import org.service.RaceEventService;
import org.utils.enums.status.RaceEventStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PersonEventController extends AbstractPagingTableViewController<EventGuiDTO, EventGUIFilter> implements Observer<RaceObserverEvent> {

    private RaceEventService raceEventService;
    private AuthService authService;

    @FXML private TableView<EventGuiDTO> tableView;
    @FXML private TableColumn<EventGuiDTO, Long> idCol;
    @FXML private TableColumn<EventGuiDTO, String> nameCol;
    @FXML private TableColumn<EventGuiDTO, Double> maxTimeCol;
    @FXML private TableColumn<EventGuiDTO, RaceEventStatus> stateCol;
    @FXML private TableColumn<EventGuiDTO, String> ownerCol;
    @FXML private TableColumn<EventGuiDTO, Long> ducksCountCol;
    @FXML private TableColumn<EventGuiDTO, Long> winnersCol;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;

    @FXML private Label labelPage;

    @FXML private Button startRaceButton;

    @Override
    public void update(RaceObserverEvent event) {
        loadData();
    }

    public PersonEventController() {
        super(0, 2, 0, new EventGUIFilter());
    }

    public void setServices(RaceEventService raceEventService, AuthService authService) {
        this.raceEventService = raceEventService;
        this.authService = authService;

        raceEventService.addObserver(this);

        initializeTable();
        loadData();
    }

    @Override
    public void initializeTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        maxTimeCol.setCellValueFactory(new PropertyValueFactory<>("maxTime"));
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));

        ducksCountCol.setCellValueFactory(new PropertyValueFactory<>("ducks"));
        ducksCountCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setOnMouseClicked(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-cursor: hand;");
                    setOnMouseClicked(event -> showDucksPopup(getTableView().getItems().get(getIndex()).getId()));
                }
            }
        });

        winnersCol.setCellValueFactory(new PropertyValueFactory<>("winners"));
        winnersCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                EventGuiDTO dto = getTableRow().getItem();
                if (empty || dto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if ("COMPLETED".equals(dto.getState())) {
                        setText("View Results");
                        setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-cursor: hand;");
                        setOnMouseClicked(event -> showWinnersPopup(dto.getId()));
                    } else {
                        setText("-");
                        setStyle("");
                        setOnMouseClicked(null);
                    }
                }
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                handleManageDistances();
            }
        });
        tableView.setItems(model);
    }

    private void showDucksPopup(Long eventId) {
        RaceEvent event = raceEventService.findOne(eventId);
        if (event == null) return;

        List<String> duckNames = event.getSubscribers().stream()
                .map(d -> d.getUsername() + " (Speed: " + d.getSpeed() + ")" +" (Resistance: " + d.getRezistance() + ")" )
                .collect(Collectors.toList());

        showListPopup("Subscribed Ducks", duckNames);
    }

    private void showWinnersPopup(Long eventId) {
        RaceEvent event = raceEventService.findOne(eventId);
        if (event == null) return;

        List<String> winners = event.getWinners().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> "Lane " + e.getKey() + ": " + e.getValue().getUsername())
                .collect(Collectors.toList());

        showListPopup("Race Results", winners);
    }

    private void showListPopup(String title, List<String> items) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        ListView<String> list = new ListView<>();
        list.getItems().addAll(items);

        VBox layout = new VBox(10, list);
        layout.setPadding(new javafx.geometry.Insets(10));

        Scene scene = new Scene(layout, 300, 400);
        dialog.setScene(scene);
        dialog.show();
    }

    @Override
    public void loadData() {
        if (raceEventService == null) return;

        Pageable pageable = new Pageable(currentPage, pageSize);
        try {
            Page<RaceEvent> eventPage = raceEventService.findAllOnPage(pageable, filter);
            int maxPage = (int) Math.ceil((double) eventPage.getTotalNumberOfElements() / pageSize) - 1;
            if (maxPage == -1) {
                maxPage = 0;
            }
            if (currentPage > maxPage) {
                currentPage = maxPage;
                eventPage = raceEventService.findAllOnPage(pageable, filter);
            }
            totalNrOfElements = eventPage.getTotalNumberOfElements();

            labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
            buttonPrevious.setDisable(currentPage == 0);
            buttonNext.setDisable((currentPage + 1) * pageSize >= totalNrOfElements);

            model.setAll(raceEventService.getGuiRaceEventsFromPage(eventPage));
        } catch (Exception e) {
            showAlert("Error", "Could not load data: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonAddEventDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Race Event");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableView.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            PersonAddEventDialogController controller = loader.getController();
            controller.setServices(raceEventService,authService, dialogStage);

            dialogStage.showAndWait();
            if(controller.isSaveClicked()){
                loadData();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open dialog: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete(){
        EventGuiDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            RaceEvent event = raceEventService.findOne(selected.getId());
            if(event.getOwner().equals(authService.getCurrentUser())) {
                raceEventService.delete(selected.getId());
                model.remove(selected);
            }else {
                showAlert("Warning", "Can't delete other users events.");

            }
        } else {
            showAlert("Warning", "Please select a duck to delete.");
        }
    }

    @FXML
    private void handleStartRace() {
        EventGuiDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Select an event to start.");
            return;
        }

        RaceEvent event = raceEventService.findOne(selected.getId());

        if(!event.getOwner().equals(authService.getCurrentUser())) {
            showAlert("Error", "You cannot start other users events");
            return;
        }

        if (event == null) {
            showAlert("Error", "Event not found!");
            return;
        }

        if (event.getState() == RaceEventStatus.COMPLETED) {
            showAlert("Info", "Event is already finished.");
            return;
        }

        List<Integer> distances = event.getDistances();
        List<SwimmingDuck> ducks = event.getSubscribers();

        if (distances == null || distances.isEmpty()) {
            showAlert("Error", "No distances defined for this race.");
            return;
        }

        if (ducks == null || ducks.isEmpty()) {
            showAlert("Error", "No ducks subscribed to this race.");
            return;
        }

        if (distances.size() >= ducks.size()) {
            showAlert("Error", "The number of subscribed ducks (" + ducks.size() +
                    ") must be higher or equal to the number of distances/lanes (" + distances.size() + ").");
            return;
        }

        startRaceButton.setDisable(true);
        startRaceButton.setText("Running...");
        raceEventService.setAuthService(authService);

        raceEventService.solveRace(event)
                .thenAccept(bestTime -> {
                    Platform.runLater(() -> {
                        showAlert("Success", "Race finished! Best time: " + String.format("%.3f", bestTime));
                        startRaceButton.setDisable(false);
                        startRaceButton.setText("Start Race");
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showAlert("Error", "Race calculation failed: " + ex.getCause().getMessage());
                        startRaceButton.setDisable(false);
                        startRaceButton.setText("Start Race");
                    });
                    return null;
                });
    }

    @FXML
    private void handleManageDistances(){
        EventGuiDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Select an event to manage distances.");
            return;
        }

        RaceEvent event1 = raceEventService.findOne(selected.getId());
        if(event1!=null&&!event1.getOwner().equals(authService.getCurrentUser())) {
            showAlert("Error", "You cannot manage distances for other users events");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DistancesDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Manage Distances for " + selected.getName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tableView.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            RaceEvent event = raceEventService.findOne(selected.getId());

            if (event != null) {
                DistancesDialogController controller = loader.getController();
                controller.setService(raceEventService, event, dialogStage);
                dialogStage.showAndWait();
                if (controller.isSaveClicked()) {
                    loadData();
                }
            } else {
                showAlert("Error", "Could not fetch event details.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open dialog: " + e.getMessage());
        }
    }
}
