package application.utilities;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The window controller will control the current visible window for the Photo Album Library.
 * @author Kaavya Krishna-Kumar	
 * @author Abhishek Kondila
 *
 */
public class WindowController {
	
	/**
	 * This hash map will store all the different FXML Scenes.
	 */
	private HashMap<String, Pane> scenes = new HashMap<>();
	
	/**
	 * The initial scene.
	 */
	private Scene scene;

	/**
	 * Constructs a new {@code WindowController} object.
	 * @param scene The initial scene.
	 */
	public WindowController(Scene scene) {
		this.scene = scene;
	}
	
	/**
	 * Adds a window to this controller.
	 * @param name The name of the window.
	 * @param pane The window pane.
	 */
	public void addWindow(String name, Pane pane) {
		scenes.put(name, pane);
	}
	
	/**
	 * Opens a window via the name.
	 * @param name The inputted name.
	 */
	public void openWindow(String name) {
		if (scenes.get(name) == null) {
			System.out.println("MNULL");
		}
		scene.setRoot(scenes.get(name));
	}
	
	
	/**
	 * Closes the application.
	 */
	public void close() {
		Stage stage = (Stage) scene.getWindow();
		stage.close();
	}
}