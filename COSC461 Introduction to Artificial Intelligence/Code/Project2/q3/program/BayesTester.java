package Project2.q3.program;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

/**
 * The type Bayes tester.
 */
//Program tests Bayes classifier in specific application
public class BayesTester {

    // --- Application State ---
    private final TerminalInterface terminal;
    private String directory_path;
    private String trainingFile;
    private String testFile;
    private double[][][] table;
    private int fileMode = 1;

    // --- File Suffixes ---
    private String CONVERTED_SUFFIX = "_converted.txt";
    private String CLASSIFIED_ORIG_SUFFIX = "_classified_original.txt";
    private String CLASSIFIED_SUFFIX = "_classified.txt";
    private String CONVERTED_PREFIX = "(conv)";
    private String CLASSIFIED_PREFIX = "(class)";
    private String CLASSIFIED_ORIG_PREFIX = "(class_orig)";

    // --- File References ---
    private String fullTrainingPath;
    private String fullTestPath;
    private String convertedTrainingPath;
    private String convertedTestPath;
    private String classifiedOrigPath;
    private String classifiedPath;

    /**
     * Constructor
     */
    public BayesTester() {

        this.terminal = new TerminalInterface();
        // Set defaults
        this.directory_path = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q3/program/Data/"; // Default path
        this.trainingFile = "file5.txt"; // Default training file
        this.testFile = "file6.txt";     // Default test file
    }

    /**
     * Main method: Creates an instance and runs it.
     *
     * @param args the args
     */
    static void main(String[] args) {
        BayesTester app = new BayesTester();
        app.run();
    }

    /**
     * Run.
     */
    public void run() {
        initializeOptions();
        this.terminal.start();
    }

    private void initializeOptions() {
        TerminalInterface.MenuNode root = this.terminal.getRootMenu();
        setupFileReferences();
        setupFileMenu(root);
        setupActionMenu(root);
        setupProgramMenu(root);
    }

    private void setupFileMenu(TerminalInterface.MenuNode root) {
        terminal.addOption(root, "Simple Run", "Quickly run the program with nothing extra", (iface) -> {
            configureDirectory(iface);
            configureTrainingFile(iface);
            setTestFile(iface);
            convertFiles(iface);
            runValidation(iface);
            classifyTestFile(iface);
            closeApp(iface);

        });

        TerminalInterface.MenuNode fileMenu = terminal.addCategory("File Settings", root);

        terminal.addOption(fileMenu, "Set Data Directory", "Set the absolute path to your Q2  folder", this::configureDirectory);
        terminal.addOption(fileMenu, "Set Training File", "Set the name of the training file", this::configureTrainingFile);
        terminal.addOption(fileMenu, "Set Test File", "Set the name of the test file", this::setTestFile);
        terminal.addOption(fileMenu, "Set File Mode", "Set the filename format", this::setFileMode);

    }

    private void setFileMode(TerminalInterface iface) {
        iface.out("Current file mode: " + this.fileMode);
        this.fileMode = Integer.parseInt(iface.inString("Enter new file mode\n[0] = suffix\n[1] = prefix"));
        iface.out("File mode set to: " + this.fileMode);
        setupFileReferences();
        convertFiles(iface);
    }

    private void setTestFile(TerminalInterface iface) {
        iface.out("Current test file: " + testFile);
        testFile = iface.inString("Enter new test file name (e.g., file2.txt):");
        iface.out("Test file set to: " + testFile);
        setupFileReferences();
    }

    private void configureTrainingFile(TerminalInterface iface) {
        iface.out("Current training file: " + this.trainingFile);
        this.trainingFile = iface.inString("Enter new training file name (e.g., file1.txt):");
        iface.out("Training file set to: " + this.trainingFile);
        setupFileReferences();
    }

    private void configureDirectory(TerminalInterface iface) {
        iface.out("Current directory: " + this.directory_path);
        String newPath = iface.inString("Enter new absolute path to Q3 (must end with /):");
        if (!newPath.endsWith("/")) {
            newPath += "/";
        }
        if (!new File(newPath).exists() || !new File(newPath).isDirectory()) {
            iface.out("ERROR: Path does not exist or is not a directory.");
        } else {
            this.directory_path = newPath;
            iface.out("Data directory set to: " + this.directory_path);
        }
        setupFileReferences();
    }

    private void setupActionMenu(TerminalInterface.MenuNode root) {

        TerminalInterface.MenuNode actionMenu = terminal.addCategory("Classifier Actions", root);


        terminal.addOption(actionMenu, "Convert Files", "Convert data files to numeric format", this::convertFiles);
        terminal.addOption(actionMenu, "Run Validation", "Run leave-one-out validation on training file", this::runValidation);
        terminal.addOption(actionMenu, "Classify Test File", "Classify the unknown test file", this::classifyTestFile);
    }

    private void classifyTestFile(TerminalInterface iface) {
        iface.out("--- Classifying Test File ---");
        try {

            if (!new File(convertedTrainingPath).exists() || !new File(convertedTestPath).exists()) {
                iface.out("ERROR: Converted files not found.");
                iface.out("Please run '1. Convert Files' first.");
                return;
            }

            Bayes finalClassifier = new Bayes();
            finalClassifier.setSkipIndex(Integer.MIN_VALUE); // Use ALL training data
            finalClassifier.loadTrainingData(convertedTrainingPath);
            finalClassifier.computeProbability();
            //finalClassifier.setParameters(this.k_neighbors);

            finalClassifier.classifyData(this.convertedTestPath, this.classifiedOrigPath);
            convertClassFile(classifiedOrigPath, classifiedPath);

            iface.out("Classification complete.");
            iface.out("Output file: " + classifiedPath);

        } catch (Exception e) {
            iface.out("ERROR during classification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runValidation(TerminalInterface iface) {
        iface.out("--- Running Leave-One-Out Validation ---");

        try {
            if (!new File(this.convertedTrainingPath).exists()) {
                iface.out("ERROR: Converted training file not found.");
                iface.out("Please run '1. Convert Files' first.");
                return;
            }

            int numberRecords = getNumberRecords(fullTrainingPath);
            int errors = 0;
            double startClock = System.nanoTime();

            iface.out("Validating on " + numberRecords + " records");

            for (int skip = 0; skip < numberRecords; skip++) {
                Bayes classifier = new Bayes();
                classifier.setSkipIndex(skip);
                classifier.loadTrainingData(convertedTrainingPath);
                classifier.computeProbability();
                //classifier.setParameters(this.k_neighbors);

                errors += classifier.validate();

                if ((skip + 1) % 5 == 0 || skip == numberRecords - 1) {
                    iface.out("...validated " + (skip + 1) + " of " + numberRecords);
                }
            }
            double endClock = System.nanoTime();

            iface.out("--- Validation Complete ---");
            //iface.out("Number of neighbors (K): " + this.k_neighbors);
            iface.out("Error rate: " + errors + "/" + numberRecords + " = " + (errors / (float) numberRecords) * 100 + "%");
            iface.out("Time taken: " + (endClock - startClock) / 1000000000.0 + " seconds");

        } catch (Exception e) {
            iface.out("ERROR during validation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void convertFiles(TerminalInterface iface) {
        iface.out("Converting files...");
        try {
            if (!new File(fullTrainingPath).exists()) {
                iface.out("ERROR: Training file not found: " + fullTrainingPath);
                return;
            }
            if (!new File(fullTestPath).exists()) {
                iface.out("ERROR: Test file not found: " + fullTestPath);
                return;
            }

            convertTrainingFile(fullTrainingPath, convertedTrainingPath);
            iface.out("Training file converted: " + convertedTrainingPath);
            convertTestFile(fullTestPath, convertedTestPath);
            iface.out("Test file converted: " + convertedTestPath);
            iface.out("Conversion complete.");

        } catch (Exception e) {
            iface.out("ERROR during file conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFileReferences() {
        this.fullTrainingPath = this.directory_path + "program/Data/" +  this.trainingFile;
        this.fullTestPath = this.directory_path + "program/Data/" +  this.testFile;
        switch (fileMode) {
            case 0:
                this.convertedTrainingPath = this.directory_path + "output/" +  this.trainingFile + CONVERTED_SUFFIX;
                this.convertedTestPath = this.directory_path + "output/" +  this.testFile + CONVERTED_SUFFIX;
                this.classifiedOrigPath = this.directory_path + "output/" +  this.testFile + CLASSIFIED_ORIG_SUFFIX;
                this.classifiedPath = this.directory_path + "output/" +  this.testFile + CLASSIFIED_SUFFIX;
                break;
            case 1:

                this.convertedTrainingPath = this.directory_path + "output/" +  CONVERTED_PREFIX + this.trainingFile;
                this.convertedTestPath = this.directory_path + "output/" +  CONVERTED_PREFIX + this.testFile;
                this.classifiedOrigPath = this.directory_path + "output/" +  CLASSIFIED_ORIG_PREFIX + this.testFile;
                this.classifiedPath = this.directory_path  + "output/" +  CLASSIFIED_PREFIX + this.testFile;
                break;
            default:
                throw new RuntimeException("Invalid file mode: " + fileMode);
        }

    }

    private void setupProgramMenu(TerminalInterface.MenuNode root) {
        TerminalInterface.MenuNode programMenu = terminal.addCategory("Program", root);

        terminal.addOption(programMenu, "Status", "Show current settings", (iface) -> {
            iface.out("--- Current Settings ---");
            iface.out("Data Directory: " + this.directory_path);
            iface.out("Training File: " + this.trainingFile);
            iface.out("Test File: " + testFile);
            //  iface.out("K (Neighbors): " + this.k_neighbors);
            iface.out("--- Generated Files (" + this.CONVERTED_SUFFIX + ", " + this.CLASSIFIED_SUFFIX + ") ---");
            iface.out("Converted Training: " + this.trainingFile + CONVERTED_SUFFIX);
            iface.out("Converted Test: " + testFile + CONVERTED_SUFFIX);
            iface.out("Classified Output: " + testFile + CLASSIFIED_SUFFIX);
        });

        // --- New "Modify Parameters" Sub-Menu ---
        TerminalInterface.MenuNode paramsMenu = terminal.addCategory("Modify Parameters", programMenu);

        terminal.addOption(paramsMenu, "Set Converted Suffix", "Set the suffix for converted files", (iface) -> {
            setConvertedSuffix(iface);
        });

        terminal.addOption(paramsMenu, "Set Classified Suffix", "Set the suffix for classified files", (iface) -> {
            setClassifiedSuffix(iface);
        });
        terminal.addOption(paramsMenu, "Set Converted Prefix", "Set the suffix for converted files", (iface) -> {
            setConvertedPrefix(iface);
        });

        terminal.addOption(paramsMenu, "Set Classified Prefix", "Set the suffix for classified files", (iface) -> {
            setClassifiedPrefix(iface);
        });

        // --- End of new Sub-Menu ---


        terminal.addOption(programMenu, "Clean Up Files", "Delete generated _converted and _classified files", (iface) -> {
            cleanFiles(iface);
        });

        terminal.addOption(programMenu, "Exit", "Exit the application", (iface) -> {
            closeApp(iface);
        });
    }

    private static void closeApp(TerminalInterface iface) {
        iface.out("Goodbye!");
        iface.stop();
    }

    private void cleanFiles(TerminalInterface iface) {
        iface.out("Cleaning up intermediate files...");
        try {
            Files.deleteIfExists(new File(this.directory_path + this.trainingFile + CONVERTED_SUFFIX).toPath());
            Files.deleteIfExists(new File(this.directory_path + this.testFile + CONVERTED_SUFFIX).toPath());
            Files.deleteIfExists(new File(this.directory_path + this.testFile + CLASSIFIED_ORIG_SUFFIX).toPath());
            Files.deleteIfExists(new File(this.directory_path + this.testFile + CLASSIFIED_SUFFIX).toPath());
            Files.deleteIfExists(new File(this.directory_path + CONVERTED_PREFIX + this.trainingFile).toPath());
            Files.deleteIfExists(new File(this.directory_path + CONVERTED_PREFIX + this.testFile).toPath());
            Files.deleteIfExists(new File(this.directory_path + CLASSIFIED_PREFIX + this.testFile).toPath());
            Files.deleteIfExists(new File(this.directory_path + CLASSIFIED_ORIG_PREFIX + this.testFile).toPath());
            iface.out("Cleanup complete.");
        } catch (IOException e) {
            iface.out("Error during cleanup: " + e.getMessage());
        }
    }

    private void setClassifiedPrefix(TerminalInterface iface) {
        iface.out("Current prefix: " + this.CLASSIFIED_PREFIX);
        this.CLASSIFIED_PREFIX = iface.inString("Enter new prefix (e.g., classified):");
        this.CLASSIFIED_ORIG_PREFIX = "_orig"; // Keep this one linked
        iface.out("Classified prefix set to: " + this.CLASSIFIED_PREFIX);
        setupFileReferences();
    }

    private void setConvertedPrefix(TerminalInterface iface) {
        iface.out("Current suffix: " + this.CONVERTED_PREFIX);
        this.CONVERTED_PREFIX = iface.inString("Enter new prefix (e.g., converted):");
        iface.out("Converted prefix set to: " + this.CONVERTED_PREFIX);
        setupFileReferences();
    }

    private void setClassifiedSuffix(TerminalInterface iface) {
        iface.out("Current suffix: " + this.CLASSIFIED_SUFFIX);
        this.CLASSIFIED_SUFFIX = iface.inString("Enter new suffix (e.g., _classified.txt):");
        this.CLASSIFIED_ORIG_SUFFIX = "_classified_original.txt"; // Keep this one linked
        iface.out("Classified suffix set to: " + this.CLASSIFIED_SUFFIX);
        setupFileReferences();
    }

    private void setConvertedSuffix(TerminalInterface iface) {
        iface.out("Current suffix: " + this.CONVERTED_SUFFIX);
        this.CONVERTED_SUFFIX = iface.inString("Enter new suffix (e.g., _converted.txt):");
        iface.out("Converted suffix set to: " + this.CONVERTED_SUFFIX);
        setupFileReferences();
    }


    /****************************************************************************/

    //Method converts training file to numerical format
    private static void convertTrainingFile(String inputFile, String outputFile) throws IOException {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records, attributes, classes
        int numberRecords = inFile.nextInt();
        int numberAttributes = inFile.nextInt();
        int numberClasses = inFile.nextInt();

        //read attribute values
        int[] attributeValues = new int[numberAttributes];
        for (int i = 0; i < numberAttributes; i++)
            attributeValues[i] = inFile.nextInt();

        //write number of records, attributes, classes
        outFile.println(numberRecords + " " + numberAttributes + " " + numberClasses);

        //write attribute values
        for (int i = 0; i < numberAttributes; i++)
            outFile.print(attributeValues[i] + " ");
        outFile.println();

        //for each record
        for (int i = 0; i < numberRecords; i++) {
            attributes_to_Number(inFile, outFile);

//            String className = inFile.next();                    //convert class name
//            int classNumber = convertClassToNumber(className);
//            outFile.print(classNumber);
            outFile.print(doFun(inFile.next(), x -> switch (x.trim()) {
                case "interview" -> 1;
                case "no" -> 2;
                default -> throw new RuntimeException("Error reading attribute: " + x);
            }) + " ");

            outFile.println();
        }

        inFile.close();
        outFile.close();
    }

    /****************************************************************************/

    private static int getNumberRecords(String filepath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filepath));
        int numberRecords = scanner.nextInt();
        scanner.close();
        return numberRecords;
    }

    /****************************************************************************/

    //Method converts test file to numerical format
    private static void convertTestFile(String inputFile, String outputFile) throws IOException {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records
        int numberRecords = inFile.nextInt();

        //write number of records
        outFile.println(numberRecords);

        //for each record
        for (int i = 0; i < numberRecords; i++) {
            attributes_to_Number(inFile, outFile);
            outFile.println();
        }

        inFile.close();
        outFile.close();
    }

    private static void attributes_to_Number(Scanner inFile, PrintWriter outFile) {
        outFile.print(doFun(inFile.next(), x -> switch (x) {
            case "0" -> 1;
            case "1" -> 2;
            case "2" -> 3;
            default -> throw new RuntimeException("Error reading attribute: " + x);
        }) + " ");
        outFile.print(doFun(inFile.next(), x -> switch (x) {
            case "java", "yes" -> 1;
            case "no" -> 2;
            default -> throw new RuntimeException("Error reading attribute: " + x);
        }) + " ");
        outFile.print(doFun(inFile.next(), x -> switch (x) {
            case "0" -> 1;
            case "1" -> 2;
            case "2" -> 3;
            default -> throw new RuntimeException("Error reading attribute: " + x);
        }) + " ");
        outFile.print(doFun(inFile.next(), x -> switch (x) {
            case "cs" -> 1;
            case "other" -> 2;
            default -> throw new RuntimeException("Error reading attribute: " + x);
        }) + " ");
        outFile.print(doFun(inFile.next(), x -> switch (x) {
            case "A" -> 1;
            case "B" -> 2;
            case "C" -> 3;
            case "D" -> 4;
            default -> throw new RuntimeException("Error reading attribute: " + x);
        }) + " ");
    }

    /****************************************************************************/

    //Method converts class file to text format
    private static void convertClassFile(String inputFile, String outputFile) throws IOException {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records
        int numberRecords = inFile.nextInt();

        //write number of records
        outFile.println(numberRecords);

        //for each record
        for (int i = 0; i < numberRecords; i++) {
            outFile.println(doFun(inFile.nextInt(), x -> switch (x) {
                case 1 -> "interview";
                case 2 -> "no";
                default -> throw new RuntimeException("Error reading attribute: " + x);
            }) + " ");
        }

        inFile.close();
        outFile.close();
    }

    /****************************************************************************/

    private static <T, R> R doFun(T value, Function<T, R> function) {
        return function.apply(value);
    }

}


