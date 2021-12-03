package org.amazons.mainui;

/**
 * Class that represent the entirety of a game move. The from square, to square, and where the arrow goes
 */
public class GameMove implements Comparable {

    private byte amazonFromRow;
    private byte amazonFromColumn;
    private byte amazonToRow;
    private byte amazonToColumn;
    private byte arrowRow;
    private byte arrowColumn;

    public GameMove(byte amazonFromRow, byte amazonFromColumn, byte amazonToRow, byte amazonToColumn)
    {
        this.amazonFromRow = amazonFromRow;
        this.amazonFromColumn = amazonFromColumn;
        this.amazonToRow = amazonToRow;
        this.amazonToColumn = amazonToColumn;
        this.arrowRow = -1;
        this.arrowColumn = -1;
    }

    public GameMove(byte amazonFromRow, byte amazonFromColumn, byte amazonToRow, byte amazonToColumn,
                    byte arrowRow, byte arrowColumn)
    {
        this.amazonFromRow = amazonFromRow;
        this.amazonFromColumn = amazonFromColumn;
        this.amazonToRow = amazonToRow;
        this.amazonToColumn = amazonToColumn;
        this.arrowRow = arrowRow;
        this.arrowColumn = arrowColumn;
    }

    public byte getAmazonFromRow() { return amazonFromRow; }
    public byte getAmazonFromColumn() { return amazonFromColumn; }
    public byte getAmazonToRow() { return amazonToRow; }
    public byte getAmazonToColumn() { return amazonToColumn; }
    public byte getArrowRow() { return arrowRow; }
    public byte getArrowColumn() { return arrowColumn; }

    public void setArrowMove(byte row, byte column) {
        this.arrowRow = row;
        this.arrowColumn = column;
    }

    @Override
    public String toString() {
        return "GameMove{" +
                "amazonFromRow=" + amazonFromRow +
                ", amazonFromColumn=" + amazonFromColumn +
                ", amazonToRow=" + amazonToRow +
                ", amazonToColumn=" + amazonToColumn +
                ", arrowRow=" + arrowRow +
                ", arrowColumn=" + arrowColumn +
                '}';
    }


    @Override
    public int compareTo(Object o) {
        GameMove inputMove = (GameMove)o;

        if (inputMove.getAmazonFromRow() < getAmazonFromRow()) {
            return -1;
        }

        if (inputMove.getAmazonFromRow() > getAmazonFromRow()) {
            return 1;
        }

        if (inputMove.getAmazonFromColumn() < getAmazonFromColumn()) {
            return -1;
        }

        if (inputMove.getAmazonFromColumn() > getAmazonFromColumn()) {
            return 1;
        }

        if (inputMove.getAmazonToRow() < getAmazonToRow()) {
            return -1;
        }

        if (inputMove.getAmazonToRow() > getAmazonToRow()) {
            return 1;
        }

        if (inputMove.getAmazonToColumn() < getAmazonToColumn()) {
            return -1;
        }

        if (inputMove.getAmazonToColumn() > getAmazonToColumn()) {
            return 1;
        }

        if (inputMove.getArrowRow() < getArrowRow()) {
            return -1;
        }

        if (inputMove.getArrowRow() > getArrowRow()) {
            return 1;
        }

        if (inputMove.getArrowColumn() < getArrowColumn()) {
            return -1;
        }

        if (inputMove.getArrowColumn() > getArrowColumn()) {
            return 1;
        }

        return 0;
    }
}
