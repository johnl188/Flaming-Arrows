package org.amazons;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.function.Predicate;

public class GameSquare extends StackPane {

    private SquareInfo squareInfo;
    private GameInfo gameInfo;
    private boolean isWhite;

    private ImageViewPane imageView = null;

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

    public void switchToStillFire() {
        if (imageView != null) {
            getChildren().clear();
            imageView = null;
        }

        squareInfo = new Fire(squareInfo.getRow(), squareInfo.getColumn());

        Image image = new Image("still_fire.png");

        imageView = new ImageViewPane();
        imageView.setImageView(new ImageView(image));

        getChildren().add(imageView);
    }

    public void removePiece() {
        if (imageView != null) {
            getChildren().clear();
            imageView = null;
        }

        squareInfo = new Empty(squareInfo.row, squareInfo.column);

        setStyle(normalStyle());
    }

    public void resetStyle() {
        setStyle(normalStyle());
    }

    private void onDragDetected(MouseEvent e) {

        if (gameInfo.getIsMovePhase() && gameInfo.getIsWhitesTurn() == squareInfo.getIsWhite() &&
                squareInfo instanceof Amazon) {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            if (imageView == null) {
                return;
            }

            ImageView view = imageView.getImageView();
            Image image = view.getImage();
            Image scaledImage = new Image(image.getUrl(), view.getFitWidth(), view.getFitHeight(), true, true);

            double offsetX = e.getX() - view.getX();
            double offsetY = e.getY() - view.getY();

            db.setDragView(scaledImage);
            db.setDragViewOffsetX(offsetX);
            db.setDragViewOffsetY(offsetY);

            ClipboardContent content = new ClipboardContent();

            content.put(SquareInfo.SQUARE_INFO, squareInfo);

            db.setContent(content);

            //ArrayList<SquareInfo> validList = ValidMoveCalculator.getValidSquares(gameInfo.getCurrentBoardState(), getSquareInfo());

            ArrayList<SquareInfo> validList = ValidMoveCalculator.getValidSquares(PositionConverter.convertBoardStateToBitSet(gameInfo.getCurrentBoardState()), getSquareInfo(), gameInfo.getGameSize());

            for(SquareInfo info : validList) {
                GameSquare square = gameInfo.getGameSquare(info.getRow(), info.getColumn());
                square.setStyle(square.availableStyle());
            }

            e.consume();
        }
    }

    private void onDragOver(DragEvent e) {
        Dragboard db = e.getDragboard();

        if (db.hasContent(SquareInfo.SQUARE_INFO))
        {
            SquareInfo movingPiece = (SquareInfo)db.getContent(SquareInfo.SQUARE_INFO);

            e.acceptTransferModes(TransferMode.MOVE);

            if (!canDrop(movingPiece)) {
                setStyle(notAvailableStyle());
            }
        }

        e.consume();
    }

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

    private void onDragDropped(DragEvent e) {
        Dragboard db = e.getDragboard();
        if (db.hasContent(SquareInfo.SQUARE_INFO))
        {
            SquareInfo movingPiece = (SquareInfo)db.getContent(SquareInfo.SQUARE_INFO);

            if (canDrop(movingPiece)) {

                ArrayList<SquareInfo> validList = ValidMoveCalculator.getValidSquares(gameInfo.getCurrentBoardState(), movingPiece);

                for(SquareInfo info : validList) {
                    GameSquare square = gameInfo.getGameSquare(info.getRow(), info.getColumn());
                    square.setStyle(square.normalStyle());
                }

                gameInfo.movePiece(movingPiece, squareInfo, false, null);
                gameInfo.addMove(movingPiece, squareInfo);

                validList = ValidMoveCalculator.getValidSquares(gameInfo.getCurrentBoardState(), getSquareInfo());
                for(SquareInfo info : validList) {
                    GameSquare square = gameInfo.getGameSquare(info.getRow(), info.getColumn());
                    square.setStyle(square.availableStyle());
                }
            }
        }

        e.consume();
    }

    private void onMouseClicked(MouseEvent mouseEvent) {

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

    private boolean canDrop(SquareInfo movingPiece) {

        ArrayList<SquareInfo> validList = ValidMoveCalculator.getValidSquares(gameInfo.getCurrentBoardState(), movingPiece);

        Predicate<SquareInfo> rowEqual = e -> e.getRow() == getSquareInfo().getRow();
        Predicate<SquareInfo> columnEqual = e -> e.getColumn() == getSquareInfo().getColumn();
        Predicate<SquareInfo> combined = rowEqual.and(columnEqual);

        return validList.stream().anyMatch(combined);
    }
}
