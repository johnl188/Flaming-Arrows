package org.amazons;

import javafx.scene.input.DataFormat;

import java.io.Serializable;

public abstract class SquareInfo implements Serializable {

    public static final DataFormat SQUARE_INFO = new DataFormat("squareInfo");

    boolean isWhite = false;
    int row = 0;
    int column = 0;
    String imageFileName = "";

    public boolean getIsWhite() {
        return this instanceof Empty ? true : false;
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

    public abstract SquareInfo makeCopy();
}


