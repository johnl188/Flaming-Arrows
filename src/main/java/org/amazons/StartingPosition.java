package org.amazons;

public class StartingPosition {

    private int row;

    private int column;

    private boolean isWhite;

    public StartingPosition(int row, int column, boolean isWhite) {
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean getIsWhite() {
        return isWhite;
    }
}
