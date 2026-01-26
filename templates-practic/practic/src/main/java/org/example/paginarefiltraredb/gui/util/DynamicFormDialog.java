package org.example.paginarefiltraredb.gui.util;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.example.paginarefiltraredb.gui.util.form.FieldType;
import org.example.paginarefiltraredb.gui.util.form.FormField;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * <h2>Dynamic Form Dialog</h2>
 * <p>
 * A generic UI generator that builds a JavaFX {@link Dialog} at runtime based on a list of configuration objects.
 * It implements the <b>Abstract Factory</b> pattern for UI controls, automatically choosing the correct
 * input widget (TextField, ComboBox, RadioGroup, etc.) based on the {@link FieldType}.
 * </p>
 * <p>
 * <b>Key Features:</b>
 * <ul>
 * <li>Auto-layout using {@link GridPane}.</li>
 * <li>Supports various input types (Text, Number, Date, Choice, Radio, Boolean).</li>
 * <li>Handles data extraction via a unified {@code extractValue} strategy.</li>
 * </ul>
 * </p>
 */
public class DynamicFormDialog {

    private final String title;
    private final List<FormField> fields;

    /**
     * Constructs a new Dynamic Form.
     *
     * @param title  The text to display in the window title bar.
     * @param fields The list of {@link FormField} definitions that determine the rows of the form.
     */
    public DynamicFormDialog(String title, List<FormField> fields) {
        this.title = title;
        this.fields = fields;
    }

    /**
     * Builds and displays the dialog, waiting for user input.
     * <p>
     * This method performs the following steps:
     * <ol>
     * <li>Initializes a {@code Dialog<Map<String, String>>}.</li>
     * <li>Iterates through the field configuration to build the UI nodes.</li>
     * <li>Maps the generated UI nodes to their unique Field Keys.</li>
     * <li>On "Save", iterates through the nodes to extract the current values.</li>
     * </ol>
     * </p>
     *
     * @return An {@link Optional} containing a Map where:
     * <ul>
     * <li><b>Key:</b> The field identifier (e.g., "firstName").</li>
     * <li><b>Value:</b> The user input as a String (e.g., "John", "true", "2023-01-01").</li>
     * </ul>
     * Returns {@code Optional.empty()} if the user cancels.
     */
    public Optional<Map<String, String>> show() {
        // 1. Setup Dialog
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Please fill in the details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // 2. Build Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Store controls to read values later
        Map<String, Node> nodeMap = new HashMap<>();

        int row = 0;
        for (FormField field : fields) {
            Label label = new Label(field.getLabel() + ":");
            grid.add(label, 0, row);

            Node inputNode = createInputNode(field);
            grid.add(inputNode, 1, row);

            nodeMap.put(field.getKey(), inputNode);
            row++;
        }

        dialog.getDialogPane().setContent(grid);

        // 3. Result Converter: Node -> String extraction
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Map<String, String> results = new HashMap<>();
                for (Map.Entry<String, Node> entry : nodeMap.entrySet()) {
                    results.put(entry.getKey(), extractValue(entry.getValue()));
                }
                return results;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Factory method that creates the appropriate JavaFX Control based on the FieldType.
     *
     * @param field The field configuration.
     * @return A {@link Node} (subclass of Control or Pane) ready to be added to the scene.
     */
    private Node createInputNode(FormField field) {
        switch (field.getType()) {
            case TEXTAREA:
                TextArea ta = new TextArea(field.getInitialValue().toString());
                ta.setPrefRowCount(3);
                ta.setDisable(!field.isEditable());
                return ta;

            case CHECKBOX:
                CheckBox cb = new CheckBox();
                if (field.getInitialValue() instanceof Boolean) {
                    cb.setSelected((Boolean) field.getInitialValue());
                }
                cb.setDisable(!field.isEditable());
                return cb;

            case RADIO:
                return createRadioGroup(field);

            case CHOICE:
                ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList(field.getOptions()));
                if (field.getInitialValue() != null) {
                    combo.setValue(field.getInitialValue().toString());
                }
                combo.setDisable(!field.isEditable());
                return combo;

            case DATE:
                DatePicker dp = new DatePicker();
                if (field.getInitialValue() instanceof LocalDate) {
                    dp.setValue((LocalDate) field.getInitialValue());
                }
                dp.setDisable(!field.isEditable());
                return dp;

            case COLOR:
                ColorPicker cp = new ColorPicker();
                // Handling omitted for brevity
                return cp;

            case NUMBER:
            case DECIMAL:
            case TEXT:
            default:
                TextField tf = (field.getType() == FieldType.PASSWORD) ? new PasswordField() : new TextField();
                if (field.getInitialValue() != null) {
                    tf.setText(field.getInitialValue().toString());
                }
                tf.setPromptText(field.getLabel());
                tf.setDisable(!field.isEditable());
                return tf;
        }
    }

    /**
     * Creates a group of Radio Buttons wrapped in a container.
     * <p>
     * <b>Implementation Note:</b> Since {@code extractValue} only accepts a single {@code Node},
     * this method attaches the {@link ToggleGroup} to the container's {@code UserData}.
     * This allows retrieval of the selected radio button later.
     * </p>
     *
     * @param field The field configuration containing the list of options.
     * @return An {@link HBox} containing the radio buttons.
     */
    private Node createRadioGroup(FormField field) {
        HBox container = new HBox(10); // Horizontal spacing of 10px
        ToggleGroup group = new ToggleGroup();

        List<String> options = field.getOptions();

        for (String optionLabel : options) {
            RadioButton rb = new RadioButton(optionLabel);
            rb.setToggleGroup(group);

            // Set UserData so we can easily retrieve the value later
            rb.setUserData(optionLabel);

            // Check if this is the initial value
            if (optionLabel.equals(field.getInitialValue())) {
                rb.setSelected(true);
            }

            rb.setDisable(!field.isEditable());
            container.getChildren().add(rb);
        }

        // TRICK: Store the ToggleGroup in the Container's UserData
        container.setUserData(group);

        return container;
    }

    /**
     * Extracts a unified String representation of the value from any supported JavaFX Control.
     *
     * @param node The JavaFX Node (TextField, ComboBox, CheckBox, etc.).
     * @return The string representation of the user's input (e.g., "true", "selected_option", "2024-01-01").
     */
    private String extractValue(Node node) {
        // A. Handle Simple Text Inputs (TextField, PasswordField, TextArea)
        if (node instanceof TextInputControl) {
            return ((TextInputControl) node).getText();
        }

        // B. Handle Dropdowns
        else if (node instanceof ComboBox) {
            Object value = ((ComboBox<?>) node).getValue();
            return (value != null) ? value.toString() : "";
        }

        // C. Handle Checkboxes
        else if (node instanceof CheckBox) {
            return String.valueOf(((CheckBox) node).isSelected());
        }

        // D. Handle DatePicker
        else if (node instanceof DatePicker) {
            LocalDate date = ((DatePicker) node).getValue();
            return (date != null) ? date.toString() : "";
        }

        // E. Handle ColorPicker
        else if (node instanceof ColorPicker) {
            return ((ColorPicker) node).getValue().toString();
        }

        // F. Handle Radio Groups (HBox container)
        else if (node instanceof HBox) {
            // Retrieve the ToggleGroup we stored in UserData earlier
            Object userData = node.getUserData();
            if (userData instanceof ToggleGroup) {
                ToggleGroup group = (ToggleGroup) userData;
                Toggle selected = group.getSelectedToggle();
                if (selected != null) {
                    // Return the UserData of the selected radio (which is the label string)
                    return selected.getUserData().toString();
                }
            }
            return ""; // Nothing selected
        }

        return "";
    }
}