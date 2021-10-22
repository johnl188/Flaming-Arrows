package org.amazons;

import java.util.ArrayList;
import java.util.List;

public class ValidMoveCalculator {

    private static ArrayList<SquareInfo> list;
    private static int startRow = 0;
    private static int startColumn = 0;
    private static BoardState boardState;


    public static ArrayList<SquareInfo> getValidSquares(BoardState inputState, SquareInfo startInfo) {
        list = new ArrayList<>();
        boardState = inputState;
        startRow = startInfo.getRow();
        startColumn = startInfo.getColumn();

        checkAbove();
        checkBelow();
        checkLeft();
        checkRight();
        checkLeftUp();
        checkLeftDown();
        checkRightUp();
        checkRightDown();

        return list;
    }

    private static void checkAbove() {
        int row = startRow;
        int column = startColumn;

        row--;

        while(row >= 0) {
            SquareInfo info = boardState.getSquareInfo(row, column);

            if (info.getIsWhite()) {
                list.add(info);
                row--;
            }

            else {
                return;
            }
        }
    }

    private static void checkBelow() {
        int row = startRow;
        int column = startColumn;

        row++;

        while(row < boardState.getGameSize()) {
            SquareInfo info = boardState.getSquareInfo(row, column);

            if (info.getIsWhite()) {
                list.add(info);
                row++;
            }

            else {
                return;
            }
        }
    }

    private static void checkLeft() {
        int row = startRow;
        int column = startColumn;

        column--;

        while(column >= 0) {
            SquareInfo info = boardState.getSquareInfo(row, column);

            if (info.getIsWhite()) {
                list.add(info);
                column--;
            }

            else {
                return;
            }
        }
    }

    private static void checkRight() {
        int row = startRow;
        int column = startColumn;

        column++;

        while(column < boardState.getGameSize()) {
            SquareInfo info = boardState.getSquareInfo(row, column);

            if (info.getIsWhite()) {
                list.add(info);
                column++;
            }

            else {
                return;
            }
        }
    }

    private static void checkLeftUp() {
        int row = startRow;
        int column = startColumn;

        row--;
        column--;
        while(row >= 0 && column >= 0) {
            SquareInfo info = boardState.getSquareInfo(row, column);

            if (info.getIsWhite()) {
                list.add(info);
                row--;
                column--;
            }

            else {
                return;
            }
        }
    }

    private static void checkLeftDown() {
        int row = startRow;
        int column = startColumn;

        row++;
        column--;

        while(row < boardState.getGameSize() && column >= 0) {
            SquareInfo info = boardState.getSquareInfo(row, column);

            if (info.getIsWhite()) {
                list.add(info);
                row++;
                column--;
            }

            else {
                return;
            }
        }
    }

    private static void checkRightUp() {
        int row = startRow;
        int column = startColumn;

        row--;
        column++;

        while(row >= 0 && column < boardState.getGameSize()) {
            SquareInfo info = boardState.getSquareInfo(row, column);

            if (info.getIsWhite()) {
                list.add(info);
                row--;
                column++;
            }

            else {
                return;
            }
        }
    }

    private static void checkRightDown() {
        int row = startRow;
        int column = startColumn;

        row++;
        column++;

        while(row < boardState.getGameSize() && column < boardState.getGameSize()) {
            SquareInfo info = boardState.getSquareInfo(row, column);

            if (info.getIsWhite()) {
                list.add(info);
                row++;
                column++;
            }

            else {
                return;
            }
        }
    }
}
