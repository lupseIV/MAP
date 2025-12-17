package org.ui.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.domain.Observer;
import org.domain.dtos.filters.FriendshipGUIFilter;
import org.domain.dtos.guiDTOS.UserGuiDTO;
import org.domain.events.AddFriendEvent;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.domain.users.relationships.notifications.Notification;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.*;
import org.utils.enums.FriendRequestStatus;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SocialController extends AbstractPagingTableViewController<Friendship, FriendshipGUIFilter> implements Observer<AddFriendEvent> {

    @FXML private TableView<Friendship> friendshipsTable;
    @FXML private TableColumn<Friendship, Long> idCol;
    @FXML private TableColumn<Friendship, User> friendCol;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;
    @FXML private Label labelPage;

    @FXML private Label nrOfCommunities;
    @FXML private ComboBox<FriendRequestStatus> statusCombo;

    private FriendshipService service;
    private UsersService usersService;
    private AuthService authService;
    private NotificationService notificationService;

    public SocialController() {
        super(0, 14, 0,  new FriendshipGUIFilter());
    }

    @Override
    public void update(AddFriendEvent event) {
        if(event.getType() == NotificationType.FRIEND_REQUEST){
            loadData();
        }
    }

    public void setService(FriendshipService service, UsersService usersService, AuthService authService, NotificationService notificationService) {
        this.service = Objects.requireNonNull(service);
        this.usersService = Objects.requireNonNull(usersService);
        this.authService = Objects.requireNonNull(authService);
        this.notificationService = notificationService;

        notificationService.addObserver(this);

        if(authService.isLoggedIn()) {
            filter.setCurrentUser(Optional.of(authService.getCurrentUser()));
            filter.setStatus(Optional.of(FriendRequestStatus.APPROVED));
        }

        initializeTable();
        loadData();
    }

    @FXML
    private void initialize(){
        statusCombo.setItems(FXCollections.observableArrayList(FriendRequestStatus.values()));
        statusCombo.getSelectionModel().select(FriendRequestStatus.APPROVED);
    }

    @FXML
    public void onComboboxChange() {
        FriendRequestStatus selected = statusCombo.getSelectionModel().getSelectedItem();
        filter.setStatus(Optional.of(selected));
        loadData();
    }

    public void initializeTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        friendCol.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            User currentUser = authService.getCurrentUser();

            User friend;
            if (friendship.getUser1().equals(currentUser)) {
                friend = friendship.getUser2();
            } else {
                friend = friendship.getUser1();
            }
            return new SimpleObjectProperty<>(friend);
        });

        friendCol.setCellFactory(column -> new TableCell<Friendship, User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                    setOnMouseClicked(null);
                    setStyle("");
                } else {
                    setText(user.getEmail() + " (" + user.getUsername() + ")");

                    setStyle("-fx-text-fill: #4a90e2; -fx-cursor: hand; -fx-underline: true;");

                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 1 ) {
                            showPopup(user,statusCombo.getSelectionModel().getSelectedItem(),getTableRow().getItem());
                        }
                    });
                }
            }
        });

        friendshipsTable.setItems(model);
    }

    private void showPopup(User user, FriendRequestStatus status, Friendship friendship) {
        if (status == FriendRequestStatus.APPROVED || status == FriendRequestStatus.REJECTED) {
            showUserDetailsPopup(user);
        } else if (status == FriendRequestStatus.PENDING) {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendRequestPopup.fxml"));
                BorderPane root = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Friend Request");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(friendshipsTable.getScene().getWindow());
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                FriendRequestPopup controller = loader.getController();
                controller.setService(dialogStage,
                        authService.getCurrentUser().equals(friendship.getUser1()) ?
                                friendship.getUser2() : friendship.getUser1(),
                        friendship,
                        service);
                controller.setAuthService(authService);

                dialogStage.showAndWait();

                if (controller.isClicked()) {
                    loadData();
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Could not open dialog: " + e.getMessage());
            }
        }
    }


    private void showUserDetailsPopup(User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Details");
        alert.setHeaderText("Details for " + user.getUsername());
        alert.setContentText(
                "ID: " + user.getId() + "\n" +
                        "Email: " + user.getEmail() + "\n" +
                        "Type: " + user.getUserType()
        );
        alert.initOwner(friendshipsTable.getScene().getWindow());
        alert.showAndWait();
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
//            throw e;
            showAlert("Error", "Could not load data: " + e.getMessage());
        }
    }

    @FXML
    public void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendshipAddDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(friendshipsTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            FriendshipAddDialogController controller = loader.getController();
            controller.setService(service, dialogStage);
            controller.setUsersService(usersService);
            controller.setAuthService(authService);

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MostSociableCommunity.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Most Sociable Community");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(friendshipsTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

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
        alert.isResizable();
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
