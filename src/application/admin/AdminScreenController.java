package application.admin;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Photos;
import application.tags.TagType;
import application.user.User;
import application.utilities.Confirmation;
import application.utilities.DataStorage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * The admin screen controller
 * @author Kaavya Krishna-Kumar
 * @author Abhishek Kondila
 *
 */
public class AdminScreenController implements Initializable {

	/**
	 * The currently selected user.
	 */
	private User currentlySelected;
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> userColumn;

    @FXML
    private Button logoutButton;

    @FXML
    private Button addUserButton;

    @FXML
    private Button deleteUserButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		userColumn.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        userTable.getItems().setAll(Photos.users);
        userTable.getSortOrder().add(userColumn);
        userColumn.setPrefWidth(userTable.getPrefWidth() - 2);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		userTable.setPlaceholder(new Label("There are no users for this application. Click Add User to add one!"));
		userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				currentlySelected = newVal;
			}
		});
	}
	
	@FXML
	public void logout() {
		Photos.logout();
	}
	
	@FXML
	public void close() {
		Photos.windowManager.close();
	}
	
	@FXML
	public void deleteUser() {
		if (currentlySelected == null) {
			Alert alert = new Alert(AlertType.ERROR, "No user selected.");
			alert.show();
			return;
		}
		Confirmation confirm = new Confirmation("Are you sure you want to delete the user \"" + currentlySelected.getUsername() + "\"? Doing so will delete all albums and photos associated with this user.");
		confirm.setTitle("Delete User");
		confirm.run(() -> {
			Photos.users.remove(currentlySelected);
			System.out.println("Currently selected: " + currentlySelected.getUsername());
			DataStorage.deleteDirectory(new File("./userdata/" + currentlySelected.getUsername() + "/"));
			userTable.getSelectionModel().clearSelection();
			userTable.getItems().remove(currentlySelected);
			userTable.refresh();
			userTable.sort();
			currentlySelected = null;
		});
	}
	
	@FXML
	public void addUser() {
		TextInputDialog dialog = new TextInputDialog("");
		 
		dialog.setTitle("Add User");
		dialog.setHeaderText("Enter a username:");
		dialog.setContentText("Username:");
		 
		Optional<String> result = dialog.showAndWait();
		 
		result.ifPresent(name -> {
		    if (Photos.userExists(name) || name.equalsIgnoreCase("admin")) {
		    	Alert alert = new Alert(AlertType.ERROR, "This user already exists.", ButtonType.CLOSE);
				alert.show();
				return;
		    }
		    User user = new User(name);
		    user.init();
		    TagType location = new TagType("location", true);
		    TagType person = new TagType("person", false);
		    user.getTagTypes().add(location);
		    user.getTagTypes().add(person);
		    Photos.users.add(user);
		    userTable.getItems().add(user);
		    userTable.refresh();
		    userTable.sort();
		});
	}

}