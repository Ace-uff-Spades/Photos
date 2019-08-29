package application.login;

import java.net.URL;
import java.util.ResourceBundle;

import application.Photos;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The login screen controller.
 * @author Kaavya Krishna-Kumar
 * @author Abhishek Kondila 
 *
 */
public class LoginScreenController implements Initializable {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="usernameField"
    private TextField usernameField; // Value injected by FXMLLoader

    @FXML // fx:id="loginButton"
    private Button loginButton; // Value injected by FXMLLoader
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		usernameField.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
	        if (ev.getCode() == KeyCode.ENTER) {
	           loginButton.fire();
	           ev.consume(); 
	        }
	    });
	}

	@FXML
	public void close() {
		Photos.windowManager.close();
	}
	
	@FXML
	void login() {
		String username = usernameField.getText();
		if (username.isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR, "You need to enter a username before logging in.", ButtonType.CLOSE);
			alert.show();
			return;
		}
		if (username.equalsIgnoreCase("admin")) {
			Photos.windowManager.openWindow("adminScreen");
			return;
		}
		if (!Photos.userExists(username)) {
			Alert alert = new Alert(AlertType.ERROR, "\"" + username + "\" is not a user on this application.", ButtonType.CLOSE);
			alert.show();
			return;
		}
		Photos.login(username);
	}

}
