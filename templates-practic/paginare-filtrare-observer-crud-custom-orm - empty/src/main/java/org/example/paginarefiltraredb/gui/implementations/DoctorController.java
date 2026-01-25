package org.example.paginarefiltraredb.gui.implementations;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.paginarefiltraredb.domain.dtos.implementation.ProgramariDto;
import org.example.paginarefiltraredb.domain.entities.Doctor;
import org.example.paginarefiltraredb.domain.entities.Programare;
import org.example.paginarefiltraredb.domain.filters.implementations.ProgramareFilter;
import org.example.paginarefiltraredb.gui.AbstractTableViewController;
import org.example.paginarefiltraredb.service.implementations.ProgramareService;

import java.util.Optional;
import java.util.function.Function;

public class DoctorController extends AbstractTableViewController<Long, Programare, ProgramariDto, ProgramareFilter> {

    @FXML
    private TableColumn<ProgramariDto, Object> timeCol;
    @FXML
    private TableColumn<ProgramariDto, String> numePacientCol;
    @FXML
    private TableColumn<ProgramariDto, String> cnpPacientCol;

    @FXML
    private TableColumn<ProgramariDto, Void> actionCol;

    private Doctor currentDoctor;
    private ProgramareService programareService;

    public void setCurrentDoctor(Doctor currentDoctor) {
        this.currentDoctor = currentDoctor;
    }

    public void setProgramareService(ProgramareService programareService) {
        this.programareService = programareService;
        programareService.addObserver(this);
    }

    @FXML
    private CheckBox checkBoxIstoric;

    @Override
    protected Function<Programare, ProgramariDto> getDtoMapper() {
        return ProgramariDto::new;
    }

    @Override
    public void initialize() {
        super.initialize();

        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        numePacientCol.setCellValueFactory(new PropertyValueFactory<>("numePacient"));
        cnpPacientCol.setCellValueFactory(new PropertyValueFactory<>("cnpPacient"));

        checkBoxIstoric.setOnAction(event -> {
            handleFilterIstoric();
        });

        actionCol.setCellFactory(param -> new TableCell<>(){
            private Button btn = new Button("Finish");
            {
               btn.setOnAction(event -> {
                ProgramariDto data = getTableView().getItems().get(getIndex());
                Optional<Programare> programare = baseService.findOne(data.getId());
                if(programare.isPresent()) {
                    Programare p = programare.get();
                    p.setStatus("Finished");
                    baseService.update(p);
                    loadData();
                }
            });
            }

            @Override
            protected void updateItem(Void unused, boolean b) {
                super.updateItem(unused, b);
                if (b ) {
                    setGraphic(null);
                } else {
                    ProgramariDto currentRow = getTableView().getItems().get(getIndex());

                    if (currentRow != null && "Scheduled".equals(currentRow.getStatus())) {
                        btn.setText("Finish");      // Reset text back to "Finish"
                        btn.setDisable(false);      // Re-enable the button
                        setGraphic(btn);
                    } else {
                        setGraphic(btn);
                        btn.setDisable(true);
                        btn.setText("Finished");
                    }
                }
            }
        });

        loadData();
    }

    @FXML
    public void handleFilterIstoric() {
        if (checkBoxIstoric.isSelected()) {
            filter.setType(null);
        } else {
            filter.setType("Scheduled");
        }
        loadData();
    }
}
