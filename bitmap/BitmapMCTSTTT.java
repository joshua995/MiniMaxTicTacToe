package bitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class MCTSNode extends BitmapMCTSTTT {
    private int state = 0;
    private MCTSNode parent = null;
    private int action = -1;
    private boolean isPlayerOne = true;
    private List<MCTSNode> children = new ArrayList<>();
    private int visits = 0;
    private double wins = 0.0;
    private List<Integer> untriedActions = new ArrayList<>();

    public MCTSNode(int state, MCTSNode parent, int action, boolean isPlayerOne) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.isPlayerOne = isPlayerOne;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.wins = 0.0;
        this.untriedActions = availableActions(state);
    }

    public boolean isTerminal() {
        return checkWinner(this.state) != 0
                || availableActions(this.state).isEmpty();
    }

    public boolean isFullyExpanded() {
        return this.untriedActions.size() == 0;
    }

    public MCTSNode expand() {
        int action = this.untriedActions.remove(0);
        int newState = this.state;
        int playerToMove = getCurrentPlayer(this.state);
        newState |= 1 << (playerToMove == 1 ? action : action + OFFSET); // Make a move
        MCTSNode child = new MCTSNode(newState, this, action, (playerToMove == 1 ? true : false));
        this.children.add(child);
        return child;
    }

    public MCTSNode bestChild(double c) {
        for (MCTSNode child : this.children) {
            if (child.visits == 0) {
                return child;
            }
        }

        MCTSNode best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (MCTSNode child : this.children) {
            double exploit = (double) child.wins / child.visits;
            double explore = c * Math.sqrt(
                    Math.log(this.visits) / child.visits);

            double ucb = exploit + explore;

            if (ucb > bestScore) {
                bestScore = ucb;
                best = child;
            }
        }
        return best;
    }

    public int rollout() {
        int nState = this.state;
        int player = getCurrentPlayer(nState);
        while (true) {
            int winner = checkWinner(nState);
            if (winner != 0) {
                return winner;
            }
            List<Integer> actions = availableActions(nState);
            if (actions.size() == 0) {
                return 0;
            }
            int move = actions.get(new Random().nextInt(actions.size()));
            nState |= 1 << (player == 1 ? move : move + OFFSET);
            player = player == 2 ? 1 : 2;
        }
    }

    public void backpropagate(int winner) {
        this.visits += 1;
        if (winner == 0) {
            this.wins += 0;
        } else if (winner == (this.isPlayerOne ? 1 : 2)) {
            this.wins += 1;
        } else {
            this.wins += -1;
        }
        if (this.parent != null) {
            this.parent.backpropagate(winner);
        }
    }

    public int mctsSearch(int rootState, int iterations) {
        MCTSNode root = new MCTSNode(rootState, null, -1, false);
        for (int i = 0; i < iterations; i++) {
            MCTSNode node = root;
            while (!node.isTerminal() && node.isFullyExpanded()) {
                node = node.bestChild(2 * 1.4);
            }
            if (!node.isTerminal() && !node.isFullyExpanded()) {
                node = node.expand();
            }
            int winner = node.rollout();
            node.backpropagate(winner);
        }
        MCTSNode best = root.children.get(0);
        for (MCTSNode child : root.children) {
            if (child.visits > best.visits) {
                best = child;
            }
        }
        return best.action;
    }
}

public class BitmapMCTSTTT {
    static int board = 0b000_000_000_000_000_000;
    static boolean isPlayerOne = true;
    static final int OFFSET = 9;
    static final int BOARD_MASK = 0x1FF;
    static int move = 0; // 0-8 + offset
    static final int[] WIN_MASKS = {
            0b000000111, // 0 1 2
            0b000111000, // 3 4 5
            0b111000000, // 6 7 8

            0b001001001, // 0 3 6
            0b010010010, // 1 4 7
            0b100100100, // 2 5 8

            0b100010001, // 0 4 8
            0b001010100 // 2 4 6
    };
    static List<Integer> winnerList = new ArrayList<>();

    public static void main(String[] args) {
        for (int rounds = 0; rounds < 10000; rounds++) {
            if (rounds % 1000 == 0) {
                long player1Wins = winnerList.stream()
                        .filter(w -> w == 1)
                        .count();

                long player2Wins = winnerList.stream()
                        .filter(w -> w == 2)
                        .count();

                System.out.println("P1 wins: " + player1Wins);
                System.out.println("P2 wins: " + player2Wins);
                System.out.println("Draws: " + (rounds - winnerList.size()));
            }
            for (int turn = 0; turn < 9; turn++) {
                // for (int[] row : board) {
                // System.out.printf("%d %d %d\n", row[0], row[1], row[2]);
                // }
                // System.out.println();

                if (isPlayerOne) {
                    move = new MCTSNode(board, null, -1, true).mctsSearch(board, 7500);
                    // List<Integer> empty = availableActions(board);
                    // move = empty.get(new Random().nextInt(empty.size()));
                    // System.out.printf("MCTS move: %d,%d\n", move[0], move[1]);
                } else {
                    move = new MCTSNode(board, null, -1, false).mctsSearch(board, 7500);
                    // // System.out.printf("Random move: %d,%d\n", move[0], move[1]);
                }

                board |= 1 << (isPlayerOne ? move : move + OFFSET); // Make a move

                int winner = checkWinner(board);
                if (winner != 0) {
                    // for (int[] row : board) {
                    // System.out.printf("%d %d %d\n", row[0], row[1], row[2]);
                    // }
                    // System.out.println();
                    // System.out.printf("Winner %d\n", winner);
                    winnerList.add(winner);
                    break;
                }
                isPlayerOne = !isPlayerOne;
            }
            // System.out.println("DRAW");
            board = 0;
            isPlayerOne = true;
            move = -1;
        }
        long player1Wins = winnerList.stream()
                .filter(w -> w == 1)
                .count();

        long player2Wins = winnerList.stream()
                .filter(w -> w == 2)
                .count();

        System.out.println("P1 wins: " + player1Wins);
        System.out.println("P2 wins: " + player2Wins);
        System.out.println("Draws: " + (10000 - winnerList.size()));
    }

    static int checkWinner(int state) {

        int playerBoard1, playerBoard2;

        playerBoard1 = state & BOARD_MASK;
        playerBoard2 = (state >>> OFFSET) & BOARD_MASK;

        for (int mask : WIN_MASKS) {
            if ((playerBoard1 & mask) == mask) {
                return 1;
            } else if ((playerBoard2 & mask) == mask) {
                return 2;
            }
        }

        return 0;
    }

    static List<Integer> availableActions(int state) {

        List<Integer> availableActs = new ArrayList<>();

        int occupied = (state | (state >>> 9)) & 0x1FF;
        int empty = (~occupied) & 0x1FF;

        while (empty != 0) {

            int position = Integer.numberOfTrailingZeros(empty);

            availableActs.add(position);

            // Remove this available position
            empty &= empty - 1;
        }

        return availableActs;
    }

    static int getCurrentPlayer(int state) {

        int player1Count = Integer.bitCount(state & 0x1FF);
        int player2Count = Integer.bitCount((state >>> 9) & 0x1FF);

        return player1Count == player2Count ? 1 : 2;
    }
}
