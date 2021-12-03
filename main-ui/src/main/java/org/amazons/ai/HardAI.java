package org.amazons.ai;

import org.amazons.mainui.*;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * An AIPlayer that assigns a value to a board based on how many squares are owned by players. Owning squared is
 * evaluated based on a mobility algorithm
 */
public class HardAI extends AIPlayer {

    public HardAI(boolean isWhite, int gameSize) {
        this.isWhite = isWhite;
        tree = new AIGameTree(gameSize);
        this.gameSize = gameSize;
    }

    @Override
    protected int estimatedValue(BitSet board) {
        return calculateRelativeTerritory(board);
    }

    // https://project.dke.maastrichtuniversity.nl/games/files/msc/Hensgens_thesis.pdf
    /**
     * Calculate a score for a board based on mobility of pieces. This could be improved by also evaluating territory
     * as described in the thesis, but this algorithm was determined to be sufficiently diffucly to play against
     * @param board - board to evaluate
     * @return - A score for the board
     */
    private int calculateRelativeTerritory(BitSet board) {

        TerritoryMarker[] markers = createTerritoryMarkerArray();

        ArrayList<SquareInfo> whitePieces = new ArrayList<>();
        ArrayList<SquareInfo> blackPieces = new ArrayList<>();

        // Get array of white pieces and
        getListsOfPiecesFromBoard(board, whitePieces, blackPieces);

        // Calculate territory for both white and black
        calculateTerritoryForPieces(board, whitePieces, markers, true);
        calculateTerritoryForPieces(board, blackPieces, markers, false);

        int returnValue = 0;

        // For each marker, add one to value if white has fewer moves to square than black, subtract one if black has
        // fewer moves to square than white
        for (TerritoryMarker marker: markers) {
            if (marker.whiteMoves < marker.blackMoves) {
                returnValue++;
            }

            else if (marker.whiteMoves > marker.blackMoves){
                returnValue--;
            }
        }

        return returnValue;
    }

    /**
     * Create initial array of TerritoryMarkers
     * @return - new array of TerritoryMarkers
     */
    private TerritoryMarker[] createTerritoryMarkerArray() {

        // Create an array of Markers for the board
        int size = gameSize * gameSize;
        TerritoryMarker[] markers = new TerritoryMarker[size];

        for (int i = 0; i < size; i++) {
            markers[i] = new TerritoryMarker();
        }

        return markers;
    }

    /**
     * Get Lists of white pieces and black pieces
     * @param board - bard to get lists from
     * @param whitePieces - list that will contain the white pieces
     * @param blackPieces - list that will contain the black pieces
     */
    private void getListsOfPiecesFromBoard(BitSet board, ArrayList<SquareInfo> whitePieces, ArrayList<SquareInfo> blackPieces) {
        whitePieces.clear();
        blackPieces.clear();

        for (byte i = 0; i < gameSize; i++) {
            for (byte j = 0; j < gameSize; j++) {
                SquareInfo info = PositionConverter.convertBitSetBitsToInfo(board, gameSize, i, j);

                if (info instanceof Amazon) {
                    if (info.getIsWhite()) {
                        whitePieces.add(info);
                    }

                    else {
                        blackPieces.add(info);
                    }
                }
            }
        }
    }

    /**
     * Calculates territory value for each piece of the input type
     * @param board - current board state
     * @param startingList - list of pieces
     * @param markers - TerritoryMarkers to save value
     * @param isWhite - Color to evaluate for
     */
    private void calculateTerritoryForPieces(BitSet board, ArrayList<SquareInfo> startingList, TerritoryMarker[] markers, boolean isWhite) {

        ArrayList<SquareInfo> currentList = startingList;
        byte currentMoves = 0;

        // For each space in the list, calculate valid moves. For each of those moves, if the corresponding marker
        // does not have a value yet, set that value and add that square to a new list. Redo with that new list
        // Stop when the new list doesn't have any values. Moves needed to get to square goes up by one for each time
        // through main loop
        while (currentList.size() > 0) {
            currentMoves++;

            ArrayList<SquareInfo> insideList = new ArrayList<>();

            for (SquareInfo info : currentList) {
                ArrayList<SquareInfo> list = ValidMoveCalculator.getValidSquares(board, info, gameSize);

                for (SquareInfo innerInfo: list) {
                    TerritoryMarker inMarker = markers[innerInfo.getRow() * gameSize + innerInfo.getColumn()];

                    // Evaluate moves based on current turn
                    if (isWhite) {
                        if (inMarker.whiteMoves == Byte.MAX_VALUE) {
                            inMarker.whiteMoves = currentMoves;
                            insideList.add(innerInfo);
                        }
                    }

                    else {
                        if (inMarker.blackMoves == Byte.MAX_VALUE) {
                            inMarker.blackMoves = currentMoves;
                            insideList.add(innerInfo);
                        }
                    }
                }
            }

            currentList = insideList;
        }
    }

    /**
     * Class to hold number of moves the color's piece needs to get to space
     */
    class TerritoryMarker {
        byte whiteMoves = Byte.MAX_VALUE;
        byte blackMoves = Byte.MAX_VALUE;
    }
}
