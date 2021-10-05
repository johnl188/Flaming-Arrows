package org.example;

import javafx.scene.input.DataFormat;

import java.io.Serializable;

public abstract class SquareInfo implements Serializable {

    public static final DataFormat SQUARE_INFO = new DataFormat("squareInfo");

    boolean isWhite = false;
    SquareType squareType = SquareType.Empty;
    int row = 0;
    int column = 0;
    String imageFileName = "";

    public boolean isEmpty() {
        return squareType == SquareType.Empty;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public String getImageFileName() {
        return imageFileName;
    }
}


