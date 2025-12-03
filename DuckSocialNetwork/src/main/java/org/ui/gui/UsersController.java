package org.ui.gui;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.service.DucksService;
import org.service.PersonsService;
import org.service.UsersService;

public class UsersController {

    private UsersService service;
    private DucksService ducksService;
    private PersonsService personsService;

    public void setService(UsersService service, DucksService ducksService, PersonsService personsService) {
        this.service = service;
        this.ducksService = ducksService;
        this.personsService = personsService;
    }

    @FXML
    public void onDucksButtonClick() {}


}
