package q4.program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
        int evaluationFunction = -1;
        int heuristicFunction = -1;
        String inputFilePath = null;
        String outputFilePath = null;

        if (args.length > 1) {
            inputFilePath = args[0];
            outputFilePath = "q4/output/" + args[1];
        } else {

            try {
                Scanner scanner = new Scanner(System.in);
                {
                    System.out.println("I/O files not specified in args.");
                    System.out.print("Enter absolute input file path: ");
                    inputFilePath = scanner.nextLine();
                    System.out.print("Enter output file name: ");
                    outputFilePath = "q4/output/" + scanner.nextLine();
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
            goal = new char[size][size];

            for (int i = 0; i < size; i++) {
                initial[i] = scanner.nextLine().replaceAll(" ", "").toCharArray();
            }
            scanner.nextLine();
            for (int i = 0; i < size; i++) {
                goal[i] = scanner.nextLine().replaceAll(" ", "").toCharArray();
            }

            if (scanner.hasNextLine())
                evaluationFunction = Integer.parseInt(scanner.next());
            if (scanner.hasNextLine())
                heuristicFunction = Integer.parseInt(scanner.next());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        PrintStream console = System.out;
        PrintStream outputFile = new PrintStream(outputFilePath);
        System.setOut(outputFile);


        //SlidingAstar s = new SlidingAstar(initial, goal, size, evaluationFunction, heuristicFunction);


        float clockStart = System.nanoTime();
        //s.solve();
        float clockEnd = System.nanoTime();
        float finalRunTime = (float) (clockEnd - clockStart);
        System.out.printf("\nRuntime: %.3fms",(finalRunTime / 1000000));

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
