#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

char *board[] = {" ", " ", " ", " ", " ", " ", " ", " ", " "};
char *player1 = "O", *player2 = "X";

int max(int a, int b)
{
    return a > b ? a : b;
}

int min(int a, int b)
{
    return a > b ? b : a;
}

void displayBoard(char *board[])
{
    printf("%s|%s|%s\n-+-+-\n", board[0], board[1], board[2]);
    printf("%s|%s|%s\n-+-+-\n", board[3], board[4], board[5]);
    printf("%s|%s|%s\n", board[6], board[7], board[8]);
}

static void makeMove(char *board[], uint8_t position, char *currentPlayer)
{
    board[position] = currentPlayer;
}

char *checkWinner(char *board[], char *player)
{
    if ((board[0] == board[1] && board[1] == board[2] && board[0] == player) || (board[3] == board[4] && board[4] == board[5] && board[3] == player) || (board[6] == board[7] && board[7] == board[8] && board[6] == player) || (board[0] == board[3] && board[3] == board[6] && board[0] == player) || (board[1] == board[4] && board[4] == board[7] && board[1] == player) || (board[2] == board[5] && board[5] == board[8] && board[2] == player) || (board[0] == board[4] && board[4] == board[8] && board[0] == player) || (board[6] == board[4] && board[4] == board[2] && board[6] == player))
        return player;
    return "";
}

int contains(char *board[], char *target)
{
    for (int i = 0; i < 9; i++)
    {
        if (board[i] == target)
            return 1;
    }
    return 0;
}

int isDraw(char *board[])
{
    return !contains(board, " ");
}

float miniMax(char *board[], int depth, int isMaximizing, char *maxPlayer, char *minPlayer)
{
    if (checkWinner(board, maxPlayer) == maxPlayer)
        return 2;
    else if (checkWinner(board, minPlayer) == minPlayer)
        return -2;
    else if (isDraw(board))
        return 0;

    if (isMaximizing)
    {
        double bestScore = -1;
        for (int i = 0; i < 9; i++)
        {
            if (board[i] == " ")
            {
                board[i] = maxPlayer;
                double score = miniMax(board, depth + 1, 0, maxPlayer, minPlayer);
                board[i] = " ";
                bestScore = max(score, bestScore);
            }
        }
        return bestScore;
    }
    else
    {
        double bestScore = 1;
        for (int i = 0; i < 9; i++)
        {
            if (board[i] == " ")
            {
                board[i] = minPlayer;
                double score = miniMax(board, depth + 1, 1, maxPlayer, minPlayer);
                board[i] = " ";
                bestScore = min(score, bestScore);
            }
        }
        return bestScore;
    }
}

void makeBestMove(char *board[], char *maxPlayer, char *minPlayer)
{
    int moveToMake = -1;
    double bestScore = -1;
    for (int i = 0; i < 9; i++)
    {
        if (board[i] == " ")
        {
            board[i] = maxPlayer;
            double score = miniMax(board, 0, 0, maxPlayer, minPlayer);
            board[i] = " ";
            if (score > bestScore)
            {
                bestScore = score;
                moveToMake = i;
            }
        }
    }
    if (moveToMake != -1)
        makeMove(board, moveToMake, maxPlayer);
}

void main()
{
    while (1)
    {
        displayBoard(board);
        printf("Enter a spot (0-8): \n");
        char * playerMove = input();
        while (!playerMove.contains("9") && board[Integer.parseInt(playerMove)] != " ")
        {
            System.out.println("Taken, Enter a different spot (0-8) or 9 for minimax: ");
            playerMove = scanner.nextLine();
            if (playerMove.contains("9"))
                break;
        }
        if (playerMove.contains("9"))
            makeBestMove(board, player1, player2);
        else
            makeMove(board, Integer.parseInt(playerMove), player1);
        if (checkWinner(board, player1) == player1)
        {
            System.out.println("Winner!");
            break;
        }
        else if (isDraw(board))
        {
            System.out.println("Draw");
            break;
        }
        makeBestMove(board, player2, player1);
        if (checkWinner(board, player2) == player2)
        {
            System.out.println("Loser!");
            break;
        }
        else if (isDraw(board))
        {
            System.out.println("Draw");
            break;
        }
    }
    displayBoard(board);
}