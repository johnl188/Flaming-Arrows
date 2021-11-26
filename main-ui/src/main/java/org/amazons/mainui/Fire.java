package org.amazons.mainui;

/**
 * A SquareInfo that represents a square that has fire on it
 */
public class Fire extends SquareInfo {

    public Fire(byte row, byte column) {
        this.row = row;
        this.column = column;
        this.imageFileName = "images/fire_animation.gif";
    }

    @Override
    public SquareInfo makeCopy() {
        return new Fire(row, column);
    }
}
