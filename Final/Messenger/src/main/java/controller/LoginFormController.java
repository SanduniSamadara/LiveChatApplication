package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginFormController {

    public JFXTextField txtUsername;
    public Button btnLogin;
    public AnchorPane loginFormContext;

    private static final Logger logger = Logger.getLogger(LoginFormController.class.getName());

    public void btnLoginOnAction(ActionEvent actionEvent) throws IOException {
        String username = txtUsername.getText();

        // Log the username
        logger.log(Level.INFO, "Login attempt with username: " + username); //logs the username using the Logger class

        if (!username.isEmpty()) {
            setUI("MainForm");//switch to the main application form
        }
    }

    private void setUI(String URI) throws IOException {
        Stage stage1 = (Stage) loginFormContext.getScene().getWindow();
        stage1.close();//closes the current stage

        URL resource = getClass().getResource("/view/" + URI + ".fxml");
        if (resource == null) {
            throw new IllegalArgumentException("FXML file not found: " + URI);
        }
        FXMLLoader loader = new FXMLLoader(resource);//loads the specified FXML file
        Parent root = loader.load();

        MainFormController controller = loader.getController();
        controller.txtName.setText(txtUsername.getText());
        Scene scene = new Scene(root);
        Stage stage2 = new Stage();
        stage2.setTitle("Live Chat Application");
        stage2.setScene(scene);
        stage2.centerOnScreen();
        stage2.show();
    }

    public static void main(String[] args) {
        // Set log level for the logger
        logger.setLevel(Level.ALL);
    }
}
