package org.amazons.ai;

import java.util.BitSet;
import java.util.Random;

/**
 * An AIPlayer that assigns a static value to a board randomly
 * While the value assigned to a board is random, moves selected with this AI is not truly random.
 * Moves that lead to more open positions, i.e. more children nodes in the game tree, are more likely to be selected
 * because more board states to be evaluated means more chances for the random value assigned to the board to be
 * the best of all boards
 */
public class EasyAI extends AIPlayer {

    Random random = new Random();

    public EasyAI(boolean isWhite, int gameSize) {
        this.isWhite = isWhite;
        tree = new AIGameTree(gameSize);
        this.gameSize = gameSize;
    }

    @Override
    protected int estimatedValue(BitSet board) {
        return random.nextInt();
    }
}


