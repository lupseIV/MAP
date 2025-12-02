package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.domain.users.User;
import org.service.FriendshipService;

import java.util.Objects;

public class FriendshipsController {

    @FXML private VBox rootPane;
    @FXML private TableView<User> mostSociableNetwork;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, Integer> nrOfFriendsCol;
    @FXML private Label nrOfCommunitiesLabel;

    private FriendshipService friendshipService;
    private final ObservableList<User> mostSociableNetworkObservableList = FXCollections.observableArrayList();

    public void setFriendshipService(FriendshipService service) {
        this.friendshipService = Objects.requireNonNull(service);
        loadData();
    }

    public VBox getRootPane() {
        return rootPane;
    }

    @FXML
    private void initialize() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        nrOfFriendsCol.setCellValueFactory(new PropertyValueFactory<>("nrOfFriends"));

        mostSociableNetwork.setItems(mostSociableNetworkObservableList);
    }

    private void loadData() {
        mostSociableNetworkObservableList.clear();
        mostSociableNetworkObservableList.addAll(friendshipService.findMostSociableNetwork());
        nrOfCommunitiesLabel.setText(String.valueOf(friendshipService.countFriendCommunities()));
    }
}
