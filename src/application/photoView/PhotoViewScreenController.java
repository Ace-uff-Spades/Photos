package application.photoView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Photos;
import application.Album.Album;
import application.photoEdit.Photo;
import application.tags.Tag;
import application.user.User;
import application.utilities.Confirmation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Represents the controller for the Photo View Window.
 * 
 * @author Kaavya Krishna-Kumar	
 * @author Abhishek Kondila
 *
 */

public class PhotoViewScreenController implements Initializable {

	/**
	 * The cached photos, to decrease lag.
	 */
	HashMap<Photo, Group> cachedPhotos = new HashMap<>();
	
	Group selectedGroup = null;

	@FXML
	private Button addPhotoButton;

	@FXML
	private Button movePhotoButton;

	@FXML
	private Button copyPhotoButton;

	@FXML
	private Button deletePhotoButton;

	@FXML
	private ScrollPane photoScrollPane;

	@FXML
	private Button logoutButton;

	@FXML
	private Button albumListButton;

	@FXML
	private Button addTagButton;

	@FXML
	private Button deleteTagButton;

	@FXML
	private Button filterPhotobutton;

	@FXML
	private ScrollPane tagScrollPane;

	@FXML
	private Text albumName;
	
	@FXML
	private Label loadingMessage;
	
	@FXML
	private Button copyButton;
	
	@FXML
	private Button moveButton;
	
	@FXML
	private Button delButton;
	
	@FXML
	private Button editButton;
	
	@FXML
	private Button createAlbumButton;

	@FXML
	public void logout() {
		Photos.logout();
	}
	
	@FXML
	public void createAlbum() {
		User user = Photos.currentUser;
		ArrayList<Photo> currentPhotos = Photos.getCurrentPhotos();
		
		TextInputDialog dialog = new TextInputDialog("");

		dialog.setTitle("Create New Album from filter");
		dialog.setHeaderText("New Album Name");
		dialog.setContentText("Name:");

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> {
			if (user.albumExists(name)) {
				Alert alert = new Alert(AlertType.ERROR, "This album already exist.", ButtonType.CLOSE);
				alert.show();
				return;
			}
			Confirmation confirm = new Confirmation("Are you sure you want to create this album?");
			confirm.run(() -> {
				Album temp = user.addAlbum(name);
				for(Photo a: currentPhotos) {
					 temp.addPhoto(a);
				}
				Photos.currentAlbum = temp;
			});
		});
		
	}
	
	@FXML
	public void filterList() {
		Dialog<HashMap<String, String>> dialog = new Dialog<>();
	    dialog.setTitle("Search Photos");
	    ButtonType searchButton = new ButtonType("Search", ButtonData.OK_DONE);
	    dialog.getDialogPane().getButtonTypes().addAll(searchButton, ButtonType.CANCEL);

	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(20, 150, 10, 10));
	    
	    TextField tag1key = new TextField();
	    tag1key.setPromptText("key");
	    TextField tag1value = new TextField();
	    tag1value.setPromptText("value");
	    TextField tag2key = new TextField();
	    tag2key.setPromptText("key");
	    TextField tag2value = new TextField();
	    tag2value.setPromptText("value");
	    
	   final ComboBox<String> comboBox = new ComboBox<String>();
	   comboBox.getItems().addAll(
	            "AND",
	            "OR"
	        );
	    	
	    grid.add(new Label("tag 1:"), 0, 0);
	    grid.add(tag1key, 1, 0);
	    grid.add(tag1value, 1, 1);
	    grid.add(comboBox, 2, 0);
	    grid.add(new Label("tag 2:"), 3, 0);
	    grid.add(tag2key, 4, 0);
	    grid.add(tag2value, 4, 1);
	    
	    dialog.getDialogPane().setContent(grid);

	    dialog.setResultConverter(dialogButton -> {
	        if (dialogButton == searchButton) {
	            HashMap<String, String> result = new HashMap<>();
	            result.put("tag1key", tag1key.getText());
	            result.put("tag1value", tag1value.getText());
	            result.put("tag2key", tag2key.getText());
	            result.put("tag2value", tag2value.getText());
	            result.put("modifier", comboBox.getValue());
	            return result;
	        }
	        return null;
	    });
	  
	    Optional<HashMap<String, String>> result = dialog.showAndWait();
	    
	    result.ifPresent(r -> {
	    	System.out.println("tag1=" + r.get("tag1key") + " " + r.get("tag1value") + ", modifier=" + r.get("modifier") + ", tag2=" + r.get("tag2key") + " " + r.get("tag2value"));
	    	if (r.get("tag1key").equals("") && r.get("tag1value").equals("") && r.get("tag2key").equals("") && r.get("tag2value").equals("")) {
	    		this.generatePhotoIcons(false, null, null, false);
	    		return;
	    	}
	    	if (r.get("tag1key").equals("") && r.get("tag1value").equals("")) {
		    	Alert alert = new Alert(AlertType.ERROR, "Missing tag1 key and value", ButtonType.CLOSE);
				alert.show();
				return;
	    	}
	    	if (r.get("tag1value").equals("")) {
		    	Alert alert = new Alert(AlertType.ERROR, "Missing tag1value", ButtonType.CLOSE);
				alert.show();
				return;
	    	}
	    	if (r.get("tag1key").equals("")) {
		    	Alert alert = new Alert(AlertType.ERROR, "Missing tag1value", ButtonType.CLOSE);
				alert.show();
				return;
	    	}
	    	if(r.get("modifier") != null && (r.get("tag2key").equals("") || r.get("tag2value").equals(""))) {
	    		Alert alert = new Alert(AlertType.ERROR, "Missing tag2 field", ButtonType.CLOSE);
				alert.show();
				return;
	    	}
	    	if(r.get("modifier") == null && (!r.get("tag2key").equals("") || !r.get("tag2value").equals(""))) {
	    		Alert alert = new Alert(AlertType.ERROR, "Missing modifier", ButtonType.CLOSE);
				alert.show();
				return;
	    	}
	    	
	    	Tag one = new Tag(r.get("tag1key"), r.get("tag1value"));
	    	Tag two = null;
	    	if (r.get("modifier") != null) {
	    		two = new Tag(r.get("tag2key"), r.get("tag2value"));
	    	}
	    	boolean and = r.get("modifier") != null && r.get("modifier").equals("AND");
	    	this.generatePhotoIcons(true, one, two, and);
	    });
	}

	@FXML
	public void addPhoto() {
		Photo newPhoto = Photos.getFileName();
		if (newPhoto == null) {
			return;
		}
		System.out.println(Photos.currentAlbum.getAlbumName());
		Photos.currentAlbum.addPhoto(newPhoto);
		generatePhotoIcons(false, null, null, false);
	}

	@FXML
	public void copyPhoto() {
		User user = Photos.currentUser;
		TextInputDialog dialog = new TextInputDialog("");

		dialog.setTitle("Copy Photo");
		dialog.setHeaderText("Enter album name to copy to:");
		dialog.setContentText("Name:");

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> {
			if (!user.albumExists(name)) {
				Alert alert = new Alert(AlertType.ERROR, "This album doesn't exist.", ButtonType.CLOSE);
				alert.show();
				return;
			}
			Confirmation confirm = new Confirmation("Are you sure you want to add this photo to \"" + name +"\"?");
			confirm.run(() -> {
				for (Album a : user.getAlbums()) {
					if (a.getAlbumName().equalsIgnoreCase(name)) {
						a.addPhoto(Photos.currentPhoto);
						break;
					}
				}
			});
		});
	}

	@FXML
	public void movePhoto() {
		User user = Photos.currentUser;
		TextInputDialog dialog = new TextInputDialog("");

		dialog.setTitle("Move Photo");
		dialog.setHeaderText("Enter album name to move to:");
		dialog.setContentText("Name:");

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> {
			if (!user.albumExists(name)) {
				Alert alert = new Alert(AlertType.ERROR, "This album doesn't exist.", ButtonType.CLOSE);
				alert.show();
				return;
			}
			Confirmation confirm = new Confirmation("Are you sure you want to move this photo to \"" + name +"\"?");
			confirm.run(() -> {
				for (Album a : user.getAlbums()) {
					if (a.getAlbumName().equalsIgnoreCase(name)) {
						a.addPhoto(Photos.currentPhoto);
						Photos.currentAlbum.remove(Photos.currentPhoto);
						cachedPhotos.remove(Photos.currentPhoto);
						generatePhotoIcons(false, null, null, false);
						Photos.currentPhoto = null;
						break;
					}
				}
			});
		});
	}

	@FXML
	public void deletePhoto() {
		Confirmation confirm = new Confirmation("Are you sure you want to delete this photo?");
		confirm.run(() -> {
			Photos.currentAlbum.remove(Photos.currentPhoto);
			cachedPhotos.remove(Photos.currentPhoto);
			generatePhotoIcons(false, null, null, false);
			Photos.currentPhoto = null;
		});
	}

	@FXML
	public void editPhoto() {
		ImageView prev = (ImageView) selectedGroup.getChildren().get(0);
		Text prevT = (Text) selectedGroup.getChildren().get(1);
		prevT.setFill(Color.BLACK);
		prev.setOpacity(1);
		moveButton.setDisable(true);
		delButton.setDisable(true);
		copyButton.setDisable(true);
		editButton.setDisable(true);
		selectedGroup = null;
		try {
			Photos.windowManager.addWindow("editScreen", FXMLLoader.load(Photos.class.getResource("photoEdit/PhotoEditScreen.fxml")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Photos.windowManager.openWindow("editScreen");
	}
	
	@FXML
	public void addTag() {

	}

	@FXML
	public void deleteTag() {

	}

	@FXML
	public void albumList() {
		try {
			Photos.windowManager.addWindow("albumScreen", FXMLLoader.load(Photos.class.getResource("album/AlbumScreen.fxml")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Photos.windowManager.openWindow("albumScreen");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		albumName.setText(Photos.currentAlbum.getAlbumName());
		moveButton.setDisable(true);
		delButton.setDisable(true);
		copyButton.setDisable(true);
		editButton.setDisable(true);
		createAlbumButton.setDisable(true);
		generatePhotoIcons(false, null, null, false);
	}
	
	/**
	 * This filter checks if a photo passes a filter
	 * @param a is the photo you want to check
	 * @param one is the first tag
	 * @param two is the second tag, if false only filter for tag one
	 * @param and is a bool, true for "AND" and false for "OR" 
	 * @return
	 */
	private boolean filterPhoto(Photo a, Tag one, Tag two, boolean and) {
		boolean oneTrue = false;
		boolean twoTrue = false;
		ArrayList<Tag> tags = a.getTags();
		for( Tag b: tags) {
			if (b.getKey().equals(one.getKey()) && b.getValue().equals(one.getValue())){
				oneTrue = true;
				break;
			}
		}
		//if two is null return false 
		if(two == null) { 
			return oneTrue;
		}
		for( Tag b: tags) {
			if (b.getKey().equals(two.getKey()) && b.getValue().equals(two.getValue())){
				twoTrue = true;
				break;
			}
		}
		if(and) { //AND
			if(oneTrue && twoTrue) {
				return true;
			} else {
				return false;
			}
		} else { //OR
			if(oneTrue || twoTrue) {
				return true;
			} else {
				return false;
			}
		}
		
	}

	@FXML
	public void close() {
		Photos.windowManager.close();
		
	}
	
	public void generatePhotoIcons(boolean filter, Tag one, Tag two, boolean and) {
		Photos.currentPhotos.clear();
		Thread t = new Thread(() -> {
		Album album = Photos.currentAlbum;
		if (album == null) {
			return;
		}
		int size = album.getPhotos().size();
		if (size == 0) {
			Platform.runLater(()->photoScrollPane.setContent(null));
			return;
		} else {
			if (filter) {
		    	createAlbumButton.setDisable(false);
			}
		}
		int rows = size / 4 + 1;
		Group boxes = new Group();
		int index = 0;
		for (int i = 0; i < rows; i++) {
			HBox box = new HBox(14);
			double maxHeight = 100;
			for (int j = 0; j < 4; j++) {
				if (index >= size) {
					break;
				}
				Photo photo = album.getPhotos().get(index++);
				if (filter && !filterPhoto(photo, one, two, and)) {
					j--;
					continue;
				}
				Photos.currentPhotos.add(photo);
				Group group = cachedPhotos.get(photo);
				if (group == null) {
					group = new Group();
					Image image = new Image("file:" + photo.getPath());
					System.out.println(photo.getPath());
					ImageView iv1 = new ImageView();
					iv1.setPreserveRatio(true);
					iv1.setFitHeight(100);
					iv1.setFitWidth(100);
					iv1.setImage(image);
					Text text = new Text(photo.getName());
					text.setLayoutX(10);
					double height = iv1.getBoundsInLocal().getHeight();
					if (height > maxHeight) {
						maxHeight = height;
					}
					text.setLayoutY(iv1.getY() + height + 15);
					group.getChildren().add(iv1);
					group.getChildren().add(text);
					group.setLayoutX(20);
					Tooltip tooltip = new Tooltip("Select " + photo.getName());
					iv1.setOnMouseEntered((MouseEvent e) -> {
						iv1.setOpacity(0.5);
						tooltip.show(iv1, e.getScreenX(), e.getScreenY() + 15);
					});
					iv1.setOnMouseExited((MouseEvent e) -> {
						if (selectedGroup != null) {
							ImageView curr = (ImageView) selectedGroup.getChildren().get(0);
							if (curr != iv1) {
								iv1.setOpacity(1);
							}
						} else {
							iv1.setOpacity(1);
						}
						tooltip.hide();
					});
					final Group g = group;
					iv1.setOnMouseClicked((MouseEvent e) -> {
						if (selectedGroup != null && selectedGroup != g) {
							ImageView prev = (ImageView) selectedGroup.getChildren().get(0);
							Text prevT = (Text) selectedGroup.getChildren().get(1);
							prevT.setFill(Color.BLACK);
							prev.setOpacity(1);
						} else {
							moveButton.setDisable(false);
							delButton.setDisable(false);
							copyButton.setDisable(false);
							editButton.setDisable(false);
						}
						// user clicked on an album
						System.out.println("Clicked"); // change
						text.setFill(Color.RED);
						selectedGroup = g;
						Photos.currentPhoto = photo;

					});
					cachedPhotos.put(photo, group);
				}
				box.getChildren().add(group);
			}
			box.setLayoutY(i * (maxHeight + 20));
			boxes.getChildren().add(box);
		}
		Platform.runLater(()->{
		photoScrollPane.setContent(boxes);});
		
		});
		Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
		    public void uncaughtException(Thread th, Throwable ex) {
		    }
		};
		t.setUncaughtExceptionHandler(h);
		t.start();
	}
}