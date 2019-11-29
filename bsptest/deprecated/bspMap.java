import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class bspMap
{
    private Tile[][] map;
    private static int size = 500;
    private static int subdiv = 5;
    private static int idCount = 1; //testing
    private static Group group;
    private ArrayList<RoomRoot> rooms = new ArrayList<RoomRoot>((int)Math.pow(2,subdiv));
    
    public bspMap(Group g)
    {
        map = new Tile[size][size];
        RoomRoot room = new RoomRoot(0,0,size,size);
        group = g;
        room.setId(idCount++);
        rooms.add(room);
        divide();
        generatePaths();
        //display();
    }
    
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
        divide(rooms.get(0), subdiv);
    }
    
    private void divide(RoomRoot root, int count)
    {
        if (count > 0)
        {
            //determines if the room is longer in width or height
            if (root.getXScl() >= root.getYScl())
            {
                int xSi = root.getXScl();
                int x = (int)(Math.random() * xSi / 3) + (xSi / 3);
                root.setXScl(x);
                RoomRoot newRoom = new RoomRoot(root.getXPos()+x,root.getYPos(),xSi-x,root.getYScl());
                //map[root.getYPos()][root.getXPos() + x] = newRoom;
                newRoom.setId(idCount++);
                rooms.add(newRoom);
                divide(root, count-1);
                divide(newRoom, count-1);
            }
            else if (root.getXScl() < root.getYScl())
            {
                int ySi = root.getYScl();
                int y = (int)(Math.random() * ySi / 3) + (ySi / 3);
                root.setYScl(y);
                RoomRoot newRoom = new RoomRoot(root.getXPos(),root.getYPos()+y,root.getXScl(),ySi-y);
                //map[root.getYPos() + y][root.getXPos()] = newRoom;
                newRoom.setId(idCount++);
                rooms.add(newRoom);
                divide(root, count-1);
                divide(newRoom, count-1);
            }
        }
        else
        {
            for (int yP = root.getYPos(); yP < root.getYPos() + root.getYScl(); yP++)
            {
                for (int xP = root.getXPos(); xP < root.getXPos() + root.getXScl(); xP++)
                {
                    map[yP][xP] = new Tile(root);
                    map[yP][xP].setId(root.getId());
                }
            }
            generateRoom(root);
        }
    }
    
    private void generateRoom(RoomRoot root)
    {
        //scale of room set at 70% to 100% of partition size with guarenteed borders
        //clean this up maybe?
        int xPad = (int)(((Math.random() * .10) + .15) * root.getXScl() + 1); 
        int yPad = (int)(((Math.random() * .10) + .15) * root.getYScl() + 1);
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
    
    private void generatePaths()
    {
        ArrayList<Connection> conns = new ArrayList<Connection>();
        for (RoomRoot room: rooms)
        {
            int x = room.getXPos();
            int y = room.getYPos();
            //probably maybe a way to make this neater??
            //check topside
            if (y - 1 >= 0)
            {
                for (; x < room.getXPos() + room.getXScl(); x++)
                {
                    Connection conn = new Connection(room, map[y-1][x].getRoot(), 2);
                    if(!containsIdentical(conns, conn))
                        conns.add(conn);
                }
            }
            else
                x += room.getXScl();
            x--;
            //check rightside
            if (x + 1 < size)
            {
                for (; y < room.getYPos() + room.getYScl(); y++)
                {
                    Connection conn = new Connection(room, map[y][x+1].getRoot(), 3);
                    if(!containsIdentical(conns, conn))
                        conns.add(conn);
                }
            }
            else
                y += room.getYScl();
            y--;
            //check bottomside
            if (y + 1 < size)
            {
                for (; x > room.getXPos(); x--)
                {
                    Connection conn = new Connection(room, map[y+1][x].getRoot(), 1);
                    if(!containsIdentical(conns, conn))
                        conns.add(conn);
                }
            }
            else
                x -= room.getXScl();
            //check leftside
            if (x - 1 >= 0)
            {
                for (; y > room.getYPos(); y--)
                {
                    Connection conn = new Connection(room, map[y][x-1].getRoot(), 4);
                    if(!containsIdentical(conns, conn))
                        conns.add(conn);
                }
            }
            else
                y -= room.getYScl();
        }
        for (Connection conn: conns)
        {
            //System.out.println(conn.getRoom1().getId() + " to " + conn.getRoom2().getId());
            Rectangle r1;
            Rectangle r2;
            Rectangle r3;
            int x1;
            int x2;
            int y1;
            int y2;
            switch(conn.getOrient())
            {
                case 1:
                    x1 = conn.getRoom1().getExit(2);
                    x2 = conn.getRoom2().getExit(0);
                    group.getChildren().addAll(
                    bottomPath(conn.getRoom1(),x1),
                    topPath(conn.getRoom2(),x2),
                    horiPath(x1,x2,conn.getRoom2().getYPos()));
                    break;
                case 2:
                    x1 = conn.getRoom1().getExit(0);
                    x2 = conn.getRoom2().getExit(2);
                    group.getChildren().addAll(
                    bottomPath(conn.getRoom2(),x2),
                    topPath(conn.getRoom1(),x1),
                    horiPath(x1,x2,conn.getRoom1().getYPos()));
                    break;
                case 3:
                    y1 = conn.getRoom1().getExit(1);
                    y2 = conn.getRoom2().getExit(3);
                    group.getChildren().addAll(
                    rightPath(conn.getRoom1(),y1),
                    leftPath(conn.getRoom2(),y2),
                    vertPath(y1,y2,conn.getRoom2().getXPos()));
                    break;
                case 4:
                    y1 = conn.getRoom1().getExit(3);
                    y2 = conn.getRoom2().getExit(1);
                    group.getChildren().addAll(
                    rightPath(conn.getRoom2(),y2),
                    leftPath(conn.getRoom1(),y1),
                    vertPath(y1,y2,conn.getRoom1().getXPos()));
                    break;
            }
        }
    }
    
    private Rectangle bottomPath(RoomRoot root, int x)
    {
        Rectangle r;
        int xP = x;
        int yP = root.getRoom().getYPos()
        + root.getRoom().getYScl();
        int yS = root.getYPos() + root.getYScl() - yP;
        r = new Rectangle(1,yS,Color.RED);
        r.relocate(xP,yP);
        for (int y = yP; y < yP + yS; y++)
        {
            map[y][x] = new Tile(99);
            map[y][x].setOpen(true);
        }
        return r;
    }
    private Rectangle topPath(RoomRoot root, int x)
    {
        Rectangle r;
        int xP = x;
        int yP = root.getYPos();
        int yS = root.getRoom().getYPos() - yP;
        r = new Rectangle(1,yS,Color.RED);
        r.relocate(xP,yP);
        for (int y = yP; y < yP + yS; y++)
        {
            map[y][x] = new Tile(99);
            map[y][x].setOpen(true);
        }
        return r;
    }
    public Rectangle horiPath(int x1, int x2, int y)
    {
        Rectangle r;
        if (x1 <= x2)
        {
            r = new Rectangle(x2-x1 + 1,1,Color.RED);
            r.relocate(x1,y);
            for (int x = x1; x <= x2; x++)
            {
                map[y][x] = new Tile(99);
                map[y][x].setOpen(true);
            }
        }
        else
        {
            r = new Rectangle(x1-x2 + 1,1,Color.RED);
            r.relocate(x2,y);
            for (int x = x2; x <= x1; x++)
            {
                map[y][x] = new Tile(99);
                map[y][x].setOpen(true);
            }
        }
        
        return r;
    }
    private Rectangle rightPath(RoomRoot root, int y)
    {
        Rectangle r;
        int xP = root.getRoom().getXPos()
        + root.getRoom().getXScl();
        int yP = y;
        int xS = root.getXPos() + root.getXScl() - xP;
        r = new Rectangle(xS,1,Color.RED);
        r.relocate(xP,yP);
        for (int x = xP; x < xP + xS; x++)
        {
            map[y][x] = new Tile(99);
            map[y][x].setOpen(true);
        }
        return r;
    }
    private Rectangle leftPath(RoomRoot root, int y)
    {
        Rectangle r;
        int xP = root.getXPos();
        int yP = y;
        int xS = root.getRoom().getXPos() - xP;
        r = new Rectangle(xS,1,Color.RED);
        r.relocate(xP,yP);
        for (int x = xP; x < xP + xS; x++)
        {
            map[y][x] = new Tile(99);
            map[y][x].setOpen(true);
        }
        return r;
    }
    public Rectangle vertPath(int y1, int y2, int x)
    {
        Rectangle r;
        if (y1 <= y2)
        {
            r = new Rectangle(1,y2-y1 + 1,Color.RED);
            r.relocate(x,y1);
            for (int y = y1; y <= y2; y++)
            {
                map[y][x] = new Tile(99);
                map[y][x].setOpen(true);
            }
        }
        else
        {
            r = new Rectangle(1,y1-y2 + 1,Color.RED);
            r.relocate(x,y2);
            for (int y = y2; y <= y1; y++)
            {
                map[y][x] = new Tile(99);
                map[y][x].setOpen(true);
            }
        }
        return r;
    }
    
    /**
     * Checks to see if an identical Connection exists within the ArrayList
     * @param list ArrayList of connections
     * @param conn connections to be checked
     */
    private boolean containsIdentical(ArrayList<Connection> list, Connection conn)
    {
        for (Connection c: list)
        {
            if(conn.getRoom1() == c.getRoom1() && conn.getRoom2() == c.getRoom2()
            ||conn.getRoom1() == c.getRoom2() && conn.getRoom2() == c.getRoom1())
                return true;
        }
        return false;
    }
}
