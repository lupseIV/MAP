package org.example.paginarefiltraredb.gui;

import javafx.event.ActionEvent;

public interface PagingListView {
    void onNext(ActionEvent actionEvent);
    void onPrevious(ActionEvent actionEvent);
    void loadData();
}