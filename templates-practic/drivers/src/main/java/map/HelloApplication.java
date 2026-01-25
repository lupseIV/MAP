package map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import map.controller.AdminController;
import map.controller.DriverController;
import map.domain.validators.DriverValidator;
import map.domain.validators.OrderValidator;
import map.repository.DriverRepository;
import map.repository.OrderRepository;
import map.service.Service;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String url = "jdbc:postgresql://localhost:5432/practic";
        String user = "postgres";
        String password = "Injection17_DROP_TABLE";

        DriverRepository dr = new DriverRepository(url, user, password, new DriverValidator());
        OrderRepository or = new OrderRepository(url, user, password, new OrderValidator());

        Service service = new Service(dr, or);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        AdminController controller = fxmlLoader.getController();
        controller.setService(service);

        stage.setTitle("Admin");
        stage.setScene(scene);
        stage.show();

        service.getAllDrivers().forEach(d ->{
            Stage stag = new Stage();

            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("driver.fxml"));
            Scene sc = null;
            try {
                sc = new Scene(loader.load(), 320, 240);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DriverController controller2 = loader.getController();
            controller2.setDriver(d);
            controller2.setService(service);

            stag.setTitle("Driver: " + d.getName());
            stag.setScene(sc);
            stag.setWidth(800);
            stag.setHeight(400);
            stag.show();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}