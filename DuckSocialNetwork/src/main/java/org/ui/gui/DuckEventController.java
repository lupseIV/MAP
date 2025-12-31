package org.ui.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.domain.Observer;
import org.domain.dtos.filters.EventGUIFilter;
import org.domain.dtos.guiDTOS.EventGuiDTO;
import org.domain.events.RaceEvent;
import org.domain.observer_events.RaceObserverEvent;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.AuthService;
import org.service.RaceEventService;
import org.utils.enums.status.RaceEventStatus; // Check this import

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DuckEventController extends AbstractPagingTableViewController<EventGuiDTO, EventGUIFilter> implements Observer<RaceObserverEvent> {
    private RaceEventService raceEventService;
    private AuthService authService;

    @FXML private TableView<EventGuiDTO> tableView;
    @FXML private TableColumn<EventGuiDTO, Long> idCol;
    @FXML private TableColumn<EventGuiDTO, String> nameCol;
    @FXML private TableColumn<EventGuiDTO, Double> maxTimeCol;
    @FXML private TableColumn<EventGuiDTO, String> stateCol;
    @FXML private TableColumn<EventGuiDTO, String> ownerCol;
    @FXML private TableColumn<EventGuiDTO, Long> ducksCountCol;
    @FXML private TableColumn<EventGuiDTO, Long> winnersCol;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;
    @FXML private Label labelPage;

    public DuckEventController() {
        super(0, 2, 0, new EventGUIFilter());
    }

    @Override
    public void update(RaceObserverEvent event) {
        loadData();
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

        tableView.setRowFactory(tv -> new TableRow<EventGuiDTO>() {
            @Override
            protected void updateItem(EventGuiDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (isDuckSubscribed(item.getId())) {
                        setStyle("-fx-background-color: #1b5e20;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

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

        tableView.setItems(model);
    }
    private void showDucksPopup(Long eventId) {
        RaceEvent event = raceEventService.findOne(eventId);
        if (event == null) return;

        List<String> duckNames = event.getSubscribers().stream()
                .map(d -> d.getUsername() + " (Speed: " + d.getSpeed() + ")")
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
    private boolean isDuckSubscribed(Long eventId) {
        if (raceEventService == null || authService == null || authService.getCurrentUser() == null) return false;
        return raceEventService.isDuckSubscribedToEvent(eventId, authService.getCurrentUser().getId());
    }

    @Override
    public void loadData() {
        if (raceEventService == null) return;

        try {
            Pageable pageable = new Pageable(currentPage, pageSize);
            Page<RaceEvent> eventPage = raceEventService.findAllOnPage(pageable, filter);

            int maxPage = (int) Math.ceil((double) eventPage.getTotalNumberOfElements() / pageSize) - 1;
            if (maxPage == -1) maxPage = 0;

            if (currentPage > maxPage) {
                currentPage = maxPage;
                eventPage = raceEventService.findAllOnPage(pageable, filter);
            }

            final int total = eventPage.getTotalNumberOfElements();
            final int finalMaxPage = maxPage;
            final var events = raceEventService.getGuiRaceEventsFromPage(eventPage);

            Platform.runLater(() -> {
                totalNrOfElements = total;
                labelPage.setText("Page " + (currentPage + 1) + " of " + (finalMaxPage + 1));
                buttonPrevious.setDisable(currentPage == 0);
                buttonNext.setDisable((currentPage + 1) * pageSize >= totalNrOfElements);

                model.setAll(events);
                tableView.refresh();
            });
        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Error", "Could not load data: " + e.getMessage()));
        }
    }

    @FXML
    public void handleSubscribe() {
        EventGuiDTO selectedEvent = tableView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("No Selection", "Please select an event to subscribe.");
            return;
        }

        raceEventService.addDuckToEvent(selectedEvent.getId(), authService.getCurrentUser().getId())
                .thenRun(() -> Platform.runLater(() ->
                        showAlert("Success", "Successfully subscribed to: " + selectedEvent.getName())
                ))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert("Error", "Could not subscribe: " + ex.getCause().getMessage()));
                    return null;
                });
    }

    @FXML
    public void handleUnsubscribe() {
        EventGuiDTO selectedEvent = tableView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("No Selection", "Please select an event to unsubscribe.");
            return;
        }

        raceEventService.removeDuckFromEvent(selectedEvent.getId(), authService.getCurrentUser().getId())
                .thenRun(() -> Platform.runLater(() ->
                        showAlert("Success", "Successfully unsubscribed from: " + selectedEvent.getName())
                ))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert("Error", "Could not unsubscribe: " + ex.getCause().getMessage()));
                    return null;
                });
    }
}