package org.amazons;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundEffects {

    public static MediaPlayer undoSound;

    public static MediaPlayer arrowSound;

    public void createSoundEffects() {

        Media arrowMedia = new Media(getClass().getResource("/arrow.mp3").toExternalForm());
        arrowSound = new MediaPlayer(arrowMedia);

        Media undoMedia = new Media(getClass().getResource("/cancelwav.wav").toExternalForm());
        undoSound = new MediaPlayer(undoMedia);
    }
}
