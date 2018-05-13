package application;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import artists.RetrieveFromSite;
import artists.Song;
import artists.artist;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class SampleController {

	/** A list used to store items for drop down box  */
	ObservableList<String> list = FXCollections.observableArrayList();
	/** indicates if progress bar is bound to save or search functions */
	boolean setSavedSong;
	/** stores songs retrieved from web */
	ObservableList<Song> trackList;
	public static ObservableList<Song> savedTracks = FXCollections.observableArrayList();
	/** gets the desktop to allow playing a song in the browser */
	Desktop desktop = Desktop.getDesktop();

	/** box for selecting time frame of most popular songs */
	@FXML
	private ChoiceBox<String> choice_TimeFrame;

	/** box to search for an artist */
	@FXML
	private TextField tb_ArtistsName;

	/** The table that displays tracks return from web */
	@FXML
	private TableView<Song> table_DisplayTracks;

		@FXML
		private TableColumn<Song, CheckBox> tc_Check;
	
		@FXML
		private TableColumn<Song, SimpleIntegerProperty> tc_Index;
	
		@FXML
		private TableColumn<Song, String> tc_Title;
	
		@FXML
		private TableColumn<Song, String> tc_Link;

	/** indicates that an action is being performed and the user should wait until it is solid  */
	@FXML
	private ProgressBar pb_Progress;

	/** not yet implemented */
	@FXML
	private MediaView mediaPlayer; // = new MediaView(player);

	/** button to save the songs to the database */
	@FXML
	private Button goButton;

	/** opens the secondary scene, the scene to view the saved songs */
	@FXML
	void changeToDbView(ActionEvent event) {
		AnchorPane dbView;
		try {
			dbView = (AnchorPane) FXMLLoader.load(getClass().getResource("DatabaseView.fxml"));
			Scene scene2 = new Scene(dbView, 720, 480);
			scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = (Stage) goButton.getScene().getWindow();
			stage.setScene(scene2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** saves the selected songs to the database, initiated in a thread */
	@FXML
	void goTime(ActionEvent event) {
		setSavedSong = true;
		final Task firstRunnable = new Task() {

			@Override
			protected Object call() throws Exception {
				try {
					Connection conn = DriverManager.getConnection("jdbc:mysql://www.db4free.net:3306/songdb",
							"songuser", "757-song-SQL");
					Statement stmt = conn.createStatement();
					for (Song song : trackList) {
						if (song.getCheckBox() != null && song.getCheckBox().isSelected()) {
							if (!stmt.executeQuery("Select * from song where artist = '" + song.getArtistName()
									+ "' and songName = '" + song.getTitle() + "';").first()) {
								stmt.executeUpdate("Insert into song Values('" + song.getArtistName() + "', '"
										+ song.getTitle() + "', '" + song.getPlayLink() + "')");
							}
						}

					}

				} catch (Exception ex) {
					// handle any errors
				}
				updateProgress(100, 100);
				return null;
			}
		};
		Thread thr1 = new Thread(firstRunnable);
		thr1.setDaemon(true);
		pb_Progress.progressProperty().bind(firstRunnable.progressProperty());
		thr1.start();
	}

	/** Searches the web for songs that are produced by the artist entered, initiated in a thread */
	@FXML
	void searchForArtistTracks(ActionEvent event) {

		final Task firstRunnable = new Task() {

			@Override
			protected Object call() throws Exception {
				setSavedSong = false;
				artist tempArtist = RetrieveFromSite.getSongs(getArtistsName(), getTimeFrame());
				System.out.println("Songs retrieved");
				trackList = FXCollections.observableArrayList(tempArtist.getSongs());
				updateProgress(100, 100);
				return null;
			}
		};
		Thread thr1 = new Thread(firstRunnable);
		thr1.setDaemon(true);
		pb_Progress.progressProperty().bind(firstRunnable.progressProperty());
		thr1.start();

	}

	public void initialize() {

		loadTimeFrameChoiceBox();
		/** sets the column values of the table */
		tc_Check.setCellValueFactory(new PropertyValueFactory<Song, CheckBox>("checkBox"));
		tc_Index.setCellValueFactory(new PropertyValueFactory<Song, SimpleIntegerProperty>("index".toString()));
		tc_Title.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));
		tc_Link.setCellValueFactory(new PropertyValueFactory<Song, String>("button"));
		pb_Progress.progressProperty().addListener(observable -> {
			if (pb_Progress.getProgress() >= 1 - 0.000005) {
				setPlayButtonsEvents();
				pb_Progress.progressProperty().unbind();
			}

		});

	}

	/** loads the options into the time frame choice box */
	private void loadTimeFrameChoiceBox() {
		String last7Days = "Last 7 Days";
		String last30Days = "Last 30 Days";
		String last90Days = "Last 90 Days";
		String last365Days = "Last 365 Days";
		String allTime = "All Time";
		list.removeAll(list);
		list.addAll(last7Days, last30Days, last90Days, last365Days, allTime);
		choice_TimeFrame.getItems().addAll(list);

	}

	/** gets the value associated with the time frame choice box
	 * @return string representation of the time frame */
	private String getTimeFrame() {
		String retValue;
		String timeFrame = choice_TimeFrame.getValue();
		if (!timeFrame.equalsIgnoreCase("all time")) {
			retValue = String.join("_", timeFrame.split(" "));
		} else {
			retValue = "all";
		}
		return retValue;
	}

	/** plays selected song in the browser */
	private void handleButtonAction(ActionEvent event) {
		for (Song entry : trackList.filtered(p -> (p.getButton() != null))) {
			if (entry.getButton() == event.getSource()) {
				try {
					desktop.browse(new URI(entry.getPlayLink()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/** gets the value associated with the artist name text box
	 * @return string representation of artists name */
	private String getArtistsName() {
		return tb_ArtistsName.getText();
	}

	/** checks each track, if it has a play link set the button to event to play song in browser */
	private void setPlayButtonsEvents() {
		if (setSavedSong == false) {
			for (int i = 0; i < trackList.size(); ++i) {
				if (trackList.get(i).getPlayLink().length() > 1) {
					trackList.get(i).getButton().setOnAction(this::handleButtonAction);
				}
			}
			table_DisplayTracks.setItems(trackList);
		}
	}

}
