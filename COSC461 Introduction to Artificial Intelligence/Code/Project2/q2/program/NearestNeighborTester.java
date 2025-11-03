package Project2.q2.program;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

/**
 * Tests nearest neighbor classifier using a TerminalInterface.
 * This version is for the "census" data (credit score, age, etc.).
 */
public class NearestNeighborTester {

    // --- Application State ---
    private final TerminalInterface terminal;
    private int k_neighbors;
    private String directory_path;
    private String trainingFile;
    private String testFile;

    // --- File Suffixes ---
    // Now non-final so they can be changed
    private String CONVERTED_SUFFIX = "_converted.txt";
    private String CLASSIFIED_ORIG_SUFFIX = "_classified_original.txt";
    private String CLASSIFIED_SUFFIX = "_classified.txt";

    /**
     * Constructor
     */
    public NearestNeighborTester() {
        this.terminal = new TerminalInterface();
        // Set defaults
        this.k_neighbors = 6; // Default K
        // Updated default path for q1
        this.directory_path = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q2/program/Data/"; // Default path
        this.trainingFile = "training.txt"; // Default training file
        this.testFile = "test.txt";     // Default test file
    }

    /**
     * Main method: Creates an instance and runs it.
     */
    static void main(String[] args) {
        NearestNeighborTester app = new NearestNeighborTester();
        app.run();
    }

    /**
     * Initializes the menu and starts the terminal interface loop.
     */
    public void run() {
        initializeOptions();
        this.terminal.start();
    }

    /**
     * Sets up all the menu categories and options for the terminal.
     */
    private void initializeOptions() {
        TerminalInterface.MenuNode root = this.terminal.getRootMenu();

        setupFileMenu(root);
        setupActionMenu(root);
        setupProgramMenu(root);
    }

    // --- Menu Setup Methods ---

    private void setupFileMenu(TerminalInterface.MenuNode root) {
        TerminalInterface.MenuNode fileMenu = terminal.addCategory("File Settings", root);

        terminal.addOption(fileMenu, "Set Data Directory", "Set the absolute path to your Data/ folder", (iface) -> {
            iface.out("Current directory: " + this.directory_path);
            String newPath = iface.inString("Enter new absolute path (must end with /):");
            if (!newPath.endsWith("/")) {
                newPath += "/";
            }
            if (!new File(newPath).exists() || !new File(newPath).isDirectory()) {
                iface.out("ERROR: Path does not exist or is not a directory.");
            } else {
                this.directory_path = newPath;
                iface.out("Data directory set to: " + this.directory_path);
            }
        });

        terminal.addOption(fileMenu, "Set Training File", "Set the name of the training file", (iface) -> {
            iface.out("Current training file: " + this.trainingFile);
            this.trainingFile = iface.inString("Enter new training file name (e.g., file1.txt):");
            iface.out("Training file set to: " + this.trainingFile);
        });

        terminal.addOption(fileMenu, "Set Test File", "Set the name of the test file", (iface) -> {
            iface.out("Current test file: " + testFile);
            testFile = iface.inString("Enter new test file name (e.g., file2.txt):");
            iface.out("Test file set to: " + testFile);
        });
    }

    private void setupActionMenu(TerminalInterface.MenuNode root) {
        TerminalInterface.MenuNode actionMenu = terminal.addCategory("Classifier Actions", root);

        // "Set K" option has been moved to the "Program" -> "Modify Parameters" menu

        terminal.addOption(actionMenu, "Convert Files", "Convert data files to numeric format", (iface) -> {
            iface.out("Converting files...");
            try {
                String fullTrainingPath = this.directory_path + this.trainingFile;
                String fullTestPath = this.directory_path + this.testFile;
                String convertedTrainingPath = this.directory_path + this.trainingFile + CONVERTED_SUFFIX;
                String convertedTestPath = this.directory_path + this.testFile + CONVERTED_SUFFIX;

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
        });

        terminal.addOption(actionMenu, "Run Validation", "Run leave-one-out validation on training file", (iface) -> {
            iface.out("--- Running Leave-One-Out Validation ---");
            try {
                String convertedTrainingPath = this.directory_path + this.trainingFile + CONVERTED_SUFFIX;
                if (!new File(convertedTrainingPath).exists()) {
                    iface.out("ERROR: Converted training file not found.");
                    iface.out("Please run '1. Convert Files' first.");
                    return;
                }

                int numberRecords = getNumberRecords(this.directory_path + this.trainingFile);
                int errors = 0;
                double startClock = System.nanoTime();

                iface.out("Validating on " + numberRecords + " records with K=" + this.k_neighbors + "...");

                for (int skip = 0; skip < numberRecords; skip++) {
                    NearestNeighbor classifier = new NearestNeighbor();
                    classifier.setSkipIndex(skip);
                    classifier.loadTrainingData(convertedTrainingPath);
                    classifier.setParameters(this.k_neighbors);

                    errors += classifier.validate();

                    if ((skip + 1) % 10 == 0 || skip == numberRecords - 1) {
                        iface.out("...validated " + (skip + 1) + " of " + numberRecords);
                    }
                }
                double endClock = System.nanoTime();

                iface.out("--- Validation Complete ---");
                iface.out("Number of neighbors (K): " + this.k_neighbors);
                iface.out("Error rate: " + errors + "/" + numberRecords + " = " + (errors / (float) numberRecords) * 100 + "%");
                iface.out("Time taken: " + (endClock - startClock) / 1000000000.0 + " seconds");

            } catch (Exception e) {
                iface.out("ERROR during validation: " + e.getMessage());
                e.printStackTrace();
            }
        });

        terminal.addOption(actionMenu, "Classify Test File", "Classify the unknown test file", (iface) -> {
            iface.out("--- Classifying Test File ---");
            try {
                String convertedTrainingPath = this.directory_path + this.trainingFile + CONVERTED_SUFFIX;
                String convertedTestPath = this.directory_path + testFile + CONVERTED_SUFFIX;
                String classifiedOrigPath = this.directory_path + testFile + CLASSIFIED_ORIG_SUFFIX;
                String classifiedPath = this.directory_path + testFile + CLASSIFIED_SUFFIX;

                if (!new File(convertedTrainingPath).exists() || !new File(convertedTestPath).exists()) {
                    iface.out("ERROR: Converted files not found.");
                    iface.out("Please run '1. Convert Files' first.");
                    return;
                }

                NearestNeighbor finalClassifier = new NearestNeighbor();
                finalClassifier.setSkipIndex(Integer.MIN_VALUE); // Use ALL training data
                finalClassifier.loadTrainingData(convertedTrainingPath);
                finalClassifier.setParameters(this.k_neighbors);

                finalClassifier.classifyData(convertedTestPath, classifiedOrigPath);
                convertClassFile(classifiedOrigPath, classifiedPath);

                iface.out("Classification complete.");
                iface.out("Output file: " + classifiedPath);

            } catch (Exception e) {
                iface.out("ERROR during classification: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void setupProgramMenu(TerminalInterface.MenuNode root) {
        TerminalInterface.MenuNode programMenu = terminal.addCategory("Program", root);

        terminal.addOption(programMenu, "Status", "Show current settings", (iface) -> {
            iface.out("--- Current Settings ---");
            iface.out("Data Directory: " + this.directory_path);
            iface.out("Training File: " + this.trainingFile);
            iface.out("Test File: " + testFile);
            iface.out("K (Neighbors): " + this.k_neighbors);
            iface.out("--- Generated Files (" + this.CONVERTED_SUFFIX + ", " + this.CLASSIFIED_SUFFIX + ") ---");
            iface.out("Converted Training: " + this.trainingFile + CONVERTED_SUFFIX);
            iface.out("Converted Test: " + testFile + CONVERTED_SUFFIX);
            iface.out("Classified Output: " + testFile + CLASSIFIED_SUFFIX);
        });

        // --- New "Modify Parameters" Sub-Menu ---
        TerminalInterface.MenuNode paramsMenu = terminal.addCategory("Modify Parameters", programMenu);

        terminal.addOption(paramsMenu, "Set K (Neighbors)", "Set the number of nearest neighbors (k)", (iface) -> {
            iface.out("Current K value: " + this.k_neighbors);
            int k = iface.inInt("Enter new value for K (e.g., 6):");
            if (k > 0) {
                this.k_neighbors = k;
                iface.out("K set to: " + this.k_neighbors);
            } else {
                iface.out("Invalid input. K must be > 0.");
            }
        });

        terminal.addOption(paramsMenu, "Set Converted Suffix", "Set the suffix for converted files", (iface) -> {
            iface.out("Current suffix: " + this.CONVERTED_SUFFIX);
            this.CONVERTED_SUFFIX = iface.inString("Enter new suffix (e.g., _converted.txt):");
            iface.out("Converted suffix set to: " + this.CONVERTED_SUFFIX);
        });

        terminal.addOption(paramsMenu, "Set Classified Suffix", "Set the suffix for classified files", (iface) -> {
            iface.out("Current suffix: " + this.CLASSIFIED_SUFFIX);
            this.CLASSIFIED_SUFFIX = iface.inString("Enter new suffix (e.g., _classified.txt):");
            this.CLASSIFIED_ORIG_SUFFIX = "_classified_original.txt"; // Keep this one linked
            iface.out("Classified suffix set to: " + this.CLASSIFIED_SUFFIX);
        });

        // --- End of new Sub-Menu ---


        terminal.addOption(programMenu, "Clean Up Files", "Delete generated _converted and _classified files", (iface) -> {
            iface.out("Cleaning up intermediate files...");
            try {
                Files.deleteIfExists(new File(this.directory_path + this.trainingFile + CONVERTED_SUFFIX).toPath());
                Files.deleteIfExists(new File(this.directory_path + this.testFile + CONVERTED_SUFFIX).toPath());
                Files.deleteIfExists(new File(this.directory_path + this.testFile + CLASSIFIED_ORIG_SUFFIX).toPath());
                Files.deleteIfExists(new File(this.directory_path + this.testFile + CLASSIFIED_SUFFIX).toPath());
                iface.out("Cleanup complete.");
            } catch (IOException e) {
                iface.out("Error during cleanup: " + e.getMessage());
            }
        });

        terminal.addOption(programMenu, "Exit", "Exit the application", (iface) -> {
            iface.out("Goodbye!");
            iface.stop();
        });
    }


    // --- File Conversion & Parsing Logic ---

    /**
     * Reads the first line of the *original* file to get the record count.
     */
    private static int getNumberRecords(String filepath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filepath));
        int numberRecords = scanner.nextInt();
        scanner.close();
        return numberRecords;
    }

    private static int getNumberAttributes(String filepath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filepath));
        scanner.nextInt();
        int numberAttributes = scanner.nextInt();
        scanner.close();
        return numberAttributes;
    }

    /**
     * Method converts training file to numerical format
     */
    private static void convertTrainingFile(String inputFile, String outputFile) throws IOException {
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        int numberRecords = inFile.nextInt();
        int numberAttributes = inFile.nextInt();
        int numberClasses = inFile.nextInt();

        inFile.nextLine();
        // --- FIX: Remove extra blank line read ---
        // inFile.nextLine();


        outFile.println(numberRecords + " " + numberAttributes + " " + numberClasses);
        // --- FIX: Remove extra blank line write ---
        // outFile.println();

        writeTrainingfile_to_File(inFile, outFile, numberRecords);
    }

    /*************************************************************************/

    private static void writeTrainingfile_to_File(Scanner inFile, PrintWriter outFile, int numberRecords) throws IOException {
        for (int i = 0; i < numberRecords; i++) {
            // 1. Read 16 lines, write 256 doubles (all on one line)
            writeAttributes_to_File(inFile, outFile);

            // 2. Consume the blank line *after* the image from the input file
            inFile.nextLine();

            // 3. Read the class ("zero"/"one") from the input file and write it to the output file
            writeClasses_to_File(inFile, outFile);

            // 4. Finish the record by printing a newline *in the output file*
            outFile.println();

            // 5. Consume the rest of the class line from the input file
            inFile.nextLine();

            // 6. Consume the blank line *between* records in the input file
            if (inFile.hasNextLine()) {
                inFile.nextLine();
            }
        }
        inFile.close();
        outFile.close();
    }

    /*************************************************************************/

//    private static void writeTestfile_to_File(Scanner inFile, PrintWriter outFile, int numberRecords) throws FileNotFoundException {
//        for (int i = 0; i < numberRecords; i++) {
//            // 1. Read 16 lines, write 256 doubles (all on one line)
//            writeAttributes_to_File(inFile, outFile);
//
//            // 2. Finish the record by printing a newline *in the output file*
//            outFile.println();
//
//            // 3. Consume the blank line *between* records in the input file
//            inFile.nextLine(); // Consume blank line after image
//            if (inFile.hasNextLine()) {
//                inFile.nextLine(); // Consume blank line between records
//            }
//        }
//        inFile.close();
//        outFile.close();
//    }

    /*************************************************************************/

    // --- FIX: DELETING A DUPLICATE METHOD THAT WAS HERE ---

    /*************************************************************************/

    private static void writeTestfile_to_File(Scanner inFile, PrintWriter outFile, int numberRecords) throws FileNotFoundException {
        for (int i = 0; i < numberRecords; i++) {
            writeAttributes_to_File(inFile, outFile); // 1. Reads 16 lines, writes 256 doubles
            outFile.println(); // 2. Add a newline to separate records
            inFile.nextLine(); // 3. Consume the blank line after the image
        }
        inFile.close();
        outFile.close();
    }

    /*************************************************************************/

    /**
     * Method calculates normalization and writes values to a file
     */
    private static void writeAttributes_to_File(Scanner inFile, PrintWriter outFile) {
        // This method now converts the 16 lines of 'X' and ' ' into 256 doubles
        // on a single line.
        for (int i = 0; i < 16; i++) {
            String x = inFile.nextLine();
            // --- FIX: This logic was incorrect. Reverting to char-by-char logic. ---
            // Ensure the line is exactly 16 chars, padding if necessary
            String paddedLine = String.format("%-16s", x).substring(0, 16);

            for (int j = 0; j < 16; j++) {
                char c = paddedLine.charAt(j);
                if (c == '1') {
                    outFile.print("1.0 ");
                } else {
                    outFile.print("0.0 ");
                }
            }
            // --- FIX: REMOVED a mis-placed outFile.println(); from inside this loop ---
        }
        // Do not print a newline here; let the calling method do it.
    }

    private static void writeClasses_to_File(Scanner inFile, PrintWriter outFile) {
        outFile.print(applyFunction(inFile.next(), x -> switch (x.trim()) {
            case "one" -> 1;
            case "zero" -> 0;
            default -> throw new IllegalArgumentException("Invalid class: " + x);
        }) + " ");
    }


    /*************************************************************************/

    private static void convertTestFile(String inputFile, String outputFile) throws IOException {
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        int numberRecords = inFile.nextInt();


        // Read and discard attribute/class info from original file header
        int numAttributes = inFile.nextInt();
        int numClasses = inFile.nextInt();
        inFile.nextLine(); // Consume header line

        // --- FIX: Write the full header to the converted test file ---
        // This matches the format expected by classifyData
        outFile.println(numberRecords + " " + numAttributes + " " + numClasses);

        writeTestfile_to_File(inFile, outFile, numberRecords);
    }

    /*************************************************************************/

    /**
     * Method converts classified file to text format
     */
    private static void convertClassFile(String inputFile, String outputFile) throws IOException {
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        int numberRecords = inFile.nextInt();
        outFile.println(numberRecords);

        HashMap<Integer, String> options = new HashMap<>();
        options.put(0, "zero");
        options.put(1, "one");

        for (int i = 0; i < numberRecords; i++) {
            int number = inFile.nextInt();
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



