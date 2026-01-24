package org.example.paginarefiltraredb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import org.example.paginarefiltraredb.gui.implementations.*;
import org.example.paginarefiltraredb.repository.database.implementations.ClientDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.ManagerDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.StaffDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.VipClientDbRepository;
import org.example.paginarefiltraredb.service.implementations.ClientService;
import org.example.paginarefiltraredb.service.implementations.ManagerService;
import org.example.paginarefiltraredb.service.implementations.StaffService;
import org.example.paginarefiltraredb.service.implementations.VipClientService;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
        openWindow(primaryStage, "Main Dashboard", 100, 100, this::configureMainController);

        // --- WINDOW 2: The Client View (or another Dashboard) ---
        openWindow(new Stage(), "Secondary Window", 900, 100, this::configureMainController);

        // --- WINDOWS FOR MANAGERS ---
        // Fetch managers async, then open a window for each on the UI thread
        managerService.findAll().thenAccept(managers -> {
            Platform.runLater(() -> {
                int offset = 0;
                for (Manager manager : managers) {
                    Stage managerStage = new Stage();
                    String title = "Manager Dashboard: " + manager.getName();

                    // Create a window specifically for this manager
                    openWindow(managerStage, title, 200 + offset, 200 + offset,
                            (controller) -> configureManagerController(controller, manager)
                    );

                    offset += 40; // Stagger windows slightly
                }
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> System.err.println("Failed to load managers: " + ex.getMessage()));
            return null;
        });
    }

    /**
     * Helper to create a stage using the ViewLoader logic.
     */
    private void openWindow(Stage stage, String title, double x, double y, Consumer<MainController> configurer) {
        StackPane root = new StackPane();

        loadView("main_view.fxml", controller -> {
            if (controller instanceof MainController mainController) {
                // Apply the specific configuration (Admin vs Manager)
                configurer.accept(mainController);
            }
        }, root);

        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.setX(x);
        stage.setY(y);
        stage.show();
    }

    // Helper to avoid duplicate code
    private void configureMainController(MainController mainController) {
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

    /**
     * Configuration for a specific Manager window (Restricted/Specific Views)
     */
    private void configureManagerController(MainController mainController, Manager manager) {
        // Add the specialized Manager View created previously
        mainController.addMenuOption("My VIP Team", "manger_vip_listview.fxml", controller -> {
            if (controller instanceof ManagerVipListController c) {
                c.setBaseService(vipClientService);
                // Ideally, you would filter the service/view by this manager's ID here
                // e.g., c.setFilterManager(manager);
            }
        });

        mainController.addMenuOption("My VIP Team Paged", "manager_vip_paging_listview.fxml", controller -> {
            if (controller instanceof ManagerVipPagingController c) {
                c.setBaseService(vipClientService);
                // Ideally, you would filter the service/view by this manager's ID here
                // e.g., c.setFilterManager(manager);
            }
        });
        // Example: Add other manager-specific views here
        mainController.addMenuOption("My VIP Team Paged Table", "manager_vip_table_view_paging.fxml", controller -> {
            if (controller instanceof ManagerVipTableViewPagingController c) {
                c.setBaseService(vipClientService);
                // Ideally, you would filter the service/view by this manager's ID here
                // e.g., c.setFilterManager(manager);
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

        super.stop();
    }
}
