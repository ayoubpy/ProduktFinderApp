package produktfinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WarenkorbController {

    @FXML private TableView<WarenkorbItem> table;
    @FXML private TableColumn<WarenkorbItem, String> colName;
    @FXML private TableColumn<WarenkorbItem, Double> colMenge;
    @FXML private TableColumn<WarenkorbItem, Double> colPreis;
    @FXML private TableColumn<WarenkorbItem, String> colRegal;
    @FXML private Label gesamtLabel;

    // Speicherpfad außerhalb von /resources
    private final String JSON_PFAD = System.getProperty("user.home") + "/warenkorb.json";

    @FXML
    public void initialize() {
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProduktName()));
        colMenge.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getMenge()).asObject());
        colPreis.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getGesamtpreis()).asObject());
        colRegal.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRegal()));

        ladeWarenkorb();
    }

    private void ladeWarenkorb() {
        File file = new File(JSON_PFAD);
        if (!file.exists()) {
            return;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<WarenkorbItem>>(){}.getType();
            List<WarenkorbItem> items = new Gson().fromJson(reader, listType);
            table.setItems(FXCollections.observableArrayList(items));
            aktualisiereGesamtsumme();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSchliessen() {
        ((Stage) table.getScene().getWindow()).close();
    }

    @FXML
    private void handleEntfernen() {
        WarenkorbItem ausgewaehlt = table.getSelectionModel().getSelectedItem();
        if (ausgewaehlt != null) {
            table.getItems().remove(ausgewaehlt);
            speichereWarenkorbInJson();
            aktualisiereGesamtsumme();
        }
    }

    private void speichereWarenkorbInJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(JSON_PFAD), StandardCharsets.UTF_8)) {
            gson.toJson(table.getItems(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void aktualisiereGesamtsumme() {
        double summe = table.getItems().stream()
                .mapToDouble(WarenkorbItem::getGesamtpreis)
                .sum();
        gesamtLabel.setText("Gesamt: " + String.format("%.2f", summe) + " €");
    }
}
