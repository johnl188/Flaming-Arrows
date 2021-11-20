package org.amazons.mainui;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

public class RandomAI extends AIPlayer {

    public RandomAI(boolean isWhite) {
        this.isWhite = isWhite;
    }

    @Override
    public GameMove getMove(BitSet boardPositions, int turnNumber) {

        ArrayList<SquareInfo> possiblePieces = new ArrayList<>();

        int gameSize = (int)Math.sqrt(boardPositions.length() / 2);

        for(byte i = 0; i < gameSize; i++) {
            for (byte j = 0; j < gameSize; j++) {
                SquareInfo info = PositionConverter.convertBitSetBitsToInfo(boardPositions, gameSize, i, j);
                if (info instanceof Amazon && info.getIsWhite() == isWhite) {

                    if (ValidMoveCalculator.getValidSquares(boardPositions, info, gameSize).size() > 0) {
                        possiblePieces.add(info);
                    }
                }
            }
        }

        if (possiblePieces.size() == 0) {
            return null;
        }

        Collections.shuffle(possiblePieces);

        SquareInfo movingPiece = possiblePieces.get(0);

        ArrayList<SquareInfo> validMoves = ValidMoveCalculator.getValidSquares(boardPositions, movingPiece, gameSize);

        Collections.shuffle(validMoves);
        SquareInfo moveTo = validMoves.get(0);


        int fromIndex = ((movingPiece.getRow() * gameSize + movingPiece.getRow()) * 2);
        int toIndex = ((moveTo.getRow() * gameSize + moveTo.getRow()) * 2);
//        int fire = (move.getArrowRow() * gameSize + move.getArrowColumn() * 2);

        boardPositions.set(fromIndex, false);
        boardPositions.set(fromIndex + 1, false);

        boardPositions.set(toIndex, true);
        boardPositions.set(toIndex + 1, movingPiece.getIsWhite());

        // Calculate move base on new state
        ArrayList<SquareInfo> arrowMoves = ValidMoveCalculator.getValidSquares(boardPositions, moveTo, gameSize);

        Collections.shuffle(arrowMoves);
        SquareInfo arrowMove = arrowMoves.get(0);

        GameMove move = new GameMove(movingPiece.getRow(), movingPiece.getColumn(), moveTo.getRow(), moveTo.getColumn(),
                arrowMove.getRow(), arrowMove.getColumn());

        int fire = ((arrowMove.getRow() * gameSize + arrowMove.getColumn()) * 2);

        boardPositions.set(fire, false);
        boardPositions.set(fire + 1, true);

        return move;
    }
}
