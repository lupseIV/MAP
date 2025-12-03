package org.ui.gui;

import javafx.event.ActionEvent;

public interface PagingTableView {

    void onNext(ActionEvent actionEvent);
    void onPrevious(ActionEvent actionEvent);

    void initializeTable();
    void loadData();
}
