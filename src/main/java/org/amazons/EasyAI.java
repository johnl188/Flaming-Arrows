package org.amazons;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

/* Currently, just a copy of RandomAI */

public class EasyAI extends AIPlayer{

    private MoveTree tree;

    public EasyAI(boolean isWhite, int gameSize) {
        this.isWhite = isWhite;
        tree = new MoveTree(gameSize);
    }

    @Override
    public GameMove getMove(BitSet boardPositions, int gameSize) {

        ArrayList<SquareInfo> possiblePieces = new ArrayList<>();

        //tree.addNodesForPosition(boardPositions, isWhite);
        //tree.printTree();

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

        tree.informAIOfGameMove(move, !isWhite);

        return move;
    }

    @Override
    public void resetGame(int gameSize) {
        tree = new MoveTree(gameSize);
    }
}

class MoveTree {

    Node root;
    int startDepth = 3;
    int gameSize = 0;

    class Node {
        BitSet position;
        GameMove move;
        boolean isWhite;

        ArrayList<Node> children;

        Node(BitSet position, GameMove move, boolean isWhite) {
            this.position = position;
            this.isWhite = isWhite;
            this.move = move;
            children = new ArrayList<>();
        }
    }

    public MoveTree(int gameSize) {
        this.gameSize = gameSize;
        root = null;
    }

    public void addNodesForPosition(BitSet input, boolean isWhitesTurn) {

        if (root == null) {
            root = new Node(input, null, isWhitesTurn);
        }

        if (!root.position.equals(input)) {
            determineLastMove(input, isWhitesTurn);
        }

        addPossibleMovesTree(root, isWhitesTurn, startDepth, input);
    }

    public void determineLastMove(BitSet input, boolean isWhitesTurn) {

        for (Node child: root.children) {
            if (child.move != null) {
                reverseGameMoveInBitSet(input, child.move, isWhitesTurn);

                if (root.position.equals(input)) {
                    makeGameMoveInBitSet(input, child.move, isWhitesTurn);

                    child.position = input;
                    root = child;
                    return;
                }

                makeGameMoveInBitSet(input, child.move, isWhitesTurn);
            }

        }
    }


    public void informAIOfGameMove(GameMove move, boolean isWhiteTurn) {

        if (root == null) {
            return;
        }

        for (Node node: root.children) {
            if (move.compareTo(node.move) == 0) {
                BoardState preTest = PositionConverter.convertBitSetToBoardState(root.position, gameSize);

                makeGameMoveInBitSet(root.position, move, isWhiteTurn);
                node.position = root.position;

                BoardState afterTest = PositionConverter.convertBitSetToBoardState(root.position, gameSize);


                root = node;
                return;
            }
        }
    }


    private void addPossibleMovesTree(Node node, boolean isWhitesTurn, int depth, BitSet currentBoard) {

        if (depth == 0) {
            return;
        }

        if (node.children.size() > 0) {
            if (node.position != null) {
                for(Node innerNode: node.children) {
                    addPossibleMovesTree(innerNode, !isWhitesTurn, depth - 1, node.position);
                }
            }

            else if (node.move != null) {
                makeGameMoveInBitSet(currentBoard, node.move, isWhitesTurn);

                for(Node innerNode: node.children) {
                    addPossibleMovesTree(innerNode, !isWhitesTurn, depth - 1, currentBoard);
                }

                reverseGameMoveInBitSet(currentBoard, node.move, isWhitesTurn);
            }

            return;
        }

        if (isGameOver(currentBoard, isWhitesTurn)) {
            return;
        }

        int totalSquare = gameSize * gameSize;
        byte row = 0;
        byte column = 0;
        for(byte i = 0; i < gameSize; i++) {
            for(byte j = 0; j < gameSize; j++) {

                SquareInfo info = PositionConverter.convertBitSetBitsToInfo(currentBoard, gameSize, i, j);

                if (info instanceof Amazon && info.getIsWhite() == isWhitesTurn) {

                    ArrayList<GameMove> moves = ValidMoveCalculator.getValidMoves(currentBoard, info, gameSize);

                    for (GameMove move : moves) {
                        node.children.add(new Node(null, move, isWhitesTurn));
                    }

                    for (Node innerNode : node.children) {
                        makeGameMoveInBitSet(currentBoard, innerNode.move, isWhitesTurn);
                        addPossibleMovesTree(innerNode, !isWhitesTurn, depth - 1, currentBoard);
                        reverseGameMoveInBitSet(currentBoard, innerNode.move, isWhitesTurn);
                    }
                }
            }
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

        printNode(root);
    }

    private void printNode(Node node) {

        if (node.position != null) {
            System.out.println(node.position);
        }

        else {
            System.out.println(node.move);
        }


        for(int i = 0; i < node.children.size(); i++) {

            printNode(node.children.get(i));
        }
    }

    private void makeGameMoveInBitSet(BitSet board, GameMove move, boolean isWhitesTurn) {

        int fromIndex = ((move.getAmazonFromRow() * gameSize + move.getAmazonFromColumn()) * 2);
        int toIndex = ((move.getAmazonToRow() * gameSize + move.getAmazonToColumn()) * 2);
        int fire = ((move.getArrowRow() * gameSize + move.getArrowColumn()) * 2);

        board.set(fromIndex, false);
        board.set(fromIndex + 1, false);

        board.set(toIndex, true);
        board.set(toIndex + 1, !isWhitesTurn);

        board.set(fire, false);
        board.set(fire + 1, true);
    }

    private void reverseGameMoveInBitSet(BitSet board, GameMove move, boolean isWhitesTurn) {

        int fromIndex = ((move.getAmazonFromRow() * gameSize + move.getAmazonFromColumn()) * 2);
        int toIndex = ((move.getAmazonToRow() * gameSize + move.getAmazonToColumn()) * 2);
        int fire = ((move.getArrowRow() * gameSize + move.getArrowColumn()) * 2);

        board.set(fromIndex, true);
        board.set(fromIndex + 1, !isWhitesTurn);

        board.set(toIndex, false);
        board.set(toIndex + 1, false);

        board.set(fire, false);
        board.set(fire + 1, false);
    }
}
