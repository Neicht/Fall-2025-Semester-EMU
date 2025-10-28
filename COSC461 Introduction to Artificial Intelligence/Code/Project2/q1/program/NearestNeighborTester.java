package Project2.q1.program;

import java.io.*;
import java.util.*;
import java.util.function.Function;

//Program tests nearest neighbor classifier in a specific application
public class NearestNeighborTester
{
    /*************************************************************************/

    //number of nearest neighbors
    private static final int NEIGHBORS = 8;

    //Main method
    public static void main(String[] args) throws IOException
    {
        //preprocess files
        String program = "COSC461 Introduction to Artificial Intelligence/Code/Project2/q1/program/Data/";
        convertTrainingFile(program+"originaltrainingfile", program+"trainingfile");
        convertTestFile(program+"originaltestfile", program+"testfile");
        int numberRecords = getNumberRecords( program+"originaltrainingfile");
        int errors = 0;
        //construct nearest neighbor classifier

        for(int skip = 0; skip < numberRecords; skip++) {
            NearestNeighbor classifier = new NearestNeighbor();

            //load training data
            classifier.loadTrainingData(program + "trainingfile", skip);

            //set nearest neighbors
            classifier.setParameters(NEIGHBORS);

            //classify test data
            classifier.classifyData(program + "testfile", program + "originalclassifiedfile");

            //postprocess files
            convertClassFile(program + "originalclassifiedfile", program + "classifiedfile");

            //validate classfier
            errors += classifier.validate();
        }
       // System.out.println("Number of errors: " + (100.0 * errors / numberRecords) + "%");
        System.out.println("Error rate: " + errors + "/" + numberRecords + " = " + (errors / (float)numberRecords) * 100 + "%");
    }


    private static int getNumberRecords(String filepath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filepath));
        int numberRecords = scanner.nextInt();
        scanner.close();
        return numberRecords;
    }

    /*************************************************************************/

    //Method converts training file to numerical format
    private static void convertTrainingFile(String inputFile, String outputFile) throws IOException {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records, attributes, classes
        int numberRecords = inFile.nextInt();
        int numberAttributes = inFile.nextInt();
        int numberClasses = inFile.nextInt();

        //write number of records, attributes, classes
        outFile.println(numberRecords + " " + numberAttributes + " " + numberClasses);

        //for each record
        writeTrainingfile_to_File(inFile, outFile, numberRecords);
    }

    /*************************************************************************/

    private static void writeTrainingfile_to_File(Scanner inFile, PrintWriter outFile, int numberRecords) throws IOException {
        for (int i = 0; i < numberRecords; i++) {

            // a---X----b
            // y = (x-a)/(b-a)
            writeAttributes_to_File(inFile, outFile, numberRecords);
            outFile.print(applyFunction(inFile.next(), x -> switch (x) {
                case "low" -> 1;
                case "medium" -> 2;
                case "high" -> 3;
                case "undetermined" -> 4;
                default -> throw new IllegalArgumentException("Invalid class: " + x);
            }) + " ");
            outFile.println();
        }


        inFile.close();
        outFile.close();
    }

    /*************************************************************************/

    private static void writeTestfile_to_File(Scanner inFile, PrintWriter outFile, int numberRecords) {
        for (int i = 0; i < numberRecords; i++) {
            writeAttributes_to_File(inFile, outFile, numberRecords);
            outFile.println();
        }
        inFile.close();
        outFile.close();
    }

    /*************************************************************************/
    private static void writeAttributes_to_File(Scanner inFile, PrintWriter outFile, int numberRecords) {
        outFile.print(applyFunction((double) inFile.nextInt(), x -> (x - 500.0) / (900.0 - 500.0)) + " ");
        outFile.print(applyFunction((double) inFile.nextInt(), x -> (x - 30.0) / (90.0 - 30.0)) + " ");
        outFile.print(applyFunction((double) inFile.nextInt(), x -> (x - 30.0) / (80.0 - 30.0)) + " ");
        outFile.print(applyFunction(inFile.next(), x -> x.equals("male") ? 0.0 : 1.0) + " ");
        outFile.print(applyFunction(inFile.next(), x -> switch (x) {
            case "single" -> 0.0;
            case "married" -> 0.5;
            case "divorced" -> 1.0;
            default -> throw new IllegalArgumentException("Invalid attribute: " + x);
        }) + " ");
    }


    /*************************************************************************/

    private static void convertTestFile(String inputFile, String outputFile) throws IOException {
        //input and output9 files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records
        int numberRecords = inFile.nextInt();

        //write number of records
        outFile.println(numberRecords);

        writeTestfile_to_File(inFile, outFile, numberRecords);


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
        //for each record
        for (int i = 0; i < numberRecords; i++) {
            int number = inFile.nextInt();
            //convert class number
            String className = applyFunction(number, options::get);
            outFile.println(className);
        }

        inFile.close();
        outFile.close();
    }

    /****************************************************************************/

    private static <T, R> R applyFunction(T value, Function<T, R> function) {
        return function.apply(value);
    }
}

