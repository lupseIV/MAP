package org.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.domain.Observer;
import org.domain.users.User;
import org.domain.users.relationships.notifications.FriendRequestNotification;
import org.service.*;
import org.utils.enums.NotificationStatus;
import org.utils.enums.NotificationType;

import java.io.IOException;

public class MainController implements ViewController, Observer<FriendRequestNotification> {

    @FXML private StackPane contentArea;
    @FXML private Button notificationButton;

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
        initNotification();
    }

    private void initNotification() {
        if(notificationService.findAll(authService.getCurrentUser()).stream().anyMatch(
                notification -> notification.getStatus() == NotificationStatus.NEW
        )) {
            update(null);
        }
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
                ((SocialController) controller).setService(friendshipService, usersService,authService);
            }
        }, contentArea);
    }

    @FXML
    public void handleShowMessageView(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatPartnerView.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Select Users to Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contentArea.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ChatPartnerController contr = loader.getController();
            contr.setServices(usersService, dialogStage, authService.getCurrentUser());

            dialogStage.showAndWait();

            if (contr.getChatPartner() != null) {
                loadView("ChatView.fxml", controller ->  {
                    if (controller instanceof ChatController) {
                        ((ChatController) controller).setServices(messageService,authService, contr.getChatPartner());
                    }
                }, contentArea);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleShowNotificationsView(){
        notificationButton.setStyle("-fx-font-weight: regular;");
        loadView("NotificationsView.fxml", controller -> {
            if (controller instanceof NotificationController) {
                ((NotificationController) controller).setServices(notificationService, authService);
            }
        }, contentArea);
    }

    @FXML
    public void handleLogout() {
        try {
            authService.logout();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            VBox root = loader.load();

            LoginController controller = loader.getController();
            controller.setServices(ducksService, personsService, friendshipService,
                    usersService, new AuthService(usersService), messageService, notificationService);

            Stage stage = (Stage) contentArea.getScene().getWindow();
            controller.setStage(stage);

            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("Duck Social Network - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(FriendRequestNotification event) {
        notificationButton.setStyle("-fx-font-weight: bold;");
    }
}