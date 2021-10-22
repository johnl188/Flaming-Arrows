package org.amazons;

public class Empty extends SquareInfo
{
    public Empty(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public SquareInfo makeCopy() {
        return new Empty(row, column);
    }
}
