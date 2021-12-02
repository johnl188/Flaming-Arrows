package org.amazons.mainui;

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
import org.amazons.ai.AIPlayer;
import org.amazons.ai.AIPlayerType;
import org.amazons.ai.EasyAI;
import org.amazons.ai.HardAI;

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

        // Determine AIPlayer
        if (gameOptions.getAIPlayerType() == AIPlayerType.Easy) {
            aiPlayer = new EasyAI(gameOptions.getIsAIFirst(), gameOptions.getGameSize());
        }

        else if (gameOptions.getAIPlayerType() == AIPlayerType.Hard) {
            aiPlayer = new HardAI(gameOptions.getIsAIFirst(), gameOptions.getGameSize());
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

    /**
     * After a player shoots an arrow, move to the next phase of the game. If there is an AI, and it is now the
     * AI's turn, determine the AI move. Else let a human play take their turn
     */
    public void switchTurns() {

        // Switch the color's turn
        goToMovePhase();
        isWhitesTurn = !isWhitesTurn;
        setGameInfoLabel((isWhitesTurn ? "White's" : "Black's") + " turn to move a piece");

        // End game if needed
        if (isGameOver()) {
            setGameInfoLabel((!isWhitesTurn ? "White" : "Black") + " wins!");
            return;
        }

        // If it is the AIPlayer's turn, get their move
        if (aiPlayer != null && isWhitesTurn == aiPlayer.getIsWhite()) {

            setIsOkToMovePiece(false);

            BitSet bitSet = PositionConverter.convertBoardStateToBitSet(getCurrentBoardState());

            setGameInfoLabel("The computer is deciding on a move");

            // Put AI Calculation on background thread
            new Thread(() -> {

                GameMove move = aiPlayer.getMove(bitSet);

                // Put pieces movement back on UI Thread
                Platform.runLater(() -> {
                    if (move == null) {
                        return;
                    }

                    // Make sure move is valid
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

                    // After move, shot arrow
                    movePiece(movingPiece.getSquareInfo(), toSquare.getSquareInfo(), true, actionEvent -> {

                        ArrayList<SquareInfo> validMoves1 = ValidMoveCalculator.getValidSquares(getCurrentBoardState(), toSquare.getSquareInfo());
                        Predicate<SquareInfo> rowEqual1 = e -> e.getRow() == fireSquare.getSquareInfo().getRow();
                        Predicate<SquareInfo> columnEqual1 = e -> e.getColumn() == fireSquare.getSquareInfo().getColumn();
                        Predicate<SquareInfo> combined1 = rowEqual1.and(columnEqual1);

                        if (validMoves1.stream().noneMatch(combined1)) {
                            return;
                        }

                        shootArrow(toSquare, fireSquare);
                    });

                    addMove(movingPiece.getSquareInfo(), toSquare.getSquareInfo());
                });
                }).start();
        }
    }

    /**
     * Add Game Square to gameSquare Array
     * @param square - Square to add
     */
    public void addGameSquare(GameSquare square) {
        SquareInfo info = square.getSquareInfo();
        gameSquares[getGameSize() * info.getRow() + info.getColumn()] = square;
    }

    /**
     * get the gameSquare of the input row and column
     * @param row - row
     * @param column - column
     * @return - return the gameSquare
     */
    public GameSquare getGameSquare(int row, int column) {
        return gameSquares[getGameSize() * row + column];
    }

    /**
     * Start the game. If there is an AIPlayer, and they are first, trigger their move to be found
     */
    public void startGame() {
        if (aiPlayer != null && aiPlayer.getIsWhite()) {
            isWhitesTurn = false;
            switchTurns();
        }
    }


    /**
     * Update the squareInfo array so that the piece in from is moved to 'to'. Optionally animate the movement
     * @param from - Square where the piece starts
     * @param to - Square where the piece is moving to
     * @param shouldAnimate - true if the movement should be animated
     * @param afterAnimate - Function to call after animation if needed
     */
    public void movePiece(SquareInfo from, SquareInfo to, boolean shouldAnimate, EventHandler<ActionEvent> afterAnimate) {

        GameSquare fromSquare = getGameSquare(from.getRow(), from.getColumn());
        GameSquare toSquare = getGameSquare(to.getRow(), to.getColumn());

        goToArrowPhase();

        if (shouldAnimate) {

            // Create the Translation Transition for piece
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

            // Put new pieces in the right square and remove from the from square. Call the afterAnimate function
            translateTransition.setOnFinished(actionEvent -> {

                fromSquare.removePiece();
                toSquare.addAmazon(from.isWhite);

                if (afterAnimate != null) {
                    afterAnimate.handle(actionEvent);
                }
            });

            translateTransition.play();
        }

        else {
            // If no animation, just update the squares
            fromSquare.removePiece();
            toSquare.addAmazon(from.isWhite);
        }

    }

    /**
     * Add a move without the arrow part to the previousMoves array
     * @param from - Square where piece is moving from
     * @param to - Square where piece is moving to
     */
    public void addMove(SquareInfo from, SquareInfo to) {

        previousMoves.add(new GameMove(from.getRow(), from.getColumn(), to.getRow(), to.getColumn()));
    }

    public void shootArrow(GameSquare shootFrom, GameSquare addTo) {

        SoundEffects.arrowSound.seek(SoundEffects.arrowSound.getStartTime());
        SoundEffects.arrowSound.play();

        //SoundEffects.fireSound.seek(SoundEffects.fireSound.getStartTime());
        //SoundEffects.fireSound.play();

        shootFrom.toFront();

        // Create and add arrow image
        Image image = new Image("images/arrow.png");

        ImageViewPane arrowView = new ImageViewPane();
        arrowView.setImageView(new ImageView(image));

        shootFrom.getChildren().add(arrowView);

        // Update last move with arrow information
        GameMove lastMove = previousMoves.get(previousMoves.size() - 1);
        lastMove.setArrowMove(addTo.getSquareInfo().getRow(), addTo.getSquareInfo().getColumn());

        TranslateTransition translateTransition = createArrowTranslateTransition(arrowView, shootFrom, addTo);
        ScaleTransition scaleTransition = createArrowFirstsScaleTransition(arrowView, shootFrom, addTo);

        // Delay starting the animations
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            translateTransition.play();
                            scaleTransition.play();
                        });
                    }
                }, 50);

        //SoundEffects.fireSound.seek(SoundEffects.fireSound.getStartTime());
        //SoundEffects.fireSound.play();
    }

    /**
     * Create the TranslationTransition for the arrow shot
     * @param arrowView - ImageViewPane of the arrow image
     * @param shootFrom - Square that is being shot from
     * @param addTo - Square that is being shot to
     * @return - return the TranslationTransition
     */
    private TranslateTransition createArrowTranslateTransition(ImageViewPane arrowView, GameSquare shootFrom, GameSquare addTo) {

        TranslateTransition translateTransition = new TranslateTransition();

        translateTransition.setDuration(Duration.millis(1000));

        translateTransition.setNode(arrowView);

        Point2D fromPoint = shootFrom.localToScene(0, 0);
        Point2D toPoint = addTo.localToScene(0, 0);

        translateTransition.setByX(toPoint.getX() - fromPoint.getX());
        translateTransition.setByY(toPoint.getY() - fromPoint.getY());

        translateTransition.setCycleCount(1);

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
            if (translateTransition.getByX() < 0) {
                arrowView.setRotate(180);
            }
        }

        else {
            double arcTan = Math.atan2(translateTransition.getByY(), translateTransition.getByX());
            double degrees = Math.toDegrees(arcTan);

            arrowView.setRotate(degrees);
        }

        return translateTransition;
    }

    /**
     * Create the First Scale Transition for the arrow shot
     * @param arrowView - ImageViewPane of the arrow image
     * @param shootFrom - Square that is being shot from
     * @param addTo - Square that is being shot to
     * @return - return scale transition for arrow
     */
    private ScaleTransition createArrowFirstsScaleTransition(ImageViewPane arrowView, GameSquare shootFrom, GameSquare addTo) {
        ScaleTransition scaleTransition = new ScaleTransition();

        // Arrow gets 2 times bigger to make it look like it is going up
        scaleTransition.setNode(arrowView);
        scaleTransition.setByX(2.0f);
        scaleTransition.setByY(2.0f);
        scaleTransition.setDuration(Duration.millis(500));
        scaleTransition.setCycleCount(1);

        scaleTransition.setOnFinished(actionEvent -> {

            ScaleTransition scaleTransition1 = createArrowSecondScaleTransition(arrowView, shootFrom, addTo);

            scaleTransition1.play();
        });

        return scaleTransition;
    }

    /**
     * Create the Second Scale Transition for the arrow shot
     * @param arrowView - ImageViewPane of the arrow image
     * @param shootFrom - Square that is being shot from
     * @param addTo - Square that is being shot to
     * @return - return scale transition for arrow
     */
    private ScaleTransition createArrowSecondScaleTransition(ImageViewPane arrowView, GameSquare shootFrom, GameSquare addTo) {

        ScaleTransition scaleTransition = new ScaleTransition();

        // Make the arrow smaller, so it looks like it is going down
        scaleTransition.setNode(arrowView);
        scaleTransition.setByX(-3.0f);
        scaleTransition.setByY(-3.0f);
        scaleTransition.setDuration(Duration.millis(500));
        scaleTransition.setCycleCount(1);

        scaleTransition.setOnFinished(actionEvent -> {

            // Remove arrow picture and add fire gif to square
            shootFrom.getChildren().remove(arrowView);
            addTo.addFire();

            // After a second, change the fire gif to the still fire image
            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                addTo.switchToStillFire();
                                setIsOkToMovePiece(true);
                                switchTurns();

                            });
                        }
                    }, 1000
            );
        });

        return scaleTransition;
    }

    /**
     * return true if the game is over
     * @return - true if the game is over
     */
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

    /**
     * Reset phases to the beginning of the game and make the whole board empty, clear the previous moves
     */
    public void resetGame() {

        isMovePhase = true;
        isWhitesTurn = true;

        setGameInfoLabel("White's turn to move a piece");

        for(GameSquare square: gameSquares) {
            square.removePiece();
        }

        previousMoves = new ArrayList<>();
    }

    /**
     * Undo the previous move, if the previous move was an AI Move, also undo that move
     * If we are in the shooting arrow phase, just undo the last piece movement
     */
    public void undoLastMove() {

        boolean priorIsMove = isMovePhase;

        if (getIsOkToMovePiece()) {

            // Return if there are not any previous moves
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

        SoundEffects.undoSound.seek(SoundEffects.undoSound.getStartTime());
        SoundEffects.undoSound.play();
    }

    /**
     * Set the highlighting of all square to the default
     */
    public void resetHighlighting() {

        for (GameSquare square: gameSquares) {
            square.resetStyle();
        }
    }

    /**
     * Convert the game squares to a BoardState by copying squares;
     * @return - return the BoardState
     */
    public BoardState getCurrentBoardState() {
        SquareInfo[] squareInfos = new SquareInfo[gameSquares.length];

        for(int i = 0; i < gameSquares.length; i++) {
            squareInfos[i] = gameSquares[i].getSquareInfo().makeCopy();
        }

        return new BoardState(squareInfos, getGameSize());
    }
}
