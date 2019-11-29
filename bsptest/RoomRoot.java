
public class RoomRoot extends Room
{
    private RoomRoot child1;
    private RoomRoot child2;
    private Room room;
    private int id; //testing
    
    public RoomRoot(int xP, int yP, int xS, int yS)
    {
        super(xP,yP,xS,yS);
    }
    
    public RoomRoot getChild1() {return child1;}
    public void setChild1(RoomRoot child) {child1 = child;}
    public RoomRoot getChild2() {return child2;}
    public void setChild2(RoomRoot child) {child2 = child;}
        
    public Room getRoom() {return room;}
    public void setRoom(Room r) {room = r;}
    
    //testing
    public void setId (int num) {id = num;}
    public int getId () {return id;}
}
