import java.io.*;
import java.util.*;

//K-means clustering class
public class Kmeans
{
    /*************************************************************************/

    private int numberRecords;            //number of records  
    private int numberAttributes;         //number of attributes
    private int numberClusters;           //number of clusters
    private int numberIterations;         //number of iterations

    private double[][] records;           //array of records
    private double[][] centroids;         //array of centroids
    private int[] clusters;               //clusters of records
    private Random rand;                  //random number generator

    /*************************************************************************/

    //Constructor of Kmeans
    public Kmeans()
    {
        //initial data is empty
        numberRecords = 0;       
        numberAttributes = 0;
        numberClusters = 0;
        numberIterations = 0;
        records = null;        
        centroids = null;  
        clusters = null;
        rand = null;
    }

    /*************************************************************************/

    //Method loads records from input file
    public void load(String inputFile) throws IOException
    {
         Scanner inFile = new Scanner(new File(inputFile));

         //read number of records, attributes
         numberRecords = inFile.nextInt();    
         numberAttributes = inFile.nextInt();        
        
         //create array of records
         records = new double[numberRecords][numberAttributes];        

         //for each record
         for (int i = 0; i < numberRecords; i++)    
         {
             //read attributes                                         
             for (int j = 0; j < numberAttributes; j++) 
                 records[i][j] = inFile.nextDouble();
         }

         inFile.close();
    }

    /*************************************************************************/

    //Method sets parameters of clustering
    public void setParameters(int numberClusters, int numberIterations, int seed)
    {
        //set number of clusters
        this.numberClusters = numberClusters;

        //set number of iterations
        this.numberIterations = numberIterations;

        //create random number generator with seed
        this.rand = new Random(seed);
    }

    /*************************************************************************/

    //Method performs k-means clustering
    public void cluster()
    {
        //initialize clusters of records
        initializeClusters();

        //initialize centroids of clusters
        initializeCentroids();

        //repeat iterations times
        for (int i = 0; i < numberIterations; i++)
        {
            //assign clusters to records
            assignClusters();

            //update centroids of clusters
            updateCentroids();
        }
    }

    /*************************************************************************/

    //Method initializes clusters of records
    private void initializeClusters()
    {
        //create array of cluster labels
        clusters = new int[numberRecords];

        //assign -1 to all records, cluster labels are unknown
        for (int i = 0; i < numberRecords; i++)
            clusters[i] = -1;
    }

    /*************************************************************************/

    //Method initializes centroids of clusters
    private void initializeCentroids()
    {
        //create array of centroids
        centroids = new double[numberClusters][numberAttributes];

        //for each cluster
        for (int i = 0; i < numberClusters; i++)
        {
            //randomly pick a record
            int index = rand.nextInt(numberRecords);

            //use the record as centroid
            for (int j = 0; j < numberAttributes; j++)
                centroids[i][j] = records[index][j];
        }
    }    

    /*************************************************************************/

    //Method assigns clusters to records
    private void assignClusters()
    {
        //go thru records and assign clusters to them
        for (int i = 0; i < numberRecords; i++)
        {
            //find distance between record and first centroid
            double minDistance = distance(records[i], centroids[0]);
            int minIndex = 0;

            //go thru centroids and find closest centroid
            for (int j = 0; j < numberClusters; j++)
            {
                //find distance between record and centroid
                double distance = distance(records[i], centroids[j]);

                //if distance is less than minimum, update minimum
                if (distance < minDistance)
                {
                    minDistance = distance;
                    minIndex = j;
                } 
            }

            //assign closest cluster to record
            clusters[i] = minIndex;
        }
    }

    /*************************************************************************/

    //Method updates centroids of clusters
    private void updateCentroids()
    {
         //create array of cluster sums and initialize
         double[][] clusterSum = new double[numberClusters][numberAttributes];
         for (int i = 0; i < numberClusters; i++)
             for (int j = 0; j < numberAttributes; j++)
                 clusterSum[i][j] = 0;

         //create array of cluster sizes and initialize
         int[] clusterSize = new int[numberClusters];
         for (int i = 0; i < numberClusters; i++)
             clusterSize[i] = 0;

         //for each record
         for (int i = 0; i < numberRecords; i++)
         {
             //find cluster of record
             int cluster = clusters[i];

             //add record to cluster sum
             clusterSum[cluster] = sum(clusterSum[cluster], records[i]);
                                
             //increment cluster size
             clusterSize[cluster] += 1;
         }

         //find centroid of each cluster
         for (int i = 0; i < numberClusters; i++)
             if (clusterSize[i] > 0)
                centroids[i] = scale(clusterSum[i], 1.0/clusterSize[i]);
    }

    /*************************************************************************/

    //Method finds distance between two records, square of euclidean distance
    private double distance(double[] u, double[] v)
    {
         double sum = 0;

         //square of euclidean distance between two records
         for (int i = 0; i < u.length; i++)
             sum += (u[i] - v[i])*(u[i] - v[i]);

         return sum;
    }

    /*************************************************************************/

    //Method finds sum of two records
    private double[] sum(double[] u, double[] v)
    {
        double[] result = new double[u.length];

        //add corresponding attributes of records
        for (int i = 0; i < u.length; i++)
            result[i] = u[i] + v[i];

        return result;
    }

    /*************************************************************************/

    //Method finds scaler multiple of a record
    private double[] scale(double[] u, double k)
    {
        double[] result = new double[u.length];

        //multiply attributes of record by scaler
        for (int i = 0; i < u.length; i++)
            result[i] = u[i]*k;

        return result;
    }

    /*************************************************************************/

    //Method writes records and their clusters to output file
    public void display(String outputFile) throws IOException
    {
         PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

         //for each record
         for (int i = 0; i < numberRecords; i++)
         {
             //write attributes of record
             for (int j = 0; j < numberAttributes; j++)
                  outFile.print(records[i][j] + " ");

             //write cluster label
             outFile.println(clusters[i]+1);
         }

         outFile.close();
    }

    /*************************************************************************/
}