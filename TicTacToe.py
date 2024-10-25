"""
Joshua Liu
October 24, 2024
Tic tac toe with Minimax implementation for player and computer
"""

board = [" " for _ in range(9)]
player1, player2 = "O", "X"


def displayBoard(board):
    print(f"{board[0]}|{board[1]}|{board[2]}")
    print("-+-+-")
    print(f"{board[3]}|{board[4]}|{board[5]}")
    print("-+-+-")
    print(f"{board[6]}|{board[7]}|{board[8]}")


def makeMove(board, position, currentPlayer):
    board[position] = currentPlayer


def checkWinner(board, player):
    if (
        (board[0] == board[1] and board[1] == board[2] and board[0] == player)
        or (board[3] == board[4] and board[4] == board[5] and board[3] == player)
        or (board[6] == board[7] and board[7] == board[8] and board[6] == player)
        or (board[0] == board[3] and board[3] == board[6] and board[0] == player)
        or (board[1] == board[4] and board[4] == board[7] and board[1] == player)
        or (board[2] == board[5] and board[5] == board[8] and board[2] == player)
        or (board[0] == board[4] and board[4] == board[8] and board[0] == player)
        or (board[6] == board[4] and board[4] == board[2] and board[6] == player)
    ):
        return player


def isDraw(board):
    return not board.__contains__(" ")


def miniMax(board, depth, isMaximizing):
    if checkWinner(board, player2) == player2:
        return 2
    elif checkWinner(board, player1) == player1:
        return -2
    elif isDraw(board):
        return 0
    if isMaximizing:
        bestScore = -1
        for i, spot in enumerate(board):
            if spot == " ":
                board[i] = player2
                score = miniMax(board, depth + 1, False)
                board[i] = " "
                bestScore = max(score, bestScore)
        return bestScore
    else:
        bestScore = 1
        for i, spot in enumerate(board):
            if spot == " ":
                board[i] = player1
                score = miniMax(board, depth + 1, True)
                board[i] = " "
                bestScore = min(score, bestScore)
        return bestScore


def miniMaxForPlayer(board, depth, isMaximizing):
    if checkWinner(board, player2) == player2:
        return -2
    elif checkWinner(board, player1) == player1:
        return 2
    elif isDraw(board):
        return 0

    if isMaximizing:
        bestScore = -1
        for i, spot in enumerate(board):
            if spot == " ":
                board[i] = player1
                score = miniMaxForPlayer(board, depth + 1, False)
                board[i] = " "
                bestScore = max(score, bestScore)
        return bestScore
    else:
        bestScore = 1
        for i, spot in enumerate(board):
            if spot == " ":
                board[i] = player2
                score = miniMaxForPlayer(board, depth + 1, True)
                board[i] = " "
                bestScore = min(score, bestScore)
        return bestScore


def makeBestMove(board):
    moveToMake = -1
    bestScore = -1
    for i, spot in enumerate(board):
        if spot == " ":
            board[i] = player2
            score = miniMax(board, 0, False)
            board[i] = " "
            if score > bestScore:
                bestScore = score
                moveToMake = i
    if moveToMake != -1:
        makeMove(board, moveToMake, player2)


def makeBestMoveForPlayer(board):
    moveToMake = -1
    bestScore = -1
    for i, spot in enumerate(board):
        if spot == " ":
            board[i] = player1
            score = miniMaxForPlayer(board, 0, False)
            board[i] = " "
            if score > bestScore:
                bestScore = score
                moveToMake = i
    if moveToMake != -1:
        makeMove(board, moveToMake, player1)


if __name__ == "__main__":
    while 2:
        displayBoard(board)
        playerMove = input("Enter a spot: ")
        while playerMove != "help" and board[int(playerMove[0])] != " ":
            playerMove = input("Taken, Enter a spot: ")
        if playerMove.__contains__("help"):
            makeBestMoveForPlayer(board)
        else:
            makeMove(board, int(playerMove[0]), player1)
        if checkWinner(board, player1) == player1:
            print("Winner!")
            break
        elif isDraw(board):
            print("Draw")
            break
        makeBestMove(board)
        if checkWinner(board, player2) == player2:
            print("Loser!")
            break
        elif isDraw(board):
            print("Draw")
            break
    displayBoard(board)
