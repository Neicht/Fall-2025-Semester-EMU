package Project2.q1.program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

//Nearest neighbor classifier
public class NearestNeighbor
{
    /*************************************************************************/

    //Record class (inner class)
    private class Record 
    {
        private double[] attributes;         //attributes of record      
        private int className;               //class of record

        //Constructor of Record
        private Record(double[] attributes, int className)
        {
            this.attributes = attributes;    //set attributes 
            this.className = className;      //set class
        }
    }

    /*************************************************************************/

    private int numberRecords;               //number of training records   
    private int numberAttributes;            //number of attributes   
    private int numberClasses;               //number of classes
    private int numberNeighbors;             //number of nearest neighbors
    private ArrayList<Record> records;       //list of training records

    /*************************************************************************/

    //Constructor of NearestNeighbor
    public NearestNeighbor()
    {         
        //initial data is empty           
        numberRecords = 0;      
        numberAttributes = 0;
        numberClasses = 0;
        numberNeighbors = 0; 
        records = null;                        
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

         //create empty list of records
         records = new ArrayList<Record>();        

         //for each record
         for (int i = 0; i < numberRecords; i++)    
         {
             //create attribute array
             double[] attributeArray = new double[numberAttributes]; 
                                     
             //read attribute values
             for (int j = 0; j < numberAttributes; j++)   
                  attributeArray[j] = inFile.nextDouble();  
 
             //read class name
             int className = inFile.nextInt();

             //create record
             Record record = new Record(attributeArray, className);

             //add record to list of records
             records.add(record);
         }

         inFile.close();
    }

    /*************************************************************************/

    //Method sets number of nearest neighbors
    public void setParameters(int numberNeighbors)
    {
        this.numberNeighbors = numberNeighbors;
    }

    /*************************************************************************/

    //Method reads records from file4output file, determines their classes,
    //and writes classes to classified file
    public void classifyData(String testFile, String classifiedFile) throws IOException
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
             double[] attributeArray = new double[numberAttributes];

             //read attribute values
             for (int j = 0; j < numberAttributes; j++)
                  attributeArray[j] = inFile.nextDouble();

             //find class of attributes
             int className = classify(attributeArray);

             //write class name
             outFile.println(className);
         }

         inFile.close();
         outFile.close();
    }    

    /*************************************************************************/

    //Method determines the class of a set of attributes
    private int classify(double[] attributes)
    {
        double[] distance = new double[numberRecords];
        int[] id = new int[numberRecords];

        //find distances between attributes and all records
        for (int i = 0; i < numberRecords; i++)
        {
            distance[i] = distance(attributes, records.get(i).attributes);
            id[i] = i;
        }

        //find nearest neighbors
        nearestNeighbor(distance, id);

        //find majority class of nearest neighbors
        int className = majority(id);

        //return class
        return className;
    }

    /*************************************************************************/

    //Method finds the nearest neighbors
    private void nearestNeighbor(double[] distance, int[] id)
    {
        //sort distances and choose nearest neighbors
        for (int i = 0; i < numberNeighbors; i++)
            for (int j = i; j < numberRecords; j++)
                if (distance[i] > distance[j])
                {
                    double tempDistance = distance[i];
                    distance[i] = distance[j];
                    distance[j] = tempDistance;

                    int tempId = id[i];
                    id[i] = id[j];
                    id[j] = tempId;
                }
    }

    /*************************************************************************/

    //Method finds the majority class of nearest neighbors
    private int majority(int[] id)
    {
        double[] frequency = new double[numberClasses];

        //class frequencies are zero initially
        for (int i = 0; i < numberClasses; i++)
            frequency[i] = 0;

        //each neighbor contributes 1 to its class
        for (int i = 0; i < numberNeighbors; i++)
            frequency[records.get(id[i]).className - 1] += 1;

        //find majority class
        int maxIndex = 0;                         
        for (int i = 0; i < numberClasses; i++)   
            if (frequency[i] > frequency[maxIndex])
               maxIndex = i;

        return maxIndex + 1;
    }

    /*************************************************************************/

    //Method finds Euclidean distance between two points
    private double distance(double[] u, double[] v)
    {
        double distance = 0;         

        for (int i = 0; i < u.length; i++)
            distance = distance + (u[i] - v[i])*(u[i] - v[i]);

        distance = Math.sqrt(distance); 
 
        return distance;               
    }

    /*************************************************************************/

    //Method validates classifier using validation file and displays error rate
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
             double[] attributeArray = new double[numberAttributes];

             //read attributes
             for (int j = 0; j < numberAttributes; j++)
                  attributeArray[j] = inFile.nextDouble();

             //read actual class
             int actualClass = inFile.nextInt();

             //find class predicted by classifier
             int predictedClass = classify(attributeArray);

             //errror if predicted and actual classes do not match
             if (predictedClass != actualClass)
                numberErrors += 1;
         }
 
         //find and print error rate
         double errorRate = 100.0*numberErrors/numberRecords;
         System.out.println("validation error: " + errorRate + "%");

         inFile.close();
    }

    /************************************************************************/
}

