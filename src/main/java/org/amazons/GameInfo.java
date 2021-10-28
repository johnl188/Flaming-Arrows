package org.amazons;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

public class GameInfo {

    private GameOptions gameOptions;
    private GameSquare[] gameSquares;
    private boolean isWhitesTurn;
    private boolean isMovePhase;
    private boolean isOkToMovePiece;
    private AIPlayer aiPlayer = null;
    private ArrayList<GameMove> previousMoves;

    private SimpleStringProperty currentLabel = new SimpleStringProperty();

    public GameInfo(GameOptions gameOptions) {
        this.gameOptions = gameOptions;

        gameSquares = new GameSquare[gameOptions.getGameSize() * gameOptions.getGameSize()];
        previousMoves = new ArrayList<>();
        isMovePhase = true;
        isWhitesTurn = true;
        setIsOkToMovePiece(true);

        setGameInfoLabel((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");

        if (gameOptions.getAIPlayerType() == AIPlayerType.Random) {
            aiPlayer = new RandomAI(gameOptions.getIsAIFirst());
        }

        else if (gameOptions.getAIPlayerType() == AIPlayerType.Easy) {
            aiPlayer = new EasyAI(gameOptions.getIsAIFirst(), gameOptions.getGameSize());
        }
    }

    public int getGameSize() { return gameOptions.getGameSize(); }

    public boolean getIsMovePhase() { return isMovePhase; }

    public boolean getIsWhitesTurn() { return isWhitesTurn; }

    public boolean getIsOkToMovePiece() { return isOkToMovePiece; }

    public GameMove getLastMove() {

        if (previousMoves.size() < 1) {
            return null;
        }

        return previousMoves.get(previousMoves.size() - 1);
    }

    private void setGameInfoLabel(String label) { currentLabel.set(label); }

    public SimpleStringProperty gameInfoLabelProperty() { return currentLabel; }

    public void setIsOkToMovePiece(boolean isOk) { this.isOkToMovePiece = isOk; }

    public void goToMovePhase() { isMovePhase = true; }

    public void goToArrowPhase() {
        setGameInfoLabel((isWhitesTurn ? "White's" : "Black's") + " turn to shoot an arrow");
        isMovePhase = false;
    }

    public void switchTurns() {
        goToMovePhase();
        isWhitesTurn = !isWhitesTurn;
        setGameInfoLabel((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");

        if (isGameOver()) {
            setGameInfoLabel((!isWhitesTurn ? "White's" : "Black's") + " wins!");
            return;
        }

        if (aiPlayer != null && isWhitesTurn == aiPlayer.getIsWhite()) {
            setIsOkToMovePiece(false);

            BitSet bitSet = PositionConverter.convertBoardStateToBitSet(getCurrentBoardState());

            GameMove move = aiPlayer.getMove(bitSet, getGameSize());
            if (move == null) {
                return;
            }

            GameSquare movingPiece = getGameSquare(move.getAmazonFromRow(), move.getAmazonFromColumn());
            GameSquare toSquare = getGameSquare(move.getAmazonToRow(), move.getAmazonToColumn());
            GameSquare fireSquare = getGameSquare(move.getArrowRow(), move.getArrowColumn());

            ArrayList<SquareInfo> validMoves = ValidMoveCalculator.getValidSquares(getCurrentBoardState(), movingPiece.getSquareInfo());

            Predicate<SquareInfo> rowEqual = e -> e.getRow() == toSquare.getSquareInfo().getRow();
            Predicate<SquareInfo> columnEqual = e -> e.getColumn() == toSquare.getSquareInfo().getColumn();
            Predicate<SquareInfo> combined = rowEqual.and(columnEqual);

            if (validMoves.stream().noneMatch(combined)) {
                return;
            }

            movePiece(movingPiece.getSquareInfo(), toSquare.getSquareInfo(), true, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    ArrayList<SquareInfo> validMoves = ValidMoveCalculator.getValidSquares(getCurrentBoardState(), toSquare.getSquareInfo());
                    Predicate<SquareInfo> rowEqual = e -> e.getRow() == fireSquare.getSquareInfo().getRow();
                    Predicate<SquareInfo> columnEqual = e -> e.getColumn() == fireSquare.getSquareInfo().getColumn();
                    Predicate<SquareInfo> combined = rowEqual.and(columnEqual);

                    if (validMoves.stream().noneMatch(combined)) {
                        return;
                    }

                    shootArrow(toSquare, fireSquare);
                }
            });

            addMove(movingPiece.getSquareInfo(), toSquare.getSquareInfo());
        }
    }

    public void addGameSquare(GameSquare square) {
        SquareInfo info = square.getSquareInfo();
        gameSquares[getGameSize() * info.getRow() + info.getColumn()] = square;
    }

    public void startGame() {
        if (aiPlayer != null && aiPlayer.getIsWhite()) {
            isWhitesTurn = false;
            switchTurns();
        }
    }

    public GameSquare getGameSquare(int row, int column) {
        return gameSquares[getGameSize() * row + column];
    }

    public void movePiece(SquareInfo from, SquareInfo to, boolean shouldAnimate, EventHandler<ActionEvent> afterAnimate) {

        GameSquare fromSquare = getGameSquare(from.getRow(), from.getColumn());
        GameSquare toSquare = getGameSquare(to.getRow(), to.getColumn());

        goToArrowPhase();

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

    public void addMove(SquareInfo from, SquareInfo to) {

        previousMoves.add(new GameMove(from.getRow(), from.getColumn(), to.getRow(), to.getColumn()));
    }

    public void shootArrow(GameSquare shootFrom, GameSquare addTo) {

        ImageViewPane imageView = shootFrom.getImageView();
        shootFrom.toFront();

        Image image = new Image("arrow.png");

        ImageViewPane arrowView = new ImageViewPane();
        arrowView.setImageView(new ImageView(image));

        shootFrom.getChildren().add(arrowView);


        GameMove lastMove = previousMoves.get(previousMoves.size() - 1);
        lastMove.setArrowMove(addTo.getSquareInfo().getRow(), addTo.getSquareInfo().getColumn());

        if (aiPlayer != null && aiPlayer.getIsWhite() == !isWhitesTurn) {
            aiPlayer.informAIOfGameMove(lastMove, isWhitesTurn);
        }


        TranslateTransition translateTransition = new TranslateTransition();
        ScaleTransition scaleTransition = new ScaleTransition();

        translateTransition.setDuration(Duration.millis(1000));

        translateTransition.setNode(arrowView);

        Point2D fromPoint = shootFrom.localToScene(0, 0);
        Point2D toPoint = addTo.localToScene(0, 0);

        translateTransition.setByX(toPoint.getX() - fromPoint.getX());
        translateTransition.setByY(toPoint.getY() - fromPoint.getY());

        translateTransition.setCycleCount(1);

        translateTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
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

                ScaleTransition scaleTransition = new ScaleTransition();
                scaleTransition.setNode(arrowView);
                scaleTransition.setByX(-3.0f);
                scaleTransition.setByY(-3.0f);
                scaleTransition.setDuration(translateTransition.getDuration().divide(2));
                scaleTransition.setCycleCount(1);

                scaleTransition.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {

                        shootFrom.getChildren().remove(arrowView);
                        addTo.addFire();

                        setIsOkToMovePiece(true);
                        switchTurns();

                        new Timer().schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                addTo.switchToStillFire();
                                            }
                                        });
                                    }
                                }, 2000
                        );
                    }
                });

                scaleTransition.play();
            }
        });

        translateTransition.play();
        scaleTransition.play();

    }

    public boolean isGameOver() {
        for(GameSquare square: gameSquares) {
            SquareInfo info = square.getSquareInfo();
            if (info instanceof Amazon && info.getIsWhite() == isWhitesTurn) {

                ArrayList<SquareInfo> list = ValidMoveCalculator.getValidSquares(getCurrentBoardState(), info);

                if (list.size() > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public void resetGame() {

        isMovePhase = true;
        isWhitesTurn = true;

        setGameInfoLabel((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");

        for(GameSquare square: gameSquares) {
            square.removePiece();
        }

        previousMoves = new ArrayList<>();

        if (aiPlayer != null) {
            aiPlayer.resetGame(gameOptions.getGameSize());
        }
    }

    public void undoLastMove() {

        boolean priorIsMove = isMovePhase;
        if (getIsOkToMovePiece()) {

            if (previousMoves.size() < 1) {
                return;
            }

            GameMove lastMove = previousMoves.remove(previousMoves.size() - 1);
            if (isMovePhase) {
                GameSquare fireSquare = getGameSquare(lastMove.getArrowRow(), lastMove.getArrowColumn());
                fireSquare.removePiece();
            }

            GameSquare fromSquare = getGameSquare(lastMove.getAmazonFromRow(), lastMove.getAmazonFromColumn());
            GameSquare toSquare = getGameSquare(lastMove.getAmazonToRow(), lastMove.getAmazonToColumn());

            movePiece(toSquare.getSquareInfo(), fromSquare.getSquareInfo(), false, null);

            if (priorIsMove) {
                if (aiPlayer != null) {
                    if (aiPlayer.getIsWhite() == isWhitesTurn) {
                        switchTurns();
                    }

                    else {
                        isMovePhase = true;
                        isWhitesTurn = !isWhitesTurn;
                        undoLastMove();
                    }
                }

                else {
                    switchTurns();
                }
            }

            else {
                goToMovePhase();
            }

            resetHighlighting();
        }
    }

    public void resetHighlighting() {

        for (GameSquare square: gameSquares) {
            square.resetStyle();
        }
    }

    public BoardState getCurrentBoardState() {
        SquareInfo[] squareInfos = new SquareInfo[gameSquares.length];

        for(int i = 0; i < gameSquares.length; i++) {
            squareInfos[i] = gameSquares[i].getSquareInfo().makeCopy();
        }

        return new BoardState(squareInfos, getGameSize());
    }
}
