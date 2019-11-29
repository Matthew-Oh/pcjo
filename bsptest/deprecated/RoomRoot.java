
public class RoomRoot extends Room
{
    private Room room;
    private int id; //testing
    /**
     * Location of room exits
     * (clockwise from top)
     * 0,2 give x position
     * 1,3 give y position
     */
    private int[] exits; //location of room exits (start top clockwise)
    
    public RoomRoot(int xP, int yP, int xS, int yS)
    {
        super(xP,yP,xS,yS);
    }
    
    public int getExit(int s) {return exits[s];}
    
    public Room getRoom() {return room;}
    public void setRoom(Room r) 
    {
        room = r;
        exits = new int[4];
        exits[0] = (int)(Math.random() * r.getXScl()) + r.getXPos();
        exits[2] = (int)(Math.random() * r.getXScl()) + r.getXPos();
        exits[1] = (int)(Math.random() * r.getYScl()) + r.getYPos();
        exits[3] = (int)(Math.random() * r.getYScl()) + r.getYPos();
    }
    
    //testing
    public void setId (int num) {id = num;}
    public int getId () {return id;}
}
