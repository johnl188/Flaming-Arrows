package org.amazons.mainui;

public class GameOptions {

    private int gameSize;

    private StartingPosition[] positions;

    private AIPlayerType aiPlayerType;

    private boolean isAIFirst;

    public GameOptions(int gameSize, StartingPosition[] positions, AIPlayerType aiPlayerType, boolean isAIFirst) {
        this.gameSize = gameSize;
        this.positions = positions;
        this.aiPlayerType = aiPlayerType;
        this.isAIFirst = isAIFirst;
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
}
