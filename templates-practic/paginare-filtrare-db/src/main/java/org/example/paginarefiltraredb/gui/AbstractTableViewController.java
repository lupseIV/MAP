package org.example.paginarefiltraredb.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.service.BaseService;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;
import org.example.paginarefiltraredb.service.observer.Observer;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AbstractTableViewController<ID, E extends Entity<ID>, DTO>
        implements Observer<E> {

    // Model holds DTOs directly
    protected final ObservableList<DTO> model = FXCollections.observableArrayList();

    protected BaseService<ID, E> baseService;

    // Only the TableView is needed (no page controls)
    @FXML protected TableView<DTO> tableView;

    public void setBaseService(BaseService<ID, E> service) {
        this.baseService = service;
        this.baseService.addObserver(this);

        loadData();
    }

    protected abstract Function<E, DTO> getDtoMapper();

    @FXML
    public void initialize(){
        tableView.setItems(model);
    }

    /**
     * Loads ALL data from the service without pagination.
     */
    public void loadData() {
        if (baseService == null) return;

        try {
            Iterable<E> allEntities = baseService.findAll();
            List<DTO> allDtos = baseService.convertToDto(allEntities, getDtoMapper());
            model.setAll(allDtos);

        } catch (Exception e) {
            WindowManager.showError("Error", "Could not load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(EntityChangeEvent<E> event) {
        loadData();
    }
}