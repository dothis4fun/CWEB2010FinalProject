package artists;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class Song {
	// Constructor for creating songs with play links in search artist screen
	Song(int Index, String Title, String PlayLink, Button button, String artistName) {
		index = new SimpleIntegerProperty(Index);
		title = new SimpleStringProperty(Title);
		playLink = new SimpleStringProperty(PlayLink);
		this.artistName = new SimpleStringProperty(artistName);
		this.button = button;
		this.button.setText("Play");
		checkBox = new CheckBox();
	}

	// constructor for creating songs without play links
	Song(int Index, String Title, String artistName) {
		index = new SimpleIntegerProperty(Index);
		title = new SimpleStringProperty(Title);
		playLink = new SimpleStringProperty("");
		this.artistName = new SimpleStringProperty(artistName);
	}

	// constructor for creating songs in database view table
	public Song(String artistName, String Title, String playLink) {
		index = new SimpleIntegerProperty(0);
		title = new SimpleStringProperty(Title);
		this.playLink = new SimpleStringProperty(playLink);
		this.artistName = new SimpleStringProperty(artistName);
		this.button = new Button();
		this.button.setText("Play");
		checkBox = new CheckBox();
	}

	/** Returns the index associated with the object
	 * @return the integer value of the objects index */
	public int getIndex() {
		return index.get();
	}

	/** Returns the title associated with the object
	 * @return the string value of the objects title */
	public String getTitle() {
		return title.get();
	}

	/** Returns the play link associated with the object
	 * @return the string value of the objects play link */
	public String getPlayLink() {
		return playLink.get();
	}
	
	/** Returns the artists name associated with the object
	 * @return the string value of the objects artist name */
	public String getArtistName() {
		return artistName.get();
	}

	/** Returns the button associated with the object
	 * @return the button associated with an object */
	public Button getButton() {
		return button;
	}

	/** Sets a button to be associated with an object
	 * @param button the button to be set to the objects button property */
	public void setButton(Button button) {
		this.button = button;
	}
	
	/** Returns the checkbox associated with object
	 * @return the checkbox assigned to the checkbox property */
	public CheckBox getCheckBox() {
		return checkBox;
	}

	/** Sets a check box element to the objects checkbox property
	 * @param checkBox the checkBox to be assigned to the object */
	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

	/** sets the objects checkbox to unchecked */
	public void setFalseCheckBox() {
		this.checkBox.setSelected(false);
	}

	private final SimpleIntegerProperty index;
	private final SimpleStringProperty title;
	private final SimpleStringProperty playLink;
	private final SimpleStringProperty artistName;
	private Button button;
	private CheckBox checkBox;
}
