/*
 * Joshua Liu
 * October 24, 2024
 * Minimax Tic tac toe
 */
import java.util.Scanner;

public class TicTacToe {

    static String[] board = { " ", " ", " ", " ", " ", " ", " ", " ", " " };
    static String player1 = "O", player2 = "X";

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            displayBoard(board);
            System.out.println("Enter a spot (0-8): ");
            String playerMove = scanner.nextLine();
            while (playerMove != "help" && board[Integer.parseInt(playerMove)] != " ") {
                System.out.println("Taken, Enter a different spot (0-8): ");
                playerMove = scanner.nextLine();
            }
            if (playerMove.contains("help"))
                makeBestMove(board, player1, player2);
            else
                makeMove(board, Integer.parseInt(playerMove), player1);
            if (checkWinner(board, player1) == player1) {
                System.out.println("Winner!");
                break;
            } else if (isDraw(board)) {
                System.out.println("Draw");
                break;
            }
            makeBestMove(board, player2, player1);
            if (checkWinner(board, player2) == player2) {
                System.out.println("Loser!");
                break;
            } else if (isDraw(board)) {
                System.out.println("Draw");
                break;
            }
        }
        displayBoard(board);
        scanner.close();
    }

    static void displayBoard(String[] board) {
        System.out.printf("%s|%s|%s\n", board[0], board[1], board[2]);
        System.out.println("-+-+-");
        System.out.printf("%s|%s|%s\n", board[3], board[4], board[5]);
        System.out.println("-+-+-");
        System.out.printf("%s|%s|%s\n", board[6], board[7], board[8]);
    }

    static void makeMove(String[] board, int position, String currentPlayer) {
        board[position] = currentPlayer;
    }

    static String checkWinner(String[] board, String player) {
        if ((board[0] == board[1] && board[1] == board[2] && board[0] == player)
                || (board[3] == board[4] && board[4] == board[5] && board[3] == player)
                || (board[6] == board[7] && board[7] == board[8] && board[6] == player)
                || (board[0] == board[3] && board[3] == board[6] && board[0] == player)
                || (board[1] == board[4] && board[4] == board[7] && board[1] == player)
                || (board[2] == board[5] && board[5] == board[8] && board[2] == player)
                || (board[0] == board[4] && board[4] == board[8] && board[0] == player)
                || (board[6] == board[4] && board[4] == board[2] && board[6] == player))
            return player;
        return "";
    }

    static boolean contains(String[] board, String target) {
        for (String s : board) {
            if (s == target)
                return true;
        }
        return false;
    }

    static boolean isDraw(String[] board) {
        return !contains(board, " ");
    }

    static double miniMax(String[] board, int depth, boolean isMaximizing, String maxPlayer, String minPlayer) {
        if (checkWinner(board, maxPlayer) == maxPlayer)
            return 2;
        else if (checkWinner(board, minPlayer) == minPlayer)
            return -2;
        else if (isDraw(board))
            return 0;

        if (isMaximizing) {
            double bestScore = -1;
            for (int i = 0; i < board.length; i++) {
                if (board[i] == " ") {
                    board[i] = maxPlayer;
                    double score = miniMax(board, depth + 1, false, maxPlayer, minPlayer);
                    board[i] = " ";
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            double bestScore = 1;
            for (int i = 0; i < board.length; i++) {
                if (board[i] == " ") {
                    board[i] = minPlayer;
                    double score = miniMax(board, depth + 1, true, maxPlayer, minPlayer);
                    board[i] = " ";
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    static void makeBestMove(String[] board, String maxPlayer, String minPlayer) {
        int moveToMake = -1;
        double bestScore = -1;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == " ") {
                board[i] = maxPlayer;
                double score = miniMax(board, 0, false, maxPlayer, minPlayer);
                board[i] = " ";
                if (score > bestScore) {
                    bestScore = score;
                    moveToMake = i;
                }
            }
        }
        if (moveToMake != -1)
            makeMove(board, moveToMake, maxPlayer);
    }
}