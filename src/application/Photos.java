package application;
	
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import application.Album.Album;
import application.photoEdit.Photo;
import application.user.User;
import application.utilities.DataStorage;
import application.utilities.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Photos extends Application {
	/**
	 * Represents the users of this application.
	 */
	public static ArrayList<User> users = new ArrayList<>();
	
	/**
	 * The window controller for this application.
	 */
	public static WindowController windowManager = null;
	

	/**
	 * The current user of this application.
	 */
	public static User currentUser = null;
	
	/**
	 * The current album of this application
	 */
	public static Album currentAlbum = null;
	
	/**
	 * The current photo of this application
	 */
	public static Photo currentPhoto = null;

	/**
	 * The current photos on the screen
	 */
	public static ArrayList<Photo> currentPhotos = new ArrayList<Photo>();
	
	
	/**
	 * The main stage for this application
	 */
	public static Stage mainStage = null;
	
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("login/LoginScreen.fxml"));
		primaryStage.setTitle("Photo Album Manager");
		primaryStage.setResizable(false);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		windowManager = new WindowController(scene);
		windowManager.addWindow("loginScreen", (Pane) root);
		windowManager.addWindow("adminScreen", FXMLLoader.load(getClass().getResource("admin/AdminScreen.fxml")));
		mainStage = primaryStage;
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		users = DataStorage.parseUsers();
		launch(args);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
	}
	
	
	/**
	 * Logs a user in.
	 * @param username the username.
	 */
	public static void login(String username) {
		setCurrentUser(username);
		try {
			windowManager.addWindow("albumScreen", FXMLLoader.load(Photos.class.getResource("album/AlbumScreen.fxml")));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		windowManager.openWindow("albumScreen");
	}
	
	/**
	 * Logs the current user out.
	 */
	public static void logout() {
		Photos.currentPhoto = null;
		Photos.currentAlbum = null;
		windowManager.openWindow("loginScreen");
	}
	
	/**
	 * Whether we a user exists.
	 * @param username the username
	 * @return True if the user exists. False if they dont.
	 */
	public static boolean userExists(String username) {
		for (User u : users) {
			if (u.getUsername().equalsIgnoreCase(username)) {
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * Sets the current user.
	 * @param username The username of the user.
	 */
	public static void setCurrentUser(String username) {
		for (User u : users) {
			if (u.getUsername().equalsIgnoreCase(username)) {
				currentUser = u;
				break;
			}
		}
	}
	

	/**
	 * Gets the current photos
	 * @return Return the current photos.
	 */
	public static ArrayList<Photo> getCurrentPhotos(){
		return currentPhotos;
	}
	
	/**
	 *  Sets the current Photos
	 *  @param a The current photos.
	 */
	public static void setCurrentPhotos(ArrayList<Photo> a) {
		currentPhotos = a;
	}
	
	/**
	 * Gets the file name for the current user 
	 * @return File name of chosen picture
	 */
	public static Photo getFileName() {
		FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Open Resource File");
		 fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("Image Files", "*.png", "*.jpeg", "*.jpg", "*.gif"));
		 File selectedFile = fileChooser.showOpenDialog(mainStage);
		 if (selectedFile != null) {
			 String name = selectedFile.getName();
			 int pos = name.lastIndexOf('.');
			 name = name.substring(0, pos);
			 String fileLoc = currentUser.getUsername().equalsIgnoreCase("stock") && selectedFile.getAbsolutePath().contains("resources") ? "./resources/" + selectedFile.getAbsolutePath().split("/resources/")[1] : selectedFile.getAbsolutePath();
		    System.out.println("selected file is " + selectedFile.getName() + " path : " + fileLoc);
		    return new Photo(name, fileLoc, selectedFile.lastModified());
		 }
		 return null;
	}
	
	/**
	 * The close function for this application.
	 */
	public static void close() {
		DataStorage.saveAllUsers(users);
	}
	
}
	