package org.example.paginarefiltraredb.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.repository.paging.util.paging.Page;
import org.example.paginarefiltraredb.repository.paging.util.paging.Pageable;
import org.example.paginarefiltraredb.service.BaseService;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;
import org.example.paginarefiltraredb.service.observer.Observer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractPagingListViewController<ID, E extends Entity<ID>, DTO, Filter extends SqlFilter>
        implements PagingListView, Observer<E> {

    protected int currentPage;
    protected final int pageSize;
    protected int totalNrOfElements;
    protected final ObservableList<DTO> model = FXCollections.observableArrayList();
    protected final Filter filter;
    protected BaseService<ID, E> baseService;

    @FXML protected ListView<DTO> listView;
    @FXML protected Label labelPage;
    @FXML protected Button buttonPrevious;
    @FXML protected Button buttonNext;

    public AbstractPagingListViewController(int currentPage, int pageSize, int totalNrOfElements, Filter filter) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalNrOfElements = totalNrOfElements;
        this.filter = filter;
    }

    public void setBaseService(BaseService<ID, E> service) {
        this.baseService = service;
        this.baseService.addObserver(this);
        loadData();
    }

    protected abstract Function<E, DTO> getDtoMapper();
    protected abstract void setupListCellFactory();

    @FXML
    public void initialize() {
        listView.setItems(model);
        setupListCellFactory();
    }

    @Override
    public void loadData() {
        if (baseService == null) return;
        Pageable pageable = new Pageable(currentPage, pageSize);

        CompletableFuture<Page<E>> correctPageFuture = baseService.findAllOnPage(pageable, filter)
                .thenCompose(page -> {
                    int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
                    if (maxPage < 0) maxPage = 0;
                    if (currentPage > maxPage) {
                        return baseService.findAllOnPage(new Pageable(maxPage, pageSize), filter);
                    } else {
                        return CompletableFuture.completedFuture(page);
                    }
                });

        correctPageFuture.thenAcceptAsync(page -> {
            int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
            if (maxPage < 0) maxPage = 0;
            if (currentPage > maxPage) currentPage = maxPage;
            totalNrOfElements = page.getTotalNumberOfElements();

            labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
            buttonPrevious.setDisable(currentPage == 0);
            buttonNext.setDisable((currentPage + 1) * pageSize >= totalNrOfElements);
        }, Platform::runLater);

        baseService.convertPageToDto(correctPageFuture, getDtoMapper())
                .thenAcceptAsync(dtos -> model.setAll(dtos), Platform::runLater)
                .exceptionally(ex -> {
                    Platform.runLater(() -> WindowManager.showError("Error", "Load failed: " + ex.getMessage()));
                    return null;
                });
    }

    @Override
    public void onNext(ActionEvent actionEvent) {
        if ((currentPage + 1) * pageSize < totalNrOfElements) {
            currentPage++;
            loadData();
        }
    }

    @Override
    public void onPrevious(ActionEvent actionEvent) {
        if (currentPage > 0) {
            currentPage--;
            loadData();
        }
    }

    @Override
    public void update(EntityChangeEvent<E> event) {
        loadData();
    }
}