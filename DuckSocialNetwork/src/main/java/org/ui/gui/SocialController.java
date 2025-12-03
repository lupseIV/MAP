package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.domain.dtos.filters.DuckGUIFilter;
import org.domain.dtos.filters.FriendshipGUIFilter;
import org.domain.dtos.filters.PersonGUIFilter;
import org.domain.dtos.guiDTOS.DuckGuiDTO;
import org.domain.dtos.guiDTOS.PersonGuiDTO;
import org.domain.dtos.guiDTOS.UserGuiDTO;
import org.domain.users.duck.Duck;
import org.domain.users.person.Person;
import org.domain.users.relationships.Friendship;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.FriendshipService;
import org.service.PersonsService;
import org.service.UsersService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SocialController extends AbstractPagingTableViewController{

    @FXML private TableView<Friendship> friendshipsTable;
    @FXML private TableColumn<Friendship, Long> idCol;
    @FXML private TableColumn<Friendship, String> user1Col;
    @FXML private TableColumn<Friendship, String> user2Col;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;
    @FXML private Label labelPage;

    @FXML private Label nrOfCommunities;

    private FriendshipService service;
    private UsersService usersService;


    public SocialController() {
        super(0, 14, 0);
    }

    private final ObservableList<Friendship> model = FXCollections.observableArrayList();
    private final FriendshipGUIFilter filter = new FriendshipGUIFilter();

    public void setService(FriendshipService service, UsersService usersService) {

        this.service = Objects.requireNonNull(service);
        this.usersService = Objects.requireNonNull(usersService);
        initializeTable();
        loadData();
    }

    public void initializeTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        user1Col.setCellValueFactory(new PropertyValueFactory<>("user1"));
        user2Col.setCellValueFactory(new PropertyValueFactory<>("user2"));

        friendshipsTable.setItems(model);
    }


    public void loadData() {
        if (service == null) return;

        Pageable pageable = new Pageable(currentPage, pageSize);
        try {
            Page<Friendship> personPage = service.findAllOnPage(pageable, filter);
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

            nrOfCommunities.setText("Number of Communities: " + service.countFriendCommunities());

            model.setAll(service.getGuiFriendshipsFromPage(personPage));
        } catch (Exception e) {
            showAlert("Error", "Could not load data: " + e.getMessage());
        }
    }

    @FXML
    public void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendshipAddDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(friendshipsTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            FriendshipAddDialogController controller = loader.getController();
            controller.setService(service, dialogStage);
            controller.setUsersService(usersService);

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
    public void handleDelete() {
        Friendship selected = friendshipsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            service.delete(selected.getId());
            model.remove(selected);
            loadData();
        } else {
            showAlert("Warning", "Please select a duck to delete.");
        }
    }

    @FXML
    public void onMostSociableCommunity() {
        try {
            // 1. Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MostSociableCommunity.fxml"));
            VBox page = loader.load();

            // 2. Create the Stage (Dialog Window)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Most Sociable Community");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Blocks interaction with main window
            dialogStage.initOwner(friendshipsTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 3. Inject dependencies into the Dialog Controller
            MostSociableCommunityDialog controller = loader.getController();

            List<UserGuiDTO> dtoList = service.findMostSociableNetwork().stream()
                            .map(u -> {
                                Long id = u.getId();
                                String username = u.getUsername();
                                String email = u.getEmail();
                                String type = u.getUserType().name();
                                return new UserGuiDTO(id, username, email, type);
                            }).collect(Collectors.toCollection(ArrayList::new));

            controller.setModel(FXCollections.observableArrayList(dtoList), dialogStage);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open dialog: " + e.getMessage());
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
