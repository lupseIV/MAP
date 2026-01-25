package org.example.paginarefiltraredb.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.service.BaseService;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;
import org.example.paginarefiltraredb.service.observer.Observer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AbstractTableViewController<ID, E extends Entity<ID>, DTO, Filter extends SqlFilter>
        implements Observer<E> {

    // Model holds DTOs directly
    protected final ObservableList<DTO> model = FXCollections.observableArrayList();

    protected BaseService<ID, E> baseService;
    protected Filter filter;

    // Only the TableView is needed (no page controls)
    @FXML protected TableView<DTO> tableView;

    public void setBaseService(BaseService<ID, E> service) {
        this.baseService = service;
        this.baseService.addObserver(this);

        loadData();
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
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

        baseService.findAll(filter).thenAccept( allEntities -> {
            List<DTO> allDtos = StreamSupport.stream(allEntities.spliterator(), false)
                    .map(getDtoMapper())
                    .collect(Collectors.toList());
            Platform.runLater(() -> {
                model.setAll(allDtos);
            });
        }).exceptionally((e) -> {
            WindowManager.showError("Error", "Could not load data: " + e.getMessage());
            e.printStackTrace();
            return null;
        });
    }

    @Override
    public void update(EntityChangeEvent<E> event) {
        loadData();
    }
}