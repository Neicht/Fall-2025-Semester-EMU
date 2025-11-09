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
        this.numberIterations = 1000;
        this.seed = 1;
        this.bestSSE = Double.MAX_VALUE;

    }

    public void setInputFile() {
        this.inputFile = directoryPath + "program/Data/" + t.inString("Enter input file name (e.g., file1.txt):");
    }

    public void setDirectoryPath() {
        this.directoryPath = t.inString("Enter absolute directory path to Q4 (end with /):");
    }

    public void setOutputFile() {
        this.outputFile = directoryPath + "output/" + t.inString("Enter output file name (e.g., file2.txt):");
    }

    public void initializeOptions() {
        t.addOption(t.getRootMenu(), "Simple Run", "Simply run the program", t -> {
            setDirectory(t);
            setInputFile(t);
            setOutputFile(t);
            findSSE(t);
            closeApp(t);
        });
        TerminalInterface.MenuNode fileMenu = t.addCategory("File Settings", t.getRootMenu());
        t.addOption(fileMenu, "Change Directory", "Change the current filepath to the directory", this::setDirectory);
        t.addOption(fileMenu, "Change Input File", "Change the current input file name", this::setInputFile);
        t.addOption(fileMenu, "Change Output File", "Change the current output file name", this::setOutputFile);
        t.addOption(t.getRootMenu(), "Exit", "Terminate the program", KmeansTester::closeApp);
    }

    private static void closeApp(TerminalInterface t) {
        t.out("Goodbye!");
        System.exit(0);
    }

    private void findSSE(TerminalInterface t) {
        try {
            //iface.out("Error rate: " + errors + "/" + numberRecords + " = " + (errors / (float) numberRecords) * 100 + "%");
            this.bestSSE = Double.MAX_VALUE;
            double currentSSE = simpleRun();
            int bestClusterSize = 0;
            this.seed = 1;
            numberClusters = 10;
            int clusterRange = numberRecords;
            while (numberClusters <= clusterRange) {
                this.seed = numberClusters;
                currentSSE = simpleRun();
                t.out("SSE: " + currentSSE + " with " + numberClusters + " clusters" + " (seed: " + this.seed + ")");
                if (currentSSE < bestSSE) {
                    if (bestSSE-currentSSE >= 0.2) {
                        //System.out.println(bestSSE - currentSSE);
                        bestSSE = currentSSE;
                        bestClusterSize = numberClusters;
                    }
                }
                numberClusters += 10;
            }
            numberClusters = bestClusterSize;
            this.seed = bestClusterSize;
            simpleRun();
            t.out("Best SSE: " + bestSSE + " with " + bestClusterSize + " clusters" + " (seed: " + this.seed + ")");

            // t.out("Best SSE: " + sseList.stream().min(Double::compareTo).get() + " with " + numberClusters + " clusters");
        } catch (IOException e) {
        }
    }

    private void setOutputFile(TerminalInterface t) {
        setOutputFile();
        t.out("Output file set to: " + this.outputFile);
    }

    private void setInputFile(TerminalInterface t) {
        setInputFile();
        t.out("Input file set to: " + this.inputFile);
    }

    private void setDirectory(TerminalInterface t) {
        setDirectoryPath();
        if (!this.directoryPath.endsWith("/")) {
            this.directoryPath += "/";
        }
        t.out("Directory set to: " + this.directoryPath);
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
