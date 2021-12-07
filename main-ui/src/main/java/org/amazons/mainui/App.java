package org.amazons.mainui;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static HostServices hostServices;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        // Get menu fxml and set root
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent root = fxmlLoader.load();

        // Get and set scene
        Scene scene = new Scene(root, 800, 800);

        stage.setScene(scene);
        stage.setTitle("Flaming Arrows!");

        // Set a min size for the scene, so it can't be resized too small to play the game
        stage.setMinWidth(816);
        stage.setMinHeight(839);

        stage.show();

        // Create the static sounds used for the game so that they only needed to be loaded once, saving time
        SoundEffects soundEffects = new SoundEffects();
        soundEffects.createSoundEffects();

        SoundEffects.musicSound.seek(SoundEffects.musicSound.getStartTime());
        SoundEffects.musicSound.play();

        hostServices = getHostServices();
    }
}