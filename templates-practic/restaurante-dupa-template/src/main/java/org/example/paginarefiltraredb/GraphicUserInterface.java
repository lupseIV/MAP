package org.example.paginarefiltraredb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.MenuItem;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.entities.RestaurantTable;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.domain.validation.implementation.MenuItemValidator;
import org.example.paginarefiltraredb.domain.validation.implementation.OrderValidator;
import org.example.paginarefiltraredb.domain.validation.implementation.RestaurantTableValidator;
import org.example.paginarefiltraredb.gui.implementations.StaffController;
import org.example.paginarefiltraredb.gui.implementations.TableController;
import org.example.paginarefiltraredb.repository.database.implementations.MenuItemDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.OrderDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.RestaurantTableDbRepository;
import org.example.paginarefiltraredb.service.implementations.MenuItemService;
import org.example.paginarefiltraredb.service.implementations.OrderService;
import org.example.paginarefiltraredb.service.implementations.RestaurantTableService;

import java.io.IOException;
import java.util.List;

public class GraphicUserInterface extends Application {

    // Services
    private RestaurantTableService tableService;
    private MenuItemService menuItemService;
    private OrderService orderService;

    @Override
    public void init() throws Exception {
        // Initialize validators
        Validator<RestaurantTable> tableValidator = new RestaurantTableValidator();
        Validator<MenuItem> menuItemValidator = new MenuItemValidator();
        Validator<Order> orderValidator = new OrderValidator();

        // Initialize repositories
        RestaurantTableDbRepository tableRepo = new RestaurantTableDbRepository(tableValidator);
        MenuItemDbRepository menuItemRepo = new MenuItemDbRepository(menuItemValidator);
        OrderDbRepository orderRepo = new OrderDbRepository(orderValidator, menuItemRepo);

        // Initialize services
        tableService = new RestaurantTableService(tableRepo, tableValidator);
        menuItemService = new MenuItemService(menuItemRepo, menuItemValidator);
        orderService = new OrderService(orderRepo, orderValidator);
    }

    @Override
    public void start(Stage primaryStage) {
        // Open Staff window
        openStaffWindow();

        // Open a window for each table
        List<RestaurantTable> tables = tableService.getAllTables();
        int xOffset = 520;
        int yOffset = 50;

        for (int i = 0; i < tables.size(); i++) {
            RestaurantTable table = tables.get(i);
            int x = xOffset + (i % 2) * 520;
            int y = yOffset + (i / 2) * 350;
            openTableWindow(table.getId(), x, y);
        }
    }

    private void openStaffWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("staff_view.fxml"));
            Parent root = loader.load();

            StaffController controller = loader.getController();
            controller.setService(orderService);

            Stage stage = new Stage();
            stage.setTitle("Staff");
            stage.setScene(new Scene(root));
            stage.setX(10);
            stage.setY(50);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openTableWindow(Integer tableId, double x, double y) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("table_view.fxml"));
            Parent root = loader.load();

            TableController controller = loader.getController();
            controller.setTableId(tableId);
            controller.setServices(menuItemService, orderService);

            Stage stage = new Stage();
            stage.setTitle("Table " + tableId);
            stage.setScene(new Scene(root));
            stage.setX(x);
            stage.setY(y);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("App is shutting down...");
        DatabaseConnection.closeConnection();
        super.stop();
    }
}
