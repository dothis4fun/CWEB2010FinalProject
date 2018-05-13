package application;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import com.mysql.jdbc.Connection;
//import com.mysql.jdbc.Statement;

import artists.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DbController {
	/** Gets the desktop to be able to launch youtube videos */
	Desktop desktop = Desktop.getDesktop(); 
	/** An initial list for the table to populated from */
	ObservableList<Song> dbList =  FXCollections.observableArrayList(SampleController.savedTracks);
	/** A list used to help delete from table */
	ObservableList<Song> tempList = FXCollections.observableArrayList();
	
	/**The table that will show db items */
	@FXML
    private TableView<Song> table_DisplaySaved;

	    @FXML
	    private TableColumn<Song, CheckBox> tc_Check;
	
	    @FXML
	    private TableColumn<Song, String> tc_Artist;
	
	    @FXML
	    private TableColumn<Song, String> tc_Title;
	    
	    @FXML
	    private TableColumn<Song, String> tc_Link;

	/** Box to search through db table */
    @FXML
    private TextField tb_SearchField;
    
    /** label to inform user of various errors that may occur */
    @FXML
    private Label lbErrors;
    
    /** this method checks all songs for selected checkbox, then runs a deletion for each song  */
    @FXML
    void deleteSelected(ActionEvent event) {
    	try {
    		tempList.removeAll();
    		java.sql.Connection conn =  DriverManager.getConnection("jdbc:mysql://www.db4free.net:3306/songdb","songuser","757-song-SQL");
			java.sql.Statement stmt = conn.createStatement();
			for(Song song : dbList) {
	    		if(song.getCheckBox().isSelected()) {
        			stmt.executeUpdate("Delete from song where playLink = '"+ song.getPlayLink()+"';");
        			tempList.add(song);
	    		}
	    	}
			dbList.removeAll(tempList);
			table_DisplaySaved.refresh();
			lbErrors.setVisible(false);
    	}catch(SQLException ex) {
    		lbErrors.setText("Unable to connect to the database");
    		lbErrors.setVisible(true);
    	}
    }

    /** action for button to return to the artist search scene */
    @FXML
    void openSearchScene(ActionEvent event) {
    	AnchorPane searchView;
		try {
			searchView = (AnchorPane)FXMLLoader.load(getClass().getResource("Sample.fxml"));
			Scene scene2 = new Scene(searchView,720,480);
			scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = (Stage)tb_SearchField.getScene().getWindow();
			stage.setScene(scene2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /** sets the table to items from the database */
    public void initialize() {
    	Song track;
    	Connection conn;
    	dbList.removeAll();
    	// Connect to the database and add all tracks to the list to be displayed
    	try {
    	
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://www.db4free.net:3306/songdb","songuser","757-song-SQL");
			Statement stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from song");
			while(rs.next()) {
				track = new Song(rs.getString(1), rs.getString(2), rs.getString(3));
				dbList.add(track);
			}
			lbErrors.setVisible(false);
    	} catch (SQLException e) {
    		lbErrors.setText("Unable to connect to the database");
    		lbErrors.setVisible(true);
    		//e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		lbErrors.setText("Unable to connect to the database");
    		lbErrors.setVisible(true);
    		//e.printStackTrace();
    	}
    	
    	/** set cells properties for binding, also set each check box to false */
    	tc_Check.setCellValueFactory(new PropertyValueFactory<Song, CheckBox>("checkBox"));
    	tc_Artist.setCellValueFactory(new PropertyValueFactory<Song, String>("artistName"));
    	tc_Title.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));
    	tc_Link.setCellValueFactory(new PropertyValueFactory<Song, String>("button"));
    	dbList.forEach(s -> s.setFalseCheckBox());
    	
    	/** populate the list */
    	setPlayButtonsEvents();
    	
    	/** a list to help with user searches */
    	FilteredList<Song> filteredSongs = new FilteredList<>(dbList, s -> true);
    	tb_SearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredSongs.setPredicate(song -> {
                // If filter text is empty, display all songs.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare title and artist of each song in the list
                String lowerCaseFilter = newValue.toLowerCase();

                if (song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches title
                } else if (song.getArtistName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches artist name
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Song> sortedData = new SortedList<>(filteredSongs);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table_DisplaySaved.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        table_DisplaySaved.setItems(sortedData);
        
    }
    
    /** open play like in browser for song selected */
    private void handleButtonAction(ActionEvent event) {
    	for (Song entry : dbList.filtered(p -> (p.getButton() != null))) {
    		if(entry.getButton() == event.getSource()) {
    			try {
    				desktop.browse(new URI(entry.getPlayLink()));
    				lbErrors.setVisible(false);
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				lbErrors.setText("Unable to open the browser");
    	    		lbErrors.setVisible(true);
    				e.printStackTrace();
    			} catch (URISyntaxException e) {
    				// TODO Auto-generated catch block
    				lbErrors.setText("Unable to open the browser");
    	    		lbErrors.setVisible(true);
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    /** populates button action events and refreshes list */
    private void setPlayButtonsEvents() {
		for(int i = 0 ; i < dbList.size() ; ++i) {
    		if(dbList.get(i).getPlayLink().length()>1) {
	    		dbList.get(i).getButton().setOnAction(this::handleButtonAction);
    		}
    	}
		table_DisplaySaved.setItems(dbList);
		
    }
}
