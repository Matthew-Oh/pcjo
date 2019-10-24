
/**
 * Class Cypher
 * Identifies a variable/class with a obfuscated name
 */
public class Cypher
{
    private String original; //original name
    private String coded; //coded name
    
    public Cypher(String o)
    {
        original = o;
        coded = null;
    }
    
    /**
     * Sets the code name
     */
    public void setCoded(String c)
    {coded = c;}
    
    public String getOriginal()
    {return original;}
    
    public String getCoded()
    {return coded;}
}
