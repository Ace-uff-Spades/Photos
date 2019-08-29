package application.utilities;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * The confirmation used during a popup.
 * @author Kaavya Krishna-Kumar
 * @author Abhishek Kondila 
 *
 */
public class Confirmation extends Alert {

	/**
	 * Creates a new Confirmation object.
	 * @param contentText The content text.
	 */
	public Confirmation(String contentText) {
		super(AlertType.CONFIRMATION, contentText, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
	}
	
	/**
	 * This runs the confirmation.
	 * @param action The instructions to carry out after the Yes confirmation is received.
	 */
	public void run(PopupAction action) {
		showAndWait();
		if (getResult() == ButtonType.YES) {
			action.execute();
		}
	}
}