package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.domain.users.User;
import org.service.DucksService;
import org.service.PersonsService;
import org.service.UsersService;


public class UsersController extends AbstractPagingTableViewController<User> implements ViewController {

    private UsersService service;
    private DucksService ducksService;
    private PersonsService personsService;

    public UsersController(int currentPage, int pageSize, int totalNrOfElements) {
        super(0, 14, 0);
    }

    public void setService(UsersService service, DucksService ducksService, PersonsService personsService) {
        this.service = service;
        this.ducksService = ducksService;
        this.personsService = personsService;
        initializeTable();
        loadData();
    }

    @FXML
    public void onDucksButtonClick() {}


    @Override
    public void loadView(String fxmlFile, ControllerConfigurator configurator, Pane contentArea) {

    }

    @Override
    public void initializeTable() {

    }

    @Override
    public void loadData() {

    }
}
