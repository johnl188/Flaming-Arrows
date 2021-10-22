package org.amazons;

public abstract class AIPlayer {

    protected boolean isWhite;

    public boolean getIsWhite() { return isWhite; }

    public abstract GameMove getMove(BoardState gameInfo);
}
