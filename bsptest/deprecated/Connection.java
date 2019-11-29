
/**
 * A Connection between two rooms
 */
public class Connection
{
    private RoomRoot room1;
    private RoomRoot room2;
    //maybe use enum maybe??
    /**
     * Orientation of connection
     * 1: r1 above r2 below
     * 2: r2 above r1 below
     * 3: r1 left r2 right
     * 4: r2 left r1 right
     */
    private int orient;
    
    public Connection(RoomRoot r1, RoomRoot r2, int o)
    {
        room1 = r1;
        room2 = r2;
        orient = o;
    }
    
    public int getOrient() {return orient;}
    
    public RoomRoot getRoom1() {return room1;}
    public RoomRoot getRoom2() {return room2;}
}
