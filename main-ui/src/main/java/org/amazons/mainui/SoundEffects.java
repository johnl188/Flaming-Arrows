package org.amazons.mainui;

import javafx.beans.property.SimpleBooleanProperty;
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

    public static SimpleBooleanProperty musicMuteProperty = new SimpleBooleanProperty(false);

    public static SimpleBooleanProperty sfxMuteProperty = new SimpleBooleanProperty(false);

    public void createSoundEffects() {

        Media arrowMedia = new Media(getClass().getResource("/sounds/arrow.mp3").toExternalForm());
        arrowSound = new MediaPlayer(arrowMedia);

        Media undoMedia = new Media(getClass().getResource("/sounds/cancelwav.wav").toExternalForm());
        undoSound = new MediaPlayer(undoMedia);

        Media fireMedia = new Media(getClass().getResource("/sounds/Flame.wav").toExternalForm());
        fireSound = new MediaPlayer(fireMedia);

        Media music1Media = new Media(getClass().getResource("/sounds/Bluesy Vibes.mp3").toExternalForm());
        musicSound = new MediaPlayer(music1Media);

        arrowSound.muteProperty().bind(sfxMuteProperty);
        undoSound.muteProperty().bind(sfxMuteProperty);
        fireSound.muteProperty().bind(sfxMuteProperty);

        musicSound.muteProperty().bind(musicMuteProperty);
        musicSound.setCycleCount(Integer.MAX_VALUE);
    }
}
