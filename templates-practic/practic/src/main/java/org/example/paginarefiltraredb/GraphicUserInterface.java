package org.example.paginarefiltraredb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.User;
import org.example.paginarefiltraredb.domain.entities.UserRole;
import org.example.paginarefiltraredb.domain.validation.CarValidator;
import org.example.paginarefiltraredb.domain.validation.UserValidator;
import org.example.paginarefiltraredb.gui.implementations.AdminController;
import org.example.paginarefiltraredb.gui.implementations.DealerController;
import org.example.paginarefiltraredb.gui.implementations.LoginController;
import org.example.paginarefiltraredb.repository.database.implementations.CarDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.UserDbRepository;
import org.example.paginarefiltraredb.service.CarService;
import org.example.paginarefiltraredb.service.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GraphicUserInterface extends Application {

    private UserService userService;
    private CarService carService;

    private final List<Stage> activeStages = new ArrayList<>();

    @Override
    public void init() throws Exception {
        try {
            DatabaseConnection.initDatabaseSchema();

            UserDbRepository userRepository = new UserDbRepository(new UserValidator());
            CarDbRepository carRepository = new CarDbRepository(new CarValidator());

            userService = new UserService(userRepository, new UserValidator());
            carService = new CarService(carRepository, new CarValidator());

        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        showLoginWindow(primaryStage);
    }


    private void showLoginWindow(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login_view.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setUserService(userService);
            controller.setOnLoginSuccess(user -> {
                stage.close();
                openMainWindow(user);
            });

            stage.setScene(new Scene(root, 400, 450));
            stage.setTitle("Dacia - Login");
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openMainWindow(User user) {
        Stage stage = new Stage();
        activeStages.add(stage);

        String fxmlFile;
        String title;

        if (user.getRole() == UserRole.ADMIN) {
            fxmlFile = "admin_view.fxml";
            title = "Admin Dashboard " + user.getUsername();
        } else {
            fxmlFile = "dealer_view.fxml";
            title = "Dealer Dashboard " + user.getUsername();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof AdminController adminController) {
                adminController.setCarService(carService);
                adminController.setCurrentUser(user);
            } else if (controller instanceof DealerController dealerController) {
                dealerController.setCarService(carService);
                dealerController.setCurrentUser(user);
            }

            stage.setScene(new Scene(root, 700, 500));
            stage.setTitle(title);

            int windowIndex = activeStages.size() - 1;
            stage.setX(100 + 2*windowIndex);
            stage.setY(100 + 2*windowIndex);

            stage.setOnCloseRequest(e -> {
                activeStages.remove(stage);
                if (activeStages.isEmpty()) {
                    showLoginWindow(new Stage());
                }
            });

            stage.show();

            showLoginWindow(new Stage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("App is shutting down...");
        DatabaseConnection.registerShutdownHook();

        super.stop();
    }
}
