package Project2.q4.program;

import java.io.*;

//Program tests k-means clustering in a specific application
public class KmeansTester {
    Kmeans k;
    TerminalInterface t;
    String directoryPath;
    String inputFile;
    String outputFile;
    int numberClusters;
    int numberIterations;
    int seed;
    double bestSSE;
    int numberRecords;

    //Main method
    public double simpleRun() throws IOException {
        //create clustering object
        k = new Kmeans();

        //load records
        numberRecords = k.load(inputFile);

        //set parameters
        k.setParameters(numberClusters, numberIterations, seed);

        //perform clustering
        k.cluster();

        //display records and clusters
        k.display(outputFile);

        return k.calculateSSE();
    }

    public KmeansTester() {
        this.t = new TerminalInterface();
        // set defaults
        this.directoryPath = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project2/q4/program/Data/";
        this.inputFile = directoryPath + "file7.txt";
        this.outputFile = directoryPath + "outputfile";
        this.numberClusters = 3;
        this.numberIterations = 100;
        this.seed = 1;
        this.bestSSE = Double.MAX_VALUE;

    }

    public void setInputFile() {
        this.inputFile = directoryPath + t.inString("Enter input file name (e.g., file1.txt):");
    }

    public void setDirectoryPath() {
        this.directoryPath = t.inString("Enter directory path (end with /):");
    }

    public void setOutputFile() {
        this.outputFile = directoryPath + t.inString("Enter output file name (e.g., file2.txt):");
    }

    public void initializeOptions() {
//        TerminalInterface.MenuNode runMenu = t.addCategory("Run", t.getRootMenu());
        TerminalInterface.MenuNode programMenu = t.addCategory("Program", t.getRootMenu());
        TerminalInterface.MenuNode fileMenu = t.addCategory("File Settings", t.getRootMenu());
        TerminalInterface.MenuNode statusMenu = t.addCategory("Status", t.getRootMenu());
        t.addOption(fileMenu, "Change Directory", "Change the current filepath to the directory", t -> {
            setDirectoryPath();
            if (!this.directoryPath.endsWith("/")) {
                this.directoryPath += "/";
            }
            t.out("Directory set to: " + this.directoryPath);
        });
        t.addOption(fileMenu, "Change Input File", "Change the current input file name", t -> {
            setInputFile();
            t.out("Input file set to: " + this.inputFile);
        });
        t.addOption(fileMenu, "Change Output File", "Change the current output file name", t -> {
            setOutputFile();
            t.out("Output file set to: " + this.outputFile);
        });
        t.addOption(t.getRootMenu(), "Simple Run", "Simply run the program", t -> {
            try {
                simpleRun();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.addOption(t.getRootMenu(), "Find SSE", "Find the best SSE", t -> {
            try {
                //iface.out("Error rate: " + errors + "/" + numberRecords + " = " + (errors / (float) numberRecords) * 100 + "%");
                double currentSSE = simpleRun();
                int bestClusterSize = 0;
                numberClusters = 1;
                while (numberClusters <= numberRecords) {
                    currentSSE = simpleRun();
                    t.out("SSE: " + currentSSE + " with " + numberClusters + " clusters");
                    numberClusters += 1;
                    if (currentSSE < bestSSE) {
                        if (currentSSE / bestSSE < 0.01) {
                            bestSSE = currentSSE;
                            bestClusterSize = numberClusters;
                        }
                    }
                }
                t.out("Best SSE: " + bestSSE + " with " + bestClusterSize + " clusters");
                numberClusters = bestClusterSize;
                simpleRun();
                // t.out("Best SSE: " + sseList.stream().min(Double::compareTo).get() + " with " + numberClusters + " clusters");
            } catch (IOException e) {
            }
        });
        t.addOption(t.getRootMenu(), "Exit", "Terminate the program", t -> {
            System.exit(0);
        });
    }

    public void run() {
        initializeOptions();
        t.start();
    }

    void main() {
        KmeansTester tester = new KmeansTester();
        tester.run();
    }

}
