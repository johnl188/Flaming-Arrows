package org.amazons;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Random;

public class EasyAI extends AIPlayer{

    private MoveTree tree;

    public EasyAI(boolean isWhite, int gameSize) {
        this.isWhite = isWhite;
        tree = new MoveTree(gameSize);
    }

    @Override
    public GameMove getMove(BitSet boardPositions, int gameSize) {

        ArrayList<SquareInfo> possiblePieces = new ArrayList<>();

        BoardState state = PositionConverter.convertBitSetToBoardState(boardPositions, gameSize);

        tree.addNodesForPosition(boardPositions, isWhite);
        GameMove tempMove =  tree.calculateBestMove(isWhite);

        if (tempMove != null) {
            return tempMove;
        }

        for(byte i = 0; i < gameSize; i++) {
            for (byte j = 0; j < gameSize; j++) {
                SquareInfo info = PositionConverter.convertBitSetBitsToInfo(boardPositions, gameSize, i, j);
                if (info instanceof Amazon && info.getIsWhite() == isWhite) {

                    if (ValidMoveCalculator.getValidSquares(boardPositions, info, gameSize).size() > 0) {
                        possiblePieces.add(info);
                    }
                }
            }
        }

        if (possiblePieces.size() == 0) {
            return null;
        }

        Collections.shuffle(possiblePieces);

        SquareInfo movingPiece = possiblePieces.get(0);

        ArrayList<SquareInfo> validMoves = ValidMoveCalculator.getValidSquares(boardPositions, movingPiece, gameSize);

        Collections.shuffle(validMoves);
        SquareInfo moveTo = validMoves.get(0);


        int fromIndex = ((movingPiece.getRow() * gameSize + movingPiece.getColumn()) * 2);
        int toIndex = ((moveTo.getRow() * gameSize + moveTo.getColumn()) * 2);

        boardPositions.set(fromIndex, false);
        boardPositions.set(fromIndex + 1, false);

        boardPositions.set(toIndex, true);
        boardPositions.set(toIndex + 1, movingPiece.getIsWhite());

        // Calculate move base on new state
        ArrayList<SquareInfo> arrowMoves = ValidMoveCalculator.getValidSquares(boardPositions, moveTo, gameSize);

        Collections.shuffle(arrowMoves);
        SquareInfo arrowMove = arrowMoves.get(0);

        GameMove move = new GameMove(movingPiece.getRow(), movingPiece.getColumn(), moveTo.getRow(), moveTo.getColumn(),
                arrowMove.getRow(), arrowMove.getColumn());

        int fire = ((arrowMove.getRow() * gameSize + arrowMove.getColumn()) * 2);

        boardPositions.set(fire, false);
        boardPositions.set(fire + 1, true);

        return move;
    }

    @Override
    public void resetGame(int gameSize) {
        tree = new MoveTree(gameSize);
    }

    @Override
    public void informAIOfGameMove(GameMove move, boolean isWhiteTurn) {
        tree.informAIOfGameMove(move, isWhiteTurn);
    }
}

class MoveTree {

    Node root;
    int startDepth = 2;
    int gameSize = 0;

    long cutoffTime;
    long startTime;

    class Node implements Comparable{
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
            value = 0;
        }

        @Override
        public int compareTo(Object o) {
            Node otherNode = (Node) o;
            return this.value - otherNode.value;
        }
    }

    public MoveTree(int gameSize) {
        this.gameSize = gameSize;
        root = null;
        startDepth = 2;
    }

    public void addNodesForPosition(BitSet input, boolean isWhitesTurn) {

        if (root == null) {

            BitSet copiedSet = (BitSet)input.clone();

            root = new Node(copiedSet, null, isWhitesTurn);
        }

        startTime = System.currentTimeMillis();
        cutoffTime = startTime + 30000;

        addPossibleMovesTree(root, isWhitesTurn, startDepth, input);


        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;

        System.out.println("Time for AI: " + executionTime + " ms");


        if (executionTime < 50) {
            startDepth++;

            System.out.println("Increased depth to: " + startDepth);
        }
    }

    public void informAIOfGameMove(GameMove move, boolean isWhiteTurn) {

        if (root == null) {
            return;
        }

        for (Node node: root.children) {
            if (move.compareTo(node.move) == 0) {

                makeGameMoveInBitSet(root.position, move);
                node.position = root.position;

                root = node;
                return;
            }
        }
    }

    private void addPossibleMovesTree(Node node, boolean isWhitesTurn, int depth, BitSet currentBoard) {

        if (depth == 0 || System.currentTimeMillis() > cutoffTime) {

            return;
        }

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

        if (isGameOver(currentBoard, isWhitesTurn)) {
            return;
        }

        ArrayList<GameMove> allMoves = new ArrayList<>();

        for(byte i = 0; i < gameSize; i++) {
            for(byte j = 0; j < gameSize; j++) {

                SquareInfo info = PositionConverter.convertBitSetBitsToInfo(currentBoard, gameSize, i, j);

                if (info instanceof Amazon && info.getIsWhite() == isWhitesTurn) {

                    BitSet copiedBitSet = (BitSet)currentBoard.clone();

                    allMoves.addAll(ValidMoveCalculator.getValidMoves(copiedBitSet, info, gameSize));
                }
            }
        }

        for (GameMove move : allMoves) {
            node.children.add(new Node(null, move, isWhitesTurn));
        }

        for (Node innerNode : node.children) {

            makeGameMoveInBitSet(currentBoard, innerNode.move);

            addPossibleMovesTree(innerNode, !isWhitesTurn, depth - 1, currentBoard);

            reverseGameMoveInBitSet(currentBoard, innerNode.move);
        }
    }

    private boolean isGameOver(BitSet bitSet, boolean isWhitesTurn) {

        BoardState state = PositionConverter.convertBitSetToBoardState(bitSet, gameSize);

        for(SquareInfo info: state.getSquares()) {
            if (info instanceof Amazon && info.getIsWhite() == isWhitesTurn) {

                ArrayList<SquareInfo> list = ValidMoveCalculator.getValidSquares(state, info);

                if (list.size() > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public void printTree() {
        if (root == null) {
            return;
        }

        int numberDeep = 0;

        printNode(root, numberDeep);
    }

    private void printNode(Node node, int numberDeep) {

        if (numberDeep < startDepth) {
            System.out.println("Deep: " + numberDeep + " ChildrenSize: " + node.children.size());
        }

        for (Node innerNode: node.children) {
            printNode(innerNode, numberDeep + 1);
        }
    }

    private void makeGameMoveInBitSet(BitSet board, GameMove move) {

        int fromIndex = ((move.getAmazonFromRow() * gameSize + move.getAmazonFromColumn()) * 2);
        int toIndex = ((move.getAmazonToRow() * gameSize + move.getAmazonToColumn()) * 2);
        int fire = ((move.getArrowRow() * gameSize + move.getArrowColumn()) * 2);

        boolean previousColor = board.get(fromIndex + 1);

        board.set(fromIndex, false);
        board.set(fromIndex + 1, false);

        board.set(toIndex, true);
        board.set(toIndex + 1, previousColor);

        board.set(fire, false);
        board.set(fire + 1, true);
    }

    private void reverseGameMoveInBitSet(BitSet board, GameMove move) {

        int fromIndex = ((move.getAmazonFromRow() * gameSize + move.getAmazonFromColumn()) * 2);
        int toIndex = ((move.getAmazonToRow() * gameSize + move.getAmazonToColumn()) * 2);
        int fire = ((move.getArrowRow() * gameSize + move.getArrowColumn()) * 2);

        boolean previousColor = board.get(toIndex + 1);

        board.set(fire, false);
        board.set(fire + 1, false);

        board.set(toIndex, false);
        board.set(toIndex + 1, false);

        board.set(fromIndex, true);
        board.set(fromIndex + 1, previousColor);
    }

    public GameMove calculateBestMove(boolean isWhitesMove) {
        if (root == null) {
            return null;
        }

        alphaBeta(root, root.position, startDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, isWhitesMove);

        GameMove move = getBestMoveAfterSearch();

        root = null;

        return move;
    }

    private GameMove getBestMoveAfterSearch() {
        int max = Integer.MIN_VALUE;

        Node bestNode = null;

        ArrayList<Node> currentBest = new ArrayList<>();

        for (Node child : root.children) {
            if (max < child.value) {
                max = child.value;
                currentBest.add(child);
            }
        }

        if (currentBest.size() > 1) {
            bestNode = currentBest.get(currentBest.size() - 1);
        }

        if (bestNode != null) {
            return bestNode.move;
        }

        else {
            return null;
        }
    }

    public int alphaBeta(Node node, BitSet currentBoard, int depth, int alpha, int beta, boolean isWhitesTurn) {
        if (depth == 0 || node.children.size() == 0) {
            node.value = estimatedValue(currentBoard);
            return node.value;
        }

        for(Node child: node.children) {

            makeGameMoveInBitSet(currentBoard, child.move);

            int score = alphaBeta(child, currentBoard, depth - 1, alpha, beta, !isWhitesTurn);

            if (isWhitesTurn) {
                alpha = Math.max(alpha, score);
                if (alpha >= beta) {
                    reverseGameMoveInBitSet(currentBoard, child.move);
                    break;
                }
            }

            else {
                beta = Math.min(beta, score);

                if (beta <= alpha) {
                    reverseGameMoveInBitSet(currentBoard, child.move);
                    break;
                }
            }

            reverseGameMoveInBitSet(currentBoard, child.move);
        }

        node.value = isWhitesTurn ? alpha : beta;

        return node.value;
    }

    public int negaMax(Node node, BitSet currentBoard, int depth, int alpha, int beta) {
        if (depth == 0) {
            node.value = estimatedValue(currentBoard);
            return node.value;
        }

        int b = beta;

        Collections.sort(node.children);

        int i = 0;

        for(Node child: node.children) {
            makeGameMoveInBitSet(currentBoard, child.move);

            int score = -negaMax(child, currentBoard,depth - 1, -b, -alpha);

            if (i > 0 && score > alpha && score < beta) {
                score = -negaMax(child, currentBoard, depth - 1, -beta, -alpha);
            }

            alpha = Math.max(alpha, score);

            if (alpha >= beta) {
                reverseGameMoveInBitSet(currentBoard, child.move);
                break;
            }

            b = alpha + 1;
            i++;

            reverseGameMoveInBitSet(currentBoard, child.move);
        }

        node.value = alpha;
        return node.value;
    }

    Random random = new Random();
    private int currentInt = 0;

    public int estimatedValue(BitSet board) {
        //return random.nextInt();
        currentInt++;
        return currentInt;
    }
}
