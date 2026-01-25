package org.example.paginarefiltraredb.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.repository.paging.util.paging.Page;
import org.example.paginarefiltraredb.repository.paging.util.paging.Pageable;
import org.example.paginarefiltraredb.service.BaseService;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;
import org.example.paginarefiltraredb.service.observer.Observer;

import java.util.function.Function;


public abstract class AbstractPagingTableViewController<ID, E extends Entity<ID>, DTO, Filter extends SqlFilter>
        implements PagingTableView, Observer<E> {

    protected int currentPage;
    protected final int pageSize;
    protected int totalNrOfElements;

    // 2. MODEL NOW HOLDS DTOs, NOT ENTITIES
    protected final ObservableList<DTO> model = FXCollections.observableArrayList();

    protected final Filter filter;
    protected BaseService<ID, E> baseService;

    // 3. TABLE VIEW DISPLAYS DTOs
    @FXML protected TableView<DTO> tableView;
    @FXML protected Label labelPage;
    @FXML protected Button buttonPrevious;
    @FXML protected Button buttonNext;

    public AbstractPagingTableViewController(int currentPage, int pageSize, int totalNrOfElements, Filter filter) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalNrOfElements = totalNrOfElements;
        this.filter = filter;
    }

    public void setBaseService(BaseService<ID, E> service) {
        this.baseService = service;
        this.baseService.addObserver(this); // Hook up observer here
    }

    protected abstract Function<E, DTO> getDtoMapper();

    @FXML
    public void initialize(){
        tableView.setItems(model);
    }

    @Override
    public void loadData() {
        if (baseService == null) return;

        Pageable pageable = new Pageable(currentPage,pageSize);

        try {
            Page<E> page = baseService.findAllOnPage(pageable, filter);

            int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
            if (maxPage == -1) maxPage = 0;

            if (currentPage > maxPage) {
                currentPage = maxPage;
                pageable = new Pageable(pageSize, currentPage);
                page = baseService.findAllOnPage(pageable, filter);
            }
            totalNrOfElements = page.getTotalNumberOfElements();

            labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
            buttonPrevious.setDisable(currentPage == 0);
            buttonNext.setDisable((currentPage + 1) * pageSize >= totalNrOfElements);

            model.setAll(baseService.convertToDto(page, getDtoMapper()));

        } catch (Exception e) {
            WindowManager.showError("Error", "Could not load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Standard pagination handlers...
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