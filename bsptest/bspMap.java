import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class bspMap
{
    private Tile[][] map;
    private static int size = 100;
    private static int subdiv = 4;
    private static int idCount = 1; //testing
    private static Group group; //visual testing
    private RoomRoot absRoot;
    
    public bspMap(Group g)
    {
        map = new Tile[size][size];
        absRoot = new RoomRoot(0,0,size,size);
        absRoot.setId(idCount++);
        group = g;
        divide();
        //display();
    }
    
    //small scale testing
    public void display()
    {
        for (int col = 0; col < size; col++)
        {
            for (int row = 0; row < size; row++)
            {
                if (map[col][row] != null && map[col][row].isOpen())
                    System.out.print(map[col][row] + " ");
                else
                    System.out.print("__ ");
            }
            System.out.println();
        }
    }
   
    private void divide()
    {
        divide(absRoot, subdiv);
    }
    
    /**
     * Divides a room in two, and each resulting room, for a given number of times
     * @param root RoomRoot to be divided
     * @param count number of recursive cuts
     */
    private void divide(RoomRoot root, int count)
    {
        if (count > 0)
        {
            //determines if the room is longer in width or height
            if (root.getXScl() >= root.getYScl())
            {
                int xSi = root.getXScl();
                int x = (int)(Math.random() * xSi / 3) + (xSi / 3);
                
                RoomRoot child1 = new RoomRoot(root.getXPos(),root.getYPos(),x,root.getYScl());
                RoomRoot child2 = new RoomRoot(root.getXPos()+x,root.getYPos(),xSi-x,root.getYScl());
                child1.setId(idCount++);
                child2.setId(idCount++);
                root.setChild1(child1);
                root.setChild2(child2);
                generateHoriPath(root);
                
                Rectangle r = new Rectangle(1, root.getYScl(), Color.GREEN);
                r.relocate(root.getXPos()+x,root.getYPos());
                group.getChildren().add(r);
                
                divide(child1, count-1);
                divide(child2, count-1);
            }
            else if (root.getXScl() < root.getYScl())
            {
                int ySi = root.getYScl();
                int y = (int)(Math.random() * ySi / 3) + (ySi / 3);
                
                RoomRoot child1 = new RoomRoot(root.getXPos(),root.getYPos(),root.getXScl(),y);
                RoomRoot child2 = new RoomRoot(root.getXPos(),root.getYPos()+y,root.getXScl(), ySi-y);
                child1.setId(idCount++);
                child2.setId(idCount++);
                root.setChild1(child1);
                root.setChild2(child2);
                generateVertPath(root);
                
                Rectangle r = new Rectangle(root.getXScl(), 1, Color.GREEN);
                r.relocate(root.getXPos(),root.getYPos()+y);
                group.getChildren().add(r);
                
                divide(child1, count-1);
                divide(child2, count-1);
            }
        }
        else
            generateRoom(root);
    }
    
    /**
     * Generates a Room that exists within a RoomRoot
     * @param root RoomRoot which a Room is to be generated for
     */
    private void generateRoom(RoomRoot root)
    {
        //scale of room set at 70% to 100% of partition size with guarenteed borders
        //clean this up maybe?
        int xPad = (int)(((Math.random() * .10) + .10) * root.getXScl() + 1); 
        int yPad = (int)(((Math.random() * .10) + .10) * root.getYScl() + 1);
        int xS = root.getXScl() - 2 * xPad;
        int yS = root.getYScl() - 2 * yPad;
        int xP = root.getXPos() + xPad;
        int yP = root.getYPos() + yPad;
        root.setRoom(new Room(xP,yP,xS,yS));
        for (int col = yP; col < yP + yS; col++)
        {
            for (int row = xP; row < xP + xS; row++)
            {
                map[col][row] = new Tile(root);
                map[col][row].setId(root.getId());
                map[col][row].setOpen(true);
            }
        }
        Rectangle r = new Rectangle(xS,yS,Color.RED);
        r.relocate(xP,yP);
        group.getChildren().add(r);
    }
    
    /**
     * Generates a path between the two children of the room after a division along x
     * @param room RoomRoot whose children shall be connected
     */
    private void generateHoriPath(RoomRoot room)
    {
        int xP = room.getChild1().getXPos() + room.getChild1().getXScl() / 2;
        int yP = room.getYPos() + room.getYScl() / 2;
        int xS = room.getChild2().getXPos() + room.getChild2().getXScl() / 2 - xP;
        int yS = 1;
        Rectangle r = new Rectangle(xS,yS, Color.BLUE);
        r.relocate(xP,yP);
        group.getChildren().add(r);
        for (int x = xP; x <= xP + xS; x++)
        {
            if (map[yP][x] == null || !map[yP][x].isOpen())
            {
                map[yP][x] = new Tile(99);
                map[yP][x].setOpen(true);
            }
        }
    }
    
    /**
     * Generates paths between the two children of the room after a division along y
     * @param room RoomRoot whose children shall be connected
     */
    private void generateVertPath(RoomRoot room)
    {
        int xP = room.getXPos() + room.getXScl() / 2;
        int yP = room.getChild1().getYPos() + room.getChild1().getYScl() / 2;
        int xS = 1;
        int yS = room.getChild2().getYPos() + room.getChild2().getYScl() / 2 - yP;
        Rectangle r = new Rectangle(xS,yS, Color.BLUE);
        r.relocate(xP,yP);
        group.getChildren().add(r);
        for (int y = yP; y <= yP + yS; y++)
        {
            if (map[y][xP] == null || !map[y][xP].isOpen())
            {
                map[y][xP] = new Tile(99);
                map[y][xP].setOpen(true);
            }
        }
    }
}
