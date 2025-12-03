package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.domain.dtos.filters.DuckGUIFilter;
import org.domain.dtos.filters.PersonGUIFilter;
import org.domain.dtos.guiDTOS.DuckGuiDTO;
import org.domain.dtos.guiDTOS.PersonGuiDTO;
import org.domain.users.duck.Duck;
import org.domain.users.person.Person;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.PersonsService;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class PersonsController extends AbstractPagingTableViewController{

    @FXML private TableView<PersonGuiDTO> personsTable;
    @FXML private TableColumn<PersonGuiDTO, Long> idCol;
    @FXML private TableColumn<PersonGuiDTO, String> usernameCol;
    @FXML private TableColumn<PersonGuiDTO, String> emailCol;
    @FXML private TableColumn<PersonGuiDTO, String> firstNameCol;
    @FXML private TableColumn<PersonGuiDTO, String> lastNameCol;
    @FXML private TableColumn<PersonGuiDTO, String> occupationCol;
    @FXML private TableColumn<PersonGuiDTO, String> dobCol;
    @FXML private TableColumn<PersonGuiDTO, Double> empathyCol;

    @FXML private ComboBox<String> comboBox;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;

    @FXML private Label labelPage;

    private PersonsService service;

    public PersonsController() {
        super(0, 14, 0);
    }

    private final ObservableList<PersonGuiDTO> model = FXCollections.observableArrayList();
    private final PersonGUIFilter filter = new PersonGUIFilter();

    public void setService(PersonsService service) {
        this.service = Objects.requireNonNull(service);
        initializeTable();
        initComboBox();
        loadData();
    }

    public void initializeTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        occupationCol.setCellValueFactory(new PropertyValueFactory<>("Occupation"));
        dobCol.setCellValueFactory(new PropertyValueFactory<>("DateOfBirth"));
        empathyCol.setCellValueFactory(new PropertyValueFactory<>("EmpathyLevel"));

        personsTable.setItems(model);
    }



    public void loadData() {
        if (service == null) return;

        Pageable pageable = new Pageable(currentPage, pageSize);
        try {
            Page<Person> personPage = service.findAllOnPage(pageable, filter);
            int maxPage = (int) Math.ceil((double) personPage.getTotalNumberOfElements() / pageSize) - 1;
            if (maxPage == -1) {
                maxPage = 0;
            }
            if (currentPage > maxPage) {
                currentPage = maxPage;
                personPage = service.findAllOnPage(pageable ,filter);
            }
            totalNrOfElements = personPage.getTotalNumberOfElements();

            labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
            buttonPrevious.setDisable(currentPage == 0);
            buttonNext.setDisable((currentPage + 1) * pageSize >= totalNrOfElements);

            model.setAll(service.getGuiPersonsFromPage(personPage));
        } catch (Exception e) {
            showAlert("Error", "Could not load data: " + e.getMessage());
        }
    }

    private void initComboBox(){
        comboBox.setItems(FXCollections.observableArrayList("All"));
        comboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void onComboboxChange() {
        String selected = comboBox.getSelectionModel().getSelectedItem();
        loadData();
    }

    @FXML
    public void handleAddPerson() {
        try {
            // 1. Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonAddDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(personsTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            PersonAddDialogController controller = loader.getController();
            controller.setService(service, dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                loadData();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open dialog: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeletePerson() {
        PersonGuiDTO selected = personsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            service.delete(selected.getId());
            model.remove(selected);
            loadData();
        } else {
            showAlert("Warning", "Please select a duck to delete.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
