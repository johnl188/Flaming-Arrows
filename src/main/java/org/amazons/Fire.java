package org.amazons;

public class Fire extends SquareInfo {

    public Fire(byte row, byte column) {
        this.row = row;
        this.column = column;
        this.imageFileName = "fire_animation.gif";
    }

    @Override
    public SquareInfo makeCopy() {
        return new Fire(row, column);
    }
}
