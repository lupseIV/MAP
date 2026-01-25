package org.example.paginarefiltraredb.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.service.BaseService;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;
import org.example.paginarefiltraredb.service.observer.Observer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractListViewController<ID, E extends Entity<ID>, DTO> implements Observer<E> {

    protected final ObservableList<DTO> model = FXCollections.observableArrayList();
    protected BaseService<ID, E> baseService;

    @FXML protected ListView<DTO> listView;

    public void setBaseService(BaseService<ID, E> service) {
        this.baseService = service;
        this.baseService.addObserver(this);
        loadData();
    }

    protected abstract Function<E, DTO> getDtoMapper();
    protected abstract void setupListCellFactory(); // Forces implementation of custom cells

    @FXML
    public void initialize() {
        listView.setItems(model);
        setupListCellFactory();
    }

    public void loadData() {
        if (baseService == null) return;

        CompletableFuture<Iterable<E>> allEntities = baseService.findAll();
        baseService.convertIterableToDto(allEntities, getDtoMapper())
                .thenAcceptAsync(dtos -> Platform.runLater(() -> model.setAll(dtos)), Platform::runLater)
                .exceptionally(e -> {
                    Platform.runLater(() -> WindowManager.showError("Error", "Load failed: " + e.getMessage()));
                    return null;
                });
    }

    @Override
    public void update(EntityChangeEvent<E> event) {
        loadData();
    }
}