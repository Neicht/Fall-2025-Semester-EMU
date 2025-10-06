package q4.program;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

//Tester program for sudoku solver
public class SudokuTester {
    //Main method for testing
    public static void main(String[] args) {
        //specifc sudoku puzzle

        char[][] board = null;
        try {
            String path = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/q4/file7.txt";
            File inputFile = new File(path);
            Scanner scanner = new Scanner(inputFile);
            int n = scanner.nextInt();
            board = new char[n][n];
            scanner.nextLine();
            scanner.nextLine();
            for (int i = 0; i < n; i++) {
                String line = scanner.nextLine().replaceAll(" ", "");
                board[i] = line.toCharArray();
            }
            scanner.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Sudoku s = new Sudoku(board);
        s.solve();
    }
}