package Project2.q2.program;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Image {

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

    public void applyJitter(double distortionRate, int magnitude) {
        int[][] destination = new int[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            System.arraycopy(this.data[i], 0, destination[i], 0, WIDTH);
        }

        magnitude = Math.max(0, magnitude);
        distortionRate = Math.max(0.0, Math.min(1.0, distortionRate));
        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                if (this.data[r][c] == 1) {
                    if (Math.random() < distortionRate) {
                        int shiftR = (int) (Math.random() * (2 * magnitude + 1)) - magnitude;
                        int shiftC = (int) (Math.random() * (2 * magnitude + 1)) - magnitude;

                        int newR = Math.max(0, Math.min(HEIGHT - 1, r + shiftR));
                        int newC = Math.max(0, Math.min(WIDTH - 1, c + shiftC));

                        destination[newR][newC] = 1;
                    }
                }
            }
        }
        this.data = destination;
    }

    private int sampleBilinear(double r, double c) {
        int r0 = (int) Math.floor(r);
        int c0 = (int) Math.floor(c);
        int r1 = r0 + 1;
        int c1 = c0 + 1;
        double dr = r - r0;
        double dc = c - c0;

        double p00 = (r0 < 0 || r0 >= HEIGHT || c0 < 0 || c0 >= WIDTH) ? 0 : this.data[r0][c0];
        double p01 = (r0 < 0 || r0 >= HEIGHT || c1 < 0 || c1 >= WIDTH) ? 0 : this.data[r0][c1];
        double p10 = (r1 < 0 || r1 >= HEIGHT || c0 < 0 || c0 >= WIDTH) ? 0 : this.data[r1][c0];
        double p11 = (r1 < 0 || r1 >= HEIGHT || c1 < 0 || c1 >= WIDTH) ? 0 : this.data[r1][c1];

        double interpTop = p00 * (1.0 - dc) + p01 * dc;
        double interpBot = p10 * (1.0 - dc) + p11 * dc;

        double finalValue = interpTop * (1.0 - dr) + interpBot * dr;

        return (finalValue > 0.5) ? 1 : 0;
    }


    public void applyShear(double shearFactor) {
        int[][] destination = new int[HEIGHT][WIDTH];
        shearFactor = Math.max(-1.0, Math.min(1.0, shearFactor));
        double centerRow = (double) HEIGHT / 2.0;

        for (int destR = 0; destR < HEIGHT; destR++) {
            double shearShift = (destR - centerRow) * shearFactor;

            for (int destC = 0; destC < WIDTH; destC++) {
                double srcC_double = destC - shearShift;
                double srcR_double = destR;
                destination[destR][destC] = sampleBilinear(srcR_double, srcC_double);
            }
        }
        this.data = destination;
    }

    public void applyScale(double scaleX, double scaleY) {
        if (scaleX == 0) scaleX = 0.01;
        if (scaleY == 0) scaleY = 0.01;

        int[][] destination = new int[HEIGHT][WIDTH];
        double centerX = (double) WIDTH / 2.0;
        double centerY = (double) HEIGHT / 2.0;

        for (int destR = 0; destR < HEIGHT; destR++) {
            for (int destC = 0; destC < WIDTH; destC++) {
                double relDestX = destC - centerX;
                double relDestY = destR - centerY;


                double relSrcX = relDestX / scaleX;
                double relSrcY = relDestY / scaleY;
                double srcC_double = relSrcX + centerX;
                double srcR_double = relSrcY + centerY;

                destination[destR][destC] = sampleBilinear(srcR_double, srcC_double);
            }
        }
        this.data = destination;
    }


    public void applyDilation() {
        int[][] destination = new int[HEIGHT][WIDTH];

        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                boolean hasInkNeighbor = false;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int checkR = r + dr;
                        int checkC = c + dc;
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

    public void applyErosion() {
        int[][] destination = new int[HEIGHT][WIDTH];

        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                boolean allNeighborsAreInk = true;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int checkR = r + dr;
                        int checkC = c + dc;

                        if (checkR >= 0 && checkR < HEIGHT && checkC >= 0 && checkC < WIDTH) {
                            if (this.data[checkR][checkC] == 0) {
                                allNeighborsAreInk = false;
                                break;
                            }
                        } else {
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
                System.out.print((data[i][j] == 1 ? INK : BLANK));
            }
            System.out.println();
        }
    }

    public String parseImagetoString() {
        StringBuilder s = new StringBuilder();
        for (int[] datum : data) {
            for (int i : datum) {
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

    public void fileWriteImageOnly(String outFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile, true))) {
            writer.println(parseImagetoString());
        }
    }

    public void fileWriteRecord(String outFile, String label) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile, true))) {
            writer.println(parseImagetoString());
            writer.println(label);
            writer.println();
        }
    }


    public void load(String path) {
        System.out.println("Image loading logic not implemented.");
    }

    public void clear() {
        this.data = new int[HEIGHT][WIDTH];
    }
}

