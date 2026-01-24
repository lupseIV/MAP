package org.ui.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.domain.Observer;
import org.domain.observer_events.ObserverEvent;
import org.domain.users.User;
import org.service.*;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.UserTypes;

import java.io.IOException;

public class MainController implements ViewController, Observer<ObserverEvent> {

    @FXML private StackPane contentArea;
    @FXML private Button notificationButton;

    private DucksService ducksService;
    private PersonsService personsService;
    private FriendshipService friendshipService;
    private UsersService usersService;
    private MessageService messageService;
    private AuthService authService;
    private NotificationService notificationService;
    private RaceEventService raceEventService;

    public void setServices(DucksService ds, PersonsService ps, FriendshipService fs, UsersService us,
                            AuthService as, MessageService ms, NotificationService ns, RaceEventService res) {
        this.raceEventService = res;
        this.ducksService = ds;
        this.personsService = ps;
        this.friendshipService = fs;
        this.usersService = us;
        this.authService = as;
        this.messageService = ms;
        this.notificationService = ns;

        notificationService.addObserver(this);

        initNotification();
    }

    private void initNotification() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            notificationService.findAll(currentUser)
                    .thenAccept(notifications -> {
                        boolean hasNew = notifications.stream()
                                .anyMatch(n -> n.getStatus() == NotificationStatus.NEW);
                        if (hasNew) {
                            Platform.runLater(() ->
                                    notificationButton.setStyle("-fx-font-weight: bold;")
                            );
                        }
                    });
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
                ((SocialController) controller).setService(friendshipService, usersService,authService, notificationService);
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
    public void handleShowEventsView(){
        if(authService.getCurrentUser().getUserType() == UserTypes.PERSON) {
            loadView("PersonEventPage.fxml", controller -> {
                if (controller instanceof PersonEventController) {
                    ((PersonEventController) controller).setServices(raceEventService, authService);
                }
            }, contentArea);
        } else {
            loadView("DuckEventPage.fxml", controller -> {
                if (controller instanceof DuckEventController) {
                    ((DuckEventController) controller).setServices(raceEventService, authService);
                }
            }, contentArea);
        }
    }

    @FXML
    public void handleShowProfileView(){
        loadView("UserProfileView.fxml", controller -> {
            if (controller instanceof UserProfileController) {
                ((UserProfileController) controller).setServices(friendshipService,usersService,  authService.getCurrentUser(),true);
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
                    usersService, new AuthService(usersService), messageService, notificationService, raceEventService);

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
    public void update(ObserverEvent event) {
            if(!event.getUser().equals(authService.getCurrentUser()) && event.getStatus() == NotificationStatus.NEW) {
            notificationButton.setStyle("-fx-font-weight: bold;");
        } else if (event.getStatus() == NotificationStatus.READ){
            notificationButton.setStyle("-fx-font-weight: regular;");
        }
    }
}