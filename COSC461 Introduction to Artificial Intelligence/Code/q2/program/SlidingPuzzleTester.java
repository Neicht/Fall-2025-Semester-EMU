package q2.program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**///Tester program for given sliding board
public class SlidingPuzzleTester {

    public static void main(String[] args) throws FileNotFoundException {

        //main method for testing
        int size = -1;
        //initial board
        char[][] initial = null;
        //final board
        char[][] goal = null;
        String inputFilePath = null;
        String outputFilePath = null;

        if (args.length > 1) {
            inputFilePath = args[0];
            outputFilePath = "q2/output/" + args[1];
        } else {

            try {
                Scanner scanner = new Scanner(System.in);
                {
                    System.out.println("I/O files not specified in args.");
                    System.out.print("Enter absolute input file path: ");
                    inputFilePath = scanner.nextLine();
                    System.out.print("Enter output file name: ");
                    outputFilePath = "q2/output/" + scanner.nextLine();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        File inputFile = new File(inputFilePath);
        try {
            Scanner scanner = new Scanner(inputFile);
            size = Integer.parseInt(scanner.nextLine());
            scanner.nextLine();
            initial = new char[size][size];


            for (int i = 0; i < size; i++) {
                initial[i] = scanner.nextLine().replaceAll(" ", "").toCharArray();
            }
            ArrayList<Character> charList = new ArrayList<>();
            for (char[] i : initial) {
                for (char j : i) {
                    charList.add(j);
                }
            }
            // The custom order is defined in this string
            String customOrder = "123456789RG";
            // The lambda function creates the Comparator
            Comparator<Character> customComparator = Comparator.comparingInt(customOrder::indexOf);
            charList.sort(customComparator);
            goal = new char[size][size]; // Initialize the goal board
            for (int i = 0; i < charList.size(); i++) {
                int row = i / size; // Integer division determines the row
                int col = i % size; // Modulo operator determines the column
                goal[row][col] = charList.get(i);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        PrintStream console = System.out;
        PrintStream outputFile = new PrintStream(outputFilePath);
        System.setOut(outputFile);

        SlidingAstar s = new SlidingAstar(initial, goal, size);
        s.solve();

        System.setOut(console);
        try {
            Scanner scanner = new Scanner(new File(outputFilePath));
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
