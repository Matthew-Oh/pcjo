public class Tile
{
    private int id; //testing
    private RoomRoot root;
    private boolean open;
    
    public Tile(){open = false;}
    public Tile (int i) {id = i; open = false;}
    public Tile(RoomRoot room) {root = room; open = false;}
    
    public void setOpen(boolean o) {open = o;}
    public boolean isOpen() {return open;}
    
    public RoomRoot getRoot() {return root;}
    
	//PCJO_IGNORE_1
    public String toString() 
    {
        if (id < 10)
            return "0" + Integer.toString(id);
        return Integer.toString(id);
    }
    public void setId (int num) {id = num;}
    public int getId () {return id;}
}
