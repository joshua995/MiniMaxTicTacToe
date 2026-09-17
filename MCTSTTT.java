/*
3000 iters each draws everytime with 10000 games



*/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class MCTSNode extends MCTSTTT {
    private int[][] state = new int[3][3];
    private MCTSNode parent = null;
    private int[] action = new int[2];
    private int player = 0;
    private List<MCTSNode> children = new ArrayList<>();
    private int visits = 0;
    private double wins = 0.0;
    private List<int[]> untriedActions = new ArrayList<>();

    public MCTSNode(int[][] state, MCTSNode parent, int[] action, int player) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.player = player;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.wins = 0.0;
        this.untriedActions = availableActions(state);
    }

    public boolean isTerminal() {
        return checkWinner(this.state) != 0 || availableActions(this.state).size() == 0;
    }

    public boolean isFullyExpanded() {
        return this.untriedActions.size() == 0;
    }

    public MCTSNode expand() {
        int[] action = this.untriedActions.remove(0);
        int[][] newState = deepcopyState(this.state);
        int playerToMove = getCurrentPlayer(this.state);
        newState[action[0]][action[1]] = playerToMove;
        MCTSNode child = new MCTSNode(newState, this, action, playerToMove);
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
        int[][] nState = deepcopyState(this.state);
        int player = getCurrentPlayer(nState);
        while (true) {
            int winner = checkWinner(nState);
            if (winner != 0) {
                return winner;
            }
            List<int[]> actions = availableActions(nState);
            if (actions.size() == 0) {
                return 0;
            }
            int[] move = actions.get(new Random().nextInt(actions.size()));
            nState[move[0]][move[1]] = player;
            player = player == 2 ? 1 : 2;
        }
    }

    public void backpropagate(int winner) {
        this.visits += 1;
        if (winner == 0) {
            this.wins += 0;
        } else if (winner == this.player) {
            this.wins += 1;
        } else {
            this.wins += -1;
        }
        if (this.parent != null) {
            this.parent.backpropagate(winner);
        }
    }

    public int[] mctsSearch(int[][] rootState, int iterations) {
        MCTSNode root = new MCTSNode(rootState, null, null, 0);
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

public class MCTSTTT {
    static int[][] board = new int[3][3];
    static int currentPlayer = 1;
    static int[] move = new int[2];
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

                if (currentPlayer == 1) {
                    // move = new MCTSNode(board, null, null, 1).mctsSearch(board, 3000);
                    List<int[]> empty = availableActions(board);
                    move = empty.get(new Random().nextInt(empty.size()));
                    // System.out.printf("MCTS move: %d,%d\n", move[0], move[1]);
                } else {
                    move = new MCTSNode(board, null, null, 2).mctsSearch(board, 7500);
                    // // System.out.printf("Random move: %d,%d\n", move[0], move[1]);
                }

                board[move[0]][move[1]] = currentPlayer;

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
                currentPlayer = currentPlayer == 2 ? 1 : 2;
            }
            // System.out.println("DRAW");
            board = new int[3][3];
            currentPlayer = 1;
            move = new int[2];
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

    static int checkWinner(int[][] state) {
        for (int i = 0; i < state.length; i++) {
            if (state[i][0] == state[i][1] && state[i][0] == state[i][2] && state[i][0] != 0) {
                return state[i][0];
            }
            if (state[0][i] == state[1][i] && state[0][i] == state[2][i] && state[0][i] != 0) {
                return state[0][i];
            }
        }
        if (state[0][0] == state[1][1] && state[0][0] == state[2][2] && state[0][0] != 0) {
            return state[0][0];
        }
        if (state[0][2] == state[1][1] && state[0][2] == state[2][0] && state[0][2] != 0) {
            return state[0][2];
        }
        return 0;
    }

    static List<int[]> availableActions(int[][] state) {
        List<int[]> availableActs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == 0) {
                    availableActs.add(new int[] { i, j });
                }
            }
        }
        return availableActs;
    }

    static int getCurrentPlayer(int[][] state) {
        int count1 = 0, count2 = 0;
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] == 1) {
                    count1++;
                } else if (state[i][j] == 2) {
                    count2++;
                }
            }
        }
        return count1 == count2 ? 1 : 2;
    }

    static int[][] deepcopyState(int[][] state) {
        int[][] newState = new int[3][3];
        for (int i = 0; i < newState.length; i++) {
            for (int j = 0; j < newState[0].length; j++) {
                newState[i][j] = state[i][j];
            }
        }
        return newState;
    }
}
