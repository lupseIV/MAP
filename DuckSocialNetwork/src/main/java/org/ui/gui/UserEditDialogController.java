package org.ui.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.domain.users.User;
import org.domain.users.duck.Duck;
import org.domain.users.person.Person;
import org.service.UsersService;
import org.utils.images.ImageUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class UserEditDialogController {

    @FXML private VBox personContainer;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField jobField;
    @FXML private TextField empathyField;

    @FXML private VBox duckContainer;
    @FXML private TextField speedField;
    @FXML private TextField resistanceField;

    @FXML private TextArea descriptionField;

    @FXML private ImageView photoPreview;
    @FXML private Button uploadPhotoButton;
    @FXML private Button removePhotoButton;

    private UsersService usersService;
    private User user;
    private Stage stage;
    private byte[] newPhotoData;
    private boolean photoChanged = false;

    public void setService(UsersService usersService, User user, Stage stage) {
        this.usersService = usersService;
        this.user = user;
        this.stage = stage;
        populateFields();
        loadUserPhoto();
    }

    private void populateFields() {

        descriptionField.setText(user.getDescription());

        if (user instanceof Person) {
            Person p = (Person) user;
            personContainer.setVisible(true);
            personContainer.setManaged(true);
            firstNameField.setText(p.getFirstName());
            lastNameField.setText(p.getLastName());
            jobField.setText(p.getOccupation());
            empathyField.setText(String.valueOf(p.getEmpathyLevel()));
        } else if (user instanceof Duck) {
            Duck d = (Duck) user;
            duckContainer.setVisible(true);
            duckContainer.setManaged(true);
            speedField.setText(String.valueOf(d.getSpeed()));
            resistanceField.setText(String.valueOf(d.getRezistance()));
        }
    }

    private void loadUserPhoto() {
        if (user.getPhoto() != null && user.getPhoto().length > 0) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(user.getPhoto());
                Image image = new Image(bis);
                photoPreview.setImage(image);
                removePhotoButton.setDisable(false);
            } catch (Exception e) {
                System.err.println("Error loading user photo: " + e.getMessage());
                setDefaultPhoto();
            }
        } else {
            setDefaultPhoto();
        }
    }

    private void setDefaultPhoto() {
        photoPreview.setImage(null);
        removePhotoButton.setDisable(true);
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                if (!ImageUtil.isValidImageFile(selectedFile.getAbsolutePath())) {
                    showAlert("Invalid Image", "The selected file is not a valid image.", Alert.AlertType.ERROR);
                    return;
                }

                newPhotoData = ImageUtil.imageFileToByteArray(selectedFile);

                if (newPhotoData.length > 5 * 1024 * 1024) {
                    showAlert("File Too Large", "The image file is too large. Please select an image under 5MB.", Alert.AlertType.WARNING);
                    return;
                }

                ByteArrayInputStream bis = new ByteArrayInputStream(newPhotoData);
                Image image = new Image(bis);
                photoPreview.setImage(image);

                photoChanged = true;
                removePhotoButton.setDisable(false);

                System.out.println("Photo loaded successfully: " + newPhotoData.length + " bytes");

            } catch (IOException e) {
                showAlert("Error", "Failed to load the selected image: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRemovePhoto() {
        photoPreview.setImage(null);
        newPhotoData = null;
        photoChanged = true;
        removePhotoButton.setDisable(true);
    }

    @FXML
    private void handleSave() {
        try {
            user.setDescription(descriptionField.getText());

            if (photoChanged) {
                user.setPhoto(newPhotoData);
            }

            if (user instanceof Person) {
                Person p = (Person) user;
                p.setFirstName(firstNameField.getText());
                p.setLastName(lastNameField.getText());
                p.setOccupation(jobField.getText());
                p.setEmpathyLevel(Double.parseDouble(empathyField.getText()));
                usersService.update(p);
            } else if (user instanceof Duck) {
                Duck d = (Duck) user;
                d.setSpeed(Double.parseDouble(speedField.getText()));
                d.setRezistance(Double.parseDouble(resistanceField.getText()));
                usersService.update(d);
            }

            showAlert("Success", "Profile updated successfully!", Alert.AlertType.INFORMATION);
            stage.close();

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for numeric fields.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update profile: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}