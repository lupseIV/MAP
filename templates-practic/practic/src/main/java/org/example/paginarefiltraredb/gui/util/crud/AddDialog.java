package org.example.paginarefiltraredb.gui.util.crud;

import org.example.paginarefiltraredb.gui.util.DynamicFormDialog;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.gui.util.form.FormField;

import java.util.List;
import java.util.Map;
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
     * Executes the full "Add" workflow:
     * <ol>
     * <li>Fetches the form configuration via {@link #getAddFormConfig()}.</li>
     * <li>Displays the {@link DynamicFormDialog}.</li>
     * <li>On confirmation, creates the entity via {@link #createEntity(Map)}.</li>
     * <li>Persists the entity using the provided {@code saveAction}.</li>
     * <li>Refreshes the view using the provided {@code refreshAction}.</li>
     * <li>Handles exceptions centrally.</li>
     * </ol>
     *
     * @param title         The title to display on the dialog window.
     * @param saveAction    The service method to call for saving (e.g., {@code service::add}).
     * @param refreshAction The method to call to reload the UI (e.g., {@code this::loadData}).
     */
    default void executeAdd(String title, Consumer<E> saveAction, Runnable refreshAction) {
        // 1. Template Step: Get the specific UI config
        List<FormField> config = getAddFormConfig();

        DynamicFormDialog dialog = new DynamicFormDialog(title, config);
        dialog.show().ifPresent(results -> {
            try {
                // 2. Template Step: Delegate object creation to the controller
                E newEntity = createEntity(results);

                // 3. Execution: Save & Refresh
                saveAction.accept(newEntity);
                WindowManager.showMessage("Success", "Item added successfully!");

                if (refreshAction != null) refreshAction.run();

            } catch (NumberFormatException e) {
                WindowManager.showError("Input Error", "Please check your numeric fields.");
            } catch (Exception e) {
                WindowManager.showError("Error", "Could not add item: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}