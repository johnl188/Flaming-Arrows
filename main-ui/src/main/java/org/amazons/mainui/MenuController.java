/**
 *     This file is part of Flaming Arrows.
 *
 *     Flaming Arrows is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Flaming Arrows is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Flaming Arrows.  If not, see <https://www.gnu.org/licenses/>.
 *
 *     Copyright 2021 Paperweights
 */

package org.amazons.mainui;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.amazons.ai.AIPlayerType;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller Class for the Main Menu
 */
public class MenuController implements Initializable {

    @FXML
    private ToggleButton sixBySixBtn;

    @FXML
    private ToggleButton eightByEightBtn;

    @FXML
    private ToggleButton tenByTenBtn;

    @FXML
    private ToggleButton onePlayerBtn;

    @FXML
    private ToggleButton twoPlayerBtn;

    @FXML
    private ToggleButton easyAIBtn;

    @FXML
    private ToggleButton hardAIBtn;

    @FXML
    private ToggleButton aiRandomBtn;

    @FXML
    private ToggleButton aiWhiteBtn;

    @FXML
    private ToggleButton aiBlackBtn;

    @FXML
    private ToggleGroup playerNumbers;

    @FXML
    private ToggleGroup boardSizeGroup;

    @FXML
    private ToggleGroup aiDifficulty;

    @FXML
    private ToggleGroup aiStart;

    @FXML
    private Pane aiPane;

    @FXML
    private Slider sliderMusic;

    @FXML
    public Slider sliderSFX;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        sliderMusic.valueProperty().bindBidirectional(SoundEffects.musicVolumeProperty);
        sliderSFX.valueProperty().bindBidirectional(SoundEffects.sfxVolumeProperty);

        // On load, set default answers and add properties
        onePlayerBtn.setSelected(true);
        sixBySixBtn.setSelected(true);
        easyAIBtn.setSelected(true);
        aiRandomBtn.setSelected(true);

        playerNumbers.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        boardSizeGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        aiDifficulty.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        aiStart.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });
    }

    /**
     * event handler for start game button
     * @param actionEvent - event
     */
    public void startGame(ActionEvent actionEvent) {

        int boardSize;
        StartingPosition[] positions;
        AIPlayerType aiPlayerType = AIPlayerType.None;
        boolean isAIPlayerFirst = false;

        // Determine Game Options and set for the board screen
        if (onePlayerBtn.isSelected()) {

            if (easyAIBtn.isSelected()) {
                aiPlayerType = AIPlayerType.Easy;
            }

            else {
                aiPlayerType = AIPlayerType.Hard;
            }

            if (aiRandomBtn.isSelected()) {
                Random rand = new Random();
                int randomInt = rand.nextInt(2);

                isAIPlayerFirst = randomInt % 2 == 0;
            }

            else {
                isAIPlayerFirst = aiWhiteBtn.isSelected();
            }
        }

        if (sixBySixBtn.isSelected()) {
            boardSize = 6;
        }

        else if (eightByEightBtn.isSelected()) {
            boardSize = 8;
        }

        else {
            boardSize = 10;
        }

        GameOptions options = new GameOptions(boardSize, aiPlayerType, isAIPlayerFirst);

        Node node = (Node) actionEvent.getSource();

        Stage stage = (Stage) node.getScene().getWindow();

        goToGameWithOptions(stage, options);
    }

    /**
     * Open the board scene with the input options
     * @param stage
     * @param options
     */
    private void goToGameWithOptions(Stage stage, GameOptions options) {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("board.fxml"));
            Parent root = fxmlLoader.load();
            BoardController controller = fxmlLoader.<BoardController>getController();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.setWidth(width + 0.1);
            stage.setHeight(height + 0.1);

            controller.setGameOptions(options);

            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {

                            Platform.runLater(() -> {
                                stage.setWidth(width);
                                stage.setHeight(height);
                            });
                        }
                    }, 1
            );
        }

        catch (IOException ex) {
            System.out.println("Error loading game board");
        }
    }

    /**
     * Unblur the AI Menu
     * @param actionEvent - event
     */
    public void showAI(ActionEvent actionEvent) {
        easyAIBtn.setDisable(false);
        hardAIBtn.setDisable(false);
        aiRandomBtn.setDisable(false);
        aiBlackBtn.setDisable(false);
        aiWhiteBtn.setDisable(false);

        GaussianBlur blur = new GaussianBlur(0);
        aiPane.setEffect(blur);
    }

    /**
     * Blur the AI menu
     * @param actionEvent - event
     */
    public void hideAI(ActionEvent actionEvent) {
        easyAIBtn.setDisable(true);
        hardAIBtn.setDisable(true);
        aiRandomBtn.setDisable(true);
        aiBlackBtn.setDisable(true);
        aiWhiteBtn.setDisable(true);

        GaussianBlur blur = new GaussianBlur(10);
        aiPane.setEffect(blur);
    }

    /**
     * Set the SFX slider to 0 when toggled
     * @param actionEvent
     */
    public void muteSFX(ActionEvent actionEvent) {
        if (sliderSFX.getValue() > 0) {
            sliderSFX.setValue(0);
        }
        else {
            sliderSFX.setValue(0.5);
        }
    }

    /**
     * Set music slider to 0 when toggled
     * @param actionEvent
     */
    public void muteMusic(ActionEvent actionEvent) {
        if (sliderMusic.getValue() > 0) {
            sliderMusic.setValue(0);
        }
        else {
            sliderMusic.setValue(0.5);
        }
    }

    /**
     * Open User Guide PDF
     * @param actionEvent
     */
    public void howToPlay(ActionEvent actionEvent) {

        try {

            HostServices hostServices = App.hostServices;
            hostServices.showDocument("https://github.com/johnl188/Flaming-Arrows/blob/master/main-ui/src/main/resources/org/amazons/mainui/UserGuide.pdf");
        }

        catch(Exception ex) {
            System.out.println("Error opening instructions.");
        }
    }
}
