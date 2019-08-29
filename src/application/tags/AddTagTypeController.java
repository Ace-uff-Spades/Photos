package application.tags;

import java.net.URL;
import java.util.ResourceBundle;

import application.Photos;
import application.user.User;
import application.utilities.Confirmation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * The add tag type controller.
 * @author Kaavya Krishna-Kumar	
 * @author Abhishek Kondila
 *
 */
public class AddTagTypeController implements Initializable {
	 
	/**
	 * The currently selected tag type.
	 */
	private TagType selected = null;
	
	@FXML
    private ListView<TagType> currentTagTypes;

    @FXML
    private RadioButton limitRadioButton;

    @FXML
    private ToggleGroup groupOne;

    @FXML
    private TextField tagTypeTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		currentTagTypes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				selected = newVal;
			}
		});
		ObservableList<TagType> doList = FXCollections.observableArrayList(Photos.currentUser.getTagTypes());
		currentTagTypes.setItems(doList);
		currentTagTypes.refresh();
		
	}

	@FXML
	public void close() {
		Photos.windowManager.close();
		
	}
	
	@FXML
	public void returnToPhoto() {
		Photos.windowManager.openWindow("editScreen");
	}
	
	@FXML
	public void logout() {
		Photos.logout();
	}
	
	@FXML
	public void deleteTag() {
		if (selected != null) {
			Confirmation confirm = new Confirmation("Are you sure you want to delete this tag type?");
			confirm.run(() ->{
				Photos.currentUser.getTagTypes().remove(selected);
				currentTagTypes.getItems().remove(selected);
				currentTagTypes.refresh();
			});
		}
	}
	
	@FXML
	public void addTag() {
		User u = Photos.currentUser;
		if (tagTypeTextField.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR,"Please enter a tag name.");
			alert.showAndWait();
			return;
		}
		if (groupOne.getSelectedToggle() == null) {
			Alert alert = new Alert(AlertType.ERROR,"Please select whether this tag can be used more than once for a photo.");
			alert.showAndWait();
			return;
		}
		String name = tagTypeTextField.getText();
		for (TagType t : u.getTagTypes()) {
			if (t.getName().equalsIgnoreCase(name)) {
				Alert alert = new Alert(AlertType.ERROR,"This tag type already exists.");
				alert.showAndWait();
				return;
			}
		}
		TagType add = new TagType(name, limitRadioButton.isSelected());
		u.getTagTypes().add(add);
		currentTagTypes.getItems().add(add);
		currentTagTypes.refresh();
	}

}
