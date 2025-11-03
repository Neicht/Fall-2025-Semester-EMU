package Project2.q2.program;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Model class for a 16x16 binary image.
 * Contains all image transformation logic (scale, shear, jitter, etc.).
 */
public class Image {

    // Image properties are now self-contained
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;
    private static final char BLANK = '0';
    private static final char INK = '1';

    private int[][] data;

    public Image() {
        this.data = new int[HEIGHT][WIDTH];
    }

    public boolean isAllZeros() {
        for (int[] row : data) {
            for (int val : row) {
                if (val != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getData() {
        return data;
    }

    public void setData(int[][] data) {
        this.data = data;
    }

    /**
     * Applies a "smear" or "jitter" distortion.
     * This adds pixels, it doesn't move them, preventing gaps.
     * @param distortionRate Probability (0.0-1.0) any '1' pixel will smear.
     * @param magnitude Max distance (1, 2) the smear can go.
     */
    public void applyJitter(double distortionRate, int magnitude) {
        // 1. Create a deep copy. This preserves all original pixels.
        int[][] destination = new int[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            System.arraycopy(this.data[i], 0, destination[i], 0, WIDTH);
        }

        // Clamp inputs
        magnitude = Math.max(0, magnitude);
        distortionRate = Math.max(0.0, Math.min(1.0, distortionRate));

        // 2. Iterate over the *original* data
        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                if (this.data[r][c] == 1) {
                    // 3. Decide if this pixel should smear
                    if (Math.random() < distortionRate) {
                        // 4. Calculate a new position for the *extra* pixel
                        int shiftR = (int) (Math.random() * (2 * magnitude + 1)) - magnitude;
                        int shiftC = (int) (Math.random() * (2 * magnitude + 1)) - magnitude;

                        int newR = Math.max(0, Math.min(HEIGHT - 1, r + shiftR));
                        int newC = Math.max(0, Math.min(WIDTH - 1, c + shiftC));

                        // 5. Draw the *additional* pixel onto the destination
                        destination[newR][newC] = 1;
                    }
                }
            }
        }
        // 6. Set the image to the new, smeared version
        this.data = destination;
    }

    /**
     * NEW: Samples the image at a floating-point coordinate using bilinear interpolation.
     * This prevents gaps and aliasing from transformations.
     * @param r The fractional row coordinate.
     * @param c The fractional column coordinate.
     * @return 1 if the interpolated value is > 0.5, else 0.
     */
    private int sampleBilinear(double r, double c) {
        // Get integer and fractional parts
        int r0 = (int) Math.floor(r);
        int c0 = (int) Math.floor(c);
        int r1 = r0 + 1;
        int c1 = c0 + 1;
        double dr = r - r0;
        double dc = c - c0;

        // Get the 4 neighboring pixel values, checking boundaries
        double p00 = (r0 < 0 || r0 >= HEIGHT || c0 < 0 || c0 >= WIDTH) ? 0 : this.data[r0][c0];
        double p01 = (r0 < 0 || r0 >= HEIGHT || c1 < 0 || c1 >= WIDTH) ? 0 : this.data[r0][c1];
        double p10 = (r1 < 0 || r1 >= HEIGHT || c0 < 0 || c0 >= WIDTH) ? 0 : this.data[r1][c0];
        double p11 = (r1 < 0 || r1 >= HEIGHT || c1 < 0 || c1 >= WIDTH) ? 0 : this.data[r1][c1];

        // Interpolate horizontally
        double interpTop = p00 * (1.0 - dc) + p01 * dc;
        double interpBot = p10 * (1.0 - dc) + p11 * dc;

        // Interpolate vertically
        double finalValue = interpTop * (1.0 - dr) + interpBot * dr;

        // Apply threshold
        return (finalValue > 0.5) ? 1 : 0;
    }


    /**
     * Applies a shear (angling) transformation using bilinear interpolation.
     * @param shearFactor The factor to angle the image (e.g., 0.5 or -0.3).
     */
    public void applyShear(double shearFactor) {
        int[][] destination = new int[HEIGHT][WIDTH];
        shearFactor = Math.max(-1.0, Math.min(1.0, shearFactor));
        double centerRow = (double)HEIGHT / 2.0;

        for (int destR = 0; destR < HEIGHT; destR++) {
            // --- FIX: Use floating-point math, no integer casting ---
            double shearShift = (destR - centerRow) * shearFactor;

            for (int destC = 0; destC < WIDTH; destC++) {
                // 4. Apply INVERSE shift to find floating-point source coords
                double srcC_double = destC - shearShift;
                double srcR_double = destR; // Row stays the same

                // 5. Sample using bilinear interpolation
                destination[destR][destC] = sampleBilinear(srcR_double, srcC_double);
            }
        }
        this.data = destination;
    }

    /**
     * Scales the image from the center using bilinear interpolation.
     * @param scaleX Horizontal scale factor.
     * @param scaleY Vertical scale factor.
     */
    public void applyScale(double scaleX, double scaleY) {
        if (scaleX == 0) scaleX = 0.01;
        if (scaleY == 0) scaleY = 0.01;

        int[][] destination = new int[HEIGHT][WIDTH];
        double centerX = (double)WIDTH / 2.0;
        double centerY = (double)HEIGHT / 2.0;

        for (int destR = 0; destR < HEIGHT; destR++) {
            for (int destC = 0; destC < WIDTH; destC++) {
                double relDestX = destC - centerX;
                double relDestY = destR - centerY;

                // 2. Apply inverse scale
                double relSrcX = relDestX / scaleX;
                double relSrcY = relDestY / scaleY;

                // 3. Translate back to find floating-point source coords
                double srcC_double = relSrcX + centerX;
                double srcR_double = relSrcY + centerY;

                // 4. Sample using bilinear interpolation
                destination[destR][destC] = sampleBilinear(srcR_double, srcC_double);
            }
        }
        this.data = destination;
    }


    /**
     * Applies morphological dilation (thickening).
     * A pixel becomes '1' if any of its 3x3 neighbors are '1'.
     */
    public void applyDilation() {
        int[][] destination = new int[HEIGHT][WIDTH];

        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                boolean hasInkNeighbor = false;
                // Check 3x3 neighborhood
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int checkR = r + dr;
                        int checkC = c + dc;
                        // Check bounds
                        if (checkR >= 0 && checkR < HEIGHT && checkC >= 0 && checkC < WIDTH) {
                            if (this.data[checkR][checkC] == 1) {
                                hasInkNeighbor = true;
                                break;
                            }
                        }
                    }
                    if (hasInkNeighbor) break;
                }

                if (hasInkNeighbor) {
                    destination[r][c] = 1;
                }
            }
        }
        this.data = destination;
    }

    /**
     * Applies morphological erosion (thinning).
     * A pixel remains '1' only if all of its 3x3 neighbors are '1'.
     */
    public void applyErosion() {
        int[][] destination = new int[HEIGHT][WIDTH];

        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                boolean allNeighborsAreInk = true;
                // Check 3x3 neighborhood
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int checkR = r + dr;
                        int checkC = c + dc;

                        if (checkR >= 0 && checkR < HEIGHT && checkC >= 0 && checkC < WIDTH) {
                            // If any neighbor is blank, this pixel is an edge
                            if (this.data[checkR][checkC] == 0) {
                                allNeighborsAreInk = false;
                                break;
                            }
                        } else {
                            // If neighbor is out of bounds, treat it as blank
                            allNeighborsAreInk = false;
                            break;
                        }
                    }
                    if (!allNeighborsAreInk) break;
                }

                if (allNeighborsAreInk) {
                    destination[r][c] = 1;
                }
            }
        }
        this.data = destination;
    }


    public void display() {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                // Uses the class constants for INK and BLANK
                System.out.print((data[i][j] == 1 ? INK : BLANK));
            }
            System.out.println();
        }
    }

    public String parseImagetoString(){
        StringBuilder s = new StringBuilder();
        for (int[] datum : data) {
            for (int i : datum) {
                // Uses the class constants for INK and BLANK
                s.append(i == 1 ? INK : BLANK);
            }
            s.append("\n");
        }
        return s.toString();
    }

    public void fileWrite(String outFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile, true))) {
            String imageString = parseImagetoString();
            System.out.println("--- Writing to file ---");
            System.out.println(imageString);
            System.out.println("-----------------------");
            writer.println(imageString);
        }
    }

    /**
     * A silent version of fileWrite for batch jobs, appending only the image.
     * @param outFile The full path of the file to append to.
     * @throws IOException
     */
    public void fileWriteImageOnly(String outFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile, true))) {
            writer.println(parseImagetoString());
        }
    }

    /**
     * A silent append method for training/test batch jobs.
     * Appends the image string *and* its label.
     * @param outFile The full path of the file to append to.
     * @param label The label for this image (e.g., "0" or "1").
     * @throws IOException
     */
    public void fileWriteRecord(String outFile, String label) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile, true))) {
            writer.println(parseImagetoString()); // The 16x16 grid
            writer.println(label);    // The label
            writer.println(); // A blank line for readability
        }
    }


    public void load(String path) {
        System.out.println("Image loading logic not implemented.");
    }

    public void clear() {
        this.data = new int[HEIGHT][WIDTH];
    }
}

