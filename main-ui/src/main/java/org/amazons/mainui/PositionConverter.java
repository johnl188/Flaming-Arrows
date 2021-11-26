package org.amazons.mainui;

import java.util.BitSet;

/**
 * Static class to do various board state conversions
 */
public class PositionConverter {

    /**
     * COnvert the input BoardState to a BitSet
     * @param state - input BoardState
     * @return - new BitSet
     */
    public static BitSet convertBoardStateToBitSet(BoardState state) {
        int gameSize = state.getGameSize();

        BitSet returnSet = new BitSet(gameSize * gameSize * 2);

        SquareInfo[] squares = state.getSquares();
        int length = squares.length;

        for (int i = 0; i < length; i++) {

            int current = i * 2;
            SquareInfo info = squares[i];

            if (info instanceof Fire) {
                returnSet.set(current + 1, true);
            }

            else if (info instanceof Amazon) {
                returnSet.set(current, true);

                if (!info.getIsWhite()) {
                    returnSet.set(current + 1, true);
                }
            }
        }

        return returnSet;
    }

    /**
     * Convert the input BitSet to a BoardState
     * @param bitSet - input bitSet
     * @param gameSize - number of row/columns for the board
     * @return - new BoardState
     */
    public static BoardState convertBitSetToBoardState(BitSet bitSet, int gameSize) {
        int length = gameSize * gameSize;

        byte row = 0;
        byte column = 0;
        SquareInfo[] infos = new SquareInfo[gameSize * gameSize];

        for (int i = 0; i < length; i++) {

            infos[row * gameSize + column] = convertBitSetBitsToInfo(bitSet, gameSize, row, column);

            column++;

            if (column >= gameSize) {
                column = 0;
                row++;
            }
        }

        return new BoardState(infos, gameSize);
    }

    /**
     * Creates and returns a SquareInfo given the BitSet, row, and column
     * @param bitSet - BitSet of the board
     * @param gameSize - Number of columns/rows of the board
     * @param row - Row to retrieve for
     * @param column - Column to retrieve for
     * @return - SquareInfo of the specified square
     */
    public static SquareInfo convertBitSetBitsToInfo(BitSet bitSet, int gameSize, byte row, byte column) {
        SquareInfo info;

        int index = (row * gameSize + column) * 2;
        boolean isBitZeroOne = bitSet.get(index);
        boolean isBitOneOne = bitSet.get(index + 1);

        if (isBitZeroOne) {
            info = new Amazon(row, column, !isBitOneOne);
        }

        else if (isBitOneOne) {
            info = new Fire(row, column);
        }

        else {
            info = new Empty(row, column);
        }

        return info;
    }
}
