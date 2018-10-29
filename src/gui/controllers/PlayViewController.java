/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author nedas
 */
public class PlayViewController implements Initializable {

    @FXML
    private Label songName;
    @FXML
    private Label startTimer;
    @FXML
    private Label endTimer;
    @FXML
    private Label playlistName;
    @FXML
    private ListView<String> currentSongPlaylist;
    @FXML
    private Slider volume;
    @FXML
    private Button goBackButton;

    private int currentSong = -1;
    private MediaPlayer mediaPlayer;
    private List<List<String>> allSongList = new ArrayList<List<String>>();
    private List<String> alternativeSongList = new ArrayList<String>();
    private int currentPlayingPlaylist = 0;
    private boolean isShuffled = false;
    private String song = "";
    private int currentTime = 0;
    private Timeline timeline;
    private String linkURL = "";

    /**
     * Initializes the controller class. But cannot initialize the setUp first
     * as the controller elements are not loaded in yet. Also relevant info
     * should be passed to from mainWindowController to this controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * This method gets information from the main window class and sets
     * everything up. It specifies a song list . Specifies the alternative song
     * list . It sets the play list name Removes the name from the list so all
     * that is left are songs It calls the play method in order to start playing
     * the songs
     */
    void setInfo(int currentPlaylist, List<List<String>> playlist , String link) {
        linkURL= link;
        allSongList = playlist;
        currentPlayingPlaylist = currentPlaylist;
        playlistName.setText(playlist.get(currentPlayingPlaylist).get(0));
        allSongList.get(currentPlayingPlaylist).remove(0);
        for (int x = 0; x < allSongList.get(currentPlayingPlaylist).size(); x++) {
            alternativeSongList.add(allSongList.get(currentPlayingPlaylist).get(x));
        }
        play();
    }

    /**
     * Resets the values to default and initiates the play method early so the
     * song skips
     */
    @FXML
    private void skipSong(ActionEvent event) {
        resettoDefaults("time");
        play();
    }

    /**
     * This method is self explanatory . It shuffles a duplicated array list
     * called alternativeSongList in order to both keep a normal order and a
     * shuffled order isShuffled turns to false to signify a change in the play
     * class also to play the alternative song list and not the original. Most
     * medias and current song are reseted at this point.
     */
    @FXML
    private void shuffleArray(ActionEvent event) {
        if (isShuffled == false) {
            isShuffled = true;
            Collections.shuffle(alternativeSongList); // suffle
        } else {
            isShuffled = false;
        }
        resettoDefaults("new shuffle");
        play();
    }

    /**
     * this method rewrites lists. If the list isn't shuffled it
     * populates/repopulates the playlist with the songs that are specified in
     * that array list If the list is shuffled it populates/repopulates the
     * playlist with the songs that are specified in that array list
     */
    private void writeList(int whichPlaylist) {
        currentSongPlaylist.getItems().clear();
        if (whichPlaylist == 1) {
            for (int i = currentSong + 1; i < allSongList.get(currentPlayingPlaylist).size(); i++) {
                currentSongPlaylist.getItems().add(allSongList.get(currentPlayingPlaylist).get(i));
            }
        } else {
            for (int i = currentSong + 1; i < alternativeSongList.size(); i++) {
                currentSongPlaylist.getItems().add(alternativeSongList.get(i));
            }
        }
    }

    /**
     * This is the timer function . It counts downs each second and changes the
     * startTimer value accordingly .
     */
    private void startCounting() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            currentTime = currentTime + 1;
            int minutes = currentTime / 60;
            int seconds = currentTime % 60;
            if (10 > seconds) {
                startTimer.setText(minutes + ":0" + seconds);
            } else {
                startTimer.setText(minutes + ":" + seconds);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * This function firstly adds one to the currentSong list so when a new
     * media is created - a correct next song will play Secondly it checks if
     * the songs are shuffled or not , if they are - it changes the arrayList
     * from which the songs should be taken Lastly it calls the setUpMedia
     * method and sends the url to it.
     */
    private void play() {
        currentSong++;
        if (isShuffled == false) {
            songName.setText(allSongList.get(currentPlayingPlaylist).get(currentSong));
            song = linkURL+"/" + allSongList.get(currentPlayingPlaylist).get(currentSong);
            writeList(1);
        } else {
            songName.setText(alternativeSongList.get(currentSong));
            song = linkURL+"/" + alternativeSongList.get(currentSong);
            writeList(2);
        }
        setUpMedia(song);
    }

    /**
     * This function resets most values after events such as : clicking the back
     * button. Clicking the skip song button . Normal change of song or re
     * shuffling of songs.
     */
    private void resettoDefaults(String what) {
        switch (what) {
            case "time":
                if (currentSong >= (allSongList.get(currentPlayingPlaylist).size() - 1)) {
                    currentSong = -1;
                }
                currentTime = 0;
                break;
            case "new shuffle":
                currentSong = -1;
                currentTime = 0;
                break;
            default:
                break;
        }
        mediaPlayer.stop();
        timeline.stop();
    }

    @FXML
    private void setSound(MouseEvent event) {
        makeSound();
    }

    /**
     * This function is mostly called to equalize the sound in the song.
     */
    private void makeSound() {
        mediaPlayer.setVolume(volume.getValue());
    }

    /**
     * This function sets up the media by creating a new media file with the
     * song URL gotten from play method. It also sets the sound so it would be
     * the same level of volume as the rest of the songs . Also it sets up new
     * song ending time and calls a method to set up an new timer On end it
     * requests a reset of the time and songs and calls the play method
     */
    private void setUpMedia(String song) {
        mediaPlayer = new MediaPlayer(new Media(new File(song).toURI().toString()));
        mediaPlayer.play();
        makeSound();

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                startCounting();
                String averageSeconds = String.format("%1.0f", (mediaPlayer.getMedia().getDuration().toSeconds()));
                int minutes = Integer.parseInt(averageSeconds) / 60;
                int seconds = Integer.parseInt(averageSeconds) % 60;
                if (10 > seconds) {
                    endTimer.setText(minutes + ":0" + seconds);
                } else {
                    endTimer.setText(minutes + ":" + seconds);
                }
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                if (isShuffled == false) {
                    writeList(1);
                } else {
                    writeList(2);
                }
                resettoDefaults("time");
                play();
            }
        });
    }

    /**
     * The following method is used to get back to the previous menu.
     */
    @FXML
    private void goBack(ActionEvent event) throws IOException {
        resettoDefaults("gone back");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/view/MainWindow.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = (Stage) goBackButton.getScene().getWindow();
        stage.setScene(new Scene(root1, 800, 800));
        stage.centerOnScreen();
        stage.show();
    }

}
