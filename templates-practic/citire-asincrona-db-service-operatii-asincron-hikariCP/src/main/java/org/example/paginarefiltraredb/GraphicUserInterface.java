package org.example.paginarefiltraredb;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Client;
import org.example.paginarefiltraredb.domain.entities.Manager;
import org.example.paginarefiltraredb.domain.entities.Staff;
import org.example.paginarefiltraredb.domain.entities.VipClient;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.domain.validation.implementation.ClientValidator;
import org.example.paginarefiltraredb.domain.validation.implementation.ManagerValidator;
import org.example.paginarefiltraredb.domain.validation.implementation.StaffValidator;
import org.example.paginarefiltraredb.domain.validation.implementation.VipClientValidator;
import org.example.paginarefiltraredb.gui.implementations.ClientControllerPagingTableView;
import org.example.paginarefiltraredb.gui.implementations.VipClientControllerTableView;
import org.example.paginarefiltraredb.repository.database.implementations.ClientDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.ManagerDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.StaffDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.VipClientDbRepository;
import org.example.paginarefiltraredb.service.implementations.ClientService;
import org.example.paginarefiltraredb.service.implementations.ManagerService;
import org.example.paginarefiltraredb.service.implementations.StaffService;
import org.example.paginarefiltraredb.service.implementations.VipClientService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GraphicUserInterface extends Application implements ViewLoader{
    private final ExecutorService sharedExecutor = Executors.newCachedThreadPool();
    //services
    private ClientService clientService;
    private  ManagerService managerService;
    private  StaffService staffService ;
    private  VipClientService  vipClientService;

    @Override
    public void init() throws Exception {
        Validator<Client> clientValidator = new ClientValidator();
        Validator<Staff> staffValidator = new StaffValidator();
        Validator<VipClient> vipClientValidator = new VipClientValidator();
        Validator<Manager> managerValidator = new ManagerValidator();

        var clientRepo = new ClientDbRepository(clientValidator);
        var staffRepo = new StaffDbRepository(staffValidator);
        var vipClientRepo = new VipClientDbRepository(vipClientValidator);
        var managerRepo = new ManagerDbRepository(managerValidator);

        clientService = new ClientService(clientRepo, clientValidator,sharedExecutor);
        managerService = new ManagerService(managerRepo, managerValidator,sharedExecutor);
        staffService = new StaffService(staffRepo, staffValidator,sharedExecutor);
        vipClientService = new VipClientService(vipClientRepo, vipClientValidator,sharedExecutor);
    }

    @Override
    public void start(Stage primaryStage) {
        // --- WINDOW 1: The Main Dashboard ---
        openWindow(primaryStage, "Main Dashboard", 100, 100);

        // --- WINDOW 2: The Client View (or another Dashboard) ---
        openWindow(new Stage(), "Secondary Window", 900, 100);
    }

    /**
     * Helper to create a stage using the ViewLoader logic.
     */
    private void openWindow(Stage stage, String title, double x, double y) {
        // 1. Create a container (Root) for the scene
        StackPane root = new StackPane();

        // 2. Use ViewLoader to load the FXML and inject it into the root
        // We assume 'main_view.fxml' is the file you want
        loadView("main_view.fxml", controller -> {
            // 3. Configure the controller (inject services, add menu options)
            if (controller instanceof MainController mainController) {
                configureController(mainController);
            }
        }, root);

        // 4. Set up the Stage
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.setX(x);
        stage.setY(y);
        stage.show();
    }

    // Helper to avoid duplicate code
    private void configureController(MainController mainController) {
        mainController.addMenuOption("Manage Clients", "client_view.fxml", controller -> {
            if (controller instanceof ClientControllerPagingTableView c) {
                c.setService(clientService);
            }
        });

        mainController.addMenuOption("Manage VipClients", "vip_view.fxml", controller -> {
            if (controller instanceof VipClientControllerTableView c) {
                c.setService(vipClientService);
                c.setManagerService(managerService);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Shutting down application...");

        // Shutdown the executor
        if (sharedExecutor != null && !sharedExecutor.isShutdown()) {
            sharedExecutor.shutdown();
            try {
                // Wait for tasks to complete (optional but recommended)
                if (!sharedExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    sharedExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                sharedExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        DatabaseConnection.closePool();

        super.stop();
    }
}
