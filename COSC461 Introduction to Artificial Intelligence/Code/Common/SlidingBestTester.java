package Common;

//Tester program for sliding board solver with best first search
public class SlidingBestTester
{
    //main method for testing
    public static void main(String[] args)
    {   
        //initial board
        char[][] initial = {{'5', '7', '1'},
                            {'2', ' ', '8'},
                            {'4', '6', '3'}};
        //final board
        char[][] goal = {{'1', '4', '8'},
                         {'5', '2', '6'},
                         {' ', '3', '7'}};

        //solve sliding puzzle
        SlidingBest s = new SlidingBest(initial, goal, 3);
        s.solve();
    }
}


/*
        char[][] goal = {{'1', '4', '8'},
                         {'5', '2', '6'},
                         {' ', '3', '7'}};

        char[][] goal = {{'5', ' ', '1'},
                         {'4', '7', '8'},
                         {'6', '2', '3'}};

        char[][] goal = {{' ', '4', '7'},
                         {'5', '6', '3'},
                         {'8', '1', '2'}};

        char[][] goal = {{' ', '1', '7'},
                         {'6', '5', '3'},
                         {'8', '4', '2'}};

        char[][] goal = {{'3', '1', '7'},
                         {'6', '5', '4'},
                         {' ', '8', '2'}};
*/