package org.ui.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.domain.dtos.guiDTOS.UserGuiDTO;
import org.domain.dtos.PersonData;
import org.domain.dtos.DuckData;
import org.domain.users.User;
import org.domain.users.UserFactory;
import org.domain.users.duck.Duck;
import org.domain.users.person.Person;
import org.repository.util.paging.Pageable;
import org.service.UsersService;
import org.utils.enums.DuckTypes;
import org.utils.enums.PersonTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserController {

    private UsersService usersService;
    private UserFactory userFactory = new UserFactory();

    @FXML private TableView<UserGuiDTO> tableView;
    @FXML private TableColumn<UserGuiDTO, Long> idCol;
    @FXML private TableColumn<UserGuiDTO, String> usernameCol;
    @FXML private TableColumn<UserGuiDTO, String> emailCol;
    @FXML private TableColumn<UserGuiDTO, Integer> nrOfFriendsCol;
    @FXML private TableColumn<UserGuiDTO, String> userTypeCol;
    @FXML private ComboBox<String> comboBox;

    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button updateButton;

    @FXML private Button buttonNext;
    @FXML private Button buttonPrevious;
    @FXML private Label labelPage;

    private int pageSize = 5;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    private final ObservableList<UserGuiDTO> data = FXCollections.observableArrayList();
    private final ObservableList<UserGuiDTO> masterData = FXCollections.observableArrayList();
    private final ObservableList<String> userTypes = FXCollections.observableArrayList();

    public void setUsersService(UsersService usersService) {
        this.usersService = Objects.requireNonNull(usersService);
        Platform.runLater(this::initTable);
    }

    @FXML
    private void initTable() {
        masterData.clear();
        masterData.addAll(usersService.getGuiUsers());
        List<UserGuiDTO> page = usersService.getGuiUsersPage(new Pageable(currentPage, pageSize));
        
        int maxPage = (int) Math.ceil((double) masterData.size() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = usersService.getGuiUsersPage(new Pageable(currentPage, pageSize));
        }
        totalNumberOfElements = masterData.size();
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);

        data.setAll(page);
        labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        nrOfFriendsCol.setCellValueFactory(new PropertyValueFactory<>("nrOfFriends"));
        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));

        tableView.setItems(data);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        idCol.setMinWidth(60);
        usernameCol.setMinWidth(120);
        emailCol.setMinWidth(180);
        nrOfFriendsCol.setMinWidth(100);
        userTypeCol.setMinWidth(80);

        HBox.setHgrow(comboBox, Priority.ALWAYS);
        comboBox.setMaxWidth(Double.MAX_VALUE);

        initComboBox();
    }

    @FXML
    private void initComboBox() {
        userTypes.clear();
        userTypes.add("All");
        userTypes.add("Duck");
        userTypes.add("Person");
        comboBox.setItems(userTypes);
        comboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void filterUsers(ActionEvent actionEvent) {
        String selected = comboBox.getSelectionModel().getSelectedItem();
        if (selected == null || "All".equals(selected)) {
            data.setAll(masterData);
            return;
        }

        ObservableList<UserGuiDTO> filtered = FXCollections.observableArrayList();
        for (UserGuiDTO u : masterData) {
            if (selected.equals(u.getUserType())) {
                filtered.add(u);
            }
        }
        data.setAll(filtered);
    }

    @FXML
    private void onAdd() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Choose user type and enter details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Person", "Duck");
        typeCombo.getSelectionModel().selectFirst();

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField emailField = new TextField();

        // Person fields
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField occupationField = new TextField();
        DatePicker dobPicker = new DatePicker();
        TextField empathyField = new TextField();

        // Duck fields
        ComboBox<String> duckTypeCombo = new ComboBox<>();
        duckTypeCombo.getItems().addAll("FLYING", "SWIMMING", "FLYING_AND_SWIMMING");
        duckTypeCombo.getSelectionModel().selectFirst();
        TextField speedField = new TextField();
        TextField resistanceField = new TextField();

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeCombo, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);

        // Person-specific fields (initially visible)
        Label firstNameLabel = new Label("First Name:");
        Label lastNameLabel = new Label("Last Name:");
        Label occupationLabel = new Label("Occupation:");
        Label dobLabel = new Label("Date of Birth:");
        Label empathyLabel = new Label("Empathy (0-10):");

        grid.add(firstNameLabel, 0, 4);
        grid.add(firstNameField, 1, 4);
        grid.add(lastNameLabel, 0, 5);
        grid.add(lastNameField, 1, 5);
        grid.add(occupationLabel, 0, 6);
        grid.add(occupationField, 1, 6);
        grid.add(dobLabel, 0, 7);
        grid.add(dobPicker, 1, 7);
        grid.add(empathyLabel, 0, 8);
        grid.add(empathyField, 1, 8);

        // Duck-specific fields (initially hidden)
        Label duckTypeLabel = new Label("Duck Type:");
        Label speedLabel = new Label("Speed:");
        Label resistanceLabel = new Label("Resistance:");

        grid.add(duckTypeLabel, 2, 4);
        grid.add(duckTypeCombo, 3, 4);
        grid.add(speedLabel, 2, 5);
        grid.add(speedField, 3, 5);
        grid.add(resistanceLabel, 2, 6);
        grid.add(resistanceField, 3, 6);

        // Initially hide duck fields
        duckTypeLabel.setVisible(false);
        duckTypeCombo.setVisible(false);
        speedLabel.setVisible(false);
        speedField.setVisible(false);
        resistanceLabel.setVisible(false);
        resistanceField.setVisible(false);

        typeCombo.setOnAction(e -> {
            boolean isPerson = "Person".equals(typeCombo.getValue());
            firstNameLabel.setVisible(isPerson);
            firstNameField.setVisible(isPerson);
            lastNameLabel.setVisible(isPerson);
            lastNameField.setVisible(isPerson);
            occupationLabel.setVisible(isPerson);
            occupationField.setVisible(isPerson);
            dobLabel.setVisible(isPerson);
            dobPicker.setVisible(isPerson);
            empathyLabel.setVisible(isPerson);
            empathyField.setVisible(isPerson);

            duckTypeLabel.setVisible(!isPerson);
            duckTypeCombo.setVisible(!isPerson);
            speedLabel.setVisible(!isPerson);
            speedField.setVisible(!isPerson);
            resistanceLabel.setVisible(!isPerson);
            resistanceField.setVisible(!isPerson);
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String type = typeCombo.getValue();
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    String email = emailField.getText();

                    if ("Person".equals(type)) {
                        String firstName = firstNameField.getText();
                        String lastName = lastNameField.getText();
                        String occupation = occupationField.getText();
                        LocalDate dob = dobPicker.getValue();
                        String empathy = empathyField.getText();

                        PersonData personData = new PersonData(
                                List.of(username, password, email, firstName, lastName, occupation, 
                                       dob.toString(), empathy)
                        );
                        return userFactory.createUser(PersonTypes.DEFAULT, personData);
                    } else {
                        String duckType = duckTypeCombo.getValue();
                        String speed = speedField.getText();
                        String resistance = resistanceField.getText();

                        DuckData duckData = new DuckData(
                                List.of(username, password, email, speed, resistance)
                        );
                        DuckTypes dt = DuckTypes.valueOf(duckType);
                        return userFactory.createUser(dt, duckData);
                    }
                } catch (Exception ex) {
                    showError("Invalid input: " + ex.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            try {
                usersService.save(user);
                initTable();
                showInfo("User added successfully!");
            } catch (Exception ex) {
                showError("Error adding user: " + ex.getMessage());
            }
        });
    }

    @FXML
    private void onUpdate() {
        UserGuiDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a user to update.");
            return;
        }

        User user = usersService.findOne(selected.getId());
        if (user == null) {
            showError("User not found.");
            return;
        }

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Update User");
        dialog.setHeaderText("Update user details");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField usernameField = new TextField(user.getUsername());
        PasswordField passwordField = new PasswordField();
        passwordField.setText(user.getPassword());
        TextField emailField = new TextField(user.getEmail());

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        if (user instanceof Person person) {
            TextField firstNameField = new TextField(person.getFirstName());
            TextField lastNameField = new TextField(person.getLastName());
            TextField occupationField = new TextField(person.getOccupation());
            DatePicker dobPicker = new DatePicker(person.getDateOfBirth());
            TextField empathyField = new TextField(String.valueOf(person.getEmpathyLevel()));

            grid.add(new Label("First Name:"), 0, 3);
            grid.add(firstNameField, 1, 3);
            grid.add(new Label("Last Name:"), 0, 4);
            grid.add(lastNameField, 1, 4);
            grid.add(new Label("Occupation:"), 0, 5);
            grid.add(occupationField, 1, 5);
            grid.add(new Label("Date of Birth:"), 0, 6);
            grid.add(dobPicker, 1, 6);
            grid.add(new Label("Empathy (0-10):"), 0, 7);
            grid.add(empathyField, 1, 7);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    person.setUsername(usernameField.getText());
                    person.setPassword(passwordField.getText());
                    person.setEmail(emailField.getText());
                    person.setFirstName(firstNameField.getText());
                    person.setLastName(lastNameField.getText());
                    person.setOccupation(occupationField.getText());
                    person.setDateOfBirth(dobPicker.getValue());
                    person.setEmpathyLevel(Double.parseDouble(empathyField.getText()));
                    return person;
                }
                return null;
            });
        } else if (user instanceof Duck duck) {
            ComboBox<String> duckTypeCombo = new ComboBox<>();
            duckTypeCombo.getItems().addAll("FLYING", "SWIMMING", "FLYING_AND_SWIMMING");
            duckTypeCombo.setValue(duck.getDuckType().name());
            TextField speedField = new TextField(String.valueOf(duck.getSpeed()));
            TextField resistanceField = new TextField(String.valueOf(duck.getRezistance()));

            grid.add(new Label("Duck Type:"), 0, 3);
            grid.add(duckTypeCombo, 1, 3);
            grid.add(new Label("Speed:"), 0, 4);
            grid.add(speedField, 1, 4);
            grid.add(new Label("Resistance:"), 0, 5);
            grid.add(resistanceField, 1, 5);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    duck.setUsername(usernameField.getText());
                    duck.setPassword(passwordField.getText());
                    duck.setEmail(emailField.getText());
                    duck.setDuckType(DuckTypes.valueOf(duckTypeCombo.getValue()));
                    duck.setSpeed(Double.parseDouble(speedField.getText()));
                    duck.setRezistance(Double.parseDouble(resistanceField.getText()));
                    return duck;
                }
                return null;
            });
        }

        dialog.getDialogPane().setContent(grid);

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(updatedUser -> {
            try {
                usersService.update(updatedUser);
                initTable();
                showInfo("User updated successfully!");
            } catch (Exception ex) {
                showError("Error updating user: " + ex.getMessage());
            }
        });
    }

    @FXML
    private void onDelete() {
        UserGuiDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a user to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete User");
        confirm.setContentText("Are you sure you want to delete user: " + selected.getUsername() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                usersService.delete(selected.getId());
                initTable();
                showInfo("User deleted successfully!");
            } catch (Exception ex) {
                showError("Error deleting user: " + ex.getMessage());
            }
        }
    }

    public void onNextPage(ActionEvent actionEvent) {
        if ((currentPage + 1) * pageSize < totalNumberOfElements) {
            currentPage++;
            initTable();
        }
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        if (currentPage > 0) {
            currentPage--;
            initTable();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
