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

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * StackPane that is empty, has a piece, or has fire
 */
public class GameSquare extends StackPane {

    private SquareInfo squareInfo;
    private GameInfo gameInfo;
    private boolean isWhite;

    private ImageViewPane imageView = null;

    // Various background style for normal, when it is a valid movement square, and when it is not a valid movement
    // square
    private String normalStyle()
    {
        return isWhite ? "-fx-background-color: rgba(255, 255, 255, 0)" : "-fx-background-color: rgba(0, 0, 0, 0.5)";
    }

    private String availableStyle()
    {
        return isWhite ? "-fx-background-color: rgba(152, 251, 152, 0.75)" : "-fx-background-color: rgba(34, 139, 34, 0.75)";
    }

    private String notAvailableStyle()
    {
        return isWhite ? "-fx-background-color: rgba(255, 0, 0, 0.75)" : "-fx-background-color: rgba(139, 0, 0, 0.75)";
    }

    public GameSquare(GameInfo gameInfo, byte row, byte column, boolean isWhite) {

        this.gameInfo = gameInfo;
        this.isWhite = isWhite;

        squareInfo = new Empty(row, column);

        setOnDragOver(this::onDragOver);
        setOnDragExited(this::onDragExited);
        setOnDragDetected(this::onDragDetected);
        setOnDragDone(this::onDragDone);
        setOnDragDropped(this::onDragDropped);
        setOnMouseClicked(this::onMouseClicked);

        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0))));

        setStyle(normalStyle());
    }

    public SquareInfo getSquareInfo() {
        return squareInfo;
    }

    public ImageViewPane getImageView() { return imageView; }

    /**
     * Add an amazon image to this square
     * @param isWhite - true if the pieces should be white
     */
    public void addAmazon(boolean isWhite) {

        if (imageView != null) {
            getChildren().clear();
            imageView = null;
        }

        squareInfo = new Amazon(squareInfo.getRow(), squareInfo.getColumn(), isWhite);

        Image image = new Image(squareInfo.getImageFileName());

        imageView = new ImageViewPane();
        imageView.setImageView(new ImageView(image));

        imageView.setStyle("-fx-opacity: 100%");

        getChildren().add(imageView);
    }

    /**
     * Add the fire gif to the square
     */
    public void addFire() {
        if (imageView != null) {
            getChildren().clear();
            imageView = null;
        }

        squareInfo = new Fire(squareInfo.getRow(), squareInfo.getColumn());

        Image image = new Image(squareInfo.getImageFileName());

        imageView = new ImageViewPane();
        imageView.setImageView(new ImageView(image));

        getChildren().add(imageView);
    }

    /**
     * Replace the square's image with the still image of fire
     */
    public void switchToStillFire() {
        if (imageView != null) {
            getChildren().clear();
            imageView = null;
        }

        squareInfo = new Fire(squareInfo.getRow(), squareInfo.getColumn());

        Image image = new Image("images/still_fire.png");

        imageView = new ImageViewPane();
        imageView.setImageView(new ImageView(image));

        getChildren().add(imageView);
    }

    /**
     * Make the square empty
     */
    public void removePiece() {
        if (imageView != null) {
            getChildren().clear();
            imageView = null;
        }

        squareInfo = new Empty(squareInfo.row, squareInfo.column);

        setStyle(normalStyle());
    }

    /**
     * Revert the style back to the normal style
     */
    public void resetStyle() {
        setStyle(normalStyle());
    }

    /**
     * Event that handled when the drag of a pieces in the square starts
     * @param e - event
     */
    private void onDragDetected(MouseEvent e) {

        // Only valid to move if we are not animating something, it is the move phase of the game, and the pieces
        // is off the color of the player whose turn it is
        if (gameInfo.getIsOkToMovePiece() && gameInfo.getIsMovePhase() && gameInfo.getIsWhitesTurn() == squareInfo.getIsWhite() &&
                squareInfo instanceof Amazon) {

            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            if (imageView == null) {
                return;
            }

            // Create copy of image to show during drag
            ImageView view = imageView.getImageView();
            Image image = view.getImage();
            Scene scene = view.getScene();

            Image scaledImage = new Image(image.getUrl(), view.getFitWidth() * scene.getWindow().getOutputScaleX(),
                    view.getFitHeight() * scene.getWindow().getOutputScaleY(), true, true);

            double offsetX = e.getX() - view.getX();
            double offsetY = e.getY() - view.getY();

            db.setDragView(scaledImage);
            db.setDragViewOffsetX(offsetX);
            db.setDragViewOffsetY(offsetY);

            // Add the square info to the content of the drag
            ClipboardContent content = new ClipboardContent();
            content.put(SquareInfo.SQUARE_INFO, squareInfo);
            db.setContent(content);

            ArrayList<SquareInfo> validList = ValidMoveCalculator.getValidSquares(PositionConverter.convertBoardStateToBitSet(gameInfo.getCurrentBoardState()), getSquareInfo(), gameInfo.getGameSize());

            // Show all valid moves by changing highlights of those squares
            for(SquareInfo info : validList) {
                GameSquare square = gameInfo.getGameSquare(info.getRow(), info.getColumn());
                square.setStyle(square.availableStyle());
            }

            e.consume();
        }
    }

    /**
     * Event to handle when a drag is over a square
     * @param e - event
     */
    private void onDragOver(DragEvent e) {
        Dragboard db = e.getDragboard();

        // Only handle if the thing being dragged is the expected square info
        if (db.hasContent(SquareInfo.SQUARE_INFO))
        {
            SquareInfo movingPiece = (SquareInfo)db.getContent(SquareInfo.SQUARE_INFO);

            e.acceptTransferModes(TransferMode.MOVE);

            // If over a space that cannot be dropped in, highlight it red
            if (!canDrop(movingPiece)) {
                setStyle(notAvailableStyle());
            }
        }

        e.consume();
    }

    /**
     * After a drag is done, make all highlights go away
     * @param e - event
     */
    private void onDragDone(DragEvent e) {
        Dragboard db = e.getDragboard();

        if (gameInfo.getIsMovePhase()) {

            ArrayList<SquareInfo> validList = ValidMoveCalculator.getValidSquares(gameInfo.getCurrentBoardState(), getSquareInfo());

            for(SquareInfo info : validList) {
                GameSquare square = gameInfo.getGameSquare(info.getRow(), info.getColumn());
                square.setStyle(square.normalStyle());
            }
        }

        e.consume();
    }

    /**
     * Handle the piece being dropped in the square
     * @param e - event
     */
    private void onDragDropped(DragEvent e) {

        Dragboard db = e.getDragboard();
        if (db.hasContent(SquareInfo.SQUARE_INFO))
        {
            SquareInfo movingPiece = (SquareInfo)db.getContent(SquareInfo.SQUARE_INFO);

            if (canDrop(movingPiece)) {

                // Remove all highlighting
                ArrayList<SquareInfo> validList = ValidMoveCalculator.getValidSquares(gameInfo.getCurrentBoardState(), movingPiece);

                for(SquareInfo info : validList) {
                    GameSquare square = gameInfo.getGameSquare(info.getRow(), info.getColumn());
                    square.setStyle(square.normalStyle());
                }

                // Move the pieces to this square
                gameInfo.movePiece(movingPiece, squareInfo, false, null);
                gameInfo.addMove(movingPiece, squareInfo);

                // Highlight valid moves for the arrow
                validList = ValidMoveCalculator.getValidSquares(gameInfo.getCurrentBoardState(), getSquareInfo());
                for(SquareInfo info : validList) {
                    GameSquare square = gameInfo.getGameSquare(info.getRow(), info.getColumn());
                    square.setStyle(square.availableStyle());
                }
            }
        }

        e.consume();
    }

    /**
     * Handle the mouse click event for the square. Only used for shooting an arrow
     * @param mouseEvent
     */
    private void onMouseClicked(MouseEvent mouseEvent) {

        // Only handle if it is the arrow phase and nothing is animating
        if (!gameInfo.getIsMovePhase() && gameInfo.getIsOkToMovePiece()) {

            gameInfo.setIsOkToMovePiece(false);

            GameMove lastMove = gameInfo.getLastMove();

            if (lastMove == null) {
                return;
            }

            GameSquare squareForLastMove = gameInfo.getGameSquare(lastMove.getAmazonToRow(), lastMove.getAmazonToColumn());
            SquareInfo info = squareForLastMove.getSquareInfo();

            if (canDrop(info)) {

                ArrayList<SquareInfo> validList = ValidMoveCalculator.getValidSquares(gameInfo.getCurrentBoardState(), info);

                for(SquareInfo inInfo : validList) {
                    GameSquare square = gameInfo.getGameSquare(inInfo.getRow(), inInfo.getColumn());
                    square.setStyle(square.normalStyle());
                }

                gameInfo.shootArrow(squareForLastMove, this);
            }

            else {
                gameInfo.setIsOkToMovePiece(true);
            }
        }
    }

    /**
     * Handle when the square is no longer being dragged over. Used to reset style of invalid squares
     * @param e
     */
    private void onDragExited(DragEvent e) {
        Dragboard db = e.getDragboard();

        if (db.hasContent(SquareInfo.SQUARE_INFO))
        {
            SquareInfo movingPiece = (SquareInfo)db.getContent(SquareInfo.SQUARE_INFO);

            if (!canDrop(movingPiece)) {
                setStyle(normalStyle());
            }
        }

        e.consume();
    }

    /**
     * Return true is the movingPiece can be dropped in this square
     * @param movingPiece - piece that is being used
     * @return - true is the movingPiece can be dropped in this square
     */
    private boolean canDrop(SquareInfo movingPiece) {

        ArrayList<SquareInfo> validList = ValidMoveCalculator.getValidSquares(gameInfo.getCurrentBoardState(), movingPiece);

        Predicate<SquareInfo> rowEqual = e -> e.getRow() == getSquareInfo().getRow();
        Predicate<SquareInfo> columnEqual = e -> e.getColumn() == getSquareInfo().getColumn();
        Predicate<SquareInfo> combined = rowEqual.and(columnEqual);

        return validList.stream().anyMatch(combined);
    }
}
