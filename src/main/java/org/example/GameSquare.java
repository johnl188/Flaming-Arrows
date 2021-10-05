package org.example;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;

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
//        setOnMouseEntered(this::onMouseEntered);
//        setOnMouseEntered(this::onMouseEntered);


        setStyle(normalStyle());
    }

    public void addAmazon(boolean isWhite) {

        if (imageView != null) {
            getChildren().clear();
            imageView = null;
        }

        squareInfo = new Amazon(squareInfo.getRow(), squareInfo.getColumn(), isWhite);

        if (imageView == null) {
            Image image = new Image(squareInfo.getImageFileName());

            imageView = new ImageViewPane();
            imageView.setImageView(new ImageView(image));

            getChildren().add(imageView);
        }
    }

    public void removePiece() {
        if (imageView != null) {
            getChildren().clear();
            imageView = null;
        }

        squareInfo = new Empty(squareInfo.row, squareInfo.column);
    }

    private void onMouseEntered(MouseEvent e) {
        setStyle(notAvailableStyle());
    }

    private void onMouseExited(MouseEvent e) {
        setStyle(normalStyle());
    }

    private void onDragDetected(MouseEvent e) {

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

        e.consume();
    }

    private void onDragOver(DragEvent e) {
        Dragboard db = e.getDragboard();

        if (db.hasContent(SquareInfo.SQUARE_INFO))
        {
            e.acceptTransferModes(TransferMode.MOVE);

            if (canDrop()) {
                setStyle(availableStyle());
            }

            else {
                setStyle(notAvailableStyle());
            }
        }

        e.consume();
    }

    private void onDragDone(DragEvent e) {
        Dragboard db = e.getDragboard();

        e.consume();
    }

    private void onDragDropped(DragEvent e) {
        Dragboard db = e.getDragboard();

        if (canDrop()) {
            if (db.hasContent(SquareInfo.SQUARE_INFO)) {
                SquareInfo source = (SquareInfo)db.getContent(SquareInfo.SQUARE_INFO);

                gameInfo.movePiece(source, squareInfo);

                addAmazon(source.isWhite());
            }
        }

        e.consume();
    }

    private void onDragExited(DragEvent e) {
        Dragboard db = e.getDragboard();

        setStyle(normalStyle());

        e.consume();
    }

    private boolean canDrop() {
        return squareInfo.isEmpty();
    }
}
