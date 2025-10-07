package Common.program07;

//Tester program for tic-tac with min-max, depth limit, and
//board evaluation
public class EvaluateTester
{
   //main program for tester
   public static void main(String[] args)
   {
       //play tic-tac game
       Evaluate e = new Evaluate(4);
	   e.play();
   }
}