package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.domain.users.User;
import org.service.UsersService;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class ChatPartnerController {

    private User curentUser;
    private User chatPartner = null;
    private UsersService usersService;
    private Stage dialogStage;

    @FXML
    private ListView<String> chatPartnersListView;

    @FXML
    private Label notSelectedLabel;

    private final ObservableList<String> model = FXCollections.observableArrayList();

    public void setServices(UsersService usersService, Stage dialogStage, User currentUser) {
        this.usersService = usersService;
        this.curentUser = currentUser;
        this.dialogStage = dialogStage;
        loadModel();
    }

    private void loadModel() {
        ArrayList<String> stringUsers =  StreamSupport.stream(usersService.findAll().spliterator(),false)
                .filter(u -> !u.getId().equals(curentUser.getId()))
                .map(u -> u.getUsername() + ": " + u.getEmail()).collect(Collectors.toCollection(ArrayList::new));

        model.setAll(stringUsers);
    }


    @FXML
    private void initialize() {
        chatPartnersListView.setItems(model);
    }

    @FXML
    private void onOkPressed() {
        String userString = chatPartnersListView.getSelectionModel().getSelectedItem();
        if(userString==null) {
            notSelectedLabel.setText("Please select a user");
            return;
        }

        String username = userString.split(": ")[0];
        String email = userString.split(": ")[1];

        chatPartner = StreamSupport.stream(usersService.findAll().spliterator(),false)
                .filter(u -> u.getUsername().equals(username) && u.getEmail().equals(email)).findFirst().orElse(null);


        dialogStage.close();
    }


    @FXML
    private void onCancelPressed() {
        dialogStage.close();
    }

    public User getChatPartner() {
        return chatPartner;
    }
}
