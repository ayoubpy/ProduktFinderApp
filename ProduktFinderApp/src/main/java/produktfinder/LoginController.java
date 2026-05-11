package produktfinder;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.util.List;



import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;



    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        List<User> userList = loadUsersFromJson();
        for (User user : userList) {
            if (user.checkCredentials(username, password)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProduktFinder.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = new Stage();

                    scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

                    stage.setScene(scene);
                    stage.setTitle("ProduktFinder");
                    stage.show();

                    ((Stage) usernameField.getScene().getWindow()).close();
                    return;
                } catch (IOException e) {
                    errorLabel.setText("Fehler beim Laden.");
                    return;
                }
            }
        }
        errorLabel.setText("Benutzername oder Passwort falsch.");
    }


    private List<User> loadUsersFromJson() {
        try (InputStream inputStream = getClass().getResourceAsStream("/users.json")) {
            if (inputStream == null) {
                throw new FileNotFoundException("users.json wurde nicht gefunden!");
            }

            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<User>>(){}.getType();
            return new Gson().fromJson(json, listType);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // leere Liste bei Fehler
        }
    }


}
