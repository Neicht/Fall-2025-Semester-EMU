package Project2.q2.program;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class MakeImages {
    private String path;
    private String inFile;
    private String outFile;
    private static final char BLANK = ' ';
    private static final char INK = 'X';
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;
    private Image image;

    // --- Image Inner Class ---
    public static class Image {
        private int[][] data;

        public Image() {
            this.data = new int[HEIGHT][WIDTH];
        }

        public int[][] getData() {
            return data;
        }

        public void setData(int[][] data) {
            this.data = data;
        }

        public void distort(double distortionRate, int magnitude) {
            // Create a new blank array to draw the distorted image onto.
            int[][] destination = new int[HEIGHT][WIDTH];

            // Clamp inputs to safe values
            magnitude = Math.max(0, magnitude);
            distortionRate = Math.max(0.0, Math.min(1.0, distortionRate));

            for (int r = 0; r < HEIGHT; r++) {
                for (int c = 0; c < WIDTH; c++) {

                    // We only care about moving '1' pixels (the "ink")
                    if (this.data[r][c] == 1) {
                        int newR = r;
                        int newC = c;

                        // Step 1: Decide IF this pixel should move
                        if (Math.random() < distortionRate) {
                            // Step 2: Decide HOW FAR to move it
                            // A random shift between -magnitude and +magnitude
                            int shiftR = (int) (Math.random() * (2 * magnitude + 1)) - magnitude;
                            int shiftC = (int) (Math.random() * (2 * magnitude + 1)) - magnitude;

                            newR = r + shiftR;
                            newC = c + shiftC;

                            // Step 3: Clamp the new position to be within the 16x16 grid
                            newR = Math.max(0, Math.min(HEIGHT - 1, newR));
                            newC = Math.max(0, Math.min(WIDTH - 1, newC));
                        }

                        // Step 4: "Draw" the pixel at its new (or original) location
                        destination[newR][newC] = 1;

                    }
                }
            }
            this.data = destination;
        }

        public void display() {
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    // Print X for 1 and . for 0 to make it visual
                    System.out.print((data[i][j] == 1 ? INK : BLANK));
                }
                System.out.println();
            }
        }

        public String parseImagetoString(){
            StringBuilder s = new StringBuilder();
            for (int[] datum : data) {
                for (int i : datum) {
                    s.append(i == 1 ? INK : BLANK);
                }
            }
            return s.toString();
        }

        // public void fileWrite(String outFile, String s) throws IOException { // OLD
        public void fileWrite(String outFile) throws IOException { // NEW
            PrintWriter writer = new PrintWriter(new FileWriter(outFile));

            String imageString = parseImagetoString(); // Generate the correct string

            // System.out.println(s); // OLD
            System.out.println(imageString); // NEW: Print the *actual* image string

            // writer.println(parseImagetoString()); // OLD (was fine, but redundant)
            writer.println(imageString); // NEW: Write the same string to the file
            writer.close();
        }

        public void save(String path) {
            // TODO: Add logic to save the 'data' array to a file at 'path'
            System.out.println("Image saving logic not implemented.");
        }

        public void load(String path) {
            // TODO: Add logic to load data from a file at 'path'
            System.out.println("Image loading logic not implemented.");
        }

        public void clear() {
            this.data = new int[HEIGHT][WIDTH];
        }

        public void loadUnitZero() {
            this.data = ImagePreset.ZERO.getData();
        }

        public void loadUnitOne() {
            this.data = ImagePreset.ONE.getData();
        }
    }
    // --- End Image Inner Class ---

    public Image getImage() {
        return this.image;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Constructor for MakeImages.
     * Initializes the image and sets a default path.
     */
    public MakeImages() {
        this.image = new Image();
        this.path = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q2/program/Data/"; // Set a default path
        this.inFile = "infile.txt";
        this.outFile = "outfile.txt";
        }

    public static void main(String[] args) {
        // Create an instance of MakeImages
        MakeImages app = new MakeImages();
        // Call the non-static run method
        app.run();
    }

    /**
     * Non-static run method.
     * Creates the terminal and starts its main loop.
     */
    public void run() {
        TerminalInterface terminal = loadTerminalInterface();
        // Use the terminal's built-in start() method
        terminal.start();
    }

    /**
     * Non-static method to load the terminal.
     */
    public TerminalInterface loadTerminalInterface() {
        TerminalInterface terminal = new TerminalInterface();
        initializeOptions(terminal);
        return terminal;
    }

    /**
     * Non-static method to generate options.
     * Can now access the MakeImages instance using 'MakeImages.this'
     */
    public void initializeOptions(TerminalInterface t) {

        t.addOption("Set Path", "Set the path to the images directory", new Consumer<TerminalInterface>() {
            @Override
            public void accept(TerminalInterface terminalInterface) {
                terminalInterface.out("Current path is: " + MakeImages.this.path);
                terminalInterface.out("Enter new path:");

                // We will add an 'inString()' method to TerminalInterface
                String newPath = terminalInterface.inString();

                // Use MakeImages.this to refer to the outer class instance
                MakeImages.this.setPath(newPath);

                terminalInterface.out("Path has been set to: " + MakeImages.this.path);
            }
        });

        t.addOption("Set In-File", "Set the In-File name", new Consumer<TerminalInterface>() {
            @Override
            public void accept(TerminalInterface terminalInterface) {
                terminalInterface.out("Current In-File is: " + MakeImages.this.inFile);
                terminalInterface.out("Enter new In-File:");
                MakeImages.this.inFile = terminalInterface.inString();
            }
        });

        t.addOption("Set Out-File", "Set the Out-File name", new Consumer<TerminalInterface>() {
            @Override
            public void accept(TerminalInterface terminalInterface) {
                terminalInterface.out("Current Out-File is: " + MakeImages.this.outFile);
                terminalInterface.out("Enter new Out-File:");
                MakeImages.this.outFile = terminalInterface.inString();
            }
        });

        // I've uncommented and implemented the other options as examples

        t.addOption("Make Image", "Make an image (not implemented)", (iface) -> {
            iface.out("Editor not implemented. Use 'Make Zero' or 'Make One'.");
        });

        t.addOption("Clear Image", "Clear the image (set to all 0s)", (iface) -> {
            MakeImages.this.image.clear();
            iface.out("Image cleared.");
            MakeImages.this.image.display();
        });

        t.addOption("Save Image", "Save the image to a file", (iface) -> {
            // MakeImages.this.image.save(MakeImages.this.path); // This is still a stub
            iface.out("Writing image to file: " + MakeImages.this.outFile);
            try {
                MakeImages.this.image.fileWrite(MakeImages.this.outFile);
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        });

        t.addOption("Load Image", "Load an image from a file", (iface) -> {
            MakeImages.this.image.load(MakeImages.this.path);
            iface.out("Load image called (logic not implemented).");
        });

        t.addOption("Distort Image", "Distort the image", (iface) -> {
            MakeImages.this.image.distort(0.5, 2);
            iface.out("Image distorted.");
            MakeImages.this.image.display();
        });

        t.addOption("Load 'Zero'", "Load the unit zero image.", (iface) -> {
            MakeImages.this.image.loadUnitZero();
            iface.out("Image set to unit '0'.");
            MakeImages.this.image.display();
        });

        t.addOption("Load 'One'", "Load the unit one image.", (iface) -> {
            MakeImages.this.image.loadUnitOne();
            iface.out("Image set to unit '1'.");
            MakeImages.this.image.display();
        });

        t.addOption("Display Image", "Display the image", (iface) -> {
            iface.out("Current Image:");
            MakeImages.this.image.display();
        });

        t.addOption("Read File", "Display the contents of in-file", (iface) -> {
            iface.out("Current In-File:" + MakeImages.this.inFile);

        });

        t.addOption("Exit", "Exit the program", (iface) -> {
            iface.out("Goodbye!");
            System.exit(0);
        });
    }

    public <T, R> R fun(T value, Function<T, R> function) {
        return function.apply(value);
    }
}
