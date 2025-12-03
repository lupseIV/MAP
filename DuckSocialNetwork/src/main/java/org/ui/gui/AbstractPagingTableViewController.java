package org.ui.gui;

import javafx.fxml.Initializable;

import javafx.event.ActionEvent;

public abstract class AbstractPagingTableViewController implements PagingTableView {

    protected int currentPage ;
    protected final int pageSize ;
    protected int totalNrOfElements;

    public AbstractPagingTableViewController(int currentPage,int pageSize ,int totalNrOfElements) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalNrOfElements = totalNrOfElements;
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

}
