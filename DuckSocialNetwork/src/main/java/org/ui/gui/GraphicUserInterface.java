package org.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.domain.users.User;
import org.repository.EntityRepository;
import org.repository.db.*;
import org.service.*;
import org.domain.validators.*;
import database.DatabaseConnection; // Assuming this exists based on imports
import org.service.utils.LongIdGenerator;
import org.utils.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class GraphicUserInterface extends Application {

    private DucksService ducksService;
    private PersonsService personsService;
    private FriendshipService friendshipService;
    private FlockService flockService;
    private RaceEventService raceEventService;
    private UsersService usersService;

    @Override
    public void init() throws Exception {
        var personValidator = new PersonValidator(Constants.EMAIL_REGEX);
        var duckValidator = new DuckValidator(Constants.EMAIL_REGEX);
        var friendshipValidator = new FriendshipValidator();
        var flockValidator = new FlockValidator();
        var raceEventValidator = new RaceEventValidator();

        DatabaseConnection.initDatabaseSchema();


        DuckDatabaseRepository duckRepo = null;
        PersonDatabaseRepository personRepo = null;
        FriendshipDatabaseRepository friendshipRepo = null;
        FlockDatabaseRepository flockRepo = null;
        RaceEventDatabaseRepository raceEventRepo = null;

        personRepo = new PersonDatabaseRepository(personValidator);
        duckRepo = new DuckDatabaseRepository(duckValidator);

        friendshipRepo = new FriendshipDatabaseRepository(friendshipValidator,
                duckRepo, personRepo);

        flockRepo = new FlockDatabaseRepository(flockValidator, duckRepo);
        raceEventRepo = new RaceEventDatabaseRepository(raceEventValidator, duckRepo);


        Long maxUsersId = Math.max(
                EntityRepository.getMaxId(duckRepo.findAll()),
                EntityRepository.getMaxId(personRepo.findAll())
        );

        Long maxFriendshipId = EntityRepository.getMaxId(friendshipRepo.findAll());
        Long maxFlockId = EntityRepository.getMaxId(flockRepo.findAll());
        Long  maxEventId = EntityRepository.getMaxId(raceEventRepo.findAll());

        //id generator
        var usersIdGenerator = new LongIdGenerator(Objects.requireNonNullElse(maxUsersId, 0L) + 1);
        var friendshipIdGenerator = new LongIdGenerator(Objects.requireNonNullElse(maxFriendshipId, 0L) + 1);
        var flockIdGenerator = new LongIdGenerator(Objects.requireNonNullElse(maxFlockId, 0L) + 1);
        var eventIdGenerator = new LongIdGenerator(Objects.requireNonNullElse(maxEventId, 0L) + 1);

        //service
        ducksService = new DucksService(duckValidator, duckRepo, usersIdGenerator);
        personsService = new PersonsService(personValidator, personRepo, usersIdGenerator);
        friendshipService = new FriendshipService(friendshipValidator, friendshipRepo, friendshipIdGenerator);
        flockService = new FlockService(flockValidator, flockRepo, flockIdGenerator, ducksService);
        raceEventService = new RaceEventService(raceEventValidator, raceEventRepo, eventIdGenerator, ducksService);

        usersService = new UsersService(ducksService, personsService,friendshipService);


        friendshipService.setUsersService(usersService);
        ducksService.setFlockService(flockService);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
        VBox root = loader.load();

        LoginController controller = loader.getController();
        controller.setServices(ducksService, personsService, friendshipService, usersService);


        Scene scene = new Scene(root, 1000, 700);
        controller.setStage(primaryStage);
        primaryStage.setTitle("Duck Social Network Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}