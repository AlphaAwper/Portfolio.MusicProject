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
import java.util.Scanner;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.swing.JFileChooser;

/**
 * FXML Controller class
 *
 * @author nedas
 */
public class MainWindowController implements Initializable {

    @FXML
    private ChoiceBox<String> currentPlaylists;
    @FXML
    private Button playSong;
    @FXML
    private Button createButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;

    //private as no other class should access these variables at all
    private int currentPlaylist = 0;
    private List<List<String>> playlist = new ArrayList<List<String>>();
    private List<String> song = new ArrayList<String>();
    private String link = "";
    private boolean linkIsSet = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startUp();
    }

    /**
     * Function that clears playlists , songs , current playlist list. It also
     * reloads the playlist file
     */
    @FXML
    private void reloadPlaylist(MouseEvent event) {
        playlist = new ArrayList<List<String>>();
        song = new ArrayList<String>();
        currentPlaylists.getItems().clear();
        playSong.setDisable(true);
        startUp();
    }

    /**
     * Loads a txt file which contains the list of the playerlists names and
     * songs contained in each playlist Adds songs to a song array then adds the
     * songs to the double arraylist called currentPlaylist. Also adds a
     * listener to the newly added currentPlaylist so the variables are changed
     * when the selected playlist changes
     */
    private void startUp() {
        try {
            File file = new File("src/Playlists/AplaylistsName.txt");
            Scanner sc = new Scanner(file);
           sc.nextLine(); //fixes the weird bug with scanner that doesnt recognise the very first element as < .
            //TO DO , make a normal database instead of this file
            while (sc.hasNext()) {
                String characterName = sc.nextLine();
                String newPlaylist = "";
                System.out.println("ch :"+characterName);
                if (characterName.equals("<")) { //start of playlist
                    newPlaylist = sc.nextLine();
                    System.out.println("1 "+newPlaylist);
                    currentPlaylists.getItems().add(newPlaylist);
                    song.add(newPlaylist);
                } else if (characterName.equals(">")) { //end of playlist
                                        System.out.println("2 "+song);
                    playlist.add(song);
                    song = new ArrayList<String>();
                } else { // songs in the playlist
                                        System.out.println("3 "+characterName);
                    song.add(characterName);
                }
            }
            checkLinkToFile();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        currentPlaylists.getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        currentPlaylist = newValue.intValue();
                        if (linkIsSet == true) {
                            playSong.setDisable(false);
                        }
                    }
                });
    }

    @FXML
    private void startPlaying(ActionEvent event) throws IOException {
        setUpScenes(1, "/gui/view/PlayView.fxml");
    }

    @FXML
    private void createPlaylist(MouseEvent event) throws IOException {
        setUpScenes(2, "/gui/view/createPlaylists.fxml");
    }

    @FXML
    private void editPlaylists(ActionEvent event) throws IOException {
        setUpScenes(3, "/gui/view/editSongs.fxml");
    }

    @FXML
    private void deletePlaylist(ActionEvent event) throws IOException {
        setUpScenes(4, "/gui/view/deletePlaylists.fxml");
    }

    /**
     * This function sets up new scenes . The switch initializes the start up
     * method of the controllers called setInfo and sends all relevant info tho
     * the controller to be initialized instantly.
     */
    private void setUpScenes(int whichFunction, String locationOfFXLM) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(locationOfFXLM));
        Parent root1 = (Parent) fxmlLoader.load();
        switch (whichFunction) {
            case 1: //play screen
                fxmlLoader.<PlayViewController>getController().setInfo(currentPlaylist, playlist, link);
                break;
            case 2: //create screen
                fxmlLoader.<CreatePlaylistsController>getController().setInfo(playlist, link);
                break;
            case 3: //edit screen
                fxmlLoader.<editSongsController>getController().setInfo(playlist, link);
                break;
            case 4: //delete screen
                fxmlLoader.<DeletePlaylistsController>getController().setInfo(playlist);
                break;
        }
        Stage stage = (Stage) playSong.getScene().getWindow(); // it only gets the buttons main window. Any declared varjable can used instead
        stage.setScene(new Scene(root1, 800, 800));
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * On click , summons a file chooser which lets the user choose only folders
     * The folders will be used for song databases thus this function is vital
     * for most of the program usage. Once the user clicks the file he wants ,
     * it allows the user to click other windows and functions. When the user
     * chooses a folder location , this function calls write link to file
     * function which records the new link location.
     */
    @FXML
    private void fileBrowse(ActionEvent event) throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select song database");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            link = chooser.getSelectedFile().getAbsolutePath();
            writeLinkToFile();
            linkIsSet = true;
            createButton.setDisable(false);
            deleteButton.setDisable(false);
            editButton.setDisable(false);
            refreshButton.setDisable(false);
        }
    }

    /**
     * This function records the link directory into a file for later usage
     */
    private void writeLinkToFile() throws IOException {
        FileWriter fw = new FileWriter("src/Playlists/link.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(link);
        bw.close();
        fw.close();
    }

    /**
     * This function checks if there is a directory specified for songs . If
     * there is . It enables all buttons and lets you click playlist function
     */
    private void checkLinkToFile() throws FileNotFoundException {
        File file = new File("src/Playlists/link.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            link = sc.nextLine();
        }
        if (link != null && !link.isEmpty()) {
            linkIsSet = true;
            createButton.setDisable(false);
            deleteButton.setDisable(false);
            editButton.setDisable(false);
            refreshButton.setDisable(false);
        }
    }
}
