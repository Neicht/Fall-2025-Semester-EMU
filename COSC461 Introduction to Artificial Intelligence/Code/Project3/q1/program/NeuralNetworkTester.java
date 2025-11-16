package Project3.q1.program;

import java.io.*;
import Project3.q1.program.TerminalInterface.MenuNode;

//Program tests neural network in a specific application
public class NeuralNetworkTester {
    int numberMiddle, numberIterations, seed;
    double rate;
    String q1_directory_path;
    String input_file_name;
    String training_file_name;
    String output_file_name;
    String validation_file_name;
    NeuralNetwork network;
    TerminalInterface t;

    //Main method
    public static void main(String[] args) throws IOException {
        NeuralNetworkTester test = new NeuralNetworkTester();
        test.run();


        // variables


//        q1_directory_path = "/Users/nicholas/IdeaProjects/RemoteDevelopment/COSC461 Introduction to Artificial Intelligence/Code/Project3/q1/program/";
//        input_file_name = "Neural/inputfile";
//        output_file_name = "Neural/outputfile";
//        validation_file_name = "Neural/validationfile";
//        training_file_name = "Neural/trainingfile";
//
//        String full_input_file_path = q1_directory_path + input_file_name;
//        String full_output_file_path = q1_directory_path + output_file_name;
//        String full_validation_file_path = q1_directory_path + validation_file_name;
//        String full_training_file_path = q1_directory_path + training_file_name;


        //construct neural network

//
//        NeuralNetwork network = new NeuralNetwork();
//
//        //load training data
//        network.loadTrainingData(full_training_file_path);
//
//        //set parameters of network
//        network.setParameters(3, 1000, .5, 766701);
//
//        //train network
//        network.train();
//
//        //test network
//        network.testData(full_input_file_path, full_output_file_path);
//
//        //validate network
//        network.validate(full_validation_file_path);
    }

    // runtime framework methods

    public NeuralNetworkTester() {
        this.network = new NeuralNetwork();
        this.t = new TerminalInterface();
        initializeTerminal();
        setupFileReferences();
    }

    public void run() {
        t.start();
    }

    private void update() {

    }

    private void initializeTerminal(TerminalInterface t) {
        MenuNode networkConfig = t.addCategory("Network Configuration", t.getRootMenu());
        MenuNode trainConfig = t.addCategory("Training Configuration", t.getRootMenu());
        MenuNode fileConfig = t.addCategory("File Configuration", t.getRootMenu());


        t.addOption(t.getRootMenu(), "Quick Run", "Run the program", (iface) -> {

        });
        t.addOption(fileConfig, "Clean Up Files", "Delete generated _converted and _classified files", (iface) -> {

        });
        t.addOption(networkConfig, "Show Current Settings", "Show current settings", (iface) -> {

        });
        t.addOption(trainConfig, "Show Current Settings", "Show current settings", (iface) -> {

        });
        t.addOption(t.getRootMenu(), "Exit", "Exit the program", (iface) -> {

        });
    }

    private void setupFileReferences() {

    }

    // convenience methods
    private void setNumberMiddle(int numberMiddle) {
        this.numberMiddle = numberMiddle;
    }

    private void setNumberIterations(int numberIterations) {
        this.numberIterations = numberIterations;
    }

    private void setSeed(int seed) {
        this.seed = seed;
    }

    private void setRate(double rate) {
        this.rate = rate;
    }

    private void setQ1_directory_path(String path) {
        this.q1_directory_path = path;
    }

    private void setInput_file_name(String name) {
        this.input_file_name = name;
    }

    private void setTraining_file_name(String name) {
        this.training_file_name = name;
    }

    private void setOutput_file_name(String name) {
        this.output_file_name = name;
    }

    private void setValidation_file_name(String name) {
        this.validation_file_name = name;
    }

    private String getQ1_directory_path() {
        return this.q1_directory_path;
    }

    private String getInput_file_name() {
        return this.input_file_name;
    }

    private String getTraining_file_name() {
        return this.training_file_name;
    }

    private String getValidation_file_name() {
        return this.validation_file_name;
    }

    private String getOutput_file_name() {
        return this.output_file_name;
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
