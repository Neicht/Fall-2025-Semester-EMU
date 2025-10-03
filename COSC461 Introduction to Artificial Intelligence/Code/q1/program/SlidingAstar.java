package q1.program;

import java.util.LinkedList;
 
//This program solves sliding puzzle using A* algorithm
public class SlidingAstar
{
    char swapChar = '0';
    //Board class (inner class)
    private static class Board
    {
        private final char[][] array;                 //board array
        private int gvalue;                     //path cost
        private int hvalue;                     //heuristic value
        private int fvalue;                     //gvalue plus hvalue
        private Board parent;                   //parent board

        //Constructor of board class
        private Board(char[][] array, int size)
        {
            this.array = new char[size][size];  //create board array

            for (int i = 0; i < size; i++)      //copy given array
                System.arraycopy(array[i], 0, this.array[i], 0, size);

            this.gvalue = 0;                    //path cost, heuristic value,
            this.hvalue = 0;                    //fvalue are all 0
            this.fvalue = 0;

            this.parent = null;                 //no parent
        }
    }

    private final Board initial;                         //initial board
    private final Board goal;                            //goal board
    private int totalSearchedBoards;
    private final int size;                              //board size
    private final int heuristicFunction;                 //reference for switch
    private final int evaluationFunction;                //reference for swtich
    //Constructor of SlidingAstar class
    public SlidingAstar(char[][] initial, char[][] goal, int size, int evaluationFunction, int heuristicFunction)
    {
        this.size = size;                          //set size of board
        this.initial = new Board(initial, size);   //create initial board
        this.goal = new Board(goal, size);         //create goal board
        this.heuristicFunction = heuristicFunction;   //hold selection
        this.evaluationFunction = evaluationFunction; //hold selection
        this.totalSearchedBoards = 0;
    }

    //Method solves sliding puzzle
    public void solve()
    {

         LinkedList<Board> openList = new LinkedList<Board>();  //open list
         LinkedList<Board> closedList = new LinkedList<Board>();//closed list

         openList.addFirst(initial);   //add initial board to open list     

         while (!openList.isEmpty())   //while open list has more boards
         {
             int best = selectBest(openList);       //select best board

             Board board = openList.remove(best);   //remove board
                                                    
             closedList.addLast(board);             //add board to closed list

             if (goal(board))                       //if board is goal
             {

                 displayPath(board);                //display path to goal
                 return;                            //stop search
             }
             else                                   //if board is not goal
             {
                 LinkedList<Board> children = generate(board);//create children

                 for (int i = 0; i < children.size(); i++)
                 {                                     //for each child
                     Board child = children.get(i);    
                                                       
                     if (!exists(child, closedList))   //if child is not in closed list
                     {
                          if (!exists(child, openList))//if child is not in open list
                              openList.addLast(child); //add to open list
                          else                          
                          {                            //if child is already in open list
                              int index = find(child, openList);
                              if (child.fvalue < openList.get(index).fvalue)
                              {                            //if fvalue of new copy
                                  openList.remove(index);  //is less than old copy
                                  openList.addLast(child); //replace old copy
                              }                            //with new copy
                          }                               
                     }     
                 }                                  
             }                                       
         }

         System.out.println("no solution");            //no solution if there are
    }                                                  //no boards in open list

    //Method creates children of a board
    private LinkedList<Board> generate(Board board)
    {
        int i = 0, j = 0;
        boolean found = false;


        for (i = 0; i < size; i++)              //find location of empty slot
        {                                       //of board
            for (j = 0; j < size; j++)
                if (board.array[i][j] == swapChar)
                {   
                    found = true;
                    break;
                }
            
            if (found)
               break;
        }

        boolean north, south, east, west;       //decide whether empty slot
        north = i != 0;          //has N, S, E, W neighbors
        south = i != size - 1;
        east = j != size - 1;
        west = j != 0;

        LinkedList<Board> children = new LinkedList<Board>();//list of children

        if (north) children.addLast(createChild(board, i, j, 'N')); //add N, S, E, W
        if (south) children.addLast(createChild(board, i, j, 'S')); //children if
        if (east) children.addLast(createChild(board, i, j, 'E'));  //they exist
        if (west) children.addLast(createChild(board, i, j, 'W'));  
                                                                    
        return children;                        //return children      
    }

    //Method creates a child of a board by swapping empty slot in a 
    //given direction
    private Board createChild(Board board, int i, int j, char direction)
    {
        Board child = copy(board);                   //create copy of board


        if (direction == 'N')                        //swap empty slot to north
        {
            child.array[i][j] = child.array[i-1][j];
            child.array[i-1][j] = swapChar;
        }
        else if (direction == 'S')                   //swap empty slot to south
        {
            child.array[i][j] = child.array[i+1][j];
            child.array[i+1][j] = swapChar;
        }
        else if (direction == 'E')                   //swap empty slot to east
        {
            child.array[i][j] = child.array[i][j+1];
            child.array[i][j+1] = swapChar;
        }
        else                                         //swap empty slot to west
        {
            child.array[i][j] = child.array[i][j-1];
            child.array[i][j-1] = swapChar;
        }
        child.gvalue = board.gvalue + 1;             //parent path cost plus one
        // child.hvalue = heuristic_M(child);           //heuristic value of child
        // child.fvalue = child.gvalue + child.hvalue;  //gvalue plus hvalue
        //modified last two lines of code
        computeHeuristicValue(child);                //heuristic value computed in separate function
        computeEvaluationFunctionValue(child);       //f value computed in separate function
        child.parent = board;                        //assign parent to child
        return child;                                //return child
    }
    /*
    * Function evaluates the h value of given board.
    * Heuristic function h options:
    Option 1: h = mismatches
    Option 2: h = taxi distance
    * */
    private void computeHeuristicValue(Board child)
    {
        switch(heuristicFunction){
            case 1:
                child.hvalue = heuristic_M(child);
                break;
            case 2:
                child.hvalue = heuristic_D(child);
                break;
        }
    }
    /*
     * Function evaluates the f value of given board.
     * Evaluation function f options:
    Option 1: f = h where h is heuristic function
    Option 2: f = g where g is path cost function
    Option 3: f = h + g where h is heuristic function and g is path cost function
     * */
    private void computeEvaluationFunctionValue(Board board)
    {
        switch(evaluationFunction){
            case 1:
                board.fvalue = board.hvalue;
                break;
            case 2:
                board.fvalue = board.gvalue;
                break;
            case 3:
                board.fvalue = board.hvalue + board.gvalue;
                break;

        }
    }

    //Method computes heuristic value of board based on misplaced values
    private int heuristic_M(Board board)
    {
        int value = 0;                               //initial heuristic value

        for (int i = 0; i < size; i++)               //go thru board and
            for (int j = 0; j < size; j++)           //count misplaced values
                if (board.array[i][j] != goal.array[i][j])
                   value += 1;                       
  
        return value;                                //return heuristic value
    }

    //Method computes heuristic value of board
    //Heuristic value is the sum of taxi distances of misplaced values
    private int heuristic_D(Board board)
    {
        //initial heuristic value
        int value = 0;

        //go thru board
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                //if value mismatches in goal board    
                if (board.array[i][j] != goal.array[i][j]) 
                {                                
                    //locate value in goal board
                    int x = 0, y = 0;         
                    boolean found = false;
                    for (x = 0; x < size; x++)
                    {                       
                        for (y = 0; y < size; y++)
                            if (goal.array[x][y] == board.array[i][j])
                            {                 
                                found = true; 
                                break;
                            } 
                        if (found)
                           break;                        
                    }
            
                    //find city distance between two locations
                    value += Math.abs(x-i) + Math.abs(y-j);
                }
                     
        //return heuristic value               
        return value;
    }

    //Method locates the board with minimum fvalue in a list of boards
    private int selectBest(LinkedList<Board> list)
    {
        int minValue = list.get(0).fvalue;           //initialize minimum
        int minIndex = 0;                            //value and location

        for (int i = 0; i < list.size(); i++)
        {
            int value = list.get(i).fvalue;
            if (value < minValue)                    //updates minimums if
            {                                        //board with smaller
                minValue = value;                    //fvalue is found
                minIndex  = i;
            } 
        }

        return minIndex;                             //return minimum location
    }   

    //Method creates copy of a board
    private Board copy(Board board)
    {
         return new Board(board.array, size);
    }

    //Method decides whether a board is goal
    private boolean goal(Board board)
    {
        totalSearchedBoards ++;
        return identical(board, goal);           //compare board with goal

    }                                             

    //Method decides whether a board exists in a list
    private boolean exists(Board board, LinkedList<Board> list)
    {
        for (int i = 0; i < list.size(); i++)    //compare board with each
            if (identical(board, list.get(i)))   //element of list
               return true;

        return false;
    }

    //Method finds location of a board in a list
    private int find(Board board, LinkedList<Board> list)
    {
        for (int i = 0; i < list.size(); i++)    //compare board with each
            if (identical(board, list.get(i)))   //element of list
               return i;

        return -1;
    }
    
    //Method decides whether two boards are identical
    private boolean identical(Board p, Board q)
    {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (p.array[i][j] != q.array[i][j])
                    return false;      //if there is a mismatch then false

        return true;                   //otherwise true
    }

    //Method displays path from initial to current board
    private void displayPath(Board board)
    {
        LinkedList<Board> list = new LinkedList<Board>();

        Board pointer = board;         //start at current board

        while (pointer != null)        //go back towards initial board
        {
            list.addFirst(pointer);    //add boards to beginning of list

            pointer = pointer.parent;  //keep going back
        }
                                       //print boards in list
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Board " + (i + 1)); //visibly count boards in path
            displayBoard(list.get(i));
        }
        System.out.printf("Swaps: %s\nBoards Searched: %s", list.size(), totalSearchedBoards);
    }

    //Method displays board
    private void displayBoard(Board board)
    {
        for (int i = 0; i < size; i++) //print each element of board
        {
            for (int j = 0; j < size; j++)
                System.out.print(board.array[i][j] + " ");
            System.out.println();
        }   
        System.out.println();     
    }
}
