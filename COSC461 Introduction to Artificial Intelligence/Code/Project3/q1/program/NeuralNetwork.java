package Project3.q1.program;

import java.io.*;
import java.util.*;

//Neural network
public class NeuralNetwork {
    /*************************************************************************/

    //Record class
    private class Record {
        private double[] input;     //inputs of record  
        private double[] output;    //outputs of record        

        //Constructor of Record
        private Record(double[] input, double[] output) {
            this.input = input;     //set inputs
            this.output = output;   //set outputs
        }
    }

    /*************************************************************************/

    private ArrayList<Record> records;   //list of training records
    private int numberRecords;           //number of training records 

    private int numberInputs;            //number of inputs 
    private int numberOutputs;           //number of outputs

    private int numberMiddle;            //number of hidden nodes
    private int numberIterations;        //number of iterations
    private double rate;                 //learning rate

    private double[] input;              //inputs
    private double[] middle;             //outputs at hidden nodes
    private double[] output;             //outputs at output nodes

    private double[] errorMiddle;        //errors at hidden nodes
    private double[] errorOut;           //errors at output nodes

    private double[] thetaMiddle;        //thetas at hidden nodes
    private double[] thetaOut;           //threats! at output nodes

    private double[][] matrixMiddle;     //weights between input/hidden nodes 
    private double[][] matrixOut;        //weights between hidden/output nodes

    double[] inputMin;
    double[] inputMax;
    double[] outputMin;
    double[] outputMax;

    /*************************************************************************/

    //Constructor of neural network
    public NeuralNetwork() {
        //parameters are zero
        numberRecords = 0;
        numberInputs = 0;
        numberOutputs = 0;
        numberMiddle = 0;
        numberIterations = 0;
        rate = 0;

        //arrays are empty
        records = null;
        input = null;
        middle = null;
        output = null;
        errorMiddle = null;
        errorOut = null;
        thetaMiddle = null;
        thetaOut = null;
        matrixMiddle = null;
        matrixOut = null;
    }

    /*************************************************************************/

    //Method loads training records from training file
    public void loadTrainingData(String trainingFile) throws IOException {
        Scanner inFile = new Scanner(new File(trainingFile));

        numberRecords = inFile.nextInt();
        numberInputs = inFile.nextInt();
        numberOutputs = inFile.nextInt();

        // Initialize min/max arrays
        inputMin = new double[numberInputs];
        inputMax = new double[numberInputs];
        outputMin = new double[numberOutputs];
        outputMax = new double[numberOutputs];

        // populate min/max arrays
        for (int i = 0; i < numberInputs; i++) {
            inputMin[i] = Double.POSITIVE_INFINITY;
            inputMax[i] = Double.NEGATIVE_INFINITY;
        }
        for (int i = 0; i < numberOutputs; i++) {
            outputMin[i] = Double.POSITIVE_INFINITY;
            outputMax[i] = Double.NEGATIVE_INFINITY;
        }

        // create temporary array of records
        ArrayList<Record> tempRecords = new ArrayList<Record>();
        // read records and find min/max values
        // loop: {
        // for i : number records;
        // create double[numberInputs] and double[numberOutputs];
//             {
//             for j : numberInputs; for j : numberOutputs;
//             val = inFile.nextDouble(); if (val < min) min = val; if (val > max) max = val; for both input and output
//             separately
//             }
        // add record to tempRecords
        // }

        for (int i = 0; i < numberRecords; i++) {
            double[] input = new double[numberInputs];
            for (int j = 0; j < numberInputs; j++) {
                double val = inFile.nextDouble();
                input[j] = val;
                if (val < inputMin[j]) inputMin[j] = val;
                if (val > inputMax[j]) inputMax[j] = val;
            }

            double[] output = new double[numberOutputs];
            for (int j = 0; j < numberOutputs; j++) {
                double val = inFile.nextDouble();
                output[j] = val;
                if (val < outputMin[j]) outputMin[j] = val;
                if (val > outputMax[j]) outputMax[j] = val;
            }

            tempRecords.add(new Record(input, output));
        }

        // close infile and create new record arraylist
        inFile.close();
        records = new ArrayList<Record>();
        // for each record in tempRecords
        // new double of inputs and outputs; normalize rawRecord.input and rawRecord.output
        // add normalized record information to records
        for (Record rawRecord : tempRecords) {
            double[] normalizedInput = normalizeInput(rawRecord.input);
            double[] normalizedOutput = normalizeOutput(rawRecord.output);

            records.add(new Record(normalizedInput, normalizedOutput));
        }
    }

    //normalize given a value, with min and max
    private double normalize(double x, double min, double max) {
        double range = max - min;
        // catch errors; maybe drop for obvious error handling
        if (range == 0) {
            return 0;
        }
        return (x - min) / range;
    }

    // de-normalize
    private double deNormalize(double y, double min, double max) {
        double range = max - min;
        return y * range + min;
    }

    // normalize input array
    private double[] normalizeInput(double[] rawInput) {
        double[] normalizedInput = new double[numberInputs];
        for (int i = 0; i < numberInputs; i++) {
            normalizedInput[i] = normalize(rawInput[i], inputMin[i], inputMax[i]);
        }
        return normalizedInput;
    }

    // normalize output array
    private double[] normalizeOutput(double[] rawOutput) {
        double[] normalizedOutput = new double[numberOutputs];
        for (int i = 0; i < numberOutputs; i++) {
            normalizedOutput[i] = normalize(rawOutput[i], outputMin[i], outputMax[i]);
        }
        return normalizedOutput;
    }

    // de-normalize output array
    private double[] deNormalizeOutput(double[] normalizedOutput) {
        double[] rawOutput = new double[numberOutputs];
        for (int i = 0; i < numberOutputs; i++) {
            rawOutput[i] = deNormalize(normalizedOutput[i], outputMin[i], outputMax[i]);
        }
        return rawOutput;
    }

    /*************************************************************************/

    //Method sets parameters of neural network
    public void setParameters(int numberMiddle, int numberIterations, double rate,
                              int seed) {
        //set hidden nodes, iterations, rate
        this.numberMiddle = numberMiddle;
        this.numberIterations = numberIterations;
        this.rate = rate;

        //initialize random number generation
        Random rand = new Random(seed);

        //create input/output arrays
        input = new double[numberInputs];
        middle = new double[numberMiddle];
        output = new double[numberOutputs];

        //create error arrays
        errorMiddle = new double[numberMiddle];
        errorOut = new double[numberOutputs];

        //initialize thetas at hidden nodes
        thetaMiddle = new double[numberMiddle];
        for (int i = 0; i < numberMiddle; i++)
            thetaMiddle[i] = 2 * rand.nextDouble() - 1;

        //initialize thetas at output nodes
        thetaOut = new double[numberOutputs];
        for (int i = 0; i < numberOutputs; i++)
            thetaOut[i] = 2 * rand.nextDouble() - 1;

        //initialize weights between input/hidden nodes
        matrixMiddle = new double[numberInputs][numberMiddle];
        for (int i = 0; i < numberInputs; i++)
            for (int j = 0; j < numberMiddle; j++)
                matrixMiddle[i][j] = 2 * rand.nextDouble() - 1;

        //initialize weights between hidden/output nodes
        matrixOut = new double[numberMiddle][numberOutputs];
        for (int i = 0; i < numberMiddle; i++)
            for (int j = 0; j < numberOutputs; j++)
                matrixOut[i][j] = 2 * rand.nextDouble() - 1;
    }

    /*************************************************************************/

    //Method trains neural network
    public void train() {
        //repeat iteration number of times
        for (int i = 0; i < numberIterations; i++)
            //for each training record
            for (int j = 0; j < numberRecords; j++) {
                //calculate input/output
                forwardCalculation(records.get(j).input);

                //compute errors, update weights/thetas
                backwardCalculation(records.get(j).output);
            }
    }

    /*************************************************************************/

    //Method performs forward pass - computes input/output
    private void forwardCalculation(double[] trainingInput) {
        //feed inputs of record
        for (int i = 0; i < numberInputs; i++)
            input[i] = trainingInput[i];

        //for each hidden node
        for (int i = 0; i < numberMiddle; i++) {
            double sum = 0;

            //compute input at hidden node
            for (int j = 0; j < numberInputs; j++)
                sum += input[j] * matrixMiddle[j][i];

            //add theta
            sum += thetaMiddle[i];

            //compute output at hidden node
            middle[i] = 1 / (1 + Math.exp(-sum));
        }

        //for each output node
        for (int i = 0; i < numberOutputs; i++) {
            double sum = 0;

            //compute input at output node
            for (int j = 0; j < numberMiddle; j++)
                sum += middle[j] * matrixOut[j][i];

            //add theta
            sum += thetaOut[i];

            //compute output at output node
            output[i] = 1 / (1 + Math.exp(-sum));
        }
    }

    /*************************************************************************/

    //Method performs backward pass - computes errors, updates weights/thetas
    private void backwardCalculation(double[] trainingOutput) {
        //compute error at each output node
        for (int i = 0; i < numberOutputs; i++)
            errorOut[i] = output[i] * (1 - output[i]) * (trainingOutput[i] - output[i]);

        //compute error at each hidden node
        for (int i = 0; i < numberMiddle; i++) {
            double sum = 0;

            for (int j = 0; j < numberOutputs; j++)
                sum += matrixOut[i][j] * errorOut[j];

            errorMiddle[i] = middle[i] * (1 - middle[i]) * sum;
        }

        //update weights between hidden/output nodes
        for (int i = 0; i < numberMiddle; i++)
            for (int j = 0; j < numberOutputs; j++)
                matrixOut[i][j] += rate * middle[i] * errorOut[j];

        //update weights between input/hidden nodes
        for (int i = 0; i < numberInputs; i++)
            for (int j = 0; j < numberMiddle; j++)
                matrixMiddle[i][j] += rate * input[i] * errorMiddle[j];

        //update thetas at output nodes
        for (int i = 0; i < numberOutputs; i++)
            thetaOut[i] += rate * errorOut[i];

        //update thetas at hidden nodes
        for (int i = 0; i < numberMiddle; i++)
            thetaMiddle[i] += rate * errorMiddle[i];
    }

    /*************************************************************************/

    //Method computes output of an input
    private double[] test(double[] rawInput) {
        // Normalize the raw input before feeding it to the network
        double[] normalizedInput = normalizeInput(rawInput);

        //forward pass normalized input
        forwardCalculation(normalizedInput);

        //return normalized output produced
        return output;
    }

    /*************************************************************************/

    //Method reads inputs from input file, computes outputs, and writes outputs
    //to output file
    public void testData(String inputFile, String outputFile) throws IOException {
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        int numberRecords = inFile.nextInt();
        if (inFile.hasNextInt()) {
            inFile.nextInt();
        }

        for (int i = 0; i < numberRecords; i++) {
            double[] rawInput = new double[numberInputs];

            for (int j = 0; j < numberInputs; j++)
                rawInput[j] = inFile.nextDouble();

            double[] normalizedOutput = test(rawInput);
            double[] rawOutput = deNormalizeOutput(normalizedOutput);

            for (int j = 0; j < numberOutputs; j++)
                outFile.print(rawOutput[j] + " ");
            outFile.println();
        }

        inFile.close();
        outFile.close();
    }

    /*************************************************************************/

    //Method validates the network using the data from a file
    public double validate(String validationFile) throws IOException {
        Scanner inFile = new Scanner(new File(validationFile));

        int numberRecords = inFile.nextInt();
        double sumError = 0;

        for (int i = 0; i < numberRecords; i++) {
            // read raw inputs
            double[] rawInput = new double[numberInputs];
            for (int j = 0; j < numberInputs; j++)
                rawInput[j] = inFile.nextDouble();

            // read raw actual outputs
            double[] actualOutput = new double[numberOutputs];
            for (int j = 0; j < numberOutputs; j++)
                actualOutput[j] = inFile.nextDouble();

            // find normalized predicted outputs
            double[] predictedNormalizedOutput = test(rawInput);

            // de-normalize predicted outputs
            double[] predictedOutput = deNormalizeOutput(predictedNormalizedOutput);

            // find error between raw actual and raw predicted outputs
            sumError += computeError(actualOutput, predictedOutput);

        }
        inFile.close();
        return sumError / numberRecords;
    }

    /*************************************************************************/

    //Method finds root mean square error between actual and predicted output
    private double computeError(double[] actualOutput, double[] predictedOutput) {
        double error = 0;

        //sum of squares of errors
        for (int i = 0; i < actualOutput.length; i++)
            error += Math.pow(actualOutput[i] - predictedOutput[i], 2);

        //root mean square error
        return Math.sqrt(error / actualOutput.length);
    }

    /*************************************************************************/
}