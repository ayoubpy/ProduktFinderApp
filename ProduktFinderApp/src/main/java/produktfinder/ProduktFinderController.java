
package produktfinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;


import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class ProduktFinderController {

    @FXML private ComboBox<String> kategorieBox;
    @FXML private TextField nameField;
    @FXML private TextField mengeField;
    @FXML private Button berechnenButton;
    @FXML private Button suchenButton;
    @FXML private Label meldungLabel;
    @FXML private Label regalLabel;
    @FXML private Label preisProKgLabel;
    @FXML private Label gesamtpreisLabel;
    @FXML private FlowPane bildContainer;

    private HashMap<String, Produkt> produkte = new HashMap<>();
    private Produkt ausgewaehltesProdukt = null;

    @FXML
    public void initialize() {
        setInteraktionAktiv(false);
        ladeProdukteAusJson("/produkte.json");

        kategorieBox.getItems().addAll("Alle", "Obst", "Gemüse", "Fleisch", "Getränke");
        kategorieBox.setOnAction(e -> zeigeProdukteNachKategorie(kategorieBox.getValue()));
        kategorieBox.setValue("Alle");

        suchenButton.setOnAction(e -> onSuchen());
        zeigeProdukteNachKategorie("Alle");
    }

    private void ladeProdukteAusJson(String pfad) {
        try (InputStream input = getClass().getResourceAsStream(pfad)) {
            if (input == null) {
                throw new FileNotFoundException("Datei nicht gefunden: " + pfad);
            }

            InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
            Type type = new TypeToken<HashMap<String, Produkt>>() {}.getType();
            produkte = new Gson().fromJson(reader, type);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void zeigeProdukteNachKategorie(String kategorie) {
        bildContainer.getChildren().clear();
        for (Produkt p : produkte.values()) {
            if (kategorie.equals("Alle") || p.getKategorie().equals(kategorie)) {
                try {
                    FileInputStream input = new FileInputStream(p.getBildpfad());
                    Image image = new Image(input);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(100);
                    imageView.setPreserveRatio(true);
                    addAnimation(imageView);

                    imageView.setOnMouseClicked(event -> {
                        nameField.setText(p.getName());
                        meldungLabel.setText("✔ Produkt gefunden: " + p.getName());
                        meldungLabel.setTextFill(Color.GREEN);
                        preisProKgLabel.setText("Preis/kg: " + p.getPreis() + " €");
                        regalLabel.setText("Regal: " + p.getRegal());
                        ausgewaehltesProdukt = p;
                        mengeField.setText("1");
                        setInteraktionAktiv(true);
                    });

                    bildContainer.getChildren().add(imageView);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setInteraktionAktiv(boolean aktiv) {
        mengeField.setDisable(!aktiv);
        berechnenButton.setDisable(!aktiv);
    }

    @FXML
    public void berechnePreis() {
        if (ausgewaehltesProdukt != null && !mengeField.getText().isEmpty()) {
            try {
                double menge = Double.parseDouble(mengeField.getText());
                double gesamtpreis = menge * ausgewaehltesProdukt.getPreis();
                gesamtpreisLabel.setText("Gesamtpreis: " + String.format("%.2f", gesamtpreis) + " €");
            } catch (NumberFormatException e) {
                meldungLabel.setText("Bitte eine gültige Menge eingeben.");
            }
        }
    }

    @FXML
    public void handleInWarenkorb() {
        if (ausgewaehltesProdukt != null && !mengeField.getText().isEmpty()) {
            try {
                double menge = Double.parseDouble(mengeField.getText());
                double gesamtpreis = menge * ausgewaehltesProdukt.getPreis();
                WarenkorbItem neuesItem = new WarenkorbItem(ausgewaehltesProdukt.getName(), menge, gesamtpreis, ausgewaehltesProdukt.getRegal());

                List<WarenkorbItem> aktuellerWarenkorb = ladeAktuellenWarenkorb();
                aktuellerWarenkorb.add(neuesItem);

                speichereWarenkorbInJson(aktuellerWarenkorb);

                meldungLabel.setText("✔ Zum Warenkorb hinzugefügt.");
                meldungLabel.setTextFill(Color.GREEN);
            } catch (NumberFormatException e) {
                meldungLabel.setText("Ungültige Menge!");
                meldungLabel.setTextFill(Color.RED);
            }
        } else {
            meldungLabel.setText("Kein Produkt ausgewählt.");
            meldungLabel.setTextFill(Color.RED);
        }
    }

    private List<WarenkorbItem> ladeAktuellenWarenkorb() {
        File file = new File(System.getProperty("user.home"), "/warenkorb.json");
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<WarenkorbItem>>() {}.getType();
            return new Gson().fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    private void speichereWarenkorbInJson(List<WarenkorbItem> warenkorb) {
        File file = new File(System.getProperty("user.home"), "/warenkorb.json");

        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(warenkorb, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addAnimation(ImageView imageView) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), imageView);
        imageView.setOnMouseEntered(e -> {
            st.setToX(1.2);
            st.setToY(1.2);
            st.playFromStart();
            imageView.setCursor(Cursor.HAND);
            imageView.setEffect(new DropShadow(10, Color.DARKGRAY));
        });

        imageView.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
            imageView.setEffect(null);
        });
    }

    @FXML
    private void onSuchen() {
        String name = nameField.getText().toLowerCase();
        String kategorie = kategorieBox.getValue();
        Produkt produkt = produkte.get(name);

        if (produkt != null && (kategorie.equals("Alle") || produkt.getKategorie().equals(kategorie))) {
            ausgewaehltesProdukt = produkt;
            meldungLabel.setText("✔ Produkt gefunden: " + produkt.getName());
            meldungLabel.setTextFill(Color.GREEN);
            regalLabel.setText("Regal: " + produkt.getRegal());
            preisProKgLabel.setText("Preis/kg: " + produkt.getPreis() + " €");
            mengeField.setText("1");
            double gesamt = produkt.getPreis() * 1;
            gesamtpreisLabel.setText("Gesamtpreis: " + String.format("%.2f", gesamt) + " €");
            setInteraktionAktiv(true);
        } else {
            meldungLabel.setText("❌ Produkt nicht gefunden oder falsche Kategorie.");
            meldungLabel.setTextFill(Color.RED);
            regalLabel.setText("");
            preisProKgLabel.setText("");
            gesamtpreisLabel.setText("");
            setInteraktionAktiv(false);
        }
    }

    @FXML
    private void zeigeWarenkorb() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/warenkorb.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Warenkorb");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
