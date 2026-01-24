package org.example.paginarefiltraredb.gui.implementations;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.paginarefiltraredb.domain.dtos.implementation.ClientDto;
import org.example.paginarefiltraredb.domain.entities.Client;
import org.example.paginarefiltraredb.domain.filters.implementations.ClientFilter;
import org.example.paginarefiltraredb.gui.AbstractPagingTableViewController;
import org.example.paginarefiltraredb.gui.util.DynamicFormDialog;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.gui.util.crud.AddDialog;
import org.example.paginarefiltraredb.gui.util.crud.DeleteDialog;
import org.example.paginarefiltraredb.gui.util.crud.UpdateDialog;
import org.example.paginarefiltraredb.gui.util.form.FieldType;
import org.example.paginarefiltraredb.gui.util.form.FormField;
import org.example.paginarefiltraredb.repository.paging.util.paging.Page;
import org.example.paginarefiltraredb.repository.paging.util.paging.Pageable;
import org.example.paginarefiltraredb.service.implementations.ClientService;
import org.example.paginarefiltraredb.service.observer.Observer;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClientControllerPagingTableView extends AbstractPagingTableViewController<Integer,Client, ClientDto, ClientFilter>
implements AddDialog<Client>,
UpdateDialog<Integer, Client, ClientDto>,
        DeleteDialog<Integer, ClientDto> {

    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colType;
    @FXML private TableColumn<Client, Double> colBudget;

    @FXML private TextField searchField;

    @FXML private ComboBox<String> typeCombo;

    private ClientService service;

    // Constructor calling super with default values (page 0, size 5)
    public ClientControllerPagingTableView() {
        super(0, 5, 0, new ClientFilter());
    }

    public void setService(ClientService clientService) {
        super.setBaseService(clientService);

        this.service = clientService;

        initializeTable();
        initCombo();
        loadData();
    }

    @FXML
    public void initialize() {
        super.initialize();

        searchField.textProperty()
                .addListener((obs, oldVal, newVal) ->
                handleSearch());


        typeCombo.valueProperty()
                .addListener((obs, oldVal, newVal) ->
        {handleComboBoxFilter();});
    }

    @Override
    protected Function<Client, ClientDto> getDtoMapper() {
        return ClientDto::new;
    }

    @Override
    public void initializeTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colBudget.setCellValueFactory(new PropertyValueFactory<>("budget"));
    }



    private void initCombo() {
        if (service == null) return;

        Set<String> types = service.getAllTypes();
        // Convert Set to List to allow adding "No selection" without crashing
        ObservableList<String> options = FXCollections.observableArrayList(types);
        options.sort(String::compareTo);
        options.addFirst("No selection");

        typeCombo.setItems(options);
    }

    // --- UI ACTIONS ---

    @FXML
    public void handleSearch() {
        String searchText = searchField.getText();

        filter.setNameSearch(searchText);

        this.currentPage = 0;

        loadData();
    }

    @FXML
    public void handleComboBoxFilter() {
        String type = typeCombo.getValue();
        if("No selection".equals(type)){
            filter.setType(null);
        } else {
            filter.setType(type);
        }
        this.currentPage = 0;

        loadData();
    }

    // =========================================================
    //                CLEAN ACTION HANDLERS
    // =========================================================

    @FXML
    public void handleAdd() {
        // Just provide Title, Service Call, and Refresh
        executeAdd(
                "Add New Client",
                baseService::add,
                this::loadData);
    }

    @FXML
    public void handleUpdate() {
        executeUpdate(
                tableView.getSelectionModel().getSelectedItem(),
                ClientDto::getId,
                baseService::findOne,
                baseService::update,
                this::loadData
        );
    }

    @FXML
    public void handleDelete() {
        executeDelete(
                tableView.getSelectionModel().getSelectedItem(),
                ClientDto::getId,
                baseService::delete,
                this::loadData
        );
    }

    // =========================================================
    //           (Template Steps)
    // =========================================================

    // --- ADD Logic ---
    @Override
    public List<FormField> getAddFormConfig() {
        return List.of(
                FormField.text("name", "Full Name", "Enter Name"),
                FormField.text("type", "Client Type", "Regular"),
                FormField.decimal("budget", "Budget ($)", 0.0)
        );
    }

    @Override
    public Client createEntity(java.util.Map<String, String> results) {
        String name = results.get("name");
        String type = results.get("type");
        Double budget = Double.parseDouble(results.get("budget"));
        return new Client(name, type, budget, java.time.LocalDate.now());
    }

    // --- UPDATE Logic ---
    @Override
    public List<FormField> getUpdateFormConfig(Client client) {
        return List.of(
                FormField.readOnly("id", "Client ID", client.getId()),
                FormField.text("fullName", "Full Name", client.getName()), // Note key: "fullName"
                FormField.text("type", "Client Type", client.getType()),
                FormField.decimal("budget", "Budget ($)", client.getBudget())
        );
    }

    @Override
    public void updateEntity(Client client, java.util.Map<String, String> results) {
        client.setName(results.get("fullName"));
        client.setType(results.get("type"));
        client.setBudget(Double.parseDouble(results.get("budget")));
    }

    // --- DELETE Logic ---
    @Override
    public String getNameForDelete(ClientDto dto) {
        return dto.getName(); // Used for the Confirmation Alert
    }
}
