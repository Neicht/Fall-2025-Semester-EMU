package Project2.q1.program;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

//Program tests nearest neighbor classifier in a specific application

public class NearestNeighborTester {

    //Main method
    public static void main(String[] args) throws IOException {

        //initialize scanner
        Scanner scanner = new Scanner(System.in);

        //absolute filepath
        System.out.print("Enter absolute path to directory: ");
        String directory_path = scanner.nextLine();
        if (!new File(directory_path).exists() || !new File(directory_path).isDirectory()) {
            System.out.print("Does not exist or is not a directory");
            System.exit(1);
        }

        directory_path = directory_path.endsWith("/") ? directory_path : directory_path + "/";
        //gather training and test files
        System.out.print("Enter training file name: ");
        String trainingfile = scanner.nextLine();
        if (!new File(directory_path + trainingfile).exists()) {
            System.out.print("File does not exist");
            System.exit(1);
        }
        String trainingfile_converted = trainingfile + "_converted";
        System.out.print("Enter test file name: ");
        String testfile = scanner.nextLine();
        if (!new File(directory_path + testfile).exists()) {
            System.out.print("File does not exist");
            System.exit(1);
        }
        String testfile_converted = testfile + "_converted";
        String testfile_classified_original = testfile + "_classified_original";
        String testfile_classified = testfile + "_classified";

        //close scanner


        //preprocess files
        int numberRecords = getNumberRecords(directory_path + trainingfile);

        int minimumError = Integer.MAX_VALUE;
        int bestK = Integer.MAX_VALUE;
        for (int NEIGHBORS = 0; NEIGHBORS < numberRecords; NEIGHBORS++) {
            double startClock = System.nanoTime();
            convertTrainingFile(directory_path + trainingfile, directory_path + trainingfile_converted);
            convertTestFile(directory_path + testfile, directory_path + testfile_converted);

            int errors = 0;
            for (int skip = 0; skip < numberRecords; skip++) {
                //construct nearest neighbor classifier
                NearestNeighbor classifier = new NearestNeighbor();

                //set skip index
                classifier.setSkipIndex(skip);

                //load training data
                classifier.loadTrainingData(directory_path + trainingfile_converted);

                //set nearest neighbors
                //number of nearest neighbors

                classifier.setParameters(NEIGHBORS);


                //classify test data
                classifier.classifyData(directory_path + testfile_converted, directory_path + testfile_classified_original);

                //postprocess files
                convertClassFile(directory_path + testfile_classified_original, directory_path + testfile_classified);

                //validate classfier
                errors += classifier.validate();
            }
            double endClock = System.nanoTime();

            // System.out.println("Number of errors: " + (100.0 * errors / numberRecords) + "%");
            if(errors < minimumError){
                minimumError = errors;
                bestK = NEIGHBORS;
            }
            System.out.print("k" + NEIGHBORS + " Error rate: " + errors + "/" + numberRecords + " = " + (errors / (float) numberRecords) * 100 + "%");
            System.out.println(" (" + (endClock - startClock) / 1000000000.0 + " seconds)");
        }
        System.out.println("Best k: " + bestK);
        //clean
        System.out.println("Clean? y/n");
        if (scanner.nextLine().

                equalsIgnoreCase("y")) {
            System.out.print("Cleaning up");
            Files.deleteIfExists(new File(directory_path + trainingfile_converted).toPath());
            System.out.print(".");
            Files.deleteIfExists(new File(directory_path + testfile_converted).toPath());
            System.out.print(".");
            Files.deleteIfExists(new File(directory_path + testfile_classified_original).toPath());
            System.out.println(".");
            Files.deleteIfExists(new File(directory_path + testfile_classified).toPath());
            System.out.println("Done.");
        }
        scanner.close();
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
            writeAttributes_to_File(inFile, outFile, numberRecords);
            writeClasses_to_File(inFile, outFile, numberRecords);
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

    //Method calculates normalization and writes values to a file
    private static void writeAttributes_to_File(Scanner inFile, PrintWriter outFile, int numberRecords) {
        // a---X----b
        // y = (x-a)/(b-a)
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

    private static void writeClasses_to_File(Scanner inFile, PrintWriter outFile, int numberRecords) {
        outFile.print(applyFunction(inFile.next(), x -> switch (x) {
            case "low" -> 1;
            case "medium" -> 2;
            case "high" -> 3;
            case "undetermined" -> 4;
            default -> throw new IllegalArgumentException("Invalid class: " + x);
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

    //lambda function to apply a function<t,r> to a value t using function.apply
    private static <T, R> R applyFunction(T value, Function<T, R> function) {
        return function.apply(value);
    }
}

