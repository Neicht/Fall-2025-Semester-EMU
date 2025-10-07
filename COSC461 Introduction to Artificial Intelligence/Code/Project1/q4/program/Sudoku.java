package Project1.q4.program;//This program solves sudoku puzzle using constraint satisfication,
//backtracking, and recursion

import java.util.Objects;

public class Sudoku {
    private String[][] board;                    //sudoku board
    private int n;

    //Constructor of Sudoku class
    public Sudoku(String[][] board) {
        this.board = board;                   //set initial board
        this.n = board.length;
    }

    //Method solves a given puzzle
    public void solve() {                                         //fill the board starting
        if (fill(0))                          //at the beginning
            display();                         //if success display board
        else
            System.out.println("No solution"); //otherwise failure
    }

    //Method fills a board using recursion/backtracking. 
    //It fills the board starting at a given location
    private boolean fill(int location) {
        int x = location / n;              //find x,y coordinates of
        int y = location % n;              //current location
        int value;


        if (location > (n * n) - 1)               //if location exceeds board
            return true;                 //whole board is filled
//           //fill the rest of borad
        else if (board[x][y].equals(String.valueOf('o'))) {
            for (value = 1; value <= n; value += 2) {
                board[x][y] = (String.valueOf(value));
                if (check(x, y) && fill(location + 1))
                    return true;
            }
            board[x][y] = String.valueOf('o');
            return false;
        } else if (board[x][y].equals(String.valueOf('e'))) {                        //otherwise
            for (value = 2; value <= n; value += 2) {
                board[x][y] = (String.valueOf(value));     //try numbers 1-9 at the location

                if (check(x, y) && fill(location + 1))
                    return true;         //if number causes no conflicts and the rest
            }                            //of board can be filled then done

            board[x][y] = String.valueOf('e');            //if none of numbers 1-9 work then
            return false;                //empty the location and backtrack
        } else if (board[x][y].equals(String.valueOf('w'))) {
            for (value = 1; value <= n; value++) {
                board[x][y] = (String.valueOf(value));    //try numbers 1-9 at the location

                if (check(x, y) && fill(location + 1))
                    return true;         //if number causes no conflicts and the rest
            }                            //of board can be filled then done

            board[x][y] = String.valueOf('w');             //if none of numbers 1-9 work then
            return false;                //empty the location and backtrack
        } else {    //if location already has value
            return fill(location + 1);

        }

    }

    //Method checks whether a value at a given location causes any conflicts
    private boolean check(int x, int y) {
        int a, b, i, j;

        for (j = 0; j < n; j++)         //check value causes conflict in row
            if (j != y && Objects.equals(board[x][j], board[x][y]))
                return false;

        for (i = 0; i < n; i++)         //check value causes conflict in column
            if (i != x && Objects.equals(board[i][y], board[x][y]))
                return false;

        a = (x / (int)Math.sqrt(n)) * (int)Math.sqrt(n);
        b = (y / (int)Math.sqrt(n)) * (int)Math.sqrt(n);       //check value causes conflict in
        for (i = 0; i < (int)Math.sqrt(n); i++)         //3x3 region
            for (j = 0; j < (int)Math.sqrt(n); j++)
                if ((a + i != x) && (b + j != y) && Objects.equals(board[a + i][b + j], board[x][y]))
                    return false;

        return true;
    }

    //Method displays a board
    private void display() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                System.out.printf("%2s%s", board[i][j], (j == n-1 ? "" : " |"));

            //System.out.println();
//            for (int j = 0; j < 9; j++) {
//            }
            System.out.println();
        }
    }
}