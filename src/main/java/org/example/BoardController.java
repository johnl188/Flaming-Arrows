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

    private GameInfo gameInfo;
    private Point2D offset = new Point2D(0.0d, 0.0d);
    private boolean movingPiece = false;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        int sides = 10;
        gameInfo = new GameInfo(sides, lblInfo, true);
        createSquares(sides);
        addPieces();
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

    private void addPieces() {

        GameSquare square = gameInfo.getSquare(3, 0);
        square.addAmazon(false);

        square = gameInfo.getSquare(0, 3);
        square.addAmazon(false);

        square = gameInfo.getSquare(0, 6);
        square.addAmazon(false);

        square = gameInfo.getSquare(3, 9);
        square.addAmazon(false);

        square = gameInfo.getSquare(6, 0);
        square.addAmazon(true);

        square = gameInfo.getSquare(9, 3);
        square.addAmazon(true);

        square = gameInfo.getSquare(9, 6);
        square.addAmazon(true);

        square = gameInfo.getSquare(6, 9);
        square.addAmazon(true);
    }

    public void resetGame(ActionEvent actionEvent) {

        gameInfo.resetGame();
        addPieces();
    }
}