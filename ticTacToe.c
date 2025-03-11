#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

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

static void makeMove(char *board[], int position, char *currentPlayer)
{
    board[position] = currentPlayer;
}

char *checkWinner(char *board[], char *player)
{
    if ((!strcmp(board[0], board[1]) && !strcmp(board[1], board[2]) && !strcmp(board[0], player)) ||
        (!strcmp(board[3], board[4]) && !strcmp(board[4], board[5]) && !strcmp(board[3], player)) ||
        (!strcmp(board[6], board[7]) && !strcmp(board[7], board[8]) && !strcmp(board[6], player)) ||
        (!strcmp(board[0], board[3]) && !strcmp(board[3], board[6]) && !strcmp(board[0], player)) ||
        (!strcmp(board[1], board[4]) && !strcmp(board[4], board[7]) && !strcmp(board[1], player)) ||
        (!strcmp(board[2], board[5]) && !strcmp(board[5], board[8]) && !strcmp(board[2], player)) ||
        (!strcmp(board[0], board[4]) && !strcmp(board[4], board[8]) && !strcmp(board[0], player)) ||
        (!strcmp(board[6], board[4]) && !strcmp(board[4], board[2]) && !strcmp(board[6], player)))
        return player;
    return "";
}

int contains(char *board[], char *target)
{
    for (int i = 0; i < 9; i++)
    {
        if (!strcmp(board[i], target))
            return 1;
    }
    return 0;
}

int isDraw(char *board[])
{
    return !contains(board, " ");
}

double miniMax(char *board[], int depth, int isMaximizing, char *maxPlayer, char *minPlayer)
{
    if (!strcmp(checkWinner(board, maxPlayer), maxPlayer))
        return 2;
    else if (!strcmp(checkWinner(board, minPlayer), minPlayer))
        return -2;
    else if (isDraw(board))
        return 0;

    if (isMaximizing)
    {
        double bestScore = -1;
        for (int i = 0; i < 9; i++)
        {
            if (!strcmp(board[i], " "))
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
            if (!strcmp(board[i], " "))
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
        if (!strcmp(board[i], " "))
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
        char playerMove[1];
        scanf("%s", playerMove);
        while (strcmp(playerMove, "9") && strcmp(board[atoi(playerMove)], " "))
        {
            printf("Taken, Enter a different spot (0-8) or 9 for minimax: \n");
            scanf("%s", playerMove);
            if (!strcmp(playerMove, "9"))
                break;
        }
        if (!strcmp(playerMove, "9"))
            makeBestMove(board, player1, player2);
        else
            makeMove(board, atoi(playerMove), player1);
        if (!strcmp(checkWinner(board, player1), player1))
        {
            printf("Winner!\n");
            break;
        }
        else if (isDraw(board))
        {
            printf("Draw\n");
            break;
        }
        makeBestMove(board, player2, player1);
        if (!strcmp(checkWinner(board, player2), player2))
        {
            printf("Lost!\n");
            break;
        }
        else if (isDraw(board))
        {
            printf("Draw\n");
            break;
        }
    }
    displayBoard(board);
}