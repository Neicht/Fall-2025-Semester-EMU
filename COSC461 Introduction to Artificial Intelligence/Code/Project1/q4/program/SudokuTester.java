package Project1.q4.program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

//Tester program for sudoku solver
public class SudokuTester {
    //Main method for testing
    public static void main(String[] args) throws FileNotFoundException {
        //specifc sudoku puzzle
        String path = "";
        String outputFilePath = "";
        String[][] board = null;
        try {
            //String path = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project1/q4/file7.txt";
            Scanner inputScanner = new Scanner(System.in);
            System.out.print("Enter absolute input file path: ");
            path = inputScanner.nextLine();
            System.out.print("Enter absolute output file path: ");
            outputFilePath = inputScanner.nextLine();
            inputScanner.close();
            Scanner scanner = new Scanner(new File(path));
            int n = scanner.nextInt();
            board = new String[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    board[i][j] = scanner.next();
                }
            }
            scanner.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //String outputFilePath = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project1/q4/output9/output9";
        //String outputFilePath = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project1/q4/output/output16";
        //String outputFilePath = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project1/q4/output/output";
        PrintStream console = System.out;
        File outputFile = new File(outputFilePath);
        PrintStream file = new PrintStream(outputFile);
        System.setOut(file);
        Sudoku s = new Sudoku(board);
        s.solve();
        System.setOut(console);
        try {
            Scanner scanner = new Scanner(outputFile);
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}