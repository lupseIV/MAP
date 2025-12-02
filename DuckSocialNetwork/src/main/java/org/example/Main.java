package org.example;

import database.DatabaseConnection;
import javafx.application.Application;
import org.domain.validators.*;
import org.repository.EntityRepository;
import org.repository.db.*;

import org.service.*;
import org.service.utils.LongIdGenerator;
import org.ui.ConsoleUserInterface;
import org.ui.gui.GraphicUserInterface;
import org.utils.Constants;

import java.sql.SQLException;
import java.util.Objects;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        //validators
        var personValidator = new PersonValidator(Constants.EMAIL_REGEX);
        var duckValidator = new DuckValidator(Constants.EMAIL_REGEX);
        var friendshipValidator = new FriendshipValidator();
        var flockValidator = new FlockValidator();
        var raceEventValidator = new RaceEventValidator();

        try {
            // Initialize database schema
            DatabaseConnection.initDatabaseSchema();
            System.out.println("Database schema initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            return;
        }
        //repos
        DuckDatabaseRepository duckRepo = null;
        PersonDatabaseRepository personRepo = null;
        FriendshipDatabaseRepository friendshipRepo = null;
        FlockDatabaseRepository flockRepo = null;
        RaceEventDatabaseRepository raceEventRepo = null;
        try {
            personRepo = new PersonDatabaseRepository(personValidator);
            duckRepo = new DuckDatabaseRepository(duckValidator);

            friendshipRepo = new FriendshipDatabaseRepository(friendshipValidator,
                    duckRepo, personRepo);

            flockRepo = new FlockDatabaseRepository(flockValidator, duckRepo);
            raceEventRepo = new RaceEventDatabaseRepository(raceEventValidator, duckRepo);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

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
        var duckService = new DucksService(duckValidator, duckRepo, usersIdGenerator);
        var personService = new PersonsService(personValidator, personRepo, usersIdGenerator);
        var friendshipService = new FriendshipService(friendshipValidator, friendshipRepo, friendshipIdGenerator);
        var flockService = new FlockService(flockValidator, flockRepo, flockIdGenerator, duckService);
        var raceEventService = new RaceEventService(raceEventValidator, raceEventRepo, eventIdGenerator, duckService);

        var usersService = new UsersService(duckService, personService,friendshipService);


        friendshipService.setUsersService(usersService);
        duckService.setFlockService(flockService);

        //ui
//        var app = new ConsoleUserInterface(usersService, friendshipService, flockService, raceEventService);
//        app.run();

        //gui
        GraphicUserInterface.setDucksService(duckService);
        GraphicUserInterface.setUsersService(usersService);
        GraphicUserInterface.setFriendshipService(friendshipService);
         var app = new GraphicUserInterface();
         app.run();
        DatabaseConnection.closeConnection();
    }


}

