package org.amazons;

import java.util.BitSet;

public abstract class AIPlayer {

    protected boolean isWhite;

    public boolean getIsWhite() { return isWhite; }

    public abstract GameMove getMove(BitSet boardPositions, int gameSize);

    public abstract void resetGame(int gameSize);

    public abstract void informAIOfGameMove(GameMove move, boolean isWhiteTurn);
}
