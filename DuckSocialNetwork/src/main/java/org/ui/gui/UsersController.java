package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.domain.dtos.filters.UserGUIFilter;
import org.domain.dtos.guiDTOS.UserGuiDTO;
import org.domain.users.User;
import org.service.DucksService;
import org.service.PersonsService;
import org.service.UsersService;


public class UsersController  implements ViewController {

    @FXML private StackPane contentArea;

    private DucksService ducksService;
    private PersonsService personsService;

    public void setService(DucksService ducksService, PersonsService personsService, StackPane contentArea) {
        this.ducksService = ducksService;
        this.personsService = personsService;
        this.contentArea = contentArea;
    }

    @FXML
    public void onDucksButtonClick() {
        loadView("DucksView.fxml", controller -> {
            if (controller instanceof DucksController) {
                ((DucksController) controller).setService(ducksService);
            }
        },  contentArea);
    }

    @FXML
    public void onPersonsButtonClick() {
        loadView("PersonsView.fxml", controller -> {
            if (controller instanceof PersonsController) {
                ((PersonsController) controller).setService(personsService);
            }
        }, contentArea);
    }

}
