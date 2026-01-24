package org.example.paginarefiltraredb.gui.util.crud;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.example.paginarefiltraredb.gui.util.WindowManager;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <h2>Delete Dialog Mixin</h2>
 * <p>
 * Provides a safe, confirmation-based deletion workflow. It abstracts the standard JavaFX Alert logic
 * and error handling for database constraints.
 * </p>
 *
 * @param <ID>  The type of the identifier.
 * @param <DTO> The type of the UI Object.
 */
public interface DeleteDialog<ID, DTO> {

    // =========================================================
    //        TEMPLATE STEPS (Must be implemented by Controller)
    // =========================================================

    /**
     * Extracts a user-friendly name to display in the confirmation alert.
     *
     * @param dto The selected DTO.
     * @return A string representation (e.g., the Client's name).
     */
    String getNameForDelete(DTO dto);

    // =========================================================
    //           EXECUTION LOGIC (The Template Method)
    // =========================================================

    /**
     * Executes the full "Delete" workflow:
     * <ol>
     * <li>Validates that a DTO is selected.</li>
     * <li>Shows a Confirmation Alert using the name from {@link #getNameForDelete(Object)}.</li>
     * <li>If confirmed, executes the {@code deleteAction}.</li>
     * <li>Refreshes the view.</li>
     * <li>Handles exceptions (e.g., Foreign Key constraints).</li>
     * </ol>
     *
     * @param selectedDto   The item currently selected in the TableView.
     * @param idExtractor   Function to extract the ID from the DTO.
     * @param deleteAction  The service method to call for deletion (e.g., {@code service::delete}).
     * @param refreshAction The method to call to reload the UI.
     */
    default void executeDelete(DTO selectedDto,
                               Function<DTO, ID> idExtractor,
                               Consumer<ID> deleteAction,
                               Runnable refreshAction) {

        if (selectedDto == null) {
            WindowManager.showError("Deletion Error", "Please select an item to delete.");
            return;
        }

        // 1. Template Step: Get display name
        String displayName = getNameForDelete(selectedDto);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + displayName + "?");
        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    ID id = idExtractor.apply(selectedDto);

                    // 2. Execution: Delete & Refresh
                    deleteAction.accept(id);
                    WindowManager.showMessage("Success", "Item deleted.");

                    if (refreshAction != null) refreshAction.run();

                } catch (Exception e) {
                    WindowManager.showError("Delete Failed", e.getMessage());
                }
            }
        });
    }
}