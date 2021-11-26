package org.amazons.mainui;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Static Class to calculate valid moves for the game
 */
public class ValidMoveCalculator {

    private static ArrayList<SquareInfo> list;
    private static BoardState boardState;
    private static BitSet bitSet;
    private static int gameSize;

    private static final byte[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    /**
     * Returns a list of valid square to move to given the input state and starting square
     * @param inputState - input BoardState
     * @param startInfo - Square to calculate moves for
     * @return - List of Valid squares to move to
     */
    public static ArrayList<SquareInfo> getValidSquares(BoardState inputState, SquareInfo startInfo) {
        list = new ArrayList<>();
        boardState = inputState;
        byte startRow = startInfo.getRow();
        byte startColumn = startInfo.getColumn();

        for (byte[] direction: directions) {
            checkDirection(direction, startRow, startColumn);
        }

        return list;
    }

    /**
     * Returns a list of valid square to move to given the input state and starting square
     * @param currentBitSet - Input BitSet representing the board
     * @param startInfo - Square to get moves for
     * @param size - Game size needed for BitSet calculations
     * @return - List of Valid squares to move to
     */
    public static ArrayList<SquareInfo> getValidSquares(BitSet currentBitSet, SquareInfo startInfo, int size) {
        list = new ArrayList<>();
        bitSet = currentBitSet;
        byte startRow = startInfo.getRow();
        byte startColumn = startInfo.getColumn();
        gameSize = size;

        for (byte[] direction: directions) {
            checkDirectionsForBitSet(direction, startRow, startColumn);
        }

        return list;
    }

    /**
     * Return list of Valid GameMoves for the starting square. This includes arrow moves as well
     * @param currentBitSet - Input BitSet representing the board
     * @param startInfo - Square to get moves for
     * @param size - Game size needed for BitSet calculations
     * @return - List of Valid Game Moves
     */
    public static ArrayList<GameMove> getValidMoves(BitSet currentBitSet, SquareInfo startInfo, int size) {
        ArrayList<GameMove> moveList = new ArrayList<>();
        ArrayList<SquareInfo> amazonMoveList;
        bitSet = currentBitSet;
        byte startRow = startInfo.getRow();
        byte startColumn = startInfo.getColumn();
        gameSize = size;

        amazonMoveList = getValidSquares(currentBitSet, startInfo, size);

        for (SquareInfo info: amazonMoveList) {

            int fromIndex = ((startRow * gameSize + startColumn) * 2);
            int toIndex = ((info.getRow() * gameSize + info.getColumn()) * 2);

            currentBitSet.set(fromIndex, false);
            currentBitSet.set(fromIndex + 1, false);

            currentBitSet.set(toIndex, true);
            currentBitSet.set(toIndex + 1, startInfo.getIsWhite());

            ArrayList<SquareInfo>  arrowList = ValidMoveCalculator.getValidSquares(bitSet, info, gameSize);

            for (SquareInfo fireInfo: arrowList) {
                moveList.add(new GameMove(startRow, startColumn, info.getRow(), info.getColumn(), fireInfo.getRow(), fireInfo.getColumn()));
            }

            currentBitSet.set(toIndex, false);
            currentBitSet.set(toIndex + 1, false);

            currentBitSet.set(fromIndex, true);
            currentBitSet.set(fromIndex + 1, startInfo.getIsWhite());
        }

        return moveList;
    }

    /**
     * Check until a piece, fire, or wall is found and add valid square to the list
     * @param direction - Direction to check
     * @param startRow - Starting Row of the square
     * @param startColumn - Starting Column of the square
     */
    private static void checkDirection(byte[] direction, int startRow, int startColumn) {
        int row = startRow;
        int column = startColumn;

        row += direction[0];
        column += direction[1];

        while(row < boardState.getGameSize() && column < boardState.getGameSize() &&
                row >= 0 && column >= 0) {
            SquareInfo info = boardState.getSquareInfo(row, column);

            if (info.isEmpty()) {
                list.add(info);
                row += direction[0];
                column += direction[1];
            }

            else {
                return;
            }
        }
    }

    /**
     * Check until a piece, fire, or wall is found and add valid square to the list
     * @param direction - Direction to check
     * @param startRow - Starting Row of the square
     * @param startColumn - Starting Column of the square
     */
    private static void checkDirectionsForBitSet(byte[] direction, byte startRow, byte startColumn) {
        byte row = startRow;
        byte column = startColumn;

        row += direction[0];
        column += direction[1];

        while(row < gameSize && column < gameSize &&
                row >= 0 && column >= 0) {
            SquareInfo info = PositionConverter.convertBitSetBitsToInfo(bitSet, gameSize, row, column);

            if (info.isEmpty()) {
                list.add(info);
                row += direction[0];
                column += direction[1];
            }

            else {
                return;
            }
        }
    }
}
