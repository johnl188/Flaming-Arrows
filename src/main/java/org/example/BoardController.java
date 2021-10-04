package org.example;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardController implements Initializable {

    @FXML private Rectangle gameBoard;

    @FXML private AnchorPane anchorPane;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        CreateSquares(8);
    }

    private void CreateSquares(int perSide) {
        double sideLength = gameBoard.getWidth() - 2;

        sideLength = sideLength / perSide;

        boolean isWhite = true;

        System.out.println("X: " + gameBoard.getX() + " Y: " + gameBoard.getY());


        for(int i = 0; i < perSide; i++) {
            for(int j = 0; j < perSide; j++) {
                Rectangle square = new Rectangle();
                square.setHeight(sideLength);
                square.setWidth(sideLength);

                square.setY(gameBoard.getY() + (i * sideLength) + 1);
                square.setX(gameBoard.getX() + (j * sideLength) + 1);

                if (isWhite) {
                    square.setFill(Color.WHITE);
                }

                else {
                    square.setFill(Color.BLACK);
                }

                System.out.println("X: " + square.getX() + " Y: " + square.getY());

                isWhite = !isWhite;

                anchorPane.getChildren().add(square);
            }

            if (perSide % 2 == 0) {
                isWhite = !isWhite;
            }
        }
    }
}