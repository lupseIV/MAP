package org.example.paginarefiltraredb.gui.util.crud;

import javafx.application.Platform;
import org.example.paginarefiltraredb.gui.util.DynamicFormDialog;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.gui.util.form.FormField;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <h2>Add Dialog Mixin</h2>
 * <p>
 * Provides a standardized workflow for creating and persisting new entities via a dynamic modal form.
 * Implementing classes (Controllers) act as the concrete implementation of the Template Method pattern.
 * </p>
 *
 * @param <E> The type of the Entity to be created (e.g., {@code Client}).
 */
public interface AddDialog<E> {

    // =========================================================
    //        TEMPLATE STEPS (Must be implemented by Controller)
    // =========================================================

    /**
     * Defines the configuration for the "Add" form.
     *
     * @return A list of {@link FormField} objects defining labels, types, and default values.
     */
    List<FormField> getAddFormConfig();

    /**
     * Factory method to construct an Entity from the form results.
     *
     * @param results A map where Key = Field Name, Value = User Input (String).
     * @return A valid instance of the Entity {@code E}.
     * @throws NumberFormatException if numeric parsing fails.
     */
    E createEntity(Map<String, String> results);

    // =========================================================
    //           EXECUTION LOGIC (The Template Method)
    // =========================================================

    /**
     * Executes the full "Add" workflow asynchronously:
     * <ol>
     * <li>Fetches configuration and displays the dialog.</li>
     * <li>Creates the entity from user input (Synchronous).</li>
     * <li>Calls the {@code saveAction} (Asynchronous).</li>
     * <li>On success, updates the UI via {@code refreshAction} on the JavaFX thread.</li>
     * <li>On failure, shows an error message on the JavaFX thread.</li>
     * </ol>
     *
     * @param title         The title to display on the dialog window.
     * @param saveAction    The ASYNC service method (e.g., {@code service::add}).
     * Must return a {@code CompletableFuture}.
     * @param refreshAction The method to call to reload the UI (e.g., {@code this::loadData}).
     */
    default void executeAdd(String title, Function<E, CompletableFuture<E>> saveAction, Runnable refreshAction) {
        // 1. Template Step: Get the specific UI config
        List<FormField> config = getAddFormConfig();

        DynamicFormDialog dialog = new DynamicFormDialog(title, config);
        dialog.show().ifPresent(results -> {
            try {
                // 2. Template Step: Create the entity (Fast, blocking is fine here)
                E newEntity = createEntity(results);

                // 3. Execution: Async Save
                // We apply the function to get the Future, then attach callbacks
                saveAction.apply(newEntity)
                        .thenAccept(savedEntity -> {
                            // --- SUCCESS (Back to UI Thread) ---
                            Platform.runLater(() -> {
                                WindowManager.showMessage("Success", "Item added successfully!");
                                if (refreshAction != null) {
                                    refreshAction.run();
                                }
                            });
                        })
                        .exceptionally(ex -> {
                            // --- ERROR (Back to UI Thread) ---
                            // We unwrap the exception to get the real message
                            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                            Platform.runLater(() -> {
                                WindowManager.showError("Save Failed", cause.getMessage());
                            });
                            return null; // Return null to satisfy Void return type
                        });

            } catch (NumberFormatException e) {
                WindowManager.showError("Input Error", "Please check your numeric fields.");
            } catch (Exception e) {
                // Catch errors that happened BEFORE the future started (e.g. inside createEntity)
                WindowManager.showError("Error", "Could not create item: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}