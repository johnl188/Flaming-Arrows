package org.example;

public class Fire extends SquareInfo {

    public Fire(int row, int column) {
        this.row = row;
        this.column = column;
        this.squareType = SquareType.Fire;
        this.imageFileName = "fire.png";
    }
}
