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

import org.amazons.ai.AIPlayerType;

/**
 * Class that represent the options for the game, such as the game size and where pieces start.
 */
public class GameOptions {

    private int gameSize;

    private StartingPosition[] positions;

    private AIPlayerType aiPlayerType;

    private boolean isAIFirst;

    public GameOptions(int gameSize, AIPlayerType aiPlayerType, boolean isAIFirst) {
        this.gameSize = gameSize;
        this.aiPlayerType = aiPlayerType;
        this.isAIFirst = isAIFirst;

        if (gameSize == 6) {
            this.positions = get6x6StartingPositions();
        }

        if (gameSize == 8) {
            this.positions = get8x8StartingPositions();
        }

        if (gameSize == 10) {
            this.positions = get10x10StartingPositions();
        }
    }

    public int getGameSize() {
        return gameSize;
    }

    public StartingPosition[] getPositions() {
        return positions;
    }

    public AIPlayerType getAIPlayerType() {
        return aiPlayerType;
    }

    public boolean getIsAIFirst() {
        return isAIFirst;
    }

    /**
     * Get the starting position for various sizes, 10x10, 8x8, 6x6
     * @return
     */
    private static StartingPosition[] get10x10StartingPositions() {
        StartingPosition[] positions = new StartingPosition[8];

        positions[0] = new StartingPosition(3,0, false);
        positions[1] = new StartingPosition(0,3, false);
        positions[2] = new StartingPosition(0,6, false);
        positions[3] = new StartingPosition(3,9, false);
        positions[4] = new StartingPosition(6,0, true);
        positions[5] = new StartingPosition(9,3, true);
        positions[6] = new StartingPosition(9,6, true);
        positions[7] = new StartingPosition(6,9, true);

        return  positions;
    }

    private static StartingPosition[] get8x8StartingPositions() {
        StartingPosition[] positions = new StartingPosition[6];

        positions[0] = new StartingPosition(2,0, false);
        positions[1] = new StartingPosition(0,3, false);
        positions[2] = new StartingPosition(2,7, false);
        positions[3] = new StartingPosition(5,0, true);
        positions[4] = new StartingPosition(7,4, true);
        positions[5] = new StartingPosition(5,7, true);

        return  positions;
    }

    private static StartingPosition[] get6x6StartingPositions() {
        StartingPosition[] positions = new StartingPosition[4];

        positions[0] = new StartingPosition(0,2, false);
        positions[1] = new StartingPosition(2,5, false);
        positions[2] = new StartingPosition(3,0, true);
        positions[3] = new StartingPosition(5,3, true);

        return  positions;
    }
}
