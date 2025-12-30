package org.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.domain.users.User;
import org.domain.users.duck.Duck;
import org.domain.users.person.Person;
import org.domain.users.relationships.Friendship;
import org.service.FriendshipService;
import org.service.UsersService;
import org.utils.enums.types.UserTypes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserProfileController {

    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label typeLabel;
    @FXML private GridPane detailsGrid;

    @FXML private Label totalFriendsLabel;

    @FXML private Button editButton;
    @FXML private Button backButton;

    private FriendshipService friendshipService;
    private UsersService usersService;
    private User displayedUser;
    private boolean ownerView = true;
    private Stage stage;

    public void setServices(FriendshipService friendshipService, UsersService usersService, User user, boolean ownerView) {
        this.friendshipService = friendshipService;
        this.usersService = usersService;
        this.displayedUser = user;

        updateUI();
        setOwnerView(ownerView);
    }

    public void setOwnerView(boolean ownerView) {
        this.ownerView = ownerView;
        editButton.setVisible(ownerView);
        editButton.setManaged(ownerView);

        backButton.setVisible(!ownerView);
        backButton.setManaged(!ownerView);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void updateUI() {
        if (displayedUser == null) return;

        usernameLabel.setText("@" + displayedUser.getUsername());
        emailLabel.setText(displayedUser.getEmail());
        typeLabel.setText(displayedUser.getUserType().toString());

        detailsGrid.getChildren().clear();
        if (displayedUser instanceof Person) {
            Person p = (Person) displayedUser;
            addDetailRow("Nume:", p.getFirstName() + " " + p.getLastName(), 0);
            addDetailRow("Job:", p.getOccupation(), 1);
            addDetailRow("Empatie:", String.valueOf(p.getEmpathyLevel()), 2);
        } else if (displayedUser instanceof Duck) {
            Duck d = (Duck) displayedUser;
            addDetailRow("Tip Rață:", d.getDuckType().toString(), 0);
            addDetailRow("Viteză:", String.valueOf(d.getSpeed()), 1);
            addDetailRow("Rezistență:", String.valueOf(d.getRezistance()), 2);
        }

        calculateStats();
    }

    private void addDetailRow(String key, String value, int row) {
        Label keyLbl = new Label(key);
        keyLbl.setStyle("-fx-text-fill: #b0b0b0;");
        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        detailsGrid.add(keyLbl, 0, row);
        detailsGrid.add(valLbl, 1, row);
    }


    private void calculateStats() {
        Iterable<Friendship> allFriendships = friendshipService.findAll();

        List<User> friends = StreamSupport.stream(allFriendships.spliterator(), false)
                .filter(f -> f.getUser1().getId().equals(displayedUser.getId()) || f.getUser2().getId().equals(displayedUser.getId()))
                .map(f -> {
                    Long friendId = f.getUser1().getId().equals(displayedUser.getId()) ? f.getUser2().getId() : f.getUser1().getId();
                    return f.getUser1().getId().equals(displayedUser.getId()) ? f.getUser2() : f.getUser1();
                })
                .collect(Collectors.toList());

        totalFriendsLabel.setText(String.valueOf(friends.size()));
    }

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserEditDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Profile");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            UserEditDialogController controller = loader.getController();
            controller.setService(usersService, displayedUser, dialogStage);

            dialogStage.showAndWait();

            updateUI();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        if (stage != null) {
            stage.close();
        }
    }
}