package org.example.paginarefiltraredb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.paginarefiltraredb.domain.entities.Driver;
import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.domain.validation.implementations.DriverValidator;
import org.example.paginarefiltraredb.domain.validation.implementations.OrderValidator;
import org.example.paginarefiltraredb.gui.implementations.DriverController;
import org.example.paginarefiltraredb.repository.Repository;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.DriverDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.OrderDbRepository;
import org.example.paginarefiltraredb.service.implementations.DriverService;
import org.example.paginarefiltraredb.service.implementations.OrderService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GraphicUserInterface extends Application implements ViewLoader{
    private final ExecutorService sharedExecutor = Executors.newCachedThreadPool();
    //services

    private DriverService driverService;
    private OrderService orderService;
    
    @Override
    public void init() throws Exception {
        Validator<Order> orderValidator = new OrderValidator();
        Validator<Driver> driverValidator = new DriverValidator();

        EntityDbRepository<Integer, Driver> repoDriver = new DriverDbRepository(driverValidator);
        EntityDbRepository<Integer, Order> repoOrder = new OrderDbRepository(orderValidator);
        
        driverService = new DriverService(repoDriver, driverValidator, sharedExecutor);
        orderService = new OrderService(repoOrder, orderValidator, sharedExecutor);
     }

    @Override
    public void start(Stage primaryStage) {
        // --- WINDOW 1: The Main Dashboard ---
        openWindow(primaryStage, "Dispecerat", 100, 100, this::configureMainController);
        
        // --- WINDOWS FOR DRIVERS ---
        driverService.findAll().thenAccept(drivers -> {
            Platform.runLater(() -> {
                int offset = 0;
                for (Driver driver : drivers) {
                    Stage driverStage = new Stage();
                    String title = "Driver Dashboard: " + driver.getName();

                    // Create a window specifically for this manager
                    openWindow(driverStage, title, 200 + offset, 200 + offset,
                            (controller) -> configureDriverController(controller, driver)
                    );

                    offset += 40; // Stagger windows slightly
                }
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> System.err.println("Failed to load drivers: " + ex.getMessage()));
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

    }

    /**
     * Configuration for a specific Manager window (Restricted/Specific Views)
     */
    private void configureDriverController(MainController mainController, Driver currentDriver){
        mainController.addMenuOption("Manage Orders", "driver_list_view.fxml", controller -> {
            if (controller instanceof DriverController c) {
                c.setBaseService(orderService);
                c.setCurrentDriver(currentDriver.getId());
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
