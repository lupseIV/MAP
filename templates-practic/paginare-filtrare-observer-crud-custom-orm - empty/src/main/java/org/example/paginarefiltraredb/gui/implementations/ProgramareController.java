package org.example.paginarefiltraredb.gui.implementations;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.paginarefiltraredb.domain.dtos.implementation.PacientDto;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.gui.AbstractTableViewController;
import org.example.paginarefiltraredb.gui.util.WindowManager;
import org.example.paginarefiltraredb.service.implementations.DoctorService;
import org.example.paginarefiltraredb.service.implementations.ProgramareService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ProgramareController extends AbstractTableViewController<Long, Pacient, PacientDto, SqlFilter> {

    // --- Controale Specifice Recepției ---
    @FXML private ComboBox<Doctor> cmbDoctor;
    @FXML private DatePicker datePicker;
    @FXML private TextField txtOra; // Format HH:mm
    @FXML private Button btnProgrameaza;

    // --- Coloane Tabel (trebuie definite în FXML și legate aici) ---
    @FXML private TableColumn<PacientDto, String> colNumePacient;
    @FXML private TableColumn<PacientDto, String> colCnp;

    // --- Servicii Suplimentare ---
    private DoctorService doctorService;
    private ProgramareService appointmentService;

    // Setteri pentru servicii extra
    public void setExtraServices(DoctorService docService, ProgramareService appService) {
        this.doctorService = docService;
        this.appointmentService = appService;
        loadDoctors(); // Populăm combobox-ul la start
    }

    @Override
    public void initialize() {
        super.initialize(); // Inițializează tabelul

        // Configurare coloane tabel pacienți
        // Presupunem că PacientDto are metodele getNume() și getCnp()
        colNumePacient.setCellValueFactory(new PropertyValueFactory<>("nume"));
        colCnp.setCellValueFactory(new PropertyValueFactory<>("cnp"));

        // Configurări extra
        datePicker.setValue(LocalDate.now());
    }

    /**
     * Implementarea mapper-ului cerut de clasa abstractă
     * Transformă Entity (Pacient) -> DTO (PacientDto)
     */
    @Override
    protected Function<Pacient, PacientDto> getDtoMapper() {
        return pacient -> new PacientDto(pacient.getId(), pacient.getName(), pacient.getCnp());
    }

    private void loadDoctors() {
        // Încărcăm doctorii în ComboBox (asincron ar fi ideal, dar simplificăm)
        if (doctorService != null) {
            doctorService.findAll().thenAccept(doctors -> {
                Platform.runLater(() -> populateDoctorComboBox(doctors));
            });

        }
    }

    private void populateDoctorComboBox(Iterable<Doctor> doctors) {
        List<Doctor> doctorList = new ArrayList<>();
        doctors.forEach(doctorList::add);
        cmbDoctor.setItems(FXCollections.observableArrayList(doctorList));
    }

    @FXML
    public void handleProgrameaza() {
        try {
            // 1. Colectare Date
            PacientDto selectedPacient = tableView.getSelectionModel().getSelectedItem();
            Doctor selectedDoctor = cmbDoctor.getValue();
            LocalDate date = datePicker.getValue();
            String timeStr = txtOra.getText();

            // 2. Validări UI
            if (selectedPacient == null) {
                WindowManager.showError("Eroare", "Selectați un pacient din tabel!");
                return;
            }
            if (selectedDoctor == null || date == null || timeStr.isEmpty()) {
                WindowManager.showError("Eroare", "Completați toate câmpurile (Doctor, Data, Ora)!");
                return;
            }

            // Parsing Ora
            LocalTime time = LocalTime.parse(timeStr);
            LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);

            // 3. Validare Logică (Suprapunere) - Apelăm Service-ul
            // Metoda isDoctorAvailable trebuie să existe în AppointmentService
            boolean isFree = appointmentService.isDoctorAvailable(selectedDoctor.getId(), appointmentDateTime);

            if (!isFree) {
                WindowManager.showError("Indisponibil", "Doctorul are deja o programare la această oră!");
                return;
            }

            Optional<Pacient> p = baseService.findOne(selectedPacient.getId());
            if(p.isPresent()) {Programare newApp = new Programare(
                    selectedDoctor,p.get()
                    ,
                    appointmentDateTime,
                    "Scheduled"
            );
                appointmentService.add(newApp);} else {
                WindowManager.showError("Eroare", "Pacientul selectat nu a fost găsit!");
                return;
            }


            // Save va declanșa notifyObservers în Service, deci fereastra Doctorului se va actualiza automat


            WindowManager.showMessage("Succes", "Programare realizată cu succes!");

            // Optional: Curățare câmpuri
            txtOra.clear();

        } catch (DateTimeParseException e) {
            WindowManager.showError("Format Greșit", "Ora trebuie să fie în format HH:mm (ex: 14:30)");
        } catch (Exception e) {
            WindowManager.showError("Eroare Critică", e.getMessage());
        }
    }
}