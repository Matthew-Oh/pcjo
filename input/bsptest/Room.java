
public class Room
{
    private int xPos;
    private int yPos;
    private int xScl;
    private int yScl;
    
    public Room(int xP, int yP, int xS, int yS)
    {
        xPos = xP;
        yPos = yP;
        xScl = xS;
        yScl = yS;
    }
    
    public void setXPos(int xP) {xPos = xP;}
    public void setYPos(int yP) {yPos = yP;}
    public void setXScl(int xS) {xScl = xS;}
    public void setYScl(int yS) {yScl= yS;}
    
    public int getXPos() {return xPos;}
    public int getYPos() {return yPos;}
    public int getXScl() {return xScl;}
    public int getYScl() {return yScl;}
}
