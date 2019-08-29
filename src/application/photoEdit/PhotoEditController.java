package application.photoEdit;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Photos;
import application.tags.Tag;
import application.tags.TagType;
import application.user.User;
import application.utilities.Confirmation;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * The Photo Edit Controller.
 * @author Kaavya Krishna-Kumar
 * @author Abhishek Kondila
 *
 */
public class PhotoEditController implements Initializable {

	/**
	 * Current selected tag
	 */
	private Tag selectedTag;
	
	/**
	 * The changelistener.
	 */
	private ChangeListener<String> cl = null;

	@FXML
	private Button previousButton;

	@FXML
	private Button nextButton;

	@FXML
	private Text dateScrollPane;

	@FXML
	private TextArea captionScrollPane;

	@FXML
	private ImageView imageView;

	@FXML
	private Button logoutButton;

	@FXML
	private Button albumListButton;

	@FXML
	private Button saveChangesButton;

	@FXML
	private Button configureTagType;

	@FXML
	private TableView<Tag> tagTable;

	@FXML
	private TableColumn<Tag, String> tagNameColumn;

	@FXML
	private TableColumn<Tag, String> valueColumn;

	@FXML
	public void nextPhoto() {
		int i = Photos.currentAlbum.getPhotos().indexOf(Photos.currentPhoto);
		if (i == Photos.currentAlbum.getPhotos().size() - 1) {
			Photos.currentPhoto = Photos.currentAlbum.getPhotos().get(0);
		} else {
			Photos.currentPhoto = Photos.currentAlbum.getPhotos().get(i + 1);
		}
		init();
	}

	@FXML
	public void configureTagType() {
		try {
			Photos.windowManager.addWindow("configureTags", FXMLLoader.load(Photos.class.getResource("tags/AddTagType.fxml")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Photos.windowManager.openWindow("configureTags");
	}

	@FXML
	public void previousPhoto() {
		int i = Photos.currentAlbum.getPhotos().indexOf(Photos.currentPhoto);
		if (i == 0) {
			Photos.currentPhoto = Photos.currentAlbum.getPhotos()
					.get(Photos.currentAlbum.getPhotos().size() - 1);
		} else {
			Photos.currentPhoto = Photos.currentAlbum.getPhotos().get(i - 1);
		}
		init();
	}

	@FXML
	public void addTag() {
		User user = Photos.currentUser;
		ArrayList<TagType> tagType = new ArrayList<TagType>(user.getTagTypes());
		tagType.add(new TagType("Custom", false));
		ChoiceDialog<TagType> dialog = new ChoiceDialog<TagType>(user.getTagTypes().get(0), tagType);
		dialog.setTitle("Add a Tag");
		dialog.setHeaderText("Select a tag type");
		dialog.setContentText("Choose your tag type:");

		Optional<TagType> result = dialog.showAndWait();
		if (result.isPresent()) {
			sendTagData(result.get());
		}
	}

	/**
	 * Sends the tag data.
	 * @param result the tagtype result.
	 */
	public void sendTagData(TagType result) {
		boolean custom = result.getName().equals("Custom");
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Tag Info");
		dialog.setHeaderText("Enter your tag data");
		dialog.setGraphic(null);

		ButtonType loginButtonType = new ButtonType("Add Tag", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField tagName = new TextField();
		tagName.setPromptText("Tag name");
		if (!custom) {
			tagName.setText(result.getName());
			tagName.setDisable(true);
		}
		TextField tagValue = new TextField();
		tagValue.setPromptText("Tag value");

		grid.add(new Label("Tag Name:"), 0, 0);
		grid.add(tagName, 1, 0);
		grid.add(new Label("Tag Value:"), 0, 1);
		grid.add(tagValue, 1, 1);

		Node addTagButton = dialog.getDialogPane().lookupButton(loginButtonType);
		addTagButton.setDisable(true);

		tagName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!tagValue.getText().isEmpty())
				addTagButton.setDisable(newValue.trim().isEmpty());
		});
		tagValue.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!tagName.getText().isEmpty())
				addTagButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> {
			if (custom)
				tagName.requestFocus();
			else
				tagValue.requestFocus();
		});

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(tagName.getText(), tagValue.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> res = dialog.showAndWait();

		res.ifPresent(tag -> {
			System.out.println("TagName=" + tag.getKey() + ", TagValue=" + tag.getValue());
			Tag add = new Tag(tag.getKey(), tag.getValue());
			if (Photos.currentPhoto.canAddTag(add)) {
				Photos.currentPhoto.getTags().add(add);
				tagTable.getItems().add(add);
				tagTable.refresh();
			} else {
				Alert alert = new Alert(AlertType.ERROR, "This tag already exists for this photo.");
				alert.showAndWait();
			}
		});
	}
	
	@FXML
	public void deleteTag() {
		if (selectedTag != null) {
			Confirmation confirm = new Confirmation("Are you sure you want to delete this tag?");
			confirm.run(() -> {
				Photos.currentPhoto.getTags().remove(selectedTag);
				tagTable.getItems().remove(selectedTag);
				tagTable.refresh();
			});
		}
	}

	@FXML
	public void saveChanges() {
		Photos.currentPhoto.setCaption(captionScrollPane.getText());
	}

	@FXML
	public void logout() {
		Photos.logout();
	}

	@FXML
	public void photoList() {
		Photos.currentPhoto = null;
		Photos.windowManager.openWindow("photoViewScreen");
	}

	/**
	 * This method clears the date location and scroll text fields
	 */
	public void updateWindow() {
		Image image = new Image("file:" + Photos.currentPhoto.getPath());
		imageView.setPreserveRatio(true);
		imageView.setFitHeight(193);
		imageView.setFitWidth(269.0);
		imageView.setImage(image);
		dateScrollPane.setText(Photos.currentPhoto.getPhotoDate());
		captionScrollPane.setText(Photos.currentPhoto.getCaption());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
			init();
	}

	@FXML
	public void close() {
		Photos.windowManager.close();
		
	}
	
	/**
	 * Reinitializes the data.
	 */
	public void init() {
		if (cl != null) {
			captionScrollPane.textProperty().removeListener(cl);
		}
		cl = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newVal) {
				System.out.println("Doing this..");
				if (newVal != null) {
					Photos.currentPhoto.setCaption(captionScrollPane.getText());
				}
			}
		};
		captionScrollPane.textProperty().addListener(cl);
		tagNameColumn.setCellValueFactory(new PropertyValueFactory<Tag, String>("key"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<Tag, String>("value"));
		tagTable.getItems().setAll(Photos.currentPhoto.getTags());
		tagTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				selectedTag = newVal;
			}
		});
		tagTable.refresh();
		updateWindow();
	}

}