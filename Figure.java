
/**
 * Class Figure
 * A word or symbol
 */
public class Figure
{
    public enum FigureType
    {WORD, SYMB}
    
    private String text;
    private FigureType type;
    
    public Figure(String te, FigureType ty)
    {
        text = te;
        type = ty;
    }
    
    public String getText()
    {
        return text;
    }
    
    public String toString()
    {
        return text;
    }
    
    public FigureType getType()
    {
        return type;
    }
}
