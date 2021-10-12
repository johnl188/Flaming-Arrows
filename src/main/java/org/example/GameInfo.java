package org.example;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {

    private int gameSize;
    private GameSquare[] rectangles;
    private boolean isWhitesTurn;
    private boolean isMove;
    private GameSquare lastMove;
    private Label lblInfo;

    private AIPlayer aiPlayer = null;

    public GameInfo(int gameSize, Label lblInfo, boolean isAIGame) {
        this.gameSize = gameSize;
        this.lblInfo = lblInfo;

        rectangles = new GameSquare[gameSize * gameSize];
        isMove = true;
        isWhitesTurn = true;

        lblInfo.setText((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");

        if (isAIGame) {
            aiPlayer = new RandomAI(false);
        }
    }

    public int getGameSize() { return gameSize; }

    public boolean getIsMove() { return isMove; }

    public boolean getIsWhitesTurn() { return isWhitesTurn; }

    public void goToMove () { isMove = true; }

    public void goToArrow() {
        lblInfo.setText((isWhitesTurn ? "White's" : "Black's") + " turn to shoot an arrow");
        isMove = false;
    }

    public void switchTurns() {
        goToMove();
        isWhitesTurn = !isWhitesTurn;
        lblInfo.setText((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");

        if (isGameOver()) {
            lblInfo.setText((!isWhitesTurn ? "White's" : "Black's") + " wins!");
            return;
        }

        if (aiPlayer != null && isWhitesTurn == aiPlayer.getIsWhite()) {
            GameMove move = aiPlayer.getMove(this);
            if (move == null) {
                return;
            }

            GameSquare movingPiece = getSquare(move.getAmazonFromRow(), move.getAmazonFromColumn());
            GameSquare toSquare = getSquare(move.getAmazonToRow(), move.getAmazonToColumn());
            GameSquare fireSquare = getSquare(move.getArrowRow(), move.getArrowColumn());

            ArrayList<GameSquare> validMoves = getValidSquares(movingPiece);
            if (!validMoves.contains(toSquare)) {
                return;
            }

            movePiece(movingPiece.getSquareInfo(), toSquare.getSquareInfo(), true, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    ArrayList<GameSquare> validMoves = getValidSquares(toSquare);
                    if (!validMoves.contains(fireSquare)) {
                        return;
                    }

                    shootArrow(toSquare, fireSquare);
                }
            });
        }
    }

    public GameSquare getLastMove() { return lastMove; }

    public void setLastMove(GameSquare lastMove) { this.lastMove = lastMove; }

    public void addSquare(GameSquare square) {
        SquareInfo info = square.getSquareInfo();
        rectangles[gameSize * info.getRow() + info.getColumn()] = square;
    }

    public GameSquare getSquare(int row, int column) {
        return rectangles[gameSize * row + column];
    }

    public void movePiece(SquareInfo from, SquareInfo to, boolean shouldAnimate, EventHandler<ActionEvent> afterAnimate) {

        GameSquare fromSquare = getSquare(from.getRow(), from.getColumn());
        GameSquare toSquare = getSquare(to.getRow(), to.getColumn());

        goToArrow();
        setLastMove(toSquare);

        if (shouldAnimate) {

            ImageViewPane imageView = fromSquare.getImageView();
            fromSquare.toFront();
            imageView.toFront();

            TranslateTransition translateTransition = new TranslateTransition();

            translateTransition.setDuration(Duration.millis(1000));

            translateTransition.setNode(imageView);

            Point2D fromPoint = fromSquare.localToScene(0, 0);
            Point2D toPoint = toSquare.localToScene(0, 0);

            translateTransition.setByX(toPoint.getX() - fromPoint.getX());
            translateTransition.setByY(toPoint.getY() - fromPoint.getY());

            translateTransition.setCycleCount(1);

            translateTransition.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    fromSquare.removePiece();
                    toSquare.addAmazon(from.isWhite);

                    if (afterAnimate != null) {
                        afterAnimate.handle(actionEvent);
                    }
                }
            });

            translateTransition.play();
        }

        else {
            fromSquare.removePiece();
            toSquare.addAmazon(from.isWhite);
        }

    }

    public void shootArrow(GameSquare shootFrom, GameSquare addTo) {

        ImageViewPane imageView = shootFrom.getImageView();
        shootFrom.toFront();

        Image image = new Image("arrow.png");

        ImageViewPane arrowView = new ImageViewPane();
        arrowView.setImageView(new ImageView(image));


        shootFrom.getChildren().add(arrowView);

        TranslateTransition translateTransition = new TranslateTransition();
        ScaleTransition scaleTransition = new ScaleTransition();

        translateTransition.setDuration(Duration.millis(2000));

        translateTransition.setNode(arrowView);

        Point2D fromPoint = shootFrom.localToScene(0, 0);
        Point2D toPoint = addTo.localToScene(0, 0);

        translateTransition.setByX(toPoint.getX() - fromPoint.getX());
        translateTransition.setByY(toPoint.getY() - fromPoint.getY());

        translateTransition.setCycleCount(1);

        translateTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Move Done");
            }
        });

        // Get Rotation Angle for Arrow
        if (translateTransition.getByX() == 0) {
            if (translateTransition.getByY() > 0) {
                arrowView.setRotate(90);
            }

            else {
                arrowView.setRotate(270);
            }
        }

        else if (translateTransition.getByY() == 0) {
            if (translateTransition.getByX() > 0) {
            }

            else {
                arrowView.setRotate(180);
            }
        }

        else {
            double arcTan = Math.atan2(translateTransition.getByY(), translateTransition.getByX());
            double degrees = Math.toDegrees(arcTan);

            arrowView.setRotate(degrees);
        }

        scaleTransition.setNode(arrowView);
        scaleTransition.setByX(2.0f);
        scaleTransition.setByY(2.0f);
        scaleTransition.setDuration(translateTransition.getDuration().divide(2));
        scaleTransition.setCycleCount(1);

        scaleTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Scale 1 Done");

                ScaleTransition scaleTransition = new ScaleTransition();
                scaleTransition.setNode(arrowView);
                scaleTransition.setByX(-3.0f);
                scaleTransition.setByY(-3.0f);
                scaleTransition.setDuration(translateTransition.getDuration().divide(2));
                scaleTransition.setCycleCount(1);

                scaleTransition.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {

                        System.out.println("Scale 2 Done");

                        shootFrom.getChildren().remove(arrowView);
                        addTo.addFire();
                        switchTurns();
                    }
                });

                scaleTransition.play();
            }
        });

        translateTransition.play();
        scaleTransition.play();

    }

    public boolean isGameOver() {
        for(GameSquare square: rectangles) {
            SquareInfo info = square.getSquareInfo();
            if (info.getSquareType() == SquareType.Amazon && info.isWhite() == isWhitesTurn) {
                ArrayList<GameSquare> list = getValidSquares(square);
                if (list.size() > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public void resetGame() {

        isMove = true;
        isWhitesTurn = true;

        lblInfo.setText((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");

        for(GameSquare square: rectangles) {
            square.removePiece();
        }
    }


    public ArrayList<GameSquare> getValidSquares(GameSquare square) {
        ArrayList<GameSquare> list = new ArrayList<>();

        SquareInfo info = square.getSquareInfo();

        int startRow = info.getRow();
        int startColumn = info.getColumn();

        boolean done = true;

        checkAbove(list, startRow, startColumn);
        checkBelow(list, startRow, startColumn);
        checkLeft(list, startRow, startColumn);
        checkRight(list, startRow, startColumn);
        checkLeftUp(list, startRow, startColumn);
        checkLeftDown(list, startRow, startColumn);
        checkRightUp(list, startRow, startColumn);
        checkRightDown(list, startRow, startColumn);

        return list;
    }

    public ArrayList<GameSquare> getValidSquaresForArrowAfterMove(GameSquare moveToSquare, GameSquare moveFromSquare) {
        ArrayList<GameSquare> list = new ArrayList<>();

        SquareInfo originalInfo = moveFromSquare.getSquareInfo();
        SquareInfo emptyInfo = new Empty(originalInfo.getRow(), originalInfo.getColumn());

        GameSquare originalSquare = getSquare(originalInfo.getRow(),originalInfo.getColumn());
        originalSquare.setSquareInfo(emptyInfo);

        ImageViewPane imageView = originalSquare.getImageView();

        originalSquare.removePiece();

        SquareInfo info = moveToSquare.getSquareInfo();

        int startRow = info.getRow();
        int startColumn = info.getColumn();

        boolean done = true;

        checkAbove(list, startRow, startColumn);
        checkBelow(list, startRow, startColumn);
        checkLeft(list, startRow, startColumn);
        checkRight(list, startRow, startColumn);
        checkLeftUp(list, startRow, startColumn);
        checkLeftDown(list, startRow, startColumn);
        checkRightUp(list, startRow, startColumn);
        checkRightDown(list, startRow, startColumn);

        originalSquare.setSquareInfo(originalInfo);
        originalSquare.setImageView(imageView);

        return list;
    }

    private void checkAbove(List<GameSquare> list, int row, int column) {

        row--;

        while(row >= 0) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row--;
            }

            else {
                return;
            }
        }
    }

    private void checkBelow(List<GameSquare> list, int row, int column) {
        row++;

        while(row < gameSize) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row++;
            }

            else {
                return;
            }
        }
    }

    private void checkLeft(List<GameSquare> list, int row, int column) {
        column--;

        while(column >= 0) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                column--;
            }

            else {
                return;
            }
        }
    }

    private void checkRight(List<GameSquare> list, int row, int column) {
        column++;

        while(column < gameSize) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                column++;
            }

            else {
                return;
            }
        }
    }

    private void checkLeftUp(List<GameSquare> list, int row, int column) {
        row--;
        column--;
        while(row >= 0 && column >= 0) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row--;
                column--;
            }

            else {
                return;
            }
        }
    }

    private void checkLeftDown(List<GameSquare> list, int row, int column) {
        row++;
        column--;

        while(row < gameSize && column >= 0) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row++;
                column--;
            }

            else {
                return;
            }
        }
    }

    private void checkRightUp(List<GameSquare> list, int row, int column) {
        row--;
        column++;

        while(row >= 0 && column < gameSize) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row--;
                column++;
            }

            else {
                return;
            }
        }
    }

    private void checkRightDown(List<GameSquare> list, int row, int column) {
        row++;
        column++;

        while(row < gameSize && column < gameSize) {
            GameSquare square = getSquare(row, column);
            SquareInfo info = square.getSquareInfo();

            if (info.isEmpty()) {
                list.add(square);
                row++;
                column++;
            }

            else {
                return;
            }
        }
    }
}
