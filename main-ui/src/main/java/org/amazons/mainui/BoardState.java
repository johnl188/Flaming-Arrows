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

package org.amazons.mainui;

/**
 * Class to hold the SquareInfos of a game
 */
public class BoardState {

    private SquareInfo[] squares;
    private int gameSize;

    public BoardState(SquareInfo[] squares, int gameSize) {
        this.squares = squares;
        this.gameSize = gameSize;
    }

    public SquareInfo getSquareInfo(int row, int column) {

        return squares[row * gameSize + column];
    }

    public SquareInfo[] getSquares() { return squares; }

    public int getGameSize() { return gameSize; }
}
