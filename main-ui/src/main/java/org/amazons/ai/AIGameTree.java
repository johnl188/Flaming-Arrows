package org.amazons.ai;

import org.amazons.mainui.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;


/**
 * AIGameTree
 * This class creates the Game Tree needed to calculate a move for the game's AI
 * The tree has a root Node which links to other Nodes. Moving through the tree is done by applying the Node's move
 * to the current board state.
 * calculateBestMove is used to get a moved based on the input board by using the input AIPlayer's estimateMove method
 * and a MinMax algorithm
 */
public class AIGameTree {

    Node root;

    int startDepth;
    int gameSize;

    AIPlayer currentPlayer;

    long cutoffTime;

    public AIGameTree(int gameSize) {
        this.gameSize = gameSize;
        root = null;
        startDepth = 2;
    }

    /**
     * Calculated the best move gives the input board and AI estimateValue
     * @param boardPositions - boardPosition to evaluate
     * @param isWhitesMove - true if it is white's turn
     * @param player - AIPlayer that is provider the estimateValue method
     * @return - A GameMove that was calculated to be best
     */
    public GameMove calculateBestMove(BitSet boardPositions, boolean isWhitesMove, AIPlayer player) {

        currentPlayer = player;

        // reset root if needed
        root = null;

        // create the Nodes of the tree to evaluate
        addNodesForPosition(boardPositions, isWhitesMove);

        if (root == null) {
            return null;
        }

        // Create a cutoffTime of 30 seconds for evaluating the tree
        long startTime = System.currentTimeMillis();
        cutoffTime = startTime + 30000;

        // Perform the minmax algorithm with alphaBeta pruning
        alphaBeta(root, root.position, startDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, isWhitesMove);

        GameMove move;

        // Get the best move for the current player
        if (isWhitesMove) {
            move = getBestMoveAfterSearchForWhite();
        }

        else {
            move = getBestMoveAfterSearchForBlack();
        }

        root = null;

        return move;
    }

    /**
     * Create the game tree starting with the root position
     * @param startingBoard - Board state to start
     * @param isWhitesTurn - True if it is white's turn
     */
    private void addNodesForPosition(BitSet startingBoard, boolean isWhitesTurn) {

        // Create root
        if (root == null) {

            BitSet copiedSet = (BitSet)startingBoard.clone();

            root = new Node(copiedSet, null, isWhitesTurn);
        }

        // Get cutoffTime, only allow 30 seconds to create Nodes for the tree
        long startTime = System.currentTimeMillis();
        cutoffTime = startTime + 30000;

        // Call recursive function to create tree
        addPossibleMovesTree(root, isWhitesTurn, startDepth, startingBoard);
    }

    /**
     * Recursive function to create Nodes for Game Tree
     * @param node - Node to add children to
     * @param isWhitesTurn - True if white's turn
     * @param depth - Depth remaining to create tree, depth = 0 is base case
     * @param currentBoard - Current board state to evaluate
     */
    private void addPossibleMovesTree(Node node, boolean isWhitesTurn, int depth, BitSet currentBoard) {

        // Base case to stop recursion
        if (depth == 0 || System.currentTimeMillis() > cutoffTime) {
            return;
        }

        // If the node already has children, don't add more children, just continue down the tree
        // Not applicable right now, but can be used for iterative deepening later
        if (node.children.size() > 0) {
            if (node.position != null) {
                BitSet copiedPosition = (BitSet)node.position.clone();

                for(Node innerNode: node.children) {
                    addPossibleMovesTree(innerNode, !isWhitesTurn, depth - 1, copiedPosition);
                }
            }

            else if (node.move != null) {
                makeGameMoveInBitSet(currentBoard, node.move);

                for(Node innerNode: node.children) {
                    addPossibleMovesTree(innerNode, !isWhitesTurn, depth - 1, currentBoard);
                }

                reverseGameMoveInBitSet(currentBoard, node.move);
            }

            return;
        }

        // If the currentBoard is at the end of a game, no children to create
        if (isGameOver(currentBoard, isWhitesTurn)) {
            return;
        }

        ArrayList<GameMove> allMoves = new ArrayList<>();

        // Find all pieces for the current player's turn, get a list of valid moves for each of those pieces, then
        // all to the whole list of moves
        for(byte i = 0; i < gameSize; i++) {
            for(byte j = 0; j < gameSize; j++) {

                SquareInfo info = PositionConverter.convertBitSetBitsToInfo(currentBoard, gameSize, i, j);

                if (info instanceof Amazon && info.getIsWhite() == isWhitesTurn) {

                    BitSet copiedBitSet = (BitSet)currentBoard.clone();

                    allMoves.addAll(ValidMoveCalculator.getValidMoves(copiedBitSet, info, gameSize));
                }
            }
        }

        // Add all moves as Nodes to the current node
        for (GameMove move : allMoves) {
            node.children.add(new Node(null, move, isWhitesTurn));
        }

        // For each of those new nodes, make the game move, then add children to that node
        for (Node innerNode : node.children) {

            makeGameMoveInBitSet(currentBoard, innerNode.move);

            addPossibleMovesTree(innerNode, !isWhitesTurn, depth - 1, currentBoard);

            reverseGameMoveInBitSet(currentBoard, innerNode.move);
        }
    }

    /**
     * Returns true if the game is in an end state
     * @param boardPositions - current state of the board
     * @param isWhitesTurn - true if it is white's turn
     * @return - return true if the game is in an end state
     */
    private boolean isGameOver(BitSet boardPositions, boolean isWhitesTurn) {

        BoardState state = PositionConverter.convertBitSetToBoardState(boardPositions, gameSize);

        // For each piece of the current player's turn, if the piece can make a move, return false
        // because the existence of a valid move means the game is not over
        for(SquareInfo info: state.getSquares()) {
            if (info instanceof Amazon && info.getIsWhite() == isWhitesTurn) {

                ArrayList<SquareInfo> list = ValidMoveCalculator.getValidSquares(state, info);

                if (list.size() > 0) {
                    return false;
                }
            }
        }

        // If there are no valid moves for the current player, the game is in an end state
        return true;
    }

    /**
     * Modify the input board by making the input move
     * @param board - Board to modify
     * @param move - Move to make in the board
     */
    private void makeGameMoveInBitSet(BitSet board, GameMove move) {

        // Get index of moves in the bit set
        int fromIndex = ((move.getAmazonFromRow() * gameSize + move.getAmazonFromColumn()) * 2);
        int toIndex = ((move.getAmazonToRow() * gameSize + move.getAmazonToColumn()) * 2);
        int fire = ((move.getArrowRow() * gameSize + move.getArrowColumn()) * 2);

        boolean previousColor = board.get(fromIndex + 1);

        // Remove the pieces in the from position
        board.set(fromIndex, false);
        board.set(fromIndex + 1, false);

        // Add piece to the to position
        board.set(toIndex, true);
        board.set(toIndex + 1, previousColor);

        // Add fire to the arrow position
        board.set(fire, false);
        board.set(fire + 1, true);
    }

    /**
     * Modify the input board by reversing the input move
     * @param board - board state to modify
     * @param move - move to reverse
     */
    private void reverseGameMoveInBitSet(BitSet board, GameMove move) {

        // Get index of moves in the bit set
        int fromIndex = ((move.getAmazonFromRow() * gameSize + move.getAmazonFromColumn()) * 2);
        int toIndex = ((move.getAmazonToRow() * gameSize + move.getAmazonToColumn()) * 2);
        int fire = ((move.getArrowRow() * gameSize + move.getArrowColumn()) * 2);

        boolean previousColor = board.get(toIndex + 1);

        // Remove fire from the arrow position
        board.set(fire, false);
        board.set(fire + 1, false);

        // Remove piece from the to position
        board.set(toIndex, false);
        board.set(toIndex + 1, false);

        // Add piece back to the from position
        board.set(fromIndex, true);
        board.set(fromIndex + 1, previousColor);
    }

    /**
     * Get a list of the best moves for White and randomly selects one
     * @return - The best move for white
     */
    private GameMove getBestMoveAfterSearchForWhite() {

        int max = Integer.MIN_VALUE;

        ArrayList<Node> currentBest = new ArrayList<>();

        // For each child of the root, if the child has a value that is the new max, reset the list, and add the node
        // If the child has a value that matched the max, just add the Node to the list
        for (Node child : root.children) {
            if (max < child.value) {
                max = child.value;
                currentBest.clear();
                currentBest.add(child);
            }

            else if (max == child.value) {
                currentBest.add(child);
            }
        }

        // Select a Node from the best list randomly
        return getBestMoveFromList(currentBest);
    }

    /**
     * Get a list of the best moves for Black and randomly selects one
     * @return - The best move for Black
     */
    private GameMove getBestMoveAfterSearchForBlack() {
        int min = Integer.MAX_VALUE;

        ArrayList<Node> currentBest = new ArrayList<>();

        // For each child of the root, if the child has a value that is the new mid, reset the list, and add the node
        // If the child has a value that matched the min, just add the Node to the list
        for (Node child : root.children) {
            if (min > child.value) {
                min = child.value;
                currentBest.clear();
                currentBest.add(child);
            }

            else if (min == child.value) {
                currentBest.add(child);
            }
        }

        // Select a Node from the best list randomly
        return getBestMoveFromList(currentBest);
    }

    /**
     * Shuffle the input list and select a Node
     * @param currentBest - List to select from
     * @return - Randomly selected Node
     */
    private GameMove getBestMoveFromList(ArrayList<Node> currentBest) {

        Node bestNode = null;

        if (currentBest.size() > 0) {
            Collections.shuffle(currentBest);
            bestNode = currentBest.get(0);
        }

        if (bestNode != null) {
            return bestNode.move;
        }

        else {
            return null;
        }
    }

    /**
     * Perform the recursize minmax algorithm with alpha beta pruning
     * @param node - Current evaluation Node
     * @param currentBoard - Current board state
     * @param depth - Current depth remaining in algorithm, depth == 0 is base case
     * @param alpha - Cutoff value for white
     * @param beta - Cutoff value for black
     * @param isWhitesTurn - Current player's turn to evalute if we are trying to min or max value
     * @return - Return an int that is the value of the calling node
     */
    public int alphaBeta(Node node, BitSet currentBoard, int depth, int alpha, int beta, boolean isWhitesTurn) {

        // Base case of depth == 0 or terminating node
        // If reached, evaluate the current position and return the found value
        if (depth == 0 || node.children.size() == 0 || System.currentTimeMillis() > cutoffTime) {
            node.value = currentPlayer.estimatedValue(currentBoard);
            return node.value;
        }

        // For each child node, make the Node's move, recursively call alphaBeta, calculate alpha or beta based on
        // current player's turn
        for(Node child: node.children) {

            // Make move in board for child
            makeGameMoveInBitSet(currentBoard, child.move);

            // Get the alphaBeta score for child
            int score = alphaBeta(child, currentBoard, depth - 1, alpha, beta, !isWhitesTurn);

            // Calculate alpha if whites turn, else calculate beta
            if (isWhitesTurn) {
                alpha = Math.max(alpha, score);
            }

            else {
                beta = Math.min(beta, score);
            }

            // reverse the child's move in the board
            reverseGameMoveInBitSet(currentBoard, child.move);

            // If we don't need to continue because alpha is >= beta, we just stop evaluating children because those
            // moves will not be selected.
            if (alpha >= beta) {
                break;
            }
        }

        node.value = isWhitesTurn ? alpha : beta;

        return node.value;
    }

    /**
     * Node class for the Game Tree, contains a game move and a value associated with the resulting game position
     * It is comparable so a list of nodes can be sorted
     */
    static class Node implements Comparable{
        BitSet position;
        GameMove move;
        boolean isWhite;
        int value;

        ArrayList<Node> children;

        Node(BitSet position, GameMove move, boolean isWhite) {
            this.position = position;
            this.isWhite = isWhite;
            this.move = move;
            children = new ArrayList<>();

            // If white, give the lowest value, else give the highest value
            // Needed so this move is not picked as best if it is never evaluated
            if (isWhite) {
                value = Integer.MIN_VALUE;
            }

            else {
                value = Integer.MAX_VALUE;
            }
        }

        @Override
        public int compareTo(Object o) {
            Node otherNode = (Node) o;
            return this.value - otherNode.value;
        }
    }
}