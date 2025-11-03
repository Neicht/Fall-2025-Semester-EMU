import java.io.*;
import java.util.*;

//Bayes classifier
public class Bayes
{
    /*************************************************************************/

    //Record class (inner class)
    private class Record 
    {
        private int[] attributes;            //attributes of record     
        private int className;               //class of record

        //Constructor of record
        private Record(int[] attributes, int className)
        {
            this.attributes = attributes;    //set attributes 
            this.className = className;      //set class
        }
    }

    /*************************************************************************/

    private int numberRecords;               //number of records   
    private int numberAttributes;            //number of attributes   
    private int numberClasses;               //number of classes
    private ArrayList<Record> records;       //list of records
    private int[] attributeValues;           //number of attribute values
    double[][][] table;                      //conditional probabilities
    double[] classTable;                     //class probabilities

    /*************************************************************************/

    //Constructor of Bayes
    public Bayes()
    {         
        //initial data is empty
        numberRecords = 0;                   
        numberAttributes = 0;                
        numberClasses = 0;
        records = null;                                   
        attributeValues = null;              
        table = null;                        
        classTable = null;                                      
    }                                       

    /*************************************************************************/

    //Method loads data from training file
    public void loadTrainingData(String trainingFile) throws IOException
    {
         Scanner inFile = new Scanner(new File(trainingFile));

         //read number of records, attributes, classes
         numberRecords = inFile.nextInt();    
         numberAttributes = inFile.nextInt();        
         numberClasses = inFile.nextInt();

         //read number of attribute values
         attributeValues = new int[numberAttributes];
         for (int i = 0; i < numberAttributes; i++)
             attributeValues[i] = inFile.nextInt();

         //list of records
         records = new ArrayList<Record>();        

         //read each record
         for (int i = 0; i < numberRecords; i++)    
         {
             //create attribute array
             int[] attributeArray = new int[numberAttributes]; 
                                     
             //read attributes
             for (int j = 0; j < numberAttributes; j++) 
                  attributeArray[j] = inFile.nextInt();
 
             //read class
             int className = inFile.nextInt();

             //create record
             Record record = new Record(attributeArray, className);

             //add record to list of records
             records.add(record);
         }

         inFile.close();
    }

    /*************************************************************************/

    //Method computes probability values necessary for classification
    public void computeProbability()
    {
         //compute class probabilities
         computeClassTable();   

         //compute conditional probabilities
         computeTable();
    }

    /*************************************************************************/

    //Method computes class probabilities
    private void computeClassTable()
    {
        classTable = new double[numberClasses];

        //initialize class frequencies
        for (int i = 0; i < numberClasses; i++)
            classTable[i] = 0;

        //compute class frequencies
        for (int i = 0; i < numberRecords; i++)
            classTable[records.get(i).className-1] += 1;

        //normalize class frequencies
        for (int i = 0; i < numberClasses; i++)
            classTable[i] /= numberRecords;
    }

    /*************************************************************************/

    //Method computes conditional probabilities
    private void computeTable()
    {
        //array to store conditional probabilites
        table = new double[numberAttributes][][];

        //compute conditional probabilities of each attribute
        for (int i = 0; i < numberAttributes; i++)
            compute(i+1);
    }

    /*************************************************************************/

    //Method computes conditional probabilities of an attribute
    private void compute(int attribute)
    {
        //find number of attribute values
        int attributeValues = this.attributeValues[attribute-1];

        //create array to hold conditional probabilities
        table[attribute-1] = new double[numberClasses][attributeValues];

        //initialize conditional probabilities
        for (int i = 0; i < numberClasses; i++)
            for (int j = 0; j < attributeValues; j++)
                table[attribute-1][i][j] = 0;

        //compute class-attribute frequencies
        for (int k = 0; k < numberRecords; k++)
        {
            int i = records.get(k).className - 1;
            int j = records.get(k).attributes[attribute-1] - 1;
            table[attribute-1][i][j] += 1;
        }

        //compute conditional probabilities using laplace correction
        for (int i = 0; i < numberClasses; i++)
            for (int j = 0; j < attributeValues; j++)
            {
                double value = (table[attribute-1][i][j] + 1)/
                               (classTable[i]*numberRecords + attributeValues);
                table[attribute-1][i][j] = value;
            }
    }

    /*************************************************************************/

    //Method computes conditional probability of a class for given attributes
    private double findProbability(int className, int[] attributes)
    {
        double value;
        double product = 1;

        //find product of conditional probabilities stored in table
        for (int i = 0; i < numberAttributes; i++)
        {
            value = table[i][className-1][attributes[i]-1];
            product = product*value;
        }

        //multiply product and class probability 
        return product*classTable[className-1];
    }

    /*************************************************************************/

    //Method classifies an attribute
    private int classify(int[] attributes)
    {
        double maxProbability = 0;
        int maxClass = 0;

        //for each class
        for (int i = 0; i < numberClasses; i++)
        {
            //find conditional probability of class given the attribute
            double probability = findProbability(i+1, attributes);

            //choose the class with the maximum probability
            if (probability > maxProbability)
            {
                maxProbability = probability;
                maxClass = i;
            }
        }

        //return maximum class
        return maxClass + 1;
    }

    /*************************************************************************/

    //Method reads test records from test file and writes classified records
    //to classified file
    public void classifyData(String testFile, String classifiedFile) 
    throws IOException
    {
         Scanner inFile = new Scanner(new File(testFile));
         PrintWriter outFile = new PrintWriter(new FileWriter(classifiedFile));

         //read number of records
         int numberRecords = inFile.nextInt();

         //write number of records
         outFile.println(numberRecords);

         //for each record
         for (int i = 0; i < numberRecords; i++)
         {
             //create attribute array
             int[] attributeArray = new int[numberAttributes];

             //read attributes
             for (int j = 0; j < numberAttributes; j++)
                  attributeArray[j] = inFile.nextInt();

             //find class of attributes
             int className = classify(attributeArray);

             //write class name
             outFile.println(className);
         }

         inFile.close();
         outFile.close();
    }

    /*************************************************************************/

    //Method validates classifier using validation file and displays
    //error rate
    public void validate(String validationFile) throws IOException
    {
         Scanner inFile = new Scanner(new File(validationFile));

         //read number of records
         int numberRecords = inFile.nextInt();

         //initially zero errors
         int numberErrors = 0;

         //for each record
         for (int i = 0; i < numberRecords; i++)
         {
             //create attribute array
             int[] attributeArray = new int[numberAttributes];

             //read attributes
             for (int j = 0; j < numberAttributes; j++)
                  attributeArray[j] = inFile.nextInt();

             //read actual class
             int actualClass = inFile.nextInt();

             //find class predicted by classifier
             int predictedClass = classify(attributeArray);

             //error if predicted and actual classes do not match
             if (predictedClass != actualClass)
                numberErrors += 1;
         }

         //find and print error rate 
         double errorRate = 100.0*numberErrors/numberRecords;
         System.out.println("validation error: " + errorRate);

         inFile.close();
    }

    /*************************************************************************/
}