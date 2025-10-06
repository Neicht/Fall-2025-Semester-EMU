package q4.program;//This program solves sudoku puzzle using constraint satisfication,
//backtracking, and recursion

public class Sudoku {
    private char[][] board;                    //sudoku board
    private int n = 9;

    //Constructor of Sudoku class
    public Sudoku(char[][] board) {
        this.board = board;                   //set initial board
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
        else if (board[x][y] == 'o') {
            for (value = 1; value <= 9; value += 2) {
                board[x][y] = (char) (value + '0');
                if (check(x, y) && fill(location + 1))
                    return true;
            }
            board[x][y] = 'o';
            return false;
        } else if (board[x][y] == 'e') {                        //otherwise
            for (value = 2; value <= 8; value += 2) {
                board[x][y] = (char) (value + '0');     //try numbers 1-9 at the location

                if (check(x, y) && fill(location + 1))
                    return true;         //if number causes no conflicts and the rest
            }                            //of board can be filled then done

            board[x][y] = 'e';             //if none of numbers 1-9 work then
            return false;                //empty the location and backtrack
        } else if (board[x][y] == 'w') {
            for (value = 1; value <= 9; value++) {
                board[x][y] = (char) (value + '0');    //try numbers 1-9 at the location

                if (check(x, y) && fill(location + 1))
                    return true;         //if number causes no conflicts and the rest
            }                            //of board can be filled then done

            board[x][y] = 'w';             //if none of numbers 1-9 work then
            return false;                //empty the location and backtrack
        } else {    //if location already has value
            return fill(location + 1);

        }

    }

    //Method checks whether a value at a given location causes any conflicts
    private boolean check(int x, int y) {
        int a, b, i, j;

        for (j = 0; j < 9; j++)         //check value causes conflict in row
            if (j != y && board[x][j] == board[x][y])
                return false;

        for (i = 0; i < 9; i++)         //check value causes conflict in column
            if (i != x && board[i][y] == board[x][y])
                return false;

        a = (x / 3) * 3;
        b = (y / 3) * 3;       //check value causes conflict in
        for (i = 0; i < 3; i++)         //3x3 region
            for (j = 0; j < 3; j++)
                if ((a + i != x) && (b + j != y) && board[a + i][b + j] == board[x][y])
                    return false;

        return true;
    }

    //Method displays a board
    private void display() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++)
                System.out.print(board[i][j] + (j == 8 ? "" : " | "));

            //System.out.println();
            for (int j = 0; j < 9; j++) {
            }
            System.out.println();
        }
    }
}