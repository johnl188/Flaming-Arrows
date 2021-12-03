package org.amazons.ai;

import org.amazons.mainui.GameMove;

import java.util.BitSet;

/**
 * Abstract Class For an AI Player
 */
public abstract class AIPlayer {

    protected boolean isWhite;
    protected AIGameTree tree;
    protected int gameSize;

    public boolean getIsWhite() { return isWhite; }

    /**
     * Calculates a Game Move given the input board
     * @param boardPositions - Board to calculate move for
     * @return - A game move to execute in the game
     */
    public GameMove getMove(BitSet boardPositions) {

        // Get the best move and return it
        GameMove move = tree.calculateBestMove(boardPositions, isWhite, this);

        return move;
    }

    // Method to implement to determine the static value of an input board
    protected abstract int estimatedValue(BitSet board);
}
