package org.example;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class RandomAI extends AIPlayer {

    public RandomAI(boolean isWhite) {
        this.isWhite = isWhite;
    }

    @Override
    public GameMove getMove(GameInfo gameInfo) {

        int gameSize = gameInfo.getGameSize();
        ArrayList<GameSquare> possiblePieces = new ArrayList<>();

        for(int i = 0; i < gameSize; i++) {
            for (int j = 0; j < gameSize; j++) {
                GameSquare square = gameInfo.getSquare(i, j);
                SquareInfo info = square.getSquareInfo();
                if (info.getSquareType() == SquareType.Amazon && info.isWhite() == isWhite) {
                    if (gameInfo.getValidSquares(square).size() > 0) {
                        possiblePieces.add(square);
                    }
                }
            }
        }

        if (possiblePieces.size() == 0) {
            return null;
        }

        Collections.shuffle(possiblePieces);

        GameSquare movingPiece = possiblePieces.get(0);

        ArrayList<GameSquare> validMoves = gameInfo.getValidSquares(movingPiece);
        Collections.shuffle(validMoves);
        GameSquare moveTo = validMoves.get(0);

        ArrayList<GameSquare> arrowMoves = gameInfo.getValidSquaresForArrowAfterMove(moveTo, movingPiece);

        Collections.shuffle(arrowMoves);
        GameSquare arrowMove = arrowMoves.get(0);

        SquareInfo moveInfo = movingPiece.getSquareInfo();
        SquareInfo pieceInfo = moveTo.getSquareInfo();
        SquareInfo arrowInfo = arrowMove.getSquareInfo();

        GameMove move = new GameMove(moveInfo.getRow(), moveInfo.getColumn(), pieceInfo.getRow(), pieceInfo.getColumn(),
                                    arrowInfo.getRow(), arrowInfo.getColumn());
        return move;
    }
}
