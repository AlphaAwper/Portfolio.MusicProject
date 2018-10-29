/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author nedas
 */
public class CreatePlaylistsController implements Initializable {

    @FXML
    private ListView<String> playList;
    @FXML
    private ListView<String> songList;
    @FXML
    private Button goBackButton;
    @FXML
    private TextField customName;
    @FXML
    private Label errorNames;
    @FXML
    private Label Success;

    private List<String> existingPlaylists = new ArrayList<String>();
    private String linkURL = "";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Sets info gotten from the main window. Gets file list from folder and
     * displays it in the song list while getting already existing song list and
     * adding to the array that users don't see.
     */
    void setInfo(List<List<String>> playlist, String link) {
        linkURL = link;
        File folder = new File(linkURL);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                songList.getItems().add(listOfFiles[i].getName());
            }
        }
        for (int x = 0; x < playlist.size(); x++) {
            existingPlaylists.add(playlist.get(x).get(0));
        }

    }

    /**
     * This function on click checks if the text field is not null . If the
     * playlist has songs and the name doesn't contain bad characters If thats
     * is good . It checks for existing names as duplicate playlists would be
     * indistinguisable from other playlists Then if no exact names are found it
     * writes to file the whole information as is stated in the list.
     */
    @FXML
    private void createPlaylist(ActionEvent event) throws IOException {
        if (customName.getText() != null && !customName.getText().isEmpty() && playList.getItems().size() > 0 && !customName.getText().contains("<") && !customName.getText().contains(">")) {
            boolean isFound = searchForExistingNames();
            if (isFound == false) {
                writeToFile();
                errorMessage(true, "Playlist " + customName.getText() + " created successfully");
            } else {
                errorMessage(false, "Name already taken");
            }
        } else if (playList.getItems().size() <= 0) {
            errorMessage(false, "Please insert songs in the playlist");
        } else {
            errorMessage(false, "Please enter a valid name and avoid < and >");
        }
    }

    /**
     * This function checks if there are any duplicate names in the customName
     * text box . If there are no duplicates it returns false . If it finds any
     * duplicates . It breaks the loop and returns true .
     */
    private boolean searchForExistingNames() {
        boolean isFound = false;
        for (int y = 0; y < existingPlaylists.size(); y++) {
            if (existingPlaylists.get(y).equals(customName.getText())) {
                isFound = true;
                break;
            }
        }
        return isFound;
    }

    /**
     * Writes the whole playlist into file . However no new file is created but
     * information is added on top of already existing playlist information in
     * the txt file.
     */
    private void writeToFile() throws IOException {
        FileWriter fw = new FileWriter("src/Playlists/AplaylistsName.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("<"); //playlist start indentifier 
        bw.newLine();
        bw.write(customName.getText()); //name of the playlist
        for (int x = 0; x < playList.getItems().size(); x++) {
            bw.newLine();
            bw.write(playList.getItems().get(x)); // name of the song
        }
        bw.newLine();
        bw.write(">"); //playlist end indentifier
        bw.newLine();
        bw.close();
        fw.close();
        existingPlaylists.add(customName.getText()); //
    }

    /**
     * This function gets an error message . Then turns the previous messages
     * invisible and displays the current one.
     *
     */
    private void errorMessage(boolean success, String newText) {
        errorNames.setVisible(false);
        Success.setVisible(false);
        if (!success) {
            errorNames.setText(newText);
            errorNames.setVisible(true);
        } else {
            Success.setText(newText);
            Success.setVisible(true);
        }
    }

    /**
     * On click , removes a selected item from playlist
     */
    @FXML
    private void removeSelectedSongs(ActionEvent event) {
        int location = playList.getSelectionModel().getSelectedIndex();
        if (location != -1) {
            playList.getItems().remove(playList.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * On click , adds a selected item from playlist
     */
    @FXML
    private void addSelectedSongs(ActionEvent event) {
        int location = songList.getSelectionModel().getSelectedIndex();
        if (location != -1) {
            playList.getItems().add(songList.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * On click moves back to the main window.
     */
    @FXML
    private void goBack(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/view/MainWindow.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = (Stage) goBackButton.getScene().getWindow();
        stage.setScene(new Scene(root1, 800, 800));
        stage.centerOnScreen();
        stage.show();
    }

}
