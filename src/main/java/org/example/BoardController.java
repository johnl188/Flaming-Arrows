package org.example;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class BoardController implements Initializable {

    @FXML private BorderPane borderPane;

    @FXML private AnchorPane gameBoardPane;

    @FXML private GridPane boardGridPane;

    @FXML private Label lblInfo;

    private GameOptions gameOptions;
    private GameInfo gameInfo;
    private Point2D offset = new Point2D(0.0d, 0.0d);
    private boolean movingPiece = false;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setGameOptions(GameOptions gameOptions) {
        this.gameOptions = gameOptions;

        gameInfo = new GameInfo(gameOptions, lblInfo);

        createSquares(gameOptions.getGameSize());
        addPieces(gameOptions.getPositions());
    }

    private void createSquares(int perSide) {

        boolean isWhite = true;
        boardGridPane.getChildren().clear();
        boardGridPane.getColumnConstraints().clear();
        boardGridPane.getRowConstraints().clear();

        for(int i = 0; i < perSide; i++) {

            for(int j = 0; j < perSide; j++) {

                GameSquare square = new GameSquare(gameInfo, i, j, isWhite);

                gameInfo.addSquare(square);
                boardGridPane.add(square, j, i);

                isWhite = !isWhite;
            }

            if (perSide % 2 == 0) {
                isWhite = !isWhite;
            }
        }

        for (int i = 0; i < perSide; i++) {
            boardGridPane.getColumnConstraints().add(new ColumnConstraints(5, 500, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
            boardGridPane.getRowConstraints().add(new RowConstraints(5, 200, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }
    }

    private void addPieces(StartingPosition[] positions) {

        for (StartingPosition position: positions) {
            GameSquare square = gameInfo.getSquare(position.getRow(), position.getColumn());
            square.addAmazon(position.getIsWhite());
        }
    }

    public void resetGame(ActionEvent actionEvent) {

        if (!gameInfo.getIsOkToMove()) {
            return;
        }


        gameInfo.resetGame();
        addPieces(gameOptions.getPositions());
    }

    public void undoLastMove(ActionEvent actionEvent) {

        gameInfo.undoLastMove();
    }

    public static StartingPosition[] get10x10StartingPositions() {
        StartingPosition[] positions = new StartingPosition[8];

        positions[0] = new StartingPosition(3,0, false);
        positions[1] = new StartingPosition(0,3, false);
        positions[2] = new StartingPosition(0,6, false);
        positions[3] = new StartingPosition(3,9, false);
        positions[4] = new StartingPosition(6,0, true);
        positions[5] = new StartingPosition(9,3, true);
        positions[6] = new StartingPosition(9,6, true);
        positions[7] = new StartingPosition(6,9, true);

        return  positions;
    }

    public static StartingPosition[] get8x8StartingPositions() {
        StartingPosition[] positions = new StartingPosition[6];

        positions[0] = new StartingPosition(2,0, false);
        positions[1] = new StartingPosition(0,3, false);
        positions[2] = new StartingPosition(2,7, false);
        positions[3] = new StartingPosition(5,0, true);
        positions[4] = new StartingPosition(7,4, true);
        positions[5] = new StartingPosition(5,7, true);

        return  positions;
    }

    public static StartingPosition[] get6x6StartingPositions() {
        StartingPosition[] positions = new StartingPosition[4];

        positions[0] = new StartingPosition(0,2, false);
        positions[1] = new StartingPosition(2,5, false);
        positions[2] = new StartingPosition(3,0, true);
        positions[3] = new StartingPosition(5,3, true);

        return  positions;
    }


}