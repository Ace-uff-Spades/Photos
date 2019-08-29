package application.utilities;

/**
 * This interface handles the actions taken once an action has been successfully confirmed via popup.
 * @author Kaavya Krishna-Kumar
 * @author Abhishek Kondila
 *
 */
public interface PopupAction {

	/**
	 * The instructions to execute once the action has been confirmed.
	 */
	public void execute();
}