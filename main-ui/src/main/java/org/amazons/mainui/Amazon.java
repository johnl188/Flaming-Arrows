package org.amazons.mainui;

/**
 * A SquareInfo that represents a square that has an Amazon Piece
 */
public class Amazon extends SquareInfo
{
    public Amazon(byte row, byte column, boolean isWhite) {
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;

        if (isWhite) {
            this.imageFileName = "images/white_piece.png";
        }

        else {
            this.imageFileName = "images/black_piece.png";
        }
    }

    @Override
    public SquareInfo makeCopy() {
        return new Amazon(row, column, isWhite);
    }
}
