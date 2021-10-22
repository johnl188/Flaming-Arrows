package org.amazons;

import java.util.ArrayList;
import java.util.Collections;

public class RandomAI extends AIPlayer {

    public RandomAI(boolean isWhite) {
        this.isWhite = isWhite;
    }

    @Override
    public GameMove getMove(BoardState boardState) {

        ArrayList<SquareInfo> possiblePieces = new ArrayList<>();
        int gameSize = boardState.getGameSize();

        for(int i = 0; i < gameSize; i++) {
            for (int j = 0; j < gameSize; j++) {
                SquareInfo info = boardState.getSquareInfo(i, j);
                if (info instanceof Amazon && info.isWhite() == isWhite) {

                    if (ValidMoveCalculator.getValidSquares(boardState, info).size() > 0) {
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

        ArrayList<SquareInfo> validMoves = ValidMoveCalculator.getValidSquares(boardState, movingPiece);

        Collections.shuffle(validMoves);
        SquareInfo moveTo = validMoves.get(0);

        // Make fromInfo empty
        SquareInfo emptyInfo = new Empty(movingPiece.getRow(), movingPiece.getColumn());
        boardState.setSquareInfo(emptyInfo);

        // Make toInfo an Amazon of the right color
        SquareInfo newAmazon = new Amazon(moveTo.getRow(), moveTo.getColumn(), moveTo.getIsWhite());
        boardState.setSquareInfo(newAmazon);

        // Calculate move base on new state
        ArrayList<SquareInfo> arrowMoves = ValidMoveCalculator.getValidSquares(boardState, moveTo);

        Collections.shuffle(arrowMoves);
        SquareInfo arrowMove = arrowMoves.get(0);

        GameMove move = new GameMove(movingPiece.getRow(), movingPiece.getColumn(), moveTo.getRow(), moveTo.getColumn(),
                arrowMove.getRow(), arrowMove.getColumn());

        return move;
    }
}
