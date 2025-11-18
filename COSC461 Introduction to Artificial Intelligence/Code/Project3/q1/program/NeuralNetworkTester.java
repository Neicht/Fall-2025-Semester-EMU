package Project3.q1.program;

import java.io.*;

public class NeuralNetworkTester {
    int numberMiddle, numberIterations, seed;
    double rate;
    String input_file;
    String training_file;
    String output_file;
    String validation_file;
    NeuralNetwork network;
    TerminalInterface t;
    boolean debug = false;

    public static void main(String[] args) throws IOException {
        NeuralNetworkTester test = new NeuralNetworkTester();
        test.run();
    }

    // runtime framework methods
    public NeuralNetworkTester() {
        this.network = new NeuralNetwork();
        this.t = new TerminalInterface();

        t.pushState();
        t.setLineWidth(46).setLineStyle('=');
        t.printTitle("Neural Network Tester");
        t.popState();

        initializeTerminal(t);
    }

    public void run() {
        t.pushState();
        t.setLineWidth(46).setLineStyle('-').setStatLabelWidth(20);
        t.start();
        t.popState();
    }

    private void initializeTerminal(TerminalInterface t) {
        t.addOption(t.getRootMenu(), "Run Standard Test", "Run Data1 & Data2 with debug parameters", (iface) -> {
            // refresh network
            this.network = new NeuralNetwork();
            processInput(t);
        });
        t.addOption(t.getRootMenu(), "Tune Parameters", "Find best parameters", (iface) -> {
            try {
                tune(t);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.addOption(t.getRootMenu(), "Show Current Settings", "Display network parameters", (iface) -> {
            iface.printHeader("Current Settings");
            iface.printStat("Seed", String.valueOf(getSeed()));
            iface.printStat("Learning Rate", String.valueOf(getRate()));
            iface.printStat("Number of Iterations", String.valueOf(getNumberIterations()));
            iface.printStat("Number of Middle Nodes", String.valueOf(getNumberMiddle()));
            iface.printLine();
        });
        t.addOption(t.getRootMenu(), "Info", "Display information about the program", (iface) -> {
            iface.printHeader("Info");
            iface.printStat("Author", "Nicholas Gawenda");
            iface.printStat("Date", "11/16/2025");
            iface.out("");
            iface.printBody("The learning rate, number of iterations, and number of middle nodes are determined by iteratively searching through X (X=z*y; z-number of size-y-arrays) different preset values. These values can be further developed for greater ambiguity by stepping by r in a range x,y. The seed is processed as 0 unless otherwise specified by the user.");
            iface.printLine();
        });
        t.addOption(t.getRootMenu(), "Exit Program", "Close the application", TerminalInterface::stop);
    }

    private void tune(TerminalInterface t) throws IOException {
        t.tic();

        t.printHeader("Tuning Data");

        setupFileReferences(t);
        String trainingPath = getTraining_file();
        String validationPath = getValidation_file();
        int seed = 0;


//        double[] ratesToTest = {0.5, 0.1, 0.05};
//        int[] iterationsToTest = {1000, 2000, 5000};
//        int[] middlesToTest = {5, 10, 20};

        double[] ratesToTest = t.generateDoubleArray(10, 0.1, 0.0);
        int[] iterationsToTest = t.generateIntArray(10, 1000, 5000);
        int[] middlesToTest = t.generateIntArray(10, 5, 5);

        double bestError = Double.POSITIVE_INFINITY;
        double bestRate = 0;
        int bestIterations = 0;
        int bestMiddle = 0;
        int testCount = 0;
        int totalTests = ratesToTest.length * iterationsToTest.length * middlesToTest.length;
        double[] values = new double[totalTests];

        for (double rate : ratesToTest) {
            // learning rate seems to be the most critical parameter, showing greater results as it gets higher
            // is this due to overfitting? Or is it actually getting better?
            for (int iterations : iterationsToTest) {
                // no determined correlation for number of iterations. too hard to test within reason.
                for (int middle : middlesToTest) {
                    // the number of middle nodes seems to prefer being about 1/5 the size of the training set

                    testCount++;

                    NeuralNetwork testNetwork = new NeuralNetwork();
                    testNetwork.loadTrainingData(trainingPath);
                    testNetwork.setParameters(middle, iterations, rate, seed);
                    testNetwork.train();

                    double currentError = testNetwork.validate(validationPath);
                    if (currentError < bestError) {
                        bestError = currentError;
                        bestRate = rate;
                        this.rate = rate;
                        bestIterations = iterations;
                        this.numberIterations = iterations;
                        bestMiddle = middle;
                        this.numberMiddle = middle;
                    }
                    values[testCount - 1] = currentError;
                    t.printProgress(testCount, totalTests);
                }
            }
        }
        long toc = t.getToc();
        t.printHeader("Generating Histogram");
        t.printHistogram(values);
        t.printHeader("Tuning Results");
        String minError = String.format("%.4f", bestError);
        t.printStat("Min Error", minError);
        t.printStat("Learn Rate", String.valueOf(bestRate));
        t.printStat("Iterations", String.valueOf(bestIterations));
        t.printStat("Mid Nodes", String.valueOf(bestMiddle));
        t.printStat("Time Taken", toc + "ms");
        t.printLine();

        NeuralNetwork tempNetwork = new NeuralNetwork();
        tempNetwork.loadTrainingData(trainingPath);
        tempNetwork.setParameters(bestMiddle, bestIterations, bestRate, seed);
        tempNetwork.train();
        tempNetwork.testData(getInput_file(), getOutput_file());
        tempNetwork.validate(getValidation_file());

    }

    private void processInput(TerminalInterface t) {
        setupFileReferences(t);
        deployNetwork(t);
    }

    private void deployNetwork(TerminalInterface t) {
        try {
            network.loadTrainingData(getTraining_file());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (this.rate == 0 && this.numberIterations == 0) {
            setupNetworkParameters(t);
        } else {
            setParameters(numberMiddle, numberIterations, rate, seed);
        }

        network.train();

        try {
            network.testData(getInput_file(), getOutput_file());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            double error = network.validate(getValidation_file());
            String dataSet = (this.training_file != null && this.training_file.contains("Data1") ? "Data1" : "Data2");
            t.out(dataSet + " Validation Error: " + String.format("%.8f", error));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void setupFileReferences(TerminalInterface t) {
        String trainingPath, validationPath, inputPath, outputPath;
        trainingPath = t.inString("Enter training file path: ");
        validationPath = t.inString("Enter validation file path: ");
        inputPath = t.inString("Enter input file path: ");
        outputPath = t.inString("Enter output file path: ");
        setTraining_file(trainingPath);
        setValidation_filee(validationPath);
        setInput_file(inputPath);
        setOutput_file(outputPath);
    }

    private void setupNetworkParameters(TerminalInterface t) {
        int numberMiddle, numberIterations, seed;
        double rate;
        numberMiddle = t.inInt("Enter number of middle nodes: ");
        numberIterations = t.inInt("Enter number of iterations: ");
        rate = t.inDouble("Enter learning rate: ");
        seed = t.inInt("Enter seed: ");

        setParameters(numberMiddle, numberIterations, rate, seed);
    }

    private void setParameters(int numberMiddle, int numberIterations, double rate, int seed) {
        this.numberMiddle = numberMiddle;
        this.numberIterations = numberIterations;
        this.rate = rate;
        this.seed = seed;
        this.network.setParameters(numberMiddle, numberIterations, rate, seed);
    }

    // a million other helper methods
    private void setInput_file(String path) {
        this.input_file = path;
    }

    private void setTraining_file(String path) {
        this.training_file = path;
    }

    private void setOutput_file(String path) {
        this.output_file = path;
    }

    private void setValidation_filee(String path) {
        this.validation_file = path;
    }

    private String getTraining_file() {
        return this.training_file;
    }

    private String getValidation_file() {
        return this.validation_file;
    }

    private String getOutput_file() {
        return this.output_file;
    }

    private String getInput_file() {
        return this.input_file;
    }

    private int getNumberMiddle() {
        return this.numberMiddle;
    }

    private int getNumberIterations() {
        return this.numberIterations;
    }

    private int getSeed() {
        return this.seed;
    }

    private double getRate() {
        return this.rate;
    }
}