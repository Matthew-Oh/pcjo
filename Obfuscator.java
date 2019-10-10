import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;

/**
 * Class Obfuscator
 * Obfuscates Java code
 */
public class Obfuscator
{
    private Scanner scanner; //reads the source code
    private ArrayList<String> text; //source code condenced into a list of Strings
    private ArrayList<Figure> simp = new ArrayList<Figure>(); //source code condenced into a list of "Figures"
    private String activeLine; //active soure code line
    private int line; //active source code line number
    private int linePos; //character position in activeLine
    private int figPos; //index position in simp
    private ArrayList<String> dataTypes = new ArrayList<String>(); //names of data types
    private ArrayList<String> variables = new ArrayList<String>(); //names of variables
    private ArrayList<String> methods = new ArrayList<String>(); //names of methods
    
    //obsolete, use for testing later or something
    public enum StatementType
    {IMPORT, CLASSDEC, VARDEC, METHODDEC, OTHER}
    
    public static void main(String[] args)
    {
        Obfuscator ob = new Obfuscator("Obfuscator.java");
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
        
        simplify();
        scanFig();
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
     * Scans the condenced source code
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
                            figPos++;
                            System.out.println("MTDDEC: " + name + " (" + word + ")");
                        }
                        else
                        {
                            //variable declaration
                            variables.add(name);
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
                    System.out.println("CLSDEC: " + name);
                }
                else
                {
                    figPos++;
                }
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
