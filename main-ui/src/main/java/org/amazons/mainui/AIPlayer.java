package org.amazons.mainui;

import java.util.BitSet;

public abstract class AIPlayer {

    protected boolean isWhite;

    public boolean getIsWhite() { return isWhite; }

    public abstract GameMove getMove(BitSet boardPositions, int turnNumber);
}