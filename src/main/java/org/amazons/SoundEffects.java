package org.amazons;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundEffects {

    public static MediaPlayer undoSound;

    public static MediaPlayer arrowSound;

    public static MediaPlayer music1;

    public static MediaPlayer fireSound;

    public void createSoundEffects() {

        Media arrowMedia = new Media(getClass().getResource("/arrow.mp3").toExternalForm());
        arrowSound = new MediaPlayer(arrowMedia);

        Media undoMedia = new Media(getClass().getResource("/cancelwav.wav").toExternalForm());
        undoSound = new MediaPlayer(undoMedia);

        Media fireMedia = new Media(getClass().getResource("/Flame.wav").toExternalForm());
        fireSound = new MediaPlayer(fireMedia);

        Media music1Media = new Media(getClass().getResource("/Blusey Vibes.mp3").toExternalForm());
        music1 = new MediaPlayer(music1Media);

    }
}
