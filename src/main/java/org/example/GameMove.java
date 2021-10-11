package org.example;

public class GameMove {

    private int amazonFromRow;
    private int amazonFromColumn;
    private int amazonToRow;
    private int amazonToColumn;
    private int arrowRow;
    private int arrowColumn;

    public GameMove(int amazonFromRow, int amazonFromColumn, int amazonToRow, int amazonToColumn,
                    int arrowRow, int arrowColumn)
    {
        this.amazonFromRow = amazonFromRow;
        this.amazonFromColumn = amazonFromColumn;
        this.amazonToRow = amazonToRow;
        this.amazonToColumn = amazonToColumn;
        this.arrowRow = arrowRow;
        this.arrowColumn = arrowColumn;
    }

    public int getAmazonFromRow() { return amazonFromRow; }
    public int getAmazonFromColumn() { return amazonFromColumn; }
    public int getAmazonToRow() { return amazonToRow; }
    public int getAmazonToColumn() { return amazonToColumn; }
    public int getArrowRow() { return arrowRow; }
    public int getArrowColumn() { return arrowColumn; }
}
