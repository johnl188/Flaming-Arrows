package org.amazons.mainui;

import javafx.scene.input.DataFormat;

import java.io.Serializable;

/**
 * Abstract class to represent the state of one game square
 */
public abstract class SquareInfo implements Serializable {

    public static final DataFormat SQUARE_INFO = new DataFormat("squareInfo");

    boolean isWhite = false;
    byte row = 0;
    byte column = 0;
    String imageFileName = "";

    public boolean isEmpty() {
        return this instanceof Empty ? true : false;
    }

    public byte getRow() {
        return row;
    }

    public byte getColumn() {
        return column;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public abstract SquareInfo makeCopy();
}


