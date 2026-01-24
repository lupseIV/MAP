package org.example.paginarefiltraredb.gui.util.crud;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.example.paginarefiltraredb.gui.util.WindowManager;

import java.util.concurrent.CompletableFuture;
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
     * Executes the full "Delete" workflow asynchronously:
     * <ol>
     * <li>Validates selection.</li>
     * <li>Shows Confirmation Alert (Blocking on UI thread, which is fine for user input).</li>
     * <li>If confirmed, executes {@code deleteAction} (Asynchronous).</li>
     * <li>On success, refreshes the view on the UI thread.</li>
     * <li>On failure, shows error on the UI thread.</li>
     * </ol>
     *
     * @param selectedDto   The item currently selected in the TableView.
     * @param idExtractor   Function to extract the ID from the DTO.
     * @param deleteAction  The ASYNC service method (e.g., {@code service::delete}).
     * Accepts ID, returns a CompletableFuture (result type ignored).
     * @param refreshAction The method to call to reload the UI.
     */
    default void executeDelete(DTO selectedDto,
                               Function<DTO, ID> idExtractor,
                               Function<ID, CompletableFuture<?>> deleteAction,
                               Runnable refreshAction) {

        if (selectedDto == null) {
            WindowManager.showError("Deletion Error", "Please select an item to delete.");
            return;
        }

        // 1. Template Step: Get display name
        String displayName = getNameForDelete(selectedDto);

        // Show Confirmation (Blocks UI thread until user clicks, which is desired behavior here)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + displayName + "?");

        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    ID id = idExtractor.apply(selectedDto);

                    // 2. Execution: Async Delete
                    deleteAction.apply(id)
                            .thenAccept(ignoredResult -> {
                                // --- SUCCESS (Back to UI Thread) ---
                                Platform.runLater(() -> {
                                    WindowManager.showMessage("Success", "Item deleted.");
                                    if (refreshAction != null) {
                                        refreshAction.run();
                                    }
                                });
                            })
                            .exceptionally(ex -> {
                                // --- ERROR (Back to UI Thread) ---
                                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                                Platform.runLater(() -> {
                                    WindowManager.showError("Delete Failed", cause.getMessage());
                                });
                                return null;
                            });

                } catch (Exception e) {
                    // Catch synchronous errors (e.g. id extraction failed)
                    WindowManager.showError("Error", "Could not initiate delete: " + e.getMessage());
                }
            }
        });
    }
}
