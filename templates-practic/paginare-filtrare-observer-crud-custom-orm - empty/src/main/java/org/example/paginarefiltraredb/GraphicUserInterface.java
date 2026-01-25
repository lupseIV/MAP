package org.example.paginarefiltraredb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Doctor;
import org.example.paginarefiltraredb.domain.entities.Pacient;
import org.example.paginarefiltraredb.domain.entities.Programare;
import org.example.paginarefiltraredb.domain.filters.GenericSqlFilter;
import org.example.paginarefiltraredb.domain.filters.implementations.ProgramareFilter;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.domain.validation.implementation.DoctorValidator;
import org.example.paginarefiltraredb.domain.validation.implementation.PacientValidator;
import org.example.paginarefiltraredb.domain.validation.implementation.ProgramareValidator;
import org.example.paginarefiltraredb.gui.implementations.DoctorController;
import org.example.paginarefiltraredb.gui.implementations.ProgramareController;
import org.example.paginarefiltraredb.repository.database.implementations.DoctorDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.PacientDbRepository;
import org.example.paginarefiltraredb.repository.database.implementations.ProgramareDbRepository;
import org.example.paginarefiltraredb.service.implementations.DoctorService;
import org.example.paginarefiltraredb.service.implementations.PacientService;
import org.example.paginarefiltraredb.service.implementations.ProgramareService;

import javax.print.Doc;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GraphicUserInterface extends Application implements ViewLoader{

    private ExecutorService sharedExecutor = Executors.newCachedThreadPool();
    //services
    private DoctorService doctorService;
    private ProgramareService programareService;
    private PacientService pacientService;

    @Override
    public void init() throws Exception {
       Validator<Doctor> validatorDoctor = new DoctorValidator();
       Validator<Programare> validatorProgramare = new ProgramareValidator();
       Validator<Pacient> validatorPacient = new PacientValidator();

       var doctorRepo = new DoctorDbRepository(validatorDoctor);
       var programareRepo = new ProgramareDbRepository(validatorProgramare);
       var pacientRepo = new PacientDbRepository(validatorPacient);

       doctorService = new DoctorService(doctorRepo, validatorDoctor,sharedExecutor);
       programareService = new ProgramareService(programareRepo, validatorProgramare, sharedExecutor);
         pacientService = new PacientService(pacientRepo, validatorPacient, sharedExecutor);
    }

    @Override
    public void start(Stage primaryStage) {
        openWindow(primaryStage, "Main Dashboard", 100, 100, this::configureProgramareController);

//        // --- WINDOW 2: The Client View (or another Dashboard) ---
//        openWindow(new Stage(), "Secondary Window", 900, 100);
        final int[] offset = {0};
        doctorService.findAll().thenAccept( doctors -> {
            doctors.forEach(doctor -> {Platform.runLater(() -> {

                Stage adminStage = new Stage();
                String title = "Admin Dashboard: " + doctor.getName();

                // Create a window specifically for this admin
                openWindow(adminStage, title, 100 + offset[0], 100 + offset[0],
                        (controller) -> configureDoctorController(controller, doctor)
                );

                offset[0] += 40;

            });}
            );

        }).exceptionally(e -> {;
            e.printStackTrace();
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
    private void configureDoctorController(MainController mainController, Doctor doctor) {
        mainController.addMenuOption("Manage Pacients", "client_view.fxml", controller -> {
            if (controller instanceof DoctorController c) {
                var filter = new ProgramareFilter();
                filter.setIdMedic(doctor.getId());
                filter.setType("Scheduled");
                c.setFilter(filter);
                c.setBaseService(programareService);

            }
        });
    }

    private void configureProgramareController(MainController mainController) {
        mainController.addMenuOption("Manage Appointments", "programare_view.fxml", controller -> {
            if (controller instanceof ProgramareController c) {
                c.setFilter(new GenericSqlFilter());
                c.setBaseService(pacientService);
                c.setExtraServices(doctorService, programareService);

            }
        });
    }

    @Override
    public void stop() throws Exception {
        System.out.println("App is shutting down...");


        super.stop();
    }
}
