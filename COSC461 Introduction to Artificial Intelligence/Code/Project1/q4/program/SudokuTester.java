package Project1.q4.program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

//Tester program for sudoku solver
public class SudokuTester {
    //Main method for testing
    public static void main(String[] args) throws FileNotFoundException {
        //specifc sudoku puzzle

        String[][] board = null;
        try {
           // String path = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project1/q4/file7.txt";
            String path = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project1/q4/file8.txt";
            File inputFile = new File(path);
            Scanner scanner = new Scanner(inputFile);
            int n = scanner.nextInt();
            board = new String[n][n];
            for (int i = 0; i < n; i++) {
                for( int j = 0; j < n; j++){
                    board[i][j] = scanner.next();
                }
            }
            scanner.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String outputFilePath = "Project1/q4/output/output";
        File outputFile = new File(outputFilePath);
        PrintStream console = System.out;
        PrintStream file = new PrintStream(outputFile);
        System.setOut(file);
        Sudoku s = new Sudoku(board);
        s.solve();
    }
}