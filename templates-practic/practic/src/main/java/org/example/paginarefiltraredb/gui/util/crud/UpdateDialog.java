package org.example.paginarefiltraredb.gui.util.crud;

import org.example.paginarefiltraredb.gui.util.DynamicFormDialog;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.gui.util.form.FormField;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
     * Executes the full "Update" workflow:
     * <ol>
     * <li>Validates that a DTO is selected.</li>
     * <li>Extracts the ID and fetches the real Entity.</li>
     * <li>Generates the pre-filled form via {@link #getUpdateFormConfig(Object)}.</li>
     * <li>Displays the dialog.</li>
     * <li>On confirmation, updates the entity via {@link #updateEntity(Object, Map)}.</li>
     * <li>Persists changes and refreshes the view.</li>
     * </ol>
     *
     * @param selectedDto   The item currently selected in the TableView.
     * @param idExtractor   Function to extract the ID from the DTO (e.g., {@code Dto::getId}).
     * @param entityFetcher Function to find the real entity by ID (e.g., {@code service::findOne}).
     * @param saveAction    The service method to call for updating (e.g., {@code service::update}).
     * @param refreshAction The method to call to reload the UI.
     */
    default void executeUpdate(DTO selectedDto,
                               Function<DTO, ID> idExtractor,
                               Function<ID, Optional<E>> entityFetcher,
                               Consumer<E> saveAction,
                               Runnable refreshAction) {

        if (selectedDto == null) {
            WindowManager.showError("Warning", "Please select an item to edit.");
            return;
        }

        ID id = idExtractor.apply(selectedDto);

        // Fetch the REAL entity to ensure we are editing the latest version
        entityFetcher.apply(id).ifPresent(entity -> {

            // 1. Template Step: Get specific config based on the entity
            List<FormField> config = getUpdateFormConfig(entity);

            DynamicFormDialog dialog = new DynamicFormDialog("Edit Item", config);
            dialog.show().ifPresent(results -> {
                try {
                    // 2. Template Step: Delegate the update logic
                    updateEntity(entity, results);

                    // 3. Execution: Save & Refresh
                    saveAction.accept(entity);
                    WindowManager.showMessage("Success", "Item updated successfully!");

                    if (refreshAction != null) refreshAction.run();

                } catch (NumberFormatException e) {
                    WindowManager.showError("Input Error", "Invalid number format.");
                } catch (Exception e) {
                    WindowManager.showError("Error", "Update failed: " + e.getMessage());
                }
            });
        });
    }
}