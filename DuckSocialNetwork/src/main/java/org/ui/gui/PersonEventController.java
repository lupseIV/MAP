package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.domain.dtos.filters.EventGUIFilter;
import org.domain.dtos.filters.PersonGUIFilter;
import org.domain.dtos.guiDTOS.EventGuiDTO;
import org.domain.dtos.guiDTOS.PersonGuiDTO;
import org.domain.events.RaceEvent;
import org.domain.users.person.Person;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.AuthService;
import org.service.RaceEventService;
import org.utils.enums.EventState;


public class PersonEventController extends AbstractPagingTableViewController<EventGuiDTO, EventGUIFilter>{

    private RaceEventService raceEventService;
    private AuthService authService;

    @FXML private TableView<EventGuiDTO> tableView;
    @FXML private TableColumn<EventGuiDTO, Long> idCol;
    @FXML private TableColumn<EventGuiDTO, String> nameCol;
    @FXML private TableColumn<EventGuiDTO, Double> maxTimeCol;
    @FXML private TableColumn<EventGuiDTO, EventState> stateCol;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;

    @FXML private Label labelPage;

    public PersonEventController() {
        super(0, 2, 0, new EventGUIFilter());
    }

    public void setServices(RaceEventService raceEventService, AuthService authService) {
        this.raceEventService = raceEventService;
        this.authService = authService;

        initializeTable();
        loadData();
    }

    @Override
    public void initializeTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        maxTimeCol.setCellValueFactory(new PropertyValueFactory<>("maxTime"));
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));

        tableView.setItems(model);
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
    private void handleAdd(){}

    @FXML
    private void handleDelete(){
        EventGuiDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            raceEventService.delete(selected.getId());
            model.remove(selected);
            loadData();
        } else {
            showAlert("Warning", "Please select a duck to delete.");
        }
    }
}
