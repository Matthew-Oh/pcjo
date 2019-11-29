import java.util.Random;
//import apcslib.*;
/**
 * Write a description of class KTour here.
 * Simulates a knight moving on an empty chess board
 * @author Matthew Oh
 * @version 1.1 12/3/18
 */
public class KTour
{
    private int[][] board = new int[9][9]; //creates board 
    private final int[] vertical = {-2,-1,1,2,2,1,-1,-2}; //move direction for the vertical direction
    private final int[] horizontal = {1,2,2,1,-1,-2,-2,-1}; //move direction for the horizontal direction
    private int moves = 1; //the number of moves taken
    private Random randomNum = new Random();
    private int xPos; //the knight's horizontal position
    private int yPos; //the knight's vertical position
    
    //PCJO_IGNORE_2
    public static void main(String[] args)
    {
        KTour game;game = new KTour();
        game.play();
        game.printTour();
    }
    
    /**
     * Constructor for objects of class KTour
     * Starts the knight at 1,1
     */
    public KTour()
    {
        xPos = 1;
        yPos = 1;
        board[1][1] = 1;
    }
    
    /**
     * Constructor for objects of class KTour
     * @param x the x position the knight starts at
     * @param y the y position the knight starts at
     */
    public KTour(int x, int y)
    {
        xPos = x;
        yPos = y;
        board[y][x] = 1;
    }
    
    /**
     * Simulates a knight's tour, only visiting a square once
     */
    public void play()
    {
        //continues to move the knight, until there are no more valid moves
        while(move())
        {}
    }
    
    /**
     * Returns the number of moves in the tour
     * @return the number of moves in the tour
     */
    public int getMoves()
    {
        return moves;
    }
    
    /**
     * Prints the order in which the knight visited each square
     */
    public void printTour()
    {
        //prints the horizontal banner
        System.out.println("   1  2  3  4  5  6  7  8");
        for (int row = 1; row <= 8; row++)
        {
            //prints the vertical banner
            System.out.print(row);
            for (int column = 1; column <= 8; column++)
            {
                //prints the move in which the knight visited the square
                if (board[row][column] > 9)
                    System.out.print(" " + board[row][column]);
                else
                    System.out.print("  " + board[row][column]);
            }
            System.out.println();
        }
        //prints the number of squares visited
        System.out.print("\n" + moves + " squares visited");
    }
    
    //randomly chooses a number and moves
    //returns true if the knight moved sucessfully, false if there were no more valid moves
    private boolean move()
    {
        int[] unMove = {0, 1, 2, 3, 4, 5, 6, 7}; //array of all the unchecked moves
        int movesChecked = 1; //number of moves checked for this move
        int temp; //temporary value for unMove manipulation
        int index; //integer used for index of the 
        int moveNum = randomNum.nextInt(8);
        boolean moved = false; //value that checks if a move was taken
        //checks if the randomly selected move is valid
        if (xPos + horizontal[moveNum] >= 1 && xPos + horizontal[moveNum] <= 8
         && yPos  +  vertical[moveNum] >= 1 && yPos  +  vertical[moveNum] <= 8
         && board[yPos + vertical[moveNum]][xPos + horizontal[moveNum]] == 0)
        {
            //moves the knight
            xPos += horizontal[moveNum];
            yPos += vertical[moveNum];
            board[yPos][xPos] = ++moves;
            moved = true;
        }
        else
        {
            //if the move is not valid, randomly choose a remaining number
            index = moveNum;
            while (movesChecked <= 7)
            {
                //swaps the previous-chosen invalid move to the end
                temp = unMove[8 - movesChecked];
                unMove[8 - movesChecked] = unMove[index];
                unMove[index] = temp;
                //randomly chooses a move from the unchecked moves array
                index = randomNum.nextInt(8 - movesChecked);
                moveNum = unMove[index];
                //checks if the selected move is valid
                if (xPos + horizontal[moveNum] >= 1 && xPos + horizontal[moveNum] <= 8
                 && yPos  +  vertical[moveNum] >= 1 && yPos  +  vertical[moveNum] <= 8
                 && board[yPos + vertical[moveNum]][xPos + horizontal[moveNum]] == 0)
                {
                    //moves the knight
                    xPos += horizontal[moveNum];
                    yPos += vertical[moveNum];
                    board[yPos][xPos] = ++moves;
                    moved = true;
                }
                movesChecked++;
            }
        }
        return moved;
    }
}
