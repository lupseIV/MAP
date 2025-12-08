package org.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.domain.users.User;
import org.service.*;

import java.io.IOException;

public class MainController implements ViewController{

    @FXML private StackPane contentArea;

    private DucksService ducksService;
    private PersonsService personsService;
    private FriendshipService friendshipService;
    private UsersService usersService;
    private MessageService messageService;
    private AuthService authService;
    private NotificationService notificationService;

    public void setServices(DucksService ds, PersonsService ps, FriendshipService fs, UsersService us,
                            AuthService as, MessageService ms, NotificationService ns) {
        this.ducksService = ds;
        this.personsService = ps;
        this.friendshipService = fs;
        this.usersService = us;
        this.authService = as;
        this.messageService = ms;
        this.notificationService = ns;
    }

    @FXML public void handleShowUsersView(){
        loadView("UsersView.fxml", controller -> {
            if (controller instanceof UsersController) {
                ((UsersController) controller).setService(ducksService, personsService,usersService,contentArea);
            }
        }, contentArea);
    }

    @FXML
    public void handleShowFriendships() {

        loadView("SocialView.fxml", controller -> {
            if (controller instanceof SocialController) {
                ((SocialController) controller).setService(friendshipService, usersService);
            }
        }, contentArea);
    }

    @FXML
    public void handleShowMessageView(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserSelectionDialog.fxml"));
            Parent page = loader.load();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Select User");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(contentArea.getScene().getWindow());
            javafx.scene.Scene scene = new javafx.scene.Scene(page);
            dialogStage.setScene(scene);

            UserSelectionDialogController controller = loader.getController();
            controller.setServices(usersService, authService, dialogStage);

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                User selectedUser = controller.getSelectedUser();
                loadView("ChatView.fxml", chatController -> {
                    if (chatController instanceof ChatController) {
                        ((ChatController) chatController).setServices(messageService, authService, selectedUser);
                    }
                }, contentArea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}