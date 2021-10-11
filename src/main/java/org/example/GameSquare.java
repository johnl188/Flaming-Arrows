package org.example;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;

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
        return isWhite ? "-fx-background-color: white;" : "-fx-background-color: lightblue;";
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

                gameInfo.movePiece(movingPiece, squareInfo);
                addAmazon(movingPiece.isWhite());

                gameInfo.goToArrow();
                gameInfo.setLastMove(this);

                validList = gameInfo.getValidSquares(this);
                for(GameSquare square : validList) {
                    square.setStyle(square.availableStyle());
                }
            }
        }

        e.consume();
    }

    private void onMouseClicked(MouseEvent mouseEvent) {

        if (!gameInfo.getIsMove()) {

            SquareInfo info = gameInfo.getLastMove().getSquareInfo();
            if (canDrop(info)) {

                ArrayList<GameSquare> validList = gameInfo.getValidSquares(gameInfo.getSquare(info.getRow(), info.getColumn()));
                for(GameSquare square : validList) {
                    square.setStyle(square.normalStyle());
                }

                gameInfo.addFire(this);
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
