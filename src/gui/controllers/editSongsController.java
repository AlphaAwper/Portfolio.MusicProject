/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author nedas
 */
public class editSongsController implements Initializable {

    @FXML
    private ListView<String> playList;
    @FXML
    private ListView<String> songList;
    @FXML
    private Button goBackButton;
    @FXML
    private ChoiceBox<String> currentPlaylists;
    @FXML
    private Label success;
    @FXML
    private Button editButton;

    private List<List<String>> playlistas = new ArrayList<List<String>>();
    private List<String> song = new ArrayList<String>();
    private String oldItem = "";
    private int currentList = -1;
    private String linkURL = "";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Sets info gotten from the main window. Populates song list from database
     * folder and populates the choice box from given playlist from main window.
     */
    void setInfo(List<List<String>> playlist, String link) throws FileNotFoundException {
        linkURL = link;
        File folder = new File(linkURL + "/");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                songList.getItems().add(listOfFiles[i].getName());
            }
        }
        playlistas = playlist;
        setUpPlaylistSelection();
    }

    /**
     * Populates the choice box from given playlist from main window. Adds a
     * listener to make edit button usable and changes the current list to be
     * aligned with the showing list Also clears out the songs in that specific
     * playlist
     */
    private void setUpPlaylistSelection() throws FileNotFoundException {
        for (int i = 0; i < playlistas.size(); i++) {
            currentPlaylists.getItems().add(playlistas.get(i).get(0));
        }
        currentPlaylists.getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        playList.getItems().clear();
                        currentList = newValue.intValue();
                        editButton.setDisable(false);
                        for (int x = 1; x < (playlistas.get(newValue.intValue()).size()); x++) {
                            playList.getItems().add(playlistas.get(newValue.intValue()).get(x));
                        }
                    }
                });
    }

    /**
     * Modifies the element in the array list to add the new choices that were
     * added . Also calls writeNewInfoToFile method which then writes this whole
     * info to file. Upon success it displays the info
     */
    @FXML
    private void createPlaylist(ActionEvent event) throws IOException {
        song = new ArrayList<String>();
        song.add(playlistas.get(currentList).get(0));
        for (int z = 0; z < playList.getItems().size(); z++) {
            song.add(playList.getItems().get(z));
        }
        playlistas.set(currentList, song);
        writeNewInfoToFile();
        success.setText("Playlist " + playlistas.get(currentList).get(0) + " edited successfully ");
        success.setVisible(true);
    }

    /**
     * Rewrites all modified information from the array to the file
     */
    private void writeNewInfoToFile() throws IOException {
        FileWriter fw = new FileWriter("src/Playlists/AplaylistsName.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        for (int p = 0; p < playlistas.size(); p++) {
            bw.write("<");
            bw.newLine();
            for (int k = 0; k < playlistas.get(p).size(); k++) {
                bw.write(playlistas.get(p).get(k));
                bw.newLine();
            }
            bw.write(">");
            bw.newLine();
        }
        bw.close();
        fw.close();
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
     * On click , moves the song in the playlist up
     */
    @FXML
    private void moveSongUp(ActionEvent event) {
        int location = playList.getSelectionModel().getSelectedIndex();
        if (location != 0 && location != -1) {
            playList.getItems().add(location - 1, playList.getItems().get(location));
            playList.getItems().remove(location + 1);
        }
    }

    /**
     * On click , moves the song in the playlist down
     */
    @FXML
    private void moveSongDown(ActionEvent event) {
        int location = playList.getSelectionModel().getSelectedIndex();
        if (location + 2 <= playList.getItems().size() && location != -1) {
            playList.getItems().add(location + 2, playList.getItems().get(location));
            playList.getItems().remove(location);
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
