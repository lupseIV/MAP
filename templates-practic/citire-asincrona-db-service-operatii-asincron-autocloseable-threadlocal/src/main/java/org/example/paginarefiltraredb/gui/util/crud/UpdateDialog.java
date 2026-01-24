package org.example.paginarefiltraredb.gui.util.crud;

import javafx.application.Platform;
import org.example.paginarefiltraredb.gui.util.DynamicFormDialog;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.gui.util.form.FormField;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <h2>Update Dialog Mixin</h2>
 * <p>
 * Manages the lifecycle of updating an existing entity. It handles the complexity of bridging
 * the selected UI object (DTO) with the database object (Entity) to ensure data integrity during edits.
 * </p>
 *
 * @param <ID>  The type of the Entity's identifier (e.g., {@code Integer}).
 * @param <E>   The type of the Entity (Database Object).
 * @param <DTO> The type of the Data Transfer Object (UI Object).
 */
public interface UpdateDialog<ID, E, DTO> {

    // =========================================================
    //        TEMPLATE STEPS (Must be implemented by Controller)
    // =========================================================

    /**
     * Defines the configuration for the "Edit" form, pre-filled with existing data.
     *
     * @param entity The real database entity fetched from the service.
     * @return A list of {@link FormField} objects populated with the entity's current values.
     */
    List<FormField> getUpdateFormConfig(E entity);

    /**
     * Updates the state of the existing entity based on form input.
     *
     * @param entity  The entity to be mutated.
     * @param results A map containing the new values from the form.
     */
    void updateEntity(E entity, Map<String, String> results);

    // =========================================================
    //           EXECUTION LOGIC (The Template Method)
    // =========================================================

    /**
     * Executes the full "Update" workflow asynchronously:
     * <ol>
     * <li>Extracts ID from selected DTO.</li>
     * <li><b>Async:</b> Fetches the fresh Entity from the database.</li>
     * <li><b>UI:</b> Displays the Edit Dialog populated with fetched data.</li>
     * <li><b>Async:</b> On confirmation, saves the updated entity.</li>
     * <li><b>UI:</b> Refreshes the view or shows errors.</li>
     * </ol>
     *
     * @param selectedDto   The item currently selected in the TableView.
     * @param idExtractor   Function to extract the ID from the DTO.
     * @param entityFetcher Function to find the entity (Returns {@code CompletableFuture<Optional<E>>}).
     * @param saveAction    Function to save the entity (Returns {@code CompletableFuture<E>}).
     * @param refreshAction Runnable to reload the UI.
     */
    default void executeUpdate(DTO selectedDto,
                               Function<DTO, ID> idExtractor,
                               Function<ID, CompletableFuture<Optional<E>>> entityFetcher,
                               Function<E, CompletableFuture<E>> saveAction,
                               Runnable refreshAction) {

        if (selectedDto == null) {
            WindowManager.showError("Warning", "Please select an item to edit.");
            return;
        }

        ID id = idExtractor.apply(selectedDto);

        // STEP 1: Async Fetch
        entityFetcher.apply(id)
                .thenAccept(optEntity -> {
                    // Switch to UI Thread to show dialog
                    Platform.runLater(() -> {
                        optEntity.ifPresentOrElse(entity -> {
                            // STEP 2: Show Dialog
                            showUpdateDialog(entity, saveAction, refreshAction);
                        }, () -> {
                            WindowManager.showError("Error", "The item no longer exists in the database.");
                            if (refreshAction != null) refreshAction.run(); // Refresh to remove the ghost item
                        });
                    });
                })
                .exceptionally(ex -> {
                    // Handle Fetch Errors
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    Platform.runLater(() -> WindowManager.showError("Fetch Error", cause.getMessage()));
                    return null;
                });
    }

    /**
     * Internal helper to handle the UI part of the update flow.
     */
    private void showUpdateDialog(E entity, Function<E, CompletableFuture<E>> saveAction, Runnable refreshAction) {
        try {
            // Get config based on the FETCHED entity
            List<FormField> config = getUpdateFormConfig(entity);

            DynamicFormDialog dialog = new DynamicFormDialog("Edit Item", config);
            dialog.show().ifPresent(results -> {
                try {
                    // Update the entity object in memory
                    updateEntity(entity, results);

                    // STEP 3: Async Save
                    saveAction.apply(entity)
                            .thenAccept(saved -> {
                                // --- SUCCESS ---
                                Platform.runLater(() -> {
                                    WindowManager.showMessage("Success", "Item updated successfully!");
                                    if (refreshAction != null) refreshAction.run();
                                });
                            })
                            .exceptionally(ex -> {
                                // --- SAVE ERROR ---
                                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                                Platform.runLater(() -> WindowManager.showError("Update Failed", cause.getMessage()));
                                return null;
                            });

                } catch (NumberFormatException e) {
                    WindowManager.showError("Input Error", "Invalid number format.");
                } catch (Exception e) {
                    WindowManager.showError("Error", "Update failed: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            WindowManager.showError("Error", "Could not build form: " + e.getMessage());
        }
    }
}