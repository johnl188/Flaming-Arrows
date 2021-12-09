package org.amazons.mainui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Class to find all the Sounds used so that they only have to be loaded once
 */
public class SoundEffects {

    public static MediaPlayer undoSound;

    public static MediaPlayer arrowSound;

    public static MediaPlayer musicSound;

    public static MediaPlayer fireSound;

    public static SimpleDoubleProperty musicVolumeProperty = new SimpleDoubleProperty(1.0);

    public static SimpleDoubleProperty sfxVolumeProperty = new SimpleDoubleProperty(1.0);

    public void createSoundEffects() {

        Media arrowMedia = new Media(getClass().getResource("/sounds/arrow.mp3").toExternalForm());
        arrowSound = new MediaPlayer(arrowMedia);

        Media undoMedia = new Media(getClass().getResource("/sounds/cancelwav.wav").toExternalForm());
        undoSound = new MediaPlayer(undoMedia);

        Media fireMedia = new Media(getClass().getResource("/sounds/Flame.wav").toExternalForm());
        fireSound = new MediaPlayer(fireMedia);

        Media music1Media = new Media(getClass().getResource("/sounds/Lord of the Land - Kevin MacLeod.mp3").toExternalForm());
        musicSound = new MediaPlayer(music1Media);

        musicSound.setCycleCount(Integer.MAX_VALUE);

        arrowSound.volumeProperty().bind(sfxVolumeProperty);
        undoSound.volumeProperty().bind(sfxVolumeProperty);
        fireSound.volumeProperty().bind(sfxVolumeProperty);

        musicSound.volumeProperty().bind(musicVolumeProperty);

        sfxVolumeProperty.set(0.5);
        musicVolumeProperty.set(0.5);
    }

    public static void pauseAllSoundEffects() {
        arrowSound.stop();
        undoSound.stop();
        fireSound.stop();
    }

}
