/**
 *     This file is part of Flaming Arrows.
 *
 *     Flaming Arrows is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Flaming Arrows is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Flaming Arrows.  If not, see <https://www.gnu.org/licenses/>.
 *
 *     Copyright 2021 Paperweights
 */

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
