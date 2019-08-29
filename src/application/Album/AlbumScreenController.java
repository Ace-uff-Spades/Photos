package application.Album;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Photos;
import application.user.User;
import application.utilities.Confirmation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Represents the controller for the Album List Window.
 * 
 * @author Kaavya Krishna-Kumar
 * @author Abhishek Kondila
 *
 */
public class AlbumScreenController implements Initializable {
	
	/**
	 * Represents the folder image.
	 */
	private static final String IMAGE_PATH = "file:./resources/folder-icon.jpg";

	@FXML
	private Button createButton;
	
	@FXML
	private Button searchButton;

	@FXML
	private Button deleteButton;

	@FXML
	private ScrollPane scrollPane;

	@FXML
	private Button logoutButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		generateAlbumIcons(false, "");
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
	public void addAlbum() {
		User user = Photos.currentUser;
		TextInputDialog dialog = new TextInputDialog("");
		 
		dialog.setTitle("Add Album");
		dialog.setHeaderText("Enter an album name:");
		dialog.setContentText("Name:");
		 
		Optional<String> result = dialog.showAndWait();
		 
		result.ifPresent(name -> {
		    if (user.albumExists(name)) {
		    	Alert alert = new Alert(AlertType.ERROR, "This album already exists.", ButtonType.CLOSE);
				alert.show();
				return;
		    }
		    user.addAlbum(name);
		    generateAlbumIcons(false, "");
		});
	}
	
	@FXML
	public void deleteAlbum() {
		User user = Photos.currentUser;
		TextInputDialog dialog = new TextInputDialog("");
		 
		dialog.setTitle("Delete Album");
		dialog.setHeaderText("Enter album name to delete:");
		dialog.setContentText("Name:");
		 
		Optional<String> result = dialog.showAndWait();
		 
		result.ifPresent(name -> {
		    if (!user.albumExists(name)) {
		    	Alert alert = new Alert(AlertType.ERROR, "This album doesn't exist.", ButtonType.CLOSE);
				alert.show();
				return;
		    }
		    Confirmation confirm = new Confirmation("Are you sure you want to delete the album \"" + name + "\"? All of this albums contents will be lost.");
		    confirm.run(() -> {
			    user.deleteAlbum(name);
			    generateAlbumIcons(false, "");
		    });
		});
	}
	
	/**
	 * Renames an album.
	 * @param album the album
	 */
	public void renameAlbum(Album album) {
		User user = Photos.currentUser;
		TextInputDialog dialog = new TextInputDialog("");
		 
		dialog.setTitle("Renaming album \"" + album.getAlbumName() + "\"");
		dialog.setHeaderText("Enter new album name:");
		dialog.setContentText("Name:");
		 
		Optional<String> result = dialog.showAndWait();
		 
		result.ifPresent(name -> {
		    if (user.albumExists(name) || name.isEmpty()) {
		    	Alert alert = new Alert(AlertType.ERROR, "This album name exists or is invalid.", ButtonType.CLOSE);
				alert.show();
				return;
		    }
		    Confirmation confirm = new Confirmation("Are you sure you want to rename the album \"" + album.getAlbumName() + "\" to \"" + name + "\"?");
		    confirm.run(() -> {
			    album.rename(name);
			    generateAlbumIcons(false, "");
		    });
		});
	}
	
	@FXML
	public void search() {
		TextInputDialog dialog = new TextInputDialog("");
		 
		dialog.setTitle("Filter Albums");
		dialog.setHeaderText("Enter an album name (leave blank to unfilter):");
		dialog.setContentText("Filter:");
		 
		Optional<String> result = dialog.showAndWait();
		 
		result.ifPresent(filter -> {
		    generateAlbumIcons(true, filter);
		});
	}
	
	
	
	/**
	 * Generates the album icons on the scroll pane.
	 * @param filter the Filter.
	 * @param check the check.
	 */
	public void generateAlbumIcons(boolean filter, String check) {
		User user = Photos.currentUser;
		if (user == null) {
			return;
		}
		int size = user.getAlbums().size();
		if (size == 0) {
			scrollPane.setContent(null);
			return;
		}
		int rows = size / 4 + 1;
		Group boxes = new Group();
		int index = 0;
		for (int i = 0; i < rows; i++) {
			HBox box = new HBox(14);
			for (int j = 0; j < 4; j++) {
				if (index >= size) {
					break;
				}
				Album album = user.getAlbums().get(index++);
				if (filter && !album.getAlbumName().toLowerCase().contains(check.toLowerCase())) {
					j--;
					continue;
				}
				Group group = new Group();
				Image image = new Image(IMAGE_PATH);
				ImageView iv1 = new ImageView();
				iv1.setImage(image);
				iv1.setPreserveRatio(false);
				iv1.setFitHeight(75);
				iv1.setFitWidth(75);
				Tooltip tooltip = new Tooltip("Open " + album.getAlbumName());
				iv1.setOnMouseEntered((MouseEvent e) -> {
		            iv1.setOpacity(0.5);
		            tooltip.show(iv1, e.getScreenX(), e.getScreenY() + 15);
		        });
				iv1.setOnMouseExited((MouseEvent e) -> {
		            iv1.setOpacity(1);
		            tooltip.hide();
		        });
				iv1.setOnMouseClicked((MouseEvent e) -> {
					//user clicked on an album
		            System.out.println("Clicked " + album.getAlbumName() + " Path: " + album.getPath()); // change functionality
		            Photos.currentAlbum = album;
		            try {
						Photos.windowManager.addWindow("photoViewScreen", FXMLLoader.load(Photos.class.getResource("photoView/PhotoViewScreen.fxml")));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		            Photos.windowManager.openWindow("photoViewScreen");
		        });
				Label text = new Label(album.getAlbumName());
				Label photos = new Label("Total Photos: " + album.getPhotos().size());
				Label start = new Label("Start Date: " + album.getStartDate());
				Label end = new Label("End Date: " + album.getEndDate());
				start.setLayoutX(10);
				start.setLayoutY(110);
				end.setLayoutX(10);
				end.setLayoutY(125);
				photos.setLayoutX(10);
				photos.setLayoutY(95);
				Tooltip rename = new Tooltip("Click to rename");
				text.setLayoutX(10);
				text.setLayoutY(80);
				text.setOnMouseEntered((MouseEvent e) -> {
		            text.setOpacity(0.5);
		            rename.show(iv1, e.getScreenX(), e.getScreenY() + 15);
		        });
				text.setOnMouseExited((MouseEvent e) -> {
		            text.setOpacity(1);
		            rename.hide();
		        });
				text.setOnMouseClicked((MouseEvent e) -> {
		            renameAlbum(album);
		        });
				group.getChildren().add(iv1);
				group.getChildren().add(text);
				group.getChildren().add(photos);
				group.getChildren().add(start);
				group.getChildren().add(end);
				box.getChildren().add(group);
			}
			box.setLayoutY(i * 130);
			boxes.getChildren().add(box);
		}
		scrollPane.setContent(boxes);
	}

}