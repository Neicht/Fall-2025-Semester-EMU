package Project1.q3.program;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

//This program plays tic-tac game using min-max, depth limit,

//board evaluation, and alpha-beta pruning

public class AlphaBeta {
    private final char EMPTY = ' ';                //empty slot
    private final char COMPUTER = 'x';             //computer
    private final char PLAYER = 'o';               //player
    private int MIN = 0;                     //min level
    private int MAX = 1;                     //max level
    private final int LIMIT = 6;
    private static final String OUTPUT_FILE_PATH = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project1/q3/output/output";


    //depth limit

    //Board class (inner class)
    private class Board {
        private char[][] array;                    //board array
        private int playerScore;
        private int computerScore;

        //Constructor of Board class
        private Board(int size) {
            array = new char[size][size];          //create array

            for (int i = 0; i < size; i++)         //fill array with empty slots
                for (int j = 0; j < size; j++)
                    array[i][j] = EMPTY;
        }

        private void update() {
            this.playerScore = evaluateScore(this, PLAYER);
            this.computerScore = evaluateScore(this, COMPUTER);
        }

        private int getPlayerScore() {

            return this.playerScore;
        }

        private int getComputerScore() {

            return this.computerScore;
        }


    }

    private Board board;                           //game board
    private int size;                              //size of board


    public AlphaBeta(int size) {
        this.size = size;
        this.board = new Board(size);              //create game board
        //set board size
    }

    private void printAndWrite(String text) {
        System.out.print(text);
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
            out.print(text);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private void printlnAndWrite(String text) {
        System.out.println(text);
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
            out.println(text);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }


    //Method plays game
    public void play() {
        board = new Board(size);
        while (true) {                        //computer and player take turns
            {
                if (full(board)) {
                    if (playerWin(board)) {
                        printAndWrite("Player wins ");
                    } else if (computerWin(board)) {
                        printAndWrite("Computer wins ");
                    } else {
                        printAndWrite("Draw: ");
                    }
                    printAndWrite("\nPLAYER: " + board.playerScore + " \nCOMPUTER: " + board.computerScore + "\n");
                    break;
                }
                board = playerMove(board);             //player makes a move
                board = computerMove(board);           //computer makes a move

            }
        }
    }


    //Method lets the player make a move
    private Board playerMove(Board board) {
        while (true) {
            printAndWrite("Player move: ");         //prompt player

            Scanner scanner = new Scanner(System.in);  //read player's move
            int i = scanner.nextInt();
            int j = scanner.nextInt();

            if (i < 0 || i >= size || j < 0 || j >= size || board.array[i][j] != EMPTY) {
                printlnAndWrite("Invalid move");
            } else {
                board.array[i][j] = PLAYER;                //place player symbol
                board.update();
                printlnAndWrite(i + " " + j);
                displayBoard(board);                       //diplay board
                break;
            }

        }
        return board;                              //return updated board
    }

    //Method determines computer's move
    private Board computerMove(Board board) {//generate children of board

        LinkedList<Board> children = generate(board, COMPUTER);

        int maxIndex = -1;
        int maxValue = Integer.MIN_VALUE;
        //find the child with
        for (int i = 0; i < children.size(); i++)  //largest minmax value
        {
            int currentValue = minmax(children.get(i), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (currentValue > maxValue) {
                maxIndex = i;
                maxValue = currentValue;
            }
        }
        Board result = children.get(maxIndex);     //choose the child as next move

        printlnAndWrite("Computer move:");

        result.update();
        displayBoard(result);                       //diplay board

        return result;                             //retun updated board
    }


    private void displayBoard(Board result) {
        // Print the column headers first, indented to align with the rows
        printAndWrite("    ");
        for (int j = 0; j < size; j++) {
            printAndWrite(j + "   ");
        }
        printlnAndWrite("");

        // Print each row with its header and cell contents
        for (int i = 0; i < size; i++) {
            printAndWrite(i + " |"); // Print the row header (e.g., "0 |")
            for (int j = 0; j < size; j++) {
                printAndWrite(" " + result.array[i][j] + " |"); // Print the cell content and separator
            }
            printlnAndWrite(""); // Move to the next line after the row is complete
        }
        printAndWrite("Score\nPlayer: " + result.playerScore + "\nComputer: " + result.computerScore + "\n------------------\n");
    }

    //Method computes minmax value of a board
    private int minmax(Board board, int level, int depth, int alpha, int beta) {
        if (full(board) || depth >= LIMIT) {
            return terminalValue(board);
        }            //if board is terminal or depth limit is reached
        else                                       //evaluate board
        {
            if (level == MAX)                      //if board is at max level
            {
                LinkedList<Board> children = generate(board, COMPUTER);
                //generate children of board
                int maxValue = Integer.MIN_VALUE;

                for (int i = 0; i < children.size(); i++) {                                 //find minmax values of children
                    int currentValue = minmax(children.get(i), MIN, depth + 1, alpha, beta);

                    if (currentValue > maxValue)  //find maximum of minmax values
                        maxValue = currentValue;

                    if (maxValue >= beta)         //if maximum exceeds beta stop
                        return maxValue;

                    if (maxValue > alpha)         //if maximum exceeds alpha update alpha
                        alpha = maxValue;
                }

                return maxValue;                  //return maximum value
            } else                                   //if board is at min level
            {
                LinkedList<Board> children = generate(board, PLAYER);
                //generate children of board
                int minValue = Integer.MAX_VALUE;

                for (int i = 0; i < children.size(); i++) {//find minmax values of children
                    int currentValue = minmax(children.get(i), MAX, depth + 1, alpha, beta);

                    if (currentValue < minValue)  //find minimum of minmax values
                        minValue = currentValue;

                    if (minValue <= alpha)        //if minimum is less than alpha stop
                        return minValue;

                    if (minValue < beta)          //if minimum is less than beta update beta
                        beta = minValue;
                }

                return minValue;                  //return minimum value
            }
        }
    }

    public int terminalValue(Board board) {
        board.update();

        // Check for absolute win/loss first.
        if (full(board)) {
            if (playerWin(board)) {
                return -10000; // A large, absolute value for a definite loss
            } else if (computerWin(board)) {
                return 10000;  // A large, absolute value for a definite win
            }
        }

        // --- Heuristic Calculation for non-terminal states ---

        // 1. Base Score: Start with the defensively weighted score.
        int defensiveWeight = 2;
        int baseHeuristic = board.getComputerScore() - (board.getPlayerScore() * defensiveWeight);

        // 2. Threat Analysis: Scan for specific dangerous patterns and apply penalties.
        int threatPenalty = 0;
        int threatValue = 100; // A large penalty for leaving a threat open.

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // --- Check for Horizontal Threats from the Player ('o') ---

                // Threat: o o . (Open-ended pair)
                if (j + 2 < size && board.array[i][j] == PLAYER && board.array[i][j + 1] == PLAYER && board.array[i][j + 2] == EMPTY) {
                    threatPenalty += 50;
                }
                // Threat: . o o (Open-ended pair)
                if (j > 0 && j + 1 < size && board.array[i][j - 1] == EMPTY && board.array[i][j] == PLAYER && board.array[i][j + 1] == PLAYER) {
                    threatPenalty += 50;
                }
                // Threat: o . o (Split pair, very dangerous)
                if (j + 2 < size && board.array[i][j] == PLAYER && board.array[i][j + 1] == EMPTY && board.array[i][j + 2] == PLAYER) {
                    threatPenalty += 100;
                }

                // --- Check for Vertical Threats from the Player ('o') ---

                // Threat: o o . (stacked)
                if (i + 2 < size && board.array[i][j] == PLAYER && board.array[i + 1][j] == PLAYER && board.array[i + 2][j] == EMPTY) {
                    threatPenalty += 50;
                }
                // Threat: . o o (stacked)
                if (i > 0 && i + 1 < size && board.array[i - 1][j] == EMPTY && board.array[i][j] == PLAYER && board.array[i + 1][j] == PLAYER) {
                    threatPenalty += 50;
                }
                // Threat: o . o (stacked)
                if (i + 2 < size && board.array[i][j] == PLAYER && board.array[i + 1][j] == EMPTY && board.array[i + 2][j] == PLAYER) {
                    threatPenalty += 100;
                }
            }
        }

        // 3. Final Heuristic: The base score minus any penalties for threats.
        return baseHeuristic - threatPenalty;
    }

    private boolean draw(Board board) {
        return board.getPlayerScore() == board.getComputerScore();
    }

    private boolean full(Board board) {
        for (char[] cr : board.array) {
            for (char c : cr) {
                if (c == EMPTY)
                    return false;
            }
        }
        return true;
    }

    private boolean playerWin(Board board) {
        return board.getPlayerScore() > board.getComputerScore();
    }

    private boolean computerWin(Board board) {
        return board.getPlayerScore() < board.getComputerScore();
    }

    //Method generates children of board using a symbol
    private LinkedList<Board> generate(Board board, char symbol) {
        LinkedList<Board> children = new LinkedList<Board>();
        //empty list of children
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {     //go thru board
                if (board.array[i][j] == EMPTY) {
                    Board child = copy(board);     //put the symbol and
                    child.array[i][j] = symbol;    //create child board
                    children.addLast(child);
                }
            }
        }
        return children;                           //return list of children
    }


    private Board copy(Board board) {
        Board result = new Board(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result.array[i][j] = board.array[i][j];
            }
        }
        return result;
    }

    private int evaluateScore(Board board, char symbol) {
        int p = 0;
        int q = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.array[i][j] == symbol) {
                    if (i + 1 < size && board.array[i + 1][j] == symbol) {
                        p++;
                        if (i + 2 < size && board.array[i + 2][j] == symbol) {
                            q++;
                        }
                    }
                    if (j + 1 < size && board.array[i][j + 1] == symbol) {
                        p++;
                        if (j + 2 < size && board.array[i][j + 2] == symbol) {
                            q++;
                        }
                    }
                }
            }
        }

        return ((2 * p) + (3 * q));
    }

}