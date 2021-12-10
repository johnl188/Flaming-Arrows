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

import javafx.scene.input.DataFormat;

import java.io.Serializable;

/**
 * Abstract class to represent the state of one game square
 */
public abstract class SquareInfo implements Serializable {

    public static final DataFormat SQUARE_INFO = new DataFormat("squareInfo");

    boolean isWhite = false;
    byte row = 0;
    byte column = 0;
    String imageFileName = "";

    public boolean isEmpty() {
        return this instanceof Empty ? true : false;
    }

    public byte getRow() {
        return row;
    }

    public byte getColumn() {
        return column;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public abstract SquareInfo makeCopy();
}


