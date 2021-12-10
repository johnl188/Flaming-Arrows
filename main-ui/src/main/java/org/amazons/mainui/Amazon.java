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
 * A SquareInfo that represents a square that has an Amazon Piece
 */
public class Amazon extends SquareInfo
{
    public Amazon(byte row, byte column, boolean isWhite) {
        this.row = row;
        this.column = column;
        this.isWhite = isWhite;

        if (isWhite) {
            this.imageFileName = "images/white_piece.png";
        }

        else {
            this.imageFileName = "images/black_piece.png";
        }
    }

    @Override
    public SquareInfo makeCopy() {
        return new Amazon(row, column, isWhite);
    }
}
