package q3.program;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

//This program plays tic-tac game using min-max, depth limit,
//board evaluation, and alpha-beta pruning
public class  AlphaBeta {
    private final char EMPTY = ' ';                //empty slot
    private final char COMPUTER = 'X';             //computer
    private final char PLAYER = '0';               //player
    private final int MIN = 0;                     //min level
    private final int MAX = 1;                     //max level
    private final int LIMIT = 8;                   //depth limit

    //Board class (inner class)
    private class Board {
        private char[][] array;                    //board array
        private int PLAYER_SCORE;
        private int COMPUTER_SCORE;
        //Constructor of Board class
        private Board(int size) {
            array = new char[size][size];          //create array
            PLAYER_SCORE = 0;
            COMPUTER_SCORE = 0;

            for (int i = 0; i < size; i++)         //fill array with empty slots
                for (int j = 0; j < size; j++)
                    array[i][j] = EMPTY;
        }
    }

    private Board board;                           //game board
    private int size;                              //size of board

    //Constructor of q3.program.AlphaBeta class
    public AlphaBeta(int size) {
        this.board = new Board((int) Math.pow(size, 2));              //create game board
        this.size = size;                          //set board size
    }

    //Method plays game
    public void play() {
        while (true)                               //computer and player take turns
        {
            board = playerMove(board);             //player makes a move
            evaluateScore(board);
            if(draw(board)){
                System.out.println("Draw");
                break;
            }else if(playerWin(board)){
                System.out.println("Player wins "+board.PLAYER_SCORE+"p > "+board.COMPUTER_SCORE+"c ");
                break;
            }
            else if(computerWin(board)){
                System.out.println("Computer wins "+board.PLAYER_SCORE+"p < "+board.COMPUTER_SCORE+"c ");
                break;
            }
            board = computerMove(board);           //computer makes a move
            evaluateScore(board);
            if(draw(board)){
                System.out.println("Draw");
                break;
            }else if(playerWin(board)){
                System.out.println("Player wins "+board.PLAYER_SCORE+"p > "+board.COMPUTER_SCORE+"c ");

                break;
            }
            else if(computerWin(board)){
                System.out.println("Computer wins"+board.PLAYER_SCORE+"p < "+board.COMPUTER_SCORE+"c ");
                break;
            }
        }
    }

    //Method lets the player make a move
    private Board playerMove(Board board) {
        System.out.print("Player move: ");         //prompt player

        Scanner scanner = new Scanner(System.in);  //read player's move
        int i = scanner.nextInt();
        int j = scanner.nextInt();

        board.array[i][j] = PLAYER;                //place player symbol

        displayBoard(board);                       //diplay board

        return board;                              //return updated board
    }

    //Method determines computer's move
    private Board computerMove(Board board) {                                              //generate children of board
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

        System.out.println("Computer move:");

        displayBoard(result);                      //print next move

        return result;                             //retun updated board
    }

    private void displayBoard(Board result) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                System.out.print(result.array[i][j] + " ");
            System.out.println();
        }
        System.out.println();
    }

    //Method computes minmax value of a board
    private int minmax(Board board, int level, int depth, int alpha, int beta) {
        if (computerWin(board) || playerWin(board) || draw(board) || depth >= LIMIT)
            return terminal(board);                //if board is terminal or depth limit is reached
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

                for (int i = 0; i < children.size(); i++) {                                 //find minmax values of children
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
    public int terminal(Board board) {
        if (fillBoardPlayer(board) > fillBoardComputer(board))
            return -5;
        if (fillBoardPlayer(board) < fillBoardComputer(board))
            return 5;
        if(fillBoardPlayer(board) == fillBoardComputer(board))
            return -1;
        return 0;
    }
    private int fillBoardComputer(Board board){
        Board result = new Board(size);
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                result.array[i][j] = board.array[i][j];
                if(board.array[i][j] == EMPTY){
                    result.array[i][j] = COMPUTER;
                }
            }
        }
        evaluateScore(result);
        return result.COMPUTER_SCORE;
    }
    private int fillBoardPlayer(Board board){
        Board result = new Board(size);
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                result.array[i][j] = board.array[i][j];
                if(board.array[i][j] == EMPTY){
                    result.array[i][j] = PLAYER;
                }
            }
        }
        evaluateScore(result);
        return result.PLAYER_SCORE;
    }

    private boolean draw(Board board) {
        return board.PLAYER_SCORE == board.COMPUTER_SCORE && full(board);
    }

    private boolean full(Board board) {
        for(char[] cr: board.array){
            for(char c: cr){
                if(c == EMPTY)
                    return false;
            }
        }
        return true;
    }

    private boolean playerWin(Board board) {
        return board.PLAYER_SCORE > board.COMPUTER_SCORE && full(board);
    }

    private boolean computerWin(Board board) {
        return board.COMPUTER_SCORE > board.PLAYER_SCORE && full(board);
    }

    //Method generates children of board using a symbol
    private LinkedList<Board> generate(Board board, char symbol) {
        LinkedList<Board> children = new LinkedList<Board>();
        //empty list of children
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)         //go thru board
                if (board.array[i][j] == EMPTY) {
                    //if slot is empty
                    Board child = copy(board);     //put the symbol and
                    child.array[i][j] = symbol;    //create child board
                    children.addLast(child);
                }

        return children;                           //return list of children
    }
    private Board copy(Board board) {
        Board result = new Board(size);
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                result.array[i][j] = board.array[i][j];
            }
        }
        return result;
    }
    private void evaluateScore(Board board){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(board.array[i][j] == PLAYER){
                    if(i+1<size && board.array[i+1][j] == PLAYER || j+1<size && board.array[i][j+1] == PLAYER){
                        board.PLAYER_SCORE+=2;
                    }
                    if(i+1<size && board.array[i+1][j] == PLAYER && i-1>=0 && board.array[i-1][j] == PLAYER){
                        board.PLAYER_SCORE+=3;
                    }
                    if(i-1>=0 && board.array[i-1][j] == PLAYER || j-1>=0 && board.array[i][j-1] == PLAYER){
                        board.PLAYER_SCORE+=2;
                    }
                    if(j+1<size && board.array[i][j+1] == PLAYER && j-1>=0 && board.array[i][j-1] == PLAYER){
                        board.PLAYER_SCORE+=3;
                    }

                }else if(board.array[i][j] == COMPUTER){
                    if(i+1<size && board.array[i+1][j] == COMPUTER || j+1<size && board.array[i][j+1] == COMPUTER){
                        board.COMPUTER_SCORE+=2;
                    }
                    if(i+1<size && board.array[i+1][j] == COMPUTER && i-1>=0 && board.array[i-1][j] == COMPUTER){
                        board.COMPUTER_SCORE+=3;
                    }
                    if(i-1>=0 && board.array[i-1][j] == COMPUTER || j-1>=0 && board.array[i][j-1] == COMPUTER){
                        board.COMPUTER_SCORE+=2;
                    }
                    if(j+1<size && board.array[i][j+1] == COMPUTER && j-1>=0 && board.array[i][j-1] == COMPUTER){
                        board.COMPUTER_SCORE+=3;
                    }
                }
            }
        }
    }
}

























