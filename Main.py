board = [" " for _ in range(9)]
player1 = "1"
player2 = "2"


def displayBoard(board):
    print(f"{board[0]}|{board[1]}|{board[2]}")
    print("-+-+-")
    print(f"{board[3]}|{board[4]}|{board[5]}")
    print("-+-+-")
    print(f"{board[6]}|{board[7]}|{board[8]}")


def makeMove(board, position, currentPlayer):
    board[position] = currentPlayer
    return board


def checkWinner(board, player):
    if (
        (board[0] == board[1] and board[1] == board[2])
        or (board[3] == board[4] and board[4] == board[5])
        or (board[6] == board[7] and board[7] == board[8])
        or (board[0] == board[3] and board[3] == board[6])
        or (board[1] == board[4] and board[4] == board[7])
        or (board[2] == board[5] and board[5] == board[8])
        or (board[0] == board[4] and board[4] == board[8])
        or (board[6] == board[4] and board[4] == board[2])
    ):
        return player


def isDraw(board):
    return not board.__contains__(" ")


def miniMax(board, isMaximizing):
    if checkWinner(board, player2) == player2:
        return float("inf")
    elif checkWinner(board, player1) == player1:
        return float("-inf")
    elif isDraw(board):
        return 0

    if isMaximizing:
        bestScore = float("-inf")
        for i, spot in enumerate(board):
            if i == " ":
                board[i] = player2
                score = miniMax(board, False)
                board[i] = " "
                bestScore = max(score, bestScore)
        return bestScore
    else:
        bestScore = float("inf")
        for i, spot in enumerate(board):
            if i == " ":
                board[i] = player1
                score = miniMax(board, True)
                board[i] = " "
                bestScore = min(score, bestScore)
        return bestScore


if __name__ == "__main__":
    displayBoard(board)
    # board = makeMove(board.copy(), 0, player1)
    displayBoard(board)
