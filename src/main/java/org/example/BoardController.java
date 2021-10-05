package org.example;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.layout.*;

public class BoardController implements Initializable {

    @FXML private BorderPane borderPane;

    @FXML private AnchorPane gameBoardPane;

    @FXML private GridPane boardGridPane;

    private GameInfo gameInfo;
    private Point2D offset = new Point2D(0.0d, 0.0d);
    private boolean movingPiece = false;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        int sides = 8;
        gameInfo = new GameInfo(sides);
        CreateSquares(sides);
        AddPieces();
    }

    private void CreateSquares(int perSide) {

        boolean isWhite = true;
        boardGridPane.getChildren().clear();
        boardGridPane.getColumnConstraints().clear();
        boardGridPane.getRowConstraints().clear();



        for(int i = 0; i < perSide; i++) {

            for(int j = 0; j < perSide; j++) {

                GameSquare square = new GameSquare(gameInfo, i, j, isWhite);

                gameInfo.addSquare(i, j, square);
                boardGridPane.add(square, i, j);

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

        boardGridPane.setGridLinesVisible(true);
    }

    private void AddPieces() {
        GameSquare pane = gameInfo.getSquare(0, 0);
        pane.addAmazon(true);

        pane = gameInfo.getSquare(0, 1);
        pane.addAmazon(false);

        pane = gameInfo.getSquare(1, 0);
        pane.addAmazon(true);

        pane = gameInfo.getSquare(1, 1);
        pane.addAmazon(false);
    }

    public void AddCircle(ActionEvent actionEvent) {
        AddPieces();
    }
}