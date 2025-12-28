package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.domain.Observer;
import org.domain.dtos.filters.EventGUIFilter;
import org.domain.dtos.guiDTOS.EventGuiDTO;
import org.domain.events.RaceEvent;
import org.domain.observer_events.ObserverEvent;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.AuthService;
import org.service.RaceEventService;
import org.utils.enums.status.RaceEventStatus;

public class DuckEventController extends AbstractPagingTableViewController<EventGuiDTO, EventGUIFilter> implements Observer<ObserverEvent> {
    private RaceEventService raceEventService;
    private AuthService authService;

    @FXML
    private TableView<EventGuiDTO> tableView;
    @FXML private TableColumn<EventGuiDTO, Long> idCol;
    @FXML private TableColumn<EventGuiDTO, String> nameCol;
    @FXML private TableColumn<EventGuiDTO, Double> maxTimeCol;
    @FXML private TableColumn<EventGuiDTO, RaceEventStatus> stateCol;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;

    @FXML private Label labelPage;

    public DuckEventController() {
        super(0, 2, 0, new EventGUIFilter());
    }

    @Override
    public void update(ObserverEvent event) {
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
                    // Check if current duck is subscribed to this event
                    if (isDuckSubscribed(item.getId())) {
                        // Apply a CSS style for highlighting (e.g., light green background)
                        // Ensure this style works with your dark theme, or use a specific color like #388e3c
                        setStyle("-fx-background-color: #1b5e20;");
                    } else {
                        setStyle(""); // Reset style for other rows
                    }
                }
            }
        });

        tableView.setItems(model);
    }

    private boolean isDuckSubscribed(Long eventId) {
        if (raceEventService == null || authService == null || authService.getCurrentUser() == null) {
            return false;
        }

        Long currentDuckId = authService.getCurrentUser().getId();
        return raceEventService.isDuckSubscribedToEvent(eventId, currentDuckId);
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
    public void handleSubscribe() {
        EventGuiDTO selectedEvent = tableView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("No Selection", "Please select an event to subscribe.");
            return;
        }

        try {
            raceEventService.addDuckToEvent( selectedEvent.getId(),authService.getCurrentUser().getId());
            showAlert("Success", "Successfully subscribed to event: " + selectedEvent.getName());
        } catch (Exception e) {
            showAlert("Error", "Could not subscribe to event: " + e.getMessage());
        }
    }

    @FXML
    public void handleUnsubscribe() {
        EventGuiDTO selectedEvent = tableView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("No Selection", "Please select an event to unsubscribe.");
            return;
        }
        try {
            raceEventService.removeDuckFromEvent(selectedEvent.getId(), authService.getCurrentUser().getId());
            showAlert("Success", "Successfully unsubscribed from event: " + selectedEvent.getName());
        } catch (Exception e) {
            showAlert("Error", "Could not unsubscribe from event: " + e.getMessage());
        }
    }
}
