/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.controllers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author nedas
 */
public class DeletePlaylistsController implements Initializable {

    @FXML
    private ListView<String> playlistList;
    @FXML
    private ListView<String> songList;
    @FXML
    private Button goBackButton;
    @FXML
    private Label successs;

    private List<List<String>> existingPlaylists;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Sets info gotten from the main window. Populates both existingPlaylists
     * and playlist with the names of the playlists.
     */
    void setInfo(List<List<String>> playlist) {
        existingPlaylists = playlist;
        for (int i = 0; i < playlist.size(); i++) {
            playlistList.getItems().add(playlist.get(i).get(0));
        }
    }

    /**
     * On click on a specific playlist , displays all songs in that playlist.
     */
    @FXML
    private void populateSongs(MouseEvent event) {
        songList.getItems().clear();
        int location = playlistList.getSelectionModel().getSelectedIndex();
        for (int i = 1; i < existingPlaylists.get(location).size(); i++) {
            songList.getItems().add(existingPlaylists.get(location).get(i));
        }
    }

    /**
     * Gets the selected list and removes it from display then calls
     * rewriteNewPlaylist method. Removes the item from the current array list
     * and clears the song list . Also displays the success message.
     */
    @FXML
    private void deletePlaylist(ActionEvent event) throws IOException {
        int location = playlistList.getSelectionModel().getSelectedIndex();
        if (location > -1) {
            successs.setText("Playlist " + playlistList.getItems().get(location) + " deleted successfully ");
            successs.setVisible(true);
            existingPlaylists.remove(location);
            rewriteNewPlaylist();
            playlistList.getItems().remove(location);
            songList.getItems().clear();
        } else {
            successs.setText("Please select a playlist ");
            successs.setVisible(true);
        }
    }

    /**
     * rewrites the whole playlist but without the selected element.
     */
    private void rewriteNewPlaylist() throws IOException {
        FileWriter fw = new FileWriter("src/Playlists/AplaylistsName.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        for (int p = 0; p < existingPlaylists.size(); p++) {
            bw.write("<"); //playlist start indentifier 
            bw.newLine();
            for (int k = 0; k < existingPlaylists.get(p).size(); k++) {
                bw.write(existingPlaylists.get(p).get(k)); //songs and playlist name
                bw.newLine();
            }
            bw.write(">"); //playlist end indentifier
            bw.newLine();
        }
        bw.close();
        fw.close();
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
