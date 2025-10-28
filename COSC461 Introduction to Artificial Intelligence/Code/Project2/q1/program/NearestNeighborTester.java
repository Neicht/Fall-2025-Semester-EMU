package Project2.q1.program;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Function;

/*
        NOTES:
        1. Fix new File() not creating an interactable file in directory
        2. Correct the program to use leave-one-out validation


 */

//Program tests nearest neighbor classifier in a specific application

public class NearestNeighborTester {
    /*************************************************************************/

    //number of nearest neighbors


    //Main method
    public static void main(String[] args) throws IOException {
//        String testFile = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q1/program/NearestNeighbor/originaltestfile";
//        String trainingFile = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q1/program/NearestNeighbor/originaltrainingfile";
//        String validationFile = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q1/program/NearestNeighbor/originalvalidationfile";
//        String testFile = null, trainingFile = null, validationFile = null;
//        try {
//            Scanner scanner = new Scanner(System.in);
//            System.out.print("Enter absolute testFile path: ");
//            testFile = scanner.nextLine();
//            System.out.print("Enter absolute trainingFile path: ");
//            trainingFile = scanner.nextLine();
//            System.out.print("Enter absolute validationFile path: ");
//            validationFile = scanner.nextLine();
//            scanner.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        String testFile = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q1/testfile1.txt";
        String trainingFile = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q1/trainingfile1.txt";
        String validationFile = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q1/testValidationFile";
        String classifiedFile = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q1/classifiedfile1.txt";
        String validationOutputFile = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q1/validationOutputFile.txt";

        int neighbors = getNeighbors(trainingFile);
        convertTrainingFile(trainingFile, "trainingfile1");
        convertTestFile(testFile, "testfile1");


        //preprocess files

        //construct nearest neighbor classifier
        NearestNeighbor classifier = new NearestNeighbor();

        //load training data
        classifier.loadTrainingData(trainingFile);
        convertValidationFile(validationFile, validationOutputFile);
        //set nearest neighbors
        classifier.setParameters(neighbors);

        //classify file4output data
        classifier.classifyData("testfile", validationFile);

        //postprocess files
        convertClassFile(classifiedFile, validationFile);

        //validate classfier
        classifier.validate(validationFile);


    }

    private static int getNeighbors(String trainingFile) throws FileNotFoundException {
        int neighbors = 0;

        Scanner scanner = new Scanner(new File(trainingFile));
        neighbors = scanner.nextInt() - 1;
        scanner.close();
        return neighbors;

    }

    /*************************************************************************/

    //Method converts training file to numerical format
    private static void convertTrainingFile(String inputFile, String outputFile) throws IOException {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        FileWriter outFile = new FileWriter(outputFile);

        //read number of records, attributes, classes
        int numberRecords = inFile.nextInt();
        int numberAttributes = inFile.nextInt();
        int numberClasses = inFile.nextInt();

        //write number of records, attributes, classes
        outFile.write(numberRecords + " " + numberAttributes + " " + numberClasses);

        //for each record
        normalizeAll(inFile, outFile, numberRecords);
    }

    private static void normalizeAll(Scanner inFile, FileWriter outFile, int numberRecords) throws IOException {
        for (int i = 0; i < numberRecords; i++) {

            // a---X----b
            // y = (x-a)/(b-a)
            outFile.write(applyFunction((double) inFile.nextInt(), x -> (x - 500.0) / (900.0 - 500.0)) + " ");
            outFile.write(applyFunction((double) inFile.nextInt(), x -> (x - 30.0) / (90.0 - 30.0)) + " ");
            outFile.write(applyFunction((double) inFile.nextInt(), x -> (x - 30.0) / (80.0 - 30.0)) + " ");
            outFile.write(applyFunction(inFile.next(), x -> x.equals("male") ? 0 : 1.5) + " ");
            outFile.write(applyFunction(inFile.next(), x -> switch (x) {
                case "single" -> 0.0;
                case "married" -> 0.5;
                case "divorced" -> 1.0;
                default -> -1;
            }) + " ");
            outFile.write(applyFunction(inFile.next("\\S+"), x -> switch (x) {
                case "low" -> 1;
                case "medium" -> 2;
                case "high" -> 3;
                case "undetermined" -> 4;
                default -> -1;
            }) + " ");
        }


        inFile.close();
        outFile.close();
    }

    /*************************************************************************/

    //Method converts validation file to numerical format
    private static void convertValidationFile(String inputFile, String outputFile) throws IOException {
        //input and output9 files
        Scanner inFile = new Scanner(new File(inputFile));
        FileWriter outFile = new FileWriter(outputFile);

        //read number of records
        int numberRecords = inFile.nextInt();

        //write number of records
        outFile.write(numberRecords);

        normalizeAll(inFile, outFile, numberRecords);
    }

    /*************************************************************************/
    private static void convertAll(Scanner inFile, PrintWriter outFile, int numberRecords) {
        System.out.println("Number of records: " + numberRecords);
        for (int i = 0; i < numberRecords; i++) {
            outFile.print(applyFunction((double) inFile.nextInt(), x -> (x - 500.0) / (900.0 - 500.0)) + " ");
            outFile.print(applyFunction((double) inFile.nextInt(), x -> (x - 30.0) / (90.0 - 30.0)) + " ");
            outFile.print(applyFunction((double) inFile.nextInt(), x -> (x - 30.0) / (80.0 - 30.0)) + " ");
            outFile.print(applyFunction(inFile.next(), x -> x.equals("male") ? 0 : 0.5) + " ");
            outFile.print(applyFunction(inFile.next(), x -> switch (x) {
                case "single" -> 0.0;
                case "married" -> 0.5;
                case "divorced" -> 1.0;
                default -> -1;
            }) + " ");
        }
        inFile.close();
        outFile.close();
    }

    //Method converts file4output file to numerical format
    private static void convertTestFile(String inputFile, String outputFile) throws IOException {
        //input and output9 files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records
        int numberRecords = inFile.nextInt();

        //write number of records
        outFile.println(numberRecords);

        convertAll(inFile, outFile, numberRecords);


    }


    /*************************************************************************/

    //Method converts classified file to text format
    private static void convertClassFile(String inputFile, String outputFile) throws IOException {
        //input and output9 files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records
        int numberRecords = inFile.nextInt();

        //write number of records
        outFile.println(numberRecords);

        HashMap<Integer, String> options = new HashMap<>();
        options.put(1, "low");
        options.put(2, "medium");
        options.put(3, "high");
        options.put(4, "undetermined");
        options.put(-1, "err");
        //for each record
        for (int i = 0; i < numberRecords; i++) {
            int number = inFile.nextInt();
            //convert class number
            String className = applyFunction(number, options::get);
            System.out.println(className);
        }

        inFile.close();
        outFile.close();
    }

    private static <T, R> R applyFunction(T value, Function<T, R> function) {
        return function.apply(value);
    }

}


