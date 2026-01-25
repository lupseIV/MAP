//package org.example.paginarefiltraredb.gui.implementations;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import org.example.paginarefiltraredb.gui.AbstractTableViewController;
//import org.example.paginarefiltraredb.gui.util.crud.AddDialog;
//import org.example.paginarefiltraredb.gui.util.crud.DeleteDialog;
//import org.example.paginarefiltraredb.gui.util.crud.UpdateDialog;
//import org.example.paginarefiltraredb.gui.util.form.FormField;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//public class VipClientControllerTableView extends AbstractTableViewController<Integer, VipClient, VipClientDto>
//        implements AddDialog<VipClient>,
//        UpdateDialog<Integer, VipClient, VipClientDto>,
//        DeleteDialog<Integer, VipClientDto> {
//    // --- TABLE COLUMNS ---
//    @FXML private TableColumn<VipClientDto, Integer> colId;
//    @FXML private TableColumn<VipClientDto, String> colName;
//    @FXML private TableColumn<VipClientDto, String> colType;
//    @FXML private TableColumn<VipClientDto, Double> colBudget;
//    @FXML private TableColumn<VipClientDto, Integer> colLoyaltyPoints;
//    @FXML private TableColumn<VipClientDto, String> colPersonalManager;
//
//    private VipClientService service;
//    private ManagerService managerService;
//
//    public void setService(VipClientService clientService) {
//        super.setBaseService(clientService);
//        this.service = clientService;
//
//        initializeTable();
//        loadData();
//    }
//
//    public void setManagerService(ManagerService managerService) {
//        this.managerService = managerService;
//    }
//
//    @Override
//    protected Function<VipClient, VipClientDto> getDtoMapper() {
//        return VipClientDto::new;
//    }
//
//    public void initializeTable() {
//        // Map DTO properties to Columns
//        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
//        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
//        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
//        colBudget.setCellValueFactory(new PropertyValueFactory<>("budget"));
//
//        // Specific VIP Mappings
//        colLoyaltyPoints.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));
//        colPersonalManager.setCellValueFactory(new PropertyValueFactory<>("managerName"));
//    }
//
//    // --- ADD VIP ---
//
//    private List<String> getManagerOptions() {
//        // 1. Fetch all managers from DB
//        Iterable<Manager> managers = managerService.findAll();
//
//        // 2. Format them as "ID: Name (Region)" or similar
//        List<String> options = StreamSupport.stream(managers.spliterator(), false)
//                .map(m -> m.getId() + ": " + m.getName())
//                .collect(Collectors.toList());
//
//        // 3. Add an "Unassigned" option at the top
//        options.addFirst("Unassigned");
//        return options;
//    }
//
//    // --- HELPER: Find Manager by formatted String ---
//    private Manager resolveManagerSelection(String selection) {
//        if (selection == null || selection.equals("Unassigned") || selection.isEmpty()) {
//            return null;
//        }
//        try {
//            // Extract ID from "10: John Doe"
//            String idPart = selection.split(":")[0];
//            int id = Integer.parseInt(idPart);
//            return managerService.findOne((long) id).orElse(null);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    @FXML
//    public void handleAdd(){
//        executeAdd(
//                "Add New Client",
//                service::add,
//                this::loadData
//        );
//    }
//
//    @FXML
//    public void handleUpdate(){
//        executeUpdate(
//                tableView.getSelectionModel().getSelectedItem(),
//                VipClientDto::getId,
//                service::findOne,
//                service::update,
//                this::loadData
//        );
//    }
//
//    @FXML
//    public void handleDelete(){
//        executeDelete(
//                tableView.getSelectionModel().getSelectedItem(),
//                VipClientDto::getId,
//                service::delete,
//                this::loadData
//        );
//    }
//
//    @Override
//    public List<FormField> getAddFormConfig() {
//        return List.of(
//                FormField.text("name", "Name", ""),
//                FormField.decimal("budget", "Budget", 0.0),
//                FormField.radio("loyalty", "Loyalty", List.of("1", "2", "3", "4", "5"), "1"),
//                FormField.choice("manager", "Personal Manager", getManagerOptions(), "Unassigned")
//        );
//    }
//
//    @Override
//    public VipClient createEntity(Map<String, String> results) {
//        String name = results.get("name");
//        Double budget = Double.parseDouble(results.get("budget"));
//        int loyalty = Integer.parseInt(results.get("loyalty"));
//        String managerSelection = results.get("manager");
//
//        VipClient newVip = new VipClient(name,"VIP" ,budget, LocalDate.now());
//        newVip.setLoyaltyPoints(loyalty);
//        newVip.setPersonalManager(resolveManagerSelection(managerSelection));
//        return newVip;
//    }
//
//    @Override
//    public String getNameForDelete(VipClientDto vipClientDto) {
//        return vipClientDto.getName();
//    }
//
//    @Override
//    public List<FormField> getUpdateFormConfig(VipClient entity) {
//        String currentManagerStr = (entity.getPersonalManager() != null)
//                ? entity.getPersonalManager().getId() + ": " + entity.getPersonalManager().getName()
//                : "Unassigned";
//
//        return List.of(
//                FormField.readOnly("id", "VIP ID", entity.getId()),
//                FormField.text("name", "Full Name", entity.getName()),
//                FormField.decimal("budget", "Budget ($)", entity.getBudget()),
//                FormField.radio("loyalty", "Loyalty Level", List.of("1", "2", "3", "4", "5"), String.valueOf(entity.getLoyaltyPoints())),
//                FormField.choice("manager", "Personal Manager", getManagerOptions(), currentManagerStr)
//        );
//    }
//
//    @Override
//    public void updateEntity(VipClient entity, Map<String, String> results) {
//        entity.setName(results.get("name"));
//        entity.setBudget(Double.parseDouble(results.get("budget")));
//        entity.setLoyaltyPoints(Integer.parseInt(results.get("loyalty")));
//        entity.setPersonalManager(resolveManagerSelection(results.get("manager")));
//    }
//}