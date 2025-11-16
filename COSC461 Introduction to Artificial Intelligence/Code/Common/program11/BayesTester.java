import java.io.*;
import java.util.*;

//Program tests Bayes classifier in specific application
public class BayesTester
{
    /*************************************************************************/

    //Main method
    public static void main(String[] args) throws IOException
    {
        //preprocess files
        convertTrainingFile("originaltrainingfile", "trainingfile");
        convertValidationFile("originalvalidationfile", "validationfile"); 
        convertTestFile("originaltestfile", "testfile"); 

        //construct bayes classifier
        Bayes classifier = new Bayes();

        //load training data
        classifier.loadTrainingData("trainingfile");

        //compute probabilities
        classifier.computeProbability();

        //classify data
        classifier.classifyData("testfile", "classifiedfile");
        
        //postprocess files
        convertClassFile("classifiedfile", "originalclassifiedfile");

        //validate classifier
        classifier.validate("validationfile");
    }

    /****************************************************************************/

    //Method converts training file to numerical format
    private static void convertTrainingFile(String inputFile, String outputFile) throws IOException
    {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records, attributes, classes
        int numberRecords = inFile.nextInt(); 
        int numberAttributes = inFile.nextInt();
        int numberClasses = inFile.nextInt();

        //read attribute values
        int[] attributeValues = new int[numberAttributes];
        for (int i = 0; i < numberAttributes; i++)
            attributeValues[i] = inFile.nextInt();

        //write number of records, attributes, classes
        outFile.println(numberRecords + " " + numberAttributes + " " + numberClasses);

        //write attribute values
        for (int i = 0; i < numberAttributes; i++)
            outFile.print(attributeValues[i] + " ");
        outFile.println();

        //for each record
        for (int i = 0; i < numberRecords; i++)    
        {            
            String degree = inFile.next();                      //convert degree
            int degreeNumber = convertDegreeToNumber(degree);
            outFile.print(degreeNumber + " ");

            String smoke = inFile.next();                       //convert smoking status
            int smokeNumber = convertSmokeToNumber(smoke);
            outFile.print(smokeNumber + " ");
            
            String marital = inFile.next();                      //convert marital status
            int maritalNumber = convertMaritalToNumber(marital);
            outFile.print(maritalNumber + " ");

            String sex = inFile.next();                          //convert sex
            int sexNumber = convertSexToNumber(sex);
            outFile.print(sexNumber + " ");

            String work = inFile.next();                         //convert work
            int workNumber = convertWorkToNumber(work);
            outFile.print(workNumber + " ");

            String className = inFile.next();                    //convert class name
            int classNumber = convertClassToNumber(className);
            outFile.print(classNumber);             
                     
            outFile.println();
        }

        inFile.close();
        outFile.close();
    }

    /****************************************************************************/

    //Method converts validation file to numerical format
    private static void convertValidationFile(String inputFile, String outputFile) throws IOException
    {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records
        int numberRecords = inFile.nextInt(); 

        //write number of records
        outFile.println(numberRecords);

        //for each record
        for (int i = 0; i < numberRecords; i++)    
        {            
            String degree = inFile.next();                      //convert degree
            int degreeNumber = convertDegreeToNumber(degree);
            outFile.print(degreeNumber + " ");

            String smoke = inFile.next();                       //convert smoking status
            int smokeNumber = convertSmokeToNumber(smoke);
            outFile.print(smokeNumber + " ");
            
            String marital = inFile.next();                      //convert marital status
            int maritalNumber = convertMaritalToNumber(marital);
            outFile.print(maritalNumber + " ");

            String sex = inFile.next();                          //convert sex
            int sexNumber = convertSexToNumber(sex);
            outFile.print(sexNumber + " ");

            String work = inFile.next();                         //convert work
            int workNumber = convertWorkToNumber(work);
            outFile.print(workNumber + " ");

            String className = inFile.next();                    //convert class name
            int classNumber = convertClassToNumber(className);
            outFile.print(classNumber);             
                     
            outFile.println();
        }

        inFile.close();
        outFile.close();
    }

    /****************************************************************************/
 
    //Method converts test file to numerical format
    private static void convertTestFile(String inputFile, String outputFile) throws IOException
    {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records
        int numberRecords = inFile.nextInt();    
    
        //write number of records
        outFile.println(numberRecords);

        //for each record
        for (int i = 0; i < numberRecords; i++)    
        {            
            String degree = inFile.next();                      //convert degree
            int degreeNumber = convertDegreeToNumber(degree);
            outFile.print(degreeNumber + " ");

            String smoke = inFile.next();                       //convert smoking status
            int smokeNumber = convertSmokeToNumber(smoke);
            outFile.print(smokeNumber + " ");
            
            String marital = inFile.next();                      //convert marital status
            int maritalNumber = convertMaritalToNumber(marital);
            outFile.print(maritalNumber + " ");

            String sex = inFile.next();                          //convert sex
            int sexNumber = convertSexToNumber(sex);
            outFile.print(sexNumber + " ");

            String work = inFile.next();                         //convert work
            int workNumber = convertWorkToNumber(work);
            outFile.print(workNumber + " ");

            outFile.println();
        }

        inFile.close();
        outFile.close();
    }

    /****************************************************************************/
 
    //Method converts class file to text format
    private static void convertClassFile(String inputFile, String outputFile) throws IOException
    {
        //input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        //read number of records
        int numberRecords = inFile.nextInt();    
    
        //write number of records
        outFile.println(numberRecords);

        //for each record
        for (int i = 0; i < numberRecords; i++)    
        {      
            int number = inFile.nextInt();                       //convert class number
            String className = convertNumberToClass(number);
            outFile.println(className);
        }

        inFile.close();
        outFile.close();          
    } 

    /****************************************************************************/

    //Method converts degree type to number
    private static int convertDegreeToNumber(String degree)
    {
        if (degree.equals("college"))
            return 1;
        else
            return 2;           
    }

    /*****************************************************************************/

    //Method converts smoking status to number
    private static int convertSmokeToNumber(String smoke)
    {
        if (smoke.equals("smoker"))
            return 1;
        else
            return 2;           
    }

    /****************************************************************************/

    //Method converts marital status to number
    private static int convertMaritalToNumber(String marital)
    {
        if (marital.equals("married"))
            return 1;
        else
            return 2;           
    }

    /****************************************************************************/

    //Method converts sex to number
    private static int convertSexToNumber(String sex)
    {
        if (sex.equals("male"))
            return 1;
        else
            return 2;           
    }

    /****************************************************************************/

    //Method converts work status to number
    private static int convertWorkToNumber(String work)
    {
        if (work.equals("works"))
            return 1;
        else
            return 2;           
    }

    /****************************************************************************/

    //Method converts class name to number
    private static int convertClassToNumber(String className)
    {
        if (className.equals("lowrisk"))
            return 1;
        else if (className.equals("mediumrisk"))
            return 2;
        else if (className.equals("highrisk"))
            return 3;
        else
            return 4; 
    }

    /*****************************************************************************/

    //Method converts number to class name
    private static String convertNumberToClass(int number)
    {
        if (number == 1)
           return "lowrisk";
        else if (number == 2)
           return "mediumrisk"; 
        else if (number == 3)
           return "highrisk";
        else
           return "undefined";
    }

    /*****************************************************************************/
}


