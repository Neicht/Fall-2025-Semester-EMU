import java.io.*;

//Program tests k-means clustering in a specific application
public class KmeansTester
{
    //Main method
    public static void main(String[] args) throws IOException
    {
        //create clustering object
        Kmeans k = new Kmeans();

        //load records
        k.load("inputfile");

        //set parameters
        k.setParameters(2, 10000, 58947);

        //perform clustering
        k.cluster();

        //display records and clusters
        k.display("outputfile");
    }
}
