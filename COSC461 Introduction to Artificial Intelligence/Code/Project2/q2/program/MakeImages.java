package Project2.q2.program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import Project2.q2.program.ImagePreset;

public class MakeImages {
    // app vars
    private String path;
    private String inFile;
    private String outFile;
    public double distortionRate;
    public int magnitude;
    public double shearFactor;
    public double scaleX;
    public double scaleY;
    private Image image;

    public Image getImage() {
        return this.image;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MakeImages() {
        this.image = new Image(); // Create an instance of the external Image class
        this.path = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q2/program/Data/"; // Example: relative path
        this.inFile = "infile.txt";
        this.outFile = "outfile.txt";
        this.distortionRate = 0.2;
        this.magnitude = 1;
        this.shearFactor = 0.0;
        this.scaleX = 1.0;
        this.scaleY = 1.0;
    }

    public static void main(String[] args) {
        MakeImages app = new MakeImages();
        app.run();
    }

    public void run() {
        TerminalInterface terminal = loadTerminalInterface();
        terminal.start();
    }

    public TerminalInterface loadTerminalInterface() {
        TerminalInterface t = new TerminalInterface();
        initializeOptions(t);
        return t;
    }

    public void initializeOptions(TerminalInterface t) {
        TerminalInterface.MenuNode root = t.getRootMenu();

        setupFileMenu(t, root);
        setupImageMenu(t, root);
        setupDistortMenu(t, root);
        setupBatchMenu(t, root);
        setupProgramMenu(t, root);
    }

    private void setupFileMenu(TerminalInterface t, TerminalInterface.MenuNode root) {
        TerminalInterface.MenuNode fileMenu = t.addCategory("File", root);
        t.addOption(fileMenu, "Set Path", "Set the path to the images directory", (iface) -> {
            iface.out("Current path is: " + MakeImages.this.path);
            iface.out("Enter new path (end with /):");
            MakeImages.this.setPath(iface.inString());
            iface.out("Path has been set to: " + MakeImages.this.path);
        });
        t.addOption(fileMenu, "Set In-File", "Set the In-File name", (iface) -> {
            iface.out("Current In-File is: " + MakeImages.this.inFile);
            iface.out("Enter new In-File:");
            MakeImages.this.inFile = iface.inString();
        });
        t.addOption(fileMenu, "Set Out-File", "Set the Out-File name", (iface) -> {
            iface.out("Current Out-File is: " + MakeImages.this.outFile);
            iface.out("Enter new Out-File:");
            MakeImages.this.outFile = iface.inString();
        });
        t.addOption(fileMenu, "Save Image", "Save the image to the Out-File", (iface) -> {
            try {
                String fullPath = MakeImages.this.getPath() + MakeImages.this.outFile;
                iface.out("Appending image to file: " + fullPath);
                MakeImages.this.image.fileWrite(fullPath);
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        });
        t.addOption(fileMenu, "Load Image", "Load an image from In-File (not implemented)", (iface) -> {
            MakeImages.this.image.load(MakeImages.this.path);
        });
    }

    private void setupImageMenu(TerminalInterface t, TerminalInterface.MenuNode root) {
        TerminalInterface.MenuNode imageMenu = t.addCategory("Image", root);
        t.addOption(imageMenu, "Display Image", "Display the current image in the console", (iface) -> {
            iface.out("Current Image:");
            MakeImages.this.image.display();
        });
        t.addOption(imageMenu, "Load 'Zero'", "Load the unit zero image", (iface) -> {
            // Controller sets the image data from the preset
            MakeImages.this.image.setData(ImagePreset.ZERO.getDeepCopy());
            iface.out("Image set to unit '0'.");
            MakeImages.this.image.display();
        });
        t.addOption(imageMenu, "Load 'One'", "Load the unit one image", (iface) -> {
            // Controller sets the image data from the preset
            MakeImages.this.image.setData(ImagePreset.ONE.getDeepCopy());
            iface.out("Image set to unit '1'.");
            MakeImages.this.image.display();
        });
        t.addOption(imageMenu, "Clear Image", "Clear the image (set to all 0s)", (iface) -> {
            MakeImages.this.image.clear();
            iface.out("Image cleared.");
            MakeImages.this.image.display();
        });
    }

    /**
     * Creates all options for the "Distort" menu category and its sub-menu.
     */
    private void setupDistortMenu(TerminalInterface t, TerminalInterface.MenuNode root) {
        TerminalInterface.MenuNode distortMenu = t.addCategory("Distort", root);
        t.addOption(distortMenu, "Apply Scale", "Scale image using current X and Y factors", (iface) -> {
            iface.out("Applying scale: " + MakeImages.this.scaleX + "x, " + MakeImages.this.scaleY + "y");
            MakeImages.this.image.applyScale(MakeImages.this.scaleX, MakeImages.this.scaleY);
            MakeImages.this.image.display();
        });
        t.addOption(distortMenu, "Apply Shear", "Angle the image using current Shear Factor", (iface) -> {
            iface.out("Applying shear: " + MakeImages.this.shearFactor);
            MakeImages.this.image.applyShear(MakeImages.this.shearFactor);
            MakeImages.this.image.display();
        });
        t.addOption(distortMenu, "Apply Jitter", "Smear image using current Rate and Magnitude", (iface) -> {
            iface.out("Applying jitter: " + MakeImages.this.distortionRate + " rate, " + MakeImages.this.magnitude + " magnitude");
            MakeImages.this.image.applyJitter(MakeImages.this.distortionRate, MakeImages.this.magnitude);
            MakeImages.this.image.display();
        });
        t.addOption(distortMenu, "Apply Thicken (Dilation)", "Thicken the image", (iface) -> {
            iface.out("Applying dilation...");
            MakeImages.this.image.applyDilation();
            MakeImages.this.image.display();
        });
        t.addOption(distortMenu, "Apply Thin (Erosion)", "Thin the image", (iface) -> {
            iface.out("Applying erosion...");
            MakeImages.this.image.applyErosion();
            MakeImages.this.image.display();
        });

        // This is the sub-menu
        TerminalInterface.MenuNode paramsMenu = t.addCategory("Modify Parameters", distortMenu);
        t.addOption(paramsMenu, "Set Scale X", "Set the horizontal scale factor (1.0 = normal)", (iface) -> {
            iface.out("Current Scale X: " + MakeImages.this.scaleX);
            MakeImages.this.scaleX = iface.inDouble("Enter new Scale X (e.g., 1.2):");
            iface.out("Scale X set to: " + MakeImages.this.scaleX);
        });
        t.addOption(paramsMenu, "Set Scale Y", "Set the vertical scale factor (1.0 = normal)", (iface) -> {
            iface.out("Current Scale Y: " + MakeImages.this.scaleY);
            MakeImages.this.scaleY = iface.inDouble("Enter new Scale Y (e.g., 0.8):");
            iface.out("Scale Y set to: " + MakeImages.this.scaleY);
        });
        t.addOption(paramsMenu, "Set Distortion Rate", "Set the jitter probability (0.0 - 1.0)", (iface) -> {
            iface.out("Current distortion rate: " + MakeImages.this.distortionRate);
            MakeImages.this.distortionRate = iface.inDouble("Enter new rate:");
            iface.out("Distortion rate set to: " + MakeImages.this.distortionRate);
        });
        t.addOption(paramsMenu, "Set Magnitude", "Set the jitter distance (e.g., 1, 2)", (iface) -> {
            iface.out("Current magnitude: " + MakeImages.this.magnitude);
            MakeImages.this.magnitude = iface.inInt("Enter new magnitude:");
            iface.out("Magnitude set to: " + MakeImages.this.magnitude);
        });
        t.addOption(paramsMenu, "Set Shear Factor", "Set the angle of the image (-1.0 to 1.0)", (iface) -> {
            iface.out("Current shear factor: " + MakeImages.this.shearFactor);
            MakeImages.this.shearFactor = iface.inDouble("Enter new shear factor (e.g., 0.3):");
            iface.out("Shear factor set to: " + MakeImages.this.shearFactor);
        });
    }

    /**
     * Creates all options for the "Batch" menu category.
     */
    private void setupBatchMenu(TerminalInterface t, TerminalInterface.MenuNode root) {
        TerminalInterface.MenuNode batchMenu = t.addCategory("Batch", root);

        t.addOption(batchMenu, "Generate Training File", "Generate 20+ images (zeros and ones)", (iface) -> {
            iface.out("--- Generate Training File ---");
            int numZeros = iface.inInt("How many '0' images? (e.g., 10)");
            int numOnes = iface.inInt("How many '1' images? (e.g., 10)");
            iface.out("Enter filename (e.g., training.txt):");
            String fileName = iface.inString();

            runBatchGeneration(iface, fileName, numZeros, numOnes, "TRAIN");
        });

        t.addOption(batchMenu, "Generate Test File", "Generate 5+ images (zeros and ones)", (iface) -> {
            iface.out("--- Generate Test File ---");
            int numZeros = iface.inInt("How many '0' images? (e.g., 3)");
            int numOnes = iface.inInt("How many '1' images? (e.g., 2)");
            iface.out("Enter filename (e.g., test.txt):");
            String fileName = iface.inString();

            runBatchGeneration(iface, fileName, numZeros, numOnes, "TEST");
        });
    }

    /**
     * Helper method to write the file header. Overwrites.
     */
    private void fileWriteTrainingHeader(String outFile, int recordCount) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile, false))) {
            writer.println(recordCount + " " + 256 + " " + 2);
            writer.println();
        }
    }

    private void runBatchGeneration(TerminalInterface iface, String fileName, int numZeros, int numOnes, String setType) {
        iface.out("--- Batch Generation Parameters ---");
        double maxShear = iface.inDouble("Max Shear (e.g., 0.4)?");
        double minScale = iface.inDouble("Min Scale (e.g., 0.8)?");
        double maxScale = iface.inDouble("Max Scale (e.g., 1.2)?");
        double maxJitterRate = iface.inDouble("Max Jitter Rate (e.g., 0.5)?");
        int jitterMag = iface.inInt("Jitter Magnitude (e.g., 1)?");

        String fullPath = MakeImages.this.getPath() + fileName;

        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < numZeros; i++) labels.add("zero");
        for (int i = 0; i < numOnes; i++) labels.add("one");
        Collections.shuffle(labels);

        int totalRecords = labels.size();
        if (totalRecords == 0) {
            iface.out("No images to generate. Aborting.");
            return;
        }

        iface.out("Starting batch generation for " + totalRecords + " images...");
        long startTime = System.currentTimeMillis();

        try {

            fileWriteTrainingHeader(fullPath, totalRecords);


            for (int i = 0; i < totalRecords; i++) {
                String label = labels.get(i);


                if (label.equals("zero")) {
                    MakeImages.this.image.setData(ImagePreset.ZERO.getDeepCopy());
                } else {
                    MakeImages.this.image.setData(ImagePreset.ONE.getDeepCopy());
                }


                if (label.equals("zero")) {
                    MakeImages.this.image.applyDilation();
                }


                double randShear = (Math.random() * 2 * maxShear) - maxShear;
                double randScaleX = minScale + (Math.random() * (maxScale - minScale));
                double randScaleY = minScale + (Math.random() * (maxScale - minScale));
                double randJitterRate = Math.random() * maxJitterRate;


                MakeImages.this.image.applyJitter(randJitterRate, jitterMag);
                MakeImages.this.image.applyScale(randScaleX, randScaleY);
                MakeImages.this.image.applyShear(randShear);


                if (setType.equals("TRAIN")) {
                    MakeImages.this.image.fileWriteRecord(fullPath, label);
                }else if (setType.equals("TEST")){
                    MakeImages.this.image.fileWriteImageOnly(fullPath);
                }else{
                    throw new IllegalArgumentException("Invalid batch set type: " + setType);
                }

                if ((i + 1) % 10 == 0 || i == totalRecords - 1) {
                    iface.out("...Generated " + (i + 1) + " of " + totalRecords);
                }
            }
        } catch (IOException e) {
            iface.out("ERROR writing batch file: " + e.getMessage());
            iface.out("Aborting batch.");
            return;
        }

        long endTime = System.currentTimeMillis();
        iface.out("--- Batch Complete ---");
        iface.out("Successfully generated and saved " + totalRecords + " records.");
        iface.out("File location: " + fullPath);
        iface.out("Time taken: " + (endTime - startTime) + "ms");
    }


    /**
     * Creates all options for the "Program" menu category.
     */
    private void setupProgramMenu(TerminalInterface t, TerminalInterface.MenuNode root) {
        TerminalInterface.MenuNode programMenu = t.addCategory("Program", root);
        t.addOption(programMenu, "Status", "Display all current settings and image status", (iface) -> {
            iface.out("--- Current Program Status ---");
            iface.out("------------------------------");
            iface.out("Directory Path: " + MakeImages.this.getPath());
            iface.out("Input File:     " + MakeImages.this.inFile);
            iface.out("Output File:    " + MakeImages.this.outFile);
            iface.out("Full Out-Path:  " + MakeImages.this.getPath() + MakeImages.this.outFile);
            iface.out("");
            iface.out("---- Current Parameters ----");
            iface.out("Scale X:         " + MakeImages.this.scaleX);
            iface.out("Scale Y:         " + MakeImages.this.scaleY);
            iface.out("Shear Factor:    " + MakeImages.this.shearFactor);
            iface.out("Distortion Rate: " + MakeImages.this.distortionRate);
            iface.out("Magnitude:       " + MakeImages.this.magnitude);
            iface.out("");
            iface.out("--- Current Image Status ---");
            int[][] currentData = MakeImages.this.image.getData();
            if (Arrays.deepEquals(currentData, ImagePreset.ZERO.getData())) {
                iface.out("Image: Loaded 'ZERO' preset.");
            } else if (Arrays.deepEquals(currentData, ImagePreset.ONE.getData())) {
                iface.out("Image: Loaded 'ONE' preset.");
            } else if (MakeImages.this.image.isAllZeros()) {
                iface.out("Image: CLEARED (all zeros).");
            } else {
                iface.out("Image: Custom or distorted.");
            }
            iface.out("");
            iface.out("--- Image Preview ---");
            MakeImages.this.image.display();
            iface.out("------------------------------");
        });
        t.addOption(programMenu, "Exit", "Exit the program", (iface) -> {
            iface.out("Goodbye!");
            iface.stop();
        });
    }
}


