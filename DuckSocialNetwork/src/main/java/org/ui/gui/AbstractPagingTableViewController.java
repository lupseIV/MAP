package org.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import org.domain.dtos.filters.FriendshipGUIFilter;
import org.domain.dtos.filters.SqlFilter;
import org.domain.users.relationships.Friendship;

public abstract class AbstractPagingTableViewController<E,Filter> implements PagingTableView {

    protected int currentPage ;
    protected final int pageSize ;
    protected int totalNrOfElements;

    protected final ObservableList<E> model = FXCollections.observableArrayList();
    protected final Filter filter;

    public AbstractPagingTableViewController(int currentPage,int pageSize ,int totalNrOfElements, Filter filter) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalNrOfElements = totalNrOfElements;
        this.filter = filter;
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
