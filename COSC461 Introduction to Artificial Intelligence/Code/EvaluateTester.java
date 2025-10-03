import java.util.Scanner;

//Tester program for tic-tac with min-max, depth limit, and
//board evaluation
public class EvaluateTester
{
   //main program for tester
   public static void main(String[] args)
   {
       //play tic-tac game
       System.out.print("Enter board size: ");
       int size = -1;
       try{
           Scanner scanner = new Scanner(System.in);
           size = scanner.nextInt();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }

       Evaluate e = new Evaluate(size);
	   e.play();
   }
}