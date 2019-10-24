import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;

/**
 * Class Obfuscator
 * Obfuscates Java code
 */
public class Obfuscator
{
    private Scanner scanner; //reads the source code
    private ArrayList<String> text; //source code condenced into a list of Strings
    private ArrayList<Figure> simp = new ArrayList<Figure>(); //source code condenced into a list of "Figures"
    private ArrayList<Figure> obSimp = new ArrayList<Figure>(); //obfuscated form of simp
    private String activeLine; //active soure code line
    private int line; //active source code line number
    private int linePos; //character position in activeLine
    private int figPos; //index position in simp
    private ArrayList<String> dataTypes = new ArrayList<String>(); //names of data types
    private ArrayList<String> variables = new ArrayList<String>(); //names of variables
    private ArrayList<String> methods = new ArrayList<String>(); //names of methods
    private ArrayList<Cypher> cypher = new ArrayList<Cypher>(); //cyphered names
    
    //obsolete, use for testing later or something
    public enum StatementType
    {IMPORT, CLASSDEC, VARDEC, METHODDEC, OTHER}
    
    public static void main(String[] args)
    {
        Obfuscator ob = new Obfuscator("KTour.java");
        //System.out.println(ob.readWord());
    }
    
    public Obfuscator(String path)
    {
        //adds basic data types
        dataTypes.add("boolean");
        dataTypes.add("byte");
        dataTypes.add("char");
        dataTypes.add("short");
        dataTypes.add("int");
        dataTypes.add("long");
        dataTypes.add("float");
        dataTypes.add("double");
        dataTypes.add("void");
        //implement enum stuff
        //dataTypes.add("enum");
        dataTypes.add("String");
        dataTypes.add("Object");
        dataTypes.add("Exception");
        
        //opens file
        try 
        {
            scanner = new Scanner(new File(path));
        }
        catch (Exception e)
        {
            //dont
        }
        
        //reads file into text
        text = new ArrayList<String>();
        for (int i = 0; scanner.hasNext(); i++)
            text.add(scanner.nextLine());
        activeLine = text.get(0);
        
        simplifyNQ();
        scanFig();
        generateCypher();
        System.out.println("SCAN DONE");
        generateCypher();
        for (Cypher c: cypher)
        {
            System.out.println(c.getOriginal() + " // " + c.getCoded());
        }
        obfuscateFig();
        createObfuscated();
        System.out.println("DONE");
    }
    
    private void createObfuscated()
    {
        Formatter f = null;
        try
        {
            f = new Formatter("obfuscatedfile");
            System.out.println("FILE CREATED");
        }
        catch (Exception e)
        {
            System.out.println("FILE ERROR");
        }
        if (f != null)
        {   
            f.format(obSimp.get(0).getText());
            for (int i = 1; i < obSimp.size(); i++)
            {
                if (obSimp.get(i-1).getType() == Figure.FigureType.WORD
                && obSimp.get(i).getType() == Figure.FigureType.WORD)
                    f.format(" ");
                f.format(obSimp.get(i).getText());
            }
        }
        f.close();
    }
    
    /**
     * Creates an obfuscated version of simp using cypher
     */
    private void obfuscateFig()
    {
        boolean classStarted = false; //tracks if class has been declared
        for (Figure f: simp)
        {
            if (classStarted)
            {
                boolean nonOb = true; //tracks if figure is an obfuscated figure
                for (int i = 0; i < cypher.size(); i++)
                {
                    if (f.getText().equals(cypher.get(i).getOriginal()))
                    {
                        obSimp.add(new Figure(cypher.get(i).getCoded(), Figure.FigureType.WORD));
                        nonOb = false;
                        i = cypher.size();
                    }
                }
                if (nonOb)
                    obSimp.add(f);
            }
            else
            {
                obSimp.add(f);
                if (f.getText().equals("class"))
                    classStarted = true;
            }
        }
    }
    
    /**
     * Creates code names for cyphers
     */
    private void generateCypher()
    {
        int count = 0;
        for (Cypher c: cypher)
        {
            c.setCoded("_" + count++);
        }
    }
    
    /**
     * Simplifies the source code into a chain of Figures
     */
    private void simplify()
    {
        while (canAdvance())
        {
            seek();
            if (Character.isLetterOrDigit(activeLine.charAt(linePos)))
            {
                String word = readWord();
                //System.out.print(word + " ");
                simp.add(new Figure(word,Figure.FigureType.WORD));
            }
            else
            {
                String symb = activeLine.substring(linePos,linePos+1);
                //System.out.print(symb);
                simp.add(new Figure(symb,Figure.FigureType.SYMB));
                advance();
            }
        }
    }
    
    /**
     * simplifies code with quotes
     * wip stuff fix later idiot
     */
    private void simplifyNQ()
    {
        while (canAdvance())
        {
            seekNQ();
            if (Character.isLetterOrDigit(activeLine.charAt(linePos)))
            {
                String word = readWordNQ();
                //System.out.print(word + " ");
                simp.add(new Figure(word,Figure.FigureType.WORD));
            }
            else
            {
                String symb = activeLine.substring(linePos,linePos+1);
                //System.out.print(symb);
                simp.add(new Figure(symb,Figure.FigureType.SYMB));
                if (symb.equals("\"") || symb.equals("\'"))
                {
                    String quote = "";
                    boolean flag = true;
                    advance();
                    while (flag)
                    {
                        if (canAdvance())
                        {
                            //ensures that found quote is not a control quote
                            if (activeLine.substring(linePos,linePos+1).equals(symb))
                            {
                                if (activeLine.substring(linePos-1,linePos).equals("\\"))
                                {
                                    if (activeLine.substring(linePos-2,linePos-1).equals("\\"))
                                        flag = false;
                                    else
                                        flag = true;
                                }
                                else
                                    flag = false;
                            }
                        }
                        else
                            flag = false;
                        if (flag)
                            quote += activeLine.charAt(linePos);
                        advance();
                    }
                    simp.add(new Figure(quote,Figure.FigureType.QUTE));
                    simp.add(new Figure(symb,Figure.FigureType.SYMB));
                }
                else
                    advance();
            }
        }
    }
    
    /**
     * Scans the condenced source code
     * obsolete (probably)
     */
    private void scanFig()
    {
        while (figPos < simp.size())
        {
            String word = simp.get(figPos).getText();
            if (word.equals("import"))
            {
                //imported classes
                figPos++;
                word = simp.get(figPos).getText();
                while (!simp.get(figPos+1).getText().equals(";"))
                {
                    figPos++;
                    word = simp.get(figPos).getText();
                }
                String name = simp.get(figPos).getText();
                //IMPORT
                dataTypes.add(name);
                figPos++;
                System.out.println("IMPORT: " + name);
            }
            else if (word.equals("new"))
            {
                do
                {
                    figPos++;
                } while (!simp.get(figPos).getText().equals(";"));
            }
            else
            {
                //method/variable declaration
                //ignores access type
                if (word.equals("public") || word.equals("private") || word.equals("protected"))
                    word = simp.get(++figPos).getText();
                if (word.equals("static"))
                    word = simp.get(++figPos).getText();
                //checks if dataType is detected
                if (checkDataType(word))
                {
                    figPos++;
                    //ignores contstructors
                    if (!simp.get(figPos).getText().equals("("))
                    {
                        //ignores generics
                        if (simp.get(figPos).getText().equals("<"))
                            figPos += 3;
                        Figure fig = simp.get(figPos);
                        while (fig.getType() == Figure.FigureType.SYMB)
                        {
                            figPos++;
                            fig = simp.get(figPos);
                        }
                        String name = fig.getText();
                        figPos++;
                        //determines if the line is a method or a variable
                        if (simp.get(figPos).getText().equals("("))
                        {
                            //method declaration
                            methods.add(name);
                            //cypher.add(new Cypher(name));
                            addCypher(name);
                            figPos++;
                            System.out.println("MTDDEC: " + name + " (" + word + ")");
                        }
                        else
                        {
                            //variable declaration
                            variables.add(name);
                            //cypher.add(new Cypher(name));
                            addCypher(name);
                            figPos++;
                            System.out.println("VARDEC: " + name + " (" + word + ")");
                        }
                    }
                }
                else if (word.equals("class"))
                {
                    //class declaration
                    //CLASSDEC
                    figPos++;
                    String name = simp.get(figPos).getText();
                    figPos++;
                    dataTypes.add(name);
                    //cypher.add(new Cypher(name));
                    addCypher(name);
                    System.out.println("CLSDEC: " + name);
                    while (simp.get(figPos).getType() != Figure.FigureType.SYMB)
                        figPos++;
                }
                else
                {
                    figPos++;
                }
            }
        }
    }
    
    /**
     * version of scanFig that ignores quotes
     */
    private void scanFigNQ()
    {
        while (figPos < simp.size())
        {
            if (simp.get(figPos).getType() == Figure.FigureType.SYMB)
            {
                String symbol = simp.get(figPos).getText();
                //if a quotation is found, dont scan data until next quote
                if (symbol.equals("\"") || symbol.equals("\'"))
                {
                    boolean flag = true;
                    while (flag)
                    {
                        figPos++;
                        //ensures that found quote is not a control quote
                        if (simp.get(figPos).getText().equals(symbol))
                        {
                            if (simp.get(figPos-1).getText().equals("\\"))
                            {
                                if (simp.get(figPos-2).getText().equals("\\"))
                                    flag = false;
                                else
                                    flag = true;
                            }
                            else
                                flag = false;
                        }
                    }
                }
            }
            else if (simp.get(figPos).getType() == Figure.FigureType.WORD)
            {
                String word = simp.get(figPos).getText();
                if (word.equals("import"))
                {
                    //imported classes
                    figPos++;
                    word = simp.get(figPos).getText();
                    while (!simp.get(figPos+1).getText().equals(";"))
                    {
                        figPos++;
                        word = simp.get(figPos).getText();
                    }
                    String name = simp.get(figPos).getText();
                    //IMPORT
                    dataTypes.add(name);
                    figPos++;
                    System.out.println("IMPORT: " + name);
                }
                else if (word.equals("new"))
                {
                    do
                    {
                        figPos++;
                    } while (!simp.get(figPos).getText().equals(";"));
                }
                else
                {
                    //method/variable declaration
                    //ignores access type
                    if (word.equals("public") || word.equals("private") || word.equals("protected"))
                        word = simp.get(++figPos).getText();
                    if (word.equals("static"))
                        word = simp.get(++figPos).getText();
                    //checks if dataType is detected
                    if (checkDataType(word))
                    {
                        figPos++;
                        //ignores contstructors
                        if (!simp.get(figPos).getText().equals("("))
                        {
                            //ignores generics
                            if (simp.get(figPos).getText().equals("<"))
                                figPos += 3;
                            Figure fig = simp.get(figPos);
                            while (fig.getType() == Figure.FigureType.SYMB)
                            {
                                figPos++;
                                fig = simp.get(figPos);
                            }
                            String name = fig.getText();
                            figPos++;
                            //determines if the line is a method or a variable
                            if (simp.get(figPos).getText().equals("("))
                            {
                                //method declaration
                                methods.add(name);
                                //cypher.add(new Cypher(name));
                                addCypher(name);
                                figPos++;
                                System.out.println("MTDDEC: " + name + " (" + word + ")");
                            }
                            else
                            {
                                //variable declaration
                                variables.add(name);
                                //cypher.add(new Cypher(name));
                                addCypher(name);
                                figPos++;
                                System.out.println("VARDEC: " + name + " (" + word + ")");
                            }
                        }
                    }
                    else if (word.equals("class"))
                    {
                        //class declaration
                        //CLASSDEC
                        figPos++;
                        String name = simp.get(figPos).getText();
                        figPos++;
                        dataTypes.add(name);
                        //cypher.add(new Cypher(name));
                        addCypher(name);
                        System.out.println("CLSDEC: " + name);
                        while (simp.get(figPos).getType() != Figure.FigureType.SYMB)
                            figPos++;
                    }
                    else
                    {
                        figPos++;
                    }
                }
            }
        }
    }
    
    /**
     * Creates a new cypher if the original name is unique
     */
    private void addCypher(String name)
    {
        boolean unique = true;
        for (int i = 0; i < cypher.size(); i++)
        {
            if (cypher.get(i).getOriginal().equals(name))
            {
                unique = false;
                i = cypher.size();
            }
        }
        if (unique)
            cypher.add(new Cypher(name));
    }
    
    /**
     * version of seek that does not ignore quotes
     * wip stuff, should make irrelevant later
     */
    private void seekNQ()
    {
        boolean cont = true;
        boolean flag;
        while (cont)
        {
            if (linePos >= activeLine.length())
                nextLine();
            switch (activeLine.charAt(linePos))
            {
                case ' ':
                    advance();
                    break;
                //ignores comments
                case '/':
                    //determines if the slash could be a comment
                    if (linePos + 1 < activeLine.length())
                    {
                        //determines if the slash is a single or multiline comment, or not a comment
                        if (activeLine.charAt(linePos+1) == '/')
                        {
                            //skips to the next line
                            nextLine();
                        }
                        else if (activeLine.charAt(linePos+1) == '*')
                        {
                            //ignores all data until the end of the multiline comment
                            advance(); advance();
                            flag = true;
                            while (flag)
                            {
                                //checks if next two characters are the end of the comment
                                if (linePos + 1 < activeLine.length()
                                && activeLine.charAt(linePos) == '*'
                                && activeLine.charAt(linePos+1) == '/')
                                    {advance(); advance(); flag = false;}
                                else
                                {
                                    //continues
                                    advance();
                                    if (!canAdvance())
                                    {
                                        flag = false;
                                        cont = false;
                                    }
                                }
                            }
                        }
                        else
                            cont = false;
                    }
                    else
                        cont = false;
                    break;
                default:
                    cont = false;
            }
        }
    }
    
    /**
     * Seeks to non-white space
     */
    private void seek()
    {
        boolean cont = true;
        boolean flag;
        while (cont)
        {
            if (linePos >= activeLine.length())
                nextLine();
            switch (activeLine.charAt(linePos))
            {
                case ' ':
                    advance();
                    break;
                //ignores comments
                case '/':
                    //determines if the slash could be a comment
                    if (linePos + 1 < activeLine.length())
                    {
                        //determines if the slash is a single or multiline comment, or not a comment
                        if (activeLine.charAt(linePos+1) == '/')
                        {
                            //skips to the next line
                            nextLine();
                        }
                        else if (activeLine.charAt(linePos+1) == '*')
                        {
                            //ignores all data until the end of the multiline comment
                            advance(); advance();
                            flag = true;
                            while (flag)
                            {
                                //checks if next two characters are the end of the comment
                                if (linePos + 1 < activeLine.length()
                                && activeLine.charAt(linePos) == '*'
                                && activeLine.charAt(linePos+1) == '/')
                                    {advance(); advance(); flag = false;}
                                else
                                {
                                    //continues
                                    advance();
                                    if (!canAdvance())
                                    {
                                        flag = false;
                                        cont = false;
                                    }
                                }
                            }
                        }
                        else
                            cont = false;
                    }
                    else
                        cont = false;
                    break;
                //ignores quotes
                case '\"':
                    advance();
                    flag = true;
                    while (flag)
                    {
                        //checks for the end quote
                        if (linePos < activeLine.length()
                        && activeLine.charAt(linePos) == '\"')
                        {advance(); flag = false;}
                        else
                        {
                            //continues
                            advance();
                            if (!canAdvance())
                            {
                                flag = false;
                                cont = false;
                            }
                        }
                    }
                    break;
                case '\'':
                    advance();
                    flag = true;
                    while (flag)
                    {
                        //checks for the end quote
                        if (linePos < activeLine.length()
                        && activeLine.charAt(linePos) == '\'')
                        {advance(); flag = false;}
                        else
                        {
                            //continues
                            advance();
                            if (!canAdvance())
                            {
                                flag = false;
                                cont = false;
                            }
                        }
                    }
                    break;
                default:
                    cont = false;
            }
        }
    }
    
    /**
     * Determines if given word is a data type in dataTypes
     */
    private boolean checkDataType(String str)
    {
        for (String type: dataTypes)
        {
            if (str.equals(type))
                return true;
        }
        return false;
    }
    
    /**
     * Reads a word from the current postion in the current string
     */
    private String readWord()
    {
        seek();
        if (canAdvance())
        {
            String word = "";
            char character = activeLine.charAt(linePos);
            while(Character.isLetterOrDigit(character))
            {
                word += character;
                advance();
                character = activeLine.charAt(linePos);
            }
            return word;
        }
        return null;
    }
    
    /**
     * version of readword that doesnt ignore quotes
     * wip stuff
     */
    private String readWordNQ()
    {
        seekNQ();
        if (canAdvance())
        {
            String word = "";
            char character = activeLine.charAt(linePos);
            while(Character.isLetterOrDigit(character))
            {
                word += character;
                advance();
                character = activeLine.charAt(linePos);
            }
            return word;
        }
        return null;
    }
    
    //continues to the next line of the code
    private boolean nextLine()
    {
        if (line >= text.size() -1)
            return false;
        activeLine = text.get(++line);
        linePos = 0;
        return true;
    }
    
    //determines if the end of text is reached
    private boolean canAdvance()
    {
        return linePos < activeLine.length() || line < text.size() - 1;
    }
    
    //continues to the next space
    private boolean advance()
    {
        if (++linePos >= activeLine.length())
            return nextLine();
        return true;
    }
}
