package Project1.q3.program;

import java.util.Scanner;

//Tester program for tic-tac with min-max, depth limit,
//board evaluation, and alph-beta pruning
public class AlphaBetaTester {
    //main program for tester
    public static void main(String[] args) {
        //play tic-tac game
        int n = 0;
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter board size (ex. \"4\" for 4x4, \"6\" for 6x6): ");
            n = scanner.nextInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AlphaBeta a = new AlphaBeta(n);
        a.play();
    }
}