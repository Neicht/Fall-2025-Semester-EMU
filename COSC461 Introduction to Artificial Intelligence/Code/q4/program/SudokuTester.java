import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

//Tester program for sudoku solver
public class SudokuTester {
    //Main method for testing
    public static void main(String[] args) {
        //specifc sudoku puzzle
        int[][] board = {{0, 0, 0, 0, 0, 0, 9, 0, 0},
                {0, 0, 5, 9, 0, 3, 0, 4, 0},
                {9, 7, 0, 5, 0, 0, 6, 0, 0},
                {0, 0, 0, 0, 0, 2, 0, 6, 8},
                {1, 8, 0, 0, 0, 0, 0, 2, 9},
                {5, 2, 0, 8, 0, 0, 0, 0, 0},
                {0, 0, 2, 0, 0, 1, 0, 3, 6},
                {0, 1, 0, 7, 0, 9, 4, 0, 0},
                {0, 0, 4, 0, 0, 0, 0, 0, 0}};
        try {
            String path = "q4/file7.txt";
            File inputFile = new File(path);
            Scanner scanner = new Scanner(inputFile);
            ArrayList<Character> charList = new ArrayList<>();
            while (scanner.hasNextLine()) {
               String line = scanner.nextLine();
               for (char c : line.toCharArray()) {
                   charList.add(c);
               }
            }
            System.out.println(charList);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //solve sudoku puzzle
        Sudoku s = new Sudoku(board);
        s.solve();
    }
}