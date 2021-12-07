package org.amazons.mainui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.amazons.ai.AIPlayerType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the Board Screen
 */
public class BoardController implements Initializable {

    @FXML private AnchorPane anchorPane;

    @FXML private GridPane boardGridPane;

    @FXML private Label lblInfo;

    @FXML private Slider sliderMusic;

    @FXML private Slider sliderSFX;

    private GameOptions gameOptions;
    private GameInfo gameInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sliderMusic.valueProperty().bindBidirectional(SoundEffects.musicVolumeProperty);
        sliderSFX.valueProperty().bindBidirectional(SoundEffects.sfxVolumeProperty);
    }

    /**
     * Set the GameOptions to set up the game board to play the game
     * @param gameOptions - GameOptions to set up with
     */
    public void setGameOptions(GameOptions gameOptions) {
        this.gameOptions = gameOptions;

        // Create a GameInfo object based on the game options
        gameInfo = new GameInfo(gameOptions);

        // Link the game state label on the game board to the label property of the new GameInfo;
        lblInfo.textProperty().bind(gameInfo.gameInfoLabelProperty());

        // Create the game squares based on the game size and add pieces the board from the starting positions
        // of the game options
        createSquares(gameOptions.getGameSize());
        addPieces(gameOptions.getPositions());

        // If the game has AI, include a popup.
        if (gameOptions.getAIPlayerType() != AIPlayerType.None) {

            Stage popupStage = new Stage();
            Stage thisStage = (Stage) anchorPane.getScene().getWindow();

            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(thisStage);

            VBox dialogVbox = createPopup(gameOptions);
            dialogVbox.setAlignment(Pos.CENTER);

            Scene dialogScene = new Scene(dialogVbox, 300, 200);

            popupStage.setResizable(false);
            popupStage.initStyle(StageStyle.UNDECORATED);

            popupStage.setScene(dialogScene);

            // Center in parent stage
            popupStage.setWidth(300);
            popupStage.setHeight(300);
            popupStage.setX(thisStage.getX() + thisStage.getWidth() / 2 - popupStage.getWidth() / 2);
            popupStage.setY(thisStage.getY() + thisStage.getHeight() / 2 - popupStage.getHeight() / 2);

            // Wait to continue until the popup is dismissed
            popupStage.showAndWait();
        }

        // Signal the start of the game
        gameInfo.startGame();
    }

    /**
     * Create a popup if AI is used
     * @param gameOptions - Game Option to determine text
     * @return - Vbox to display in popup
     */
    private VBox createPopup(GameOptions gameOptions) {
        VBox dialogVbox = new VBox(20);

        String dialogText = "You are playing ";
        dialogText += gameOptions.getIsAIFirst() ? "Black" : "White";
        dialogText += " and will go ";
        dialogText += gameOptions.getIsAIFirst() ? "second." : "first.";

        dialogVbox.getChildren().add(new Text(dialogText));

        Button button = new Button("Ok");
        button.setOnAction((actionEvent -> {
            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();

            stage.close();
        }));

        dialogVbox.getChildren().add(button);

        return dialogVbox;
    }

    /**
     * Create and add square to the grid pane
     * @param perSide - number of square in row/column of game
     */
    private void createSquares(int perSide) {

        boolean isWhite = true;

        // Clear the board of children and constraints
        boardGridPane.getChildren().clear();
        boardGridPane.getColumnConstraints().clear();
        boardGridPane.getRowConstraints().clear();

        // Create each square and add to the board and game info
        for(byte i = 0; i < perSide; i++) {

            for(byte j = 0; j < perSide; j++) {

                GameSquare square = new GameSquare(gameInfo, i, j, isWhite);

                gameInfo.addGameSquare(square);
                boardGridPane.add(square, j, i);

                // Flip square color
                isWhite = !isWhite;
            }

            // If even number of squares on a side, flip color again to make checkered pattern
            if (perSide % 2 == 0) {
                isWhite = !isWhite;
            }
        }

        // Add grid pane constraints to make equal sized squares
        for (int i = 0; i < perSide; i++) {
            boardGridPane.getColumnConstraints().add(new ColumnConstraints(5, 10, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
            boardGridPane.getRowConstraints().add(new RowConstraints(5, 10, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }
    }

    /**
     * Add pieces to the board based on input positions
     * @param positions - StartingPosition that indicate where the pieces start
     */
    private void addPieces(StartingPosition[] positions) {

        for (StartingPosition position: positions) {
            GameSquare square = gameInfo.getGameSquare(position.getRow(), position.getColumn());
            square.addAmazon(position.getIsWhite());
        }
    }

    /**
     * Method to be called by Reset button on the board
     * @param actionEvent - event
     */
    public void resetGame(ActionEvent actionEvent) {

        // Don't do anything if something is being animated
        if (!gameInfo.getIsOkToMovePiece()) {
            return;
        }

        // Reset the game, re add the pieces, start the game
        gameInfo.resetGame();
        addPieces(gameOptions.getPositions());
        gameInfo.startGame();
    }

    /**
     * Method called by Undo button
     * @param actionEvent - event
     */
    public void undoLastMove(ActionEvent actionEvent) {

        gameInfo.undoLastMove();
    }

    /**
     * Method called by Quit button
     * @param actionEvent - event
     */
    public void goBackToMenu(ActionEvent actionEvent) {

        // Get the Menu fxml and set the scene to that
        try {

            SoundEffects.pauseAllSoundEffects();
            gameInfo.stopGame();

            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));

            Parent root = fxmlLoader.load();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.setWidth(width);
            stage.setHeight(height);
        }

        catch (IOException ex) {
            System.out.println("Error loading menu");
        }
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
}