package org.example;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class GameSquare extends StackPane {

    private SquareInfo squareInfo;
    private GameInfo gameInfo;
    private int row;
    private int column;
    private boolean isWhite;

    private ImageViewPane imageView = null;

    private String normalStyle()
    {
        return isWhite ? "-fx-background-color: radial-gradient(radius 50% , #f5f5dc, #8b4513);" : "-fx-background-color: radial-gradient(radius 50% , #ffebcd, #008080);";
    }

    private String availableStyle()
    {
        return isWhite ? "-fx-background-color: palegreen;" : "-fx-background-color: forestgreen;";
    }

    private String notAvailableStyle()
    {
        return isWhite ? "-fx-background-color: red;" : "-fx-background-color: darkRed;";
    }

    public GameSquare(GameInfo gameInfo, int row, int column, boolean isWhite) {

        this.gameInfo = gameInfo;
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;

        squareInfo = new Empty(row, column);

        setOnDragOver(this::onDragOver);
        setOnDragExited(this::onDragExited);
        setOnDragDetected(this::onDragDetected);
        setOnDragDone(this::onDragDone);
        setOnDragDropped(this::onDragDropped);
        setOnMouseClicked(this::onMouseClicked);

        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0))));

//        setOnMouseEntered(this::onMouseEntered);
//        setOnMouseEntered(this::onMouseEntered);


        setStyle(normalStyle());
    }

    public SquareInfo getSquareInfo() {
        return squareInfo;
    }

    public void setSquareInfo(SquareInfo info) {
        squareInfo = info;
    }

    public ImageViewPane getImageView() { return imageView; }

    public void setImageView(ImageViewPane imageView) {
        getChildren().clear();
        this.imageView = imageView;
        getChildren().add(imageView);
    }

    public void addAmazon(boolean isWhite) {

        if (imageView != null) {
            getChildren().clear();
            imageView = null;
        }

        squareInfo = new Amazon(squareInfo.getRow(), squareInfo.getColumn(), isWhite);

        Image image = new Image(squareInfo.getImageFileName());

        imageView = new ImageViewPane();
        imageView.setImageView(new ImageView(image));

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

    private void onMouseEntered(MouseEvent e) {
        setStyle(notAvailableStyle());
    }

    private void onMouseExited(MouseEvent e) {
        setStyle(normalStyle());
    }

    private void onDragDetected(MouseEvent e) {

        if (gameInfo.getIsMove() && gameInfo.getIsWhitesTurn() == squareInfo.isWhite() &&
                squareInfo.getSquareType() == SquareType.Amazon) {
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

            ArrayList<GameSquare> validList = gameInfo.getValidSquares(this);

            for(GameSquare square : validList) {
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

        if (gameInfo.getIsMove()) {
            ArrayList<GameSquare> validList = gameInfo.getValidSquares(this);
            for(GameSquare square : validList) {
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

                ArrayList<GameSquare> validList = gameInfo.getValidSquares(gameInfo.getSquare(movingPiece.getRow(), movingPiece.getColumn()));
                for(GameSquare square : validList) {
                    square.setStyle(square.normalStyle());
                }

                gameInfo.movePiece(movingPiece, squareInfo, false, null);

                validList = gameInfo.getValidSquares(this);
                for(GameSquare square : validList) {
                    square.setStyle(square.availableStyle());
                }
            }
        }

        e.consume();
    }

    private void onMouseClicked(MouseEvent mouseEvent) {

        if (!gameInfo.getIsMove() && gameInfo.getIsOkToMove()) {
            gameInfo.setIsOkToMove(false);

            GameMove lastMove = gameInfo.getLastMove();
            GameSquare squareForLastMove = gameInfo.getSquare(lastMove.getAmazonToRow(), lastMove.getAmazonToColumn());
            SquareInfo info = squareForLastMove.getSquareInfo();

            if (canDrop(info)) {

                ArrayList<GameSquare> validList = gameInfo.getValidSquares(gameInfo.getSquare(info.getRow(), info.getColumn()));
                for(GameSquare square : validList) {
                    square.setStyle(square.normalStyle());
                }

                gameInfo.shootArrow(squareForLastMove, this);
            }

            else {
                gameInfo.setIsOkToMove(true);
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

        ArrayList<GameSquare> validList = gameInfo.getValidSquares(gameInfo.getSquare(movingPiece.getRow(), movingPiece.getColumn()));

        return validList.contains(this);
    }
}
