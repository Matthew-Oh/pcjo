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
    private String activeLine; //active soure code lines
    private int line; //active source code line number
    private int linePos; //character position in activeLine
    private int figPos; //index position in simp
    private ArrayList<String> dataTypes = new ArrayList<String>(); //names of data types
    private ArrayList<String> variables = new ArrayList<String>(); //names of variables
    private ArrayList<String> methods = new ArrayList<String>(); //names of methods
    private ArrayList<Cypher> cypher = new ArrayList<Cypher>(); //cyphered names
    private File[] files;
    private Formatter log;
    
    //<PCJO_IGNORE_2>
    public static void main(String[] args)
    {
        if (args.length == 1)
            obfuscate(args[0]);
        else
            System.out.println("Error: 1 arguement expected, " 
            + args.length + " recieved");
        //Obfuscator ob = new Obfuscator("KTour.java");
        //Obfuscator ob = new Obfuscator("bsptest");
        //Obfuscator ob = new Obfuscator("test.java");
        //writeLog(ob.readWord());
    }
    
    /**
     * Obfuscates the given .java file, or all .java files in the folder within the input folder
     * @param path path to file or folder within input folder
     */
    public static void obfuscate(String path)
    {
        Obfuscator ob = new Obfuscator(path);
    }
    
    private void writeLog(String str)
    {
        System.out.println(str);
        log.format(str + "\n");
    }
    
    public Obfuscator(String path)
    {
        //creates log file
        log = null;
        try
        {
            log = new Formatter("output/log.txt.");
            writeLog("CREATED LOG FILE");
        }
        catch (Exception e)
        {
            System.out.println("ERROR: COULD NOT CREATE LOG FILE:\n" + e);
            return;
        }
        
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
        
        //writeLog(""+new File(path).isDirectory());
        File file = new File("input/" + path);
        
        //obfuscated according to file type
        if (file.isDirectory())
            obFolder(file);
        else
            obFile(file);
        
        log.close();
    }
    
    /**
     * Obfuscates all .java files in a folder
     */
    private void obFolder(File files)
    {
        writeLog("OBFUSCATE FOLDER " + files.getName());
        File[] allFiles = files.listFiles();
        ArrayList<File> javaFiles = new ArrayList<File>();
        //determines which files are .java's
        for (File f: allFiles)
        {
            String name = f.getName();
            if (name.substring(name.length()-5,name.length()).equals(".java"))
            {
                javaFiles.add(f);
                //registers file name as a data type
                String className = f.getName().substring(0,f.getName().length()-5);
                cypher.add(new Cypher(className));
                dataTypes.add(className);
            }
        }
        
        //simplifies and scans all java files
        //stores the simplified code for all files
        ArrayList<ArrayList<Figure>> fileFigs = new ArrayList<ArrayList<Figure>>();
        for (File f: javaFiles)
        {
            writeLog("\nOBFUSCATE FILE " + f.getName());
            //opens file
            try 
            {
                scanner = new Scanner(f);
            }
            catch (Exception e)
            {
                writeLog("\nERROR: COULD NOT READ FILE:\n" + e);
                return;
            }
            
            //reads file into text
            text = new ArrayList<String>();
            for (int i = 0; scanner.hasNext(); i++)
                text.add(scanner.nextLine());
            activeLine = text.get(0);
            linePos = 0;
            line = 0;
            figPos = 0;
            
            //simplifies code
            simplifyNQ();
            //scans simplified code for identification
            scanFig();
            
            fileFigs.add(simp);
            simp = new ArrayList<Figure>();
        }
        
        writeLog("\nSCAN DONE\n");
        //logs code cypher
        writeLog("CREATING CYPHER");
        generateCypher();
        for (Cypher c: cypher)
        {
            writeLog(c.getOriginal() + " // " + c.getCoded());
        }
        writeLog("");
        
        for (int i = 0; i < javaFiles.size(); i++)
        {
            File file = javaFiles.get(i);
            /*ArrayList<Figure> simpFile*/ simp = fileFigs.get(i);
            String name = file.getName().substring(0,file.getName().length()-5);
            //simp = simpFile;
            obSimp = new ArrayList<Figure>();
            //obfuscates the simplified file
            obfuscateFig();
            //obfuscates the file name
            for (int j = 0; j < cypher.size(); j++)
            {
                if (name.equals(cypher.get(j).getOriginal()))
                {
                    name = cypher.get(j).getCoded() + ".java";
                    j = cypher.size();
                }
            }
            //creates obfuscatee file
            createObfuscated(name);
        }
    }
    
    /**
     * Obfuscates a single file
     */
    private void obFile(File file)
    {
        writeLog("OBFUSCATE FILE " + file.getName());
        //opens file
        try 
        {
            scanner = new Scanner(file);
        }
        catch (Exception e)
        {
            writeLog("\nERROR: COULD NOT READ FILE:\n" + e);
            return;
        }
        
        String className = file.getName().substring(0,file.getName().length()-5);
        String obClassName = "obClassName";
        
        //reads file into text
        text = new ArrayList<String>();
        for (int i = 0; scanner.hasNext(); i++)
            text.add(scanner.nextLine());
        activeLine = text.get(0);
        
        simplifyNQ(); //simplified code
        scanFig(); //scans simplified code
        generateCypher(); //generates code cypher
        writeLog("SCAN DONE");
        //logs code cypher
        for (Cypher c: cypher)
        {
            writeLog(c.getOriginal() + " // " + c.getCoded());
            if (c.getOriginal().equals(className))
                obClassName = c.getCoded();
        }
        obfuscateFig(); //obfuscates simple code
        createObfuscated(obClassName + ".java"); //creates obfuscated file
        writeLog("DONE");
    }
    
    private void createObfuscated(String name)
    {
        //attempts to create file
        Formatter f = null;
        try
        {
            f = new Formatter("output/" + name);
            writeLog("CREATED FILE " + name);
        }
        catch (Exception e)
        {
            writeLog("ERROR: COULD NOT CREATE NEW FILE:\n" + e);
            return;
        }
        if (f != null)
        {   
            //adds first parts
            f.format(obSimp.get(0).getText());
            for (int i = 1; i < obSimp.size(); i++)
            {
                //adds subsequent parts, includes a space if the last
                //and current part are both words
                if (obSimp.get(i-1).getType() == Figure.FigureType.WORD
                && obSimp.get(i).getType() == Figure.FigureType.WORD)
                    f.format(" ");
                f.format(obSimp.get(i).getText());
            }
            f.close();
        }
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
                //checks if the figure is a name that needs to be obfuscated
                if (f.getType() == Figure.FigureType.WORD)
                {
                    String text = f.getText();
                    boolean nonOb = true; //tracks if figure is an obfuscated figure
                    for (int i = 0; i < cypher.size(); i++)
                    {
                        if (text.equals(cypher.get(i).getOriginal()))
                        {
                            obSimp.add(new Figure(cypher.get(i).getCoded(), Figure.FigureType.WORD));
                            nonOb = false;
                            i = cypher.size();
                        }
                    }
                    if (nonOb)// && f.getType() != Figure.FigureType.IGNORE)
                        obSimp.add(f);
                }
                else if (f.getType() != Figure.FigureType.IGNORE)
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
     * Simplifies code with quotes
     */
    private void simplifyNQ()
    {
        while (canAdvance())
        {
            seekNQ();
            //determines if the next character is the start of an identifier or a symbol
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
                simp.add(new Figure(symb,Figure.FigureType.SYMBOL));
                //checks if the character starts a quote
                if (symb.equals("\"") || symb.equals("\'"))
                {
                    String quote = "";
                    boolean flag = true;
                    advance();
                    while (flag)
                    {
                        if (canAdvance())
                        {
                            //should proabably count the number of slashes before the quote and judge based on that
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
                        {
                            //finishes reading without end quote, throws an error
                            writeLog("ERROR: MISSING END QUOTE");
                            flag = false;
                            return;
                        }
                        if (flag)
                            quote += activeLine.charAt(linePos);
                        advance();
                    }
                    simp.add(new Figure(quote,Figure.FigureType.QUOTE));
                    simp.add(new Figure(symb,Figure.FigureType.SYMBOL));
                }
                else
                    advance();
            }
        }
    }
    
    /**
     * Scans the condenced source code
     * obsolete (probably)????
     */
    private void scanFig()
    {
        int ignore = 0;
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
                writeLog("IMPORT: " + name);
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
                if (word.indexOf("<ignore") == 0)
                {
                    ignore = Integer.parseInt(word.substring(7,8));
                    word = simp.get(++figPos).getText();
                }
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
                        //ensures that the statement is actually a declaration
                        boolean valid = true;
                        while (fig.getType() == Figure.FigureType.SYMBOL)
                        {
                            if (!fig.getText().equals("[") && !fig.getText().equals("]"))
                                valid = false;
                            figPos++;
                            fig = simp.get(figPos);
                        }
                        if (valid)
                        {
                            String name = fig.getText();
                            figPos++;
                            //determines if the line is a method or a variable
                            if (simp.get(figPos).getText().equals("("))
                            {
                                if (ignore <= 0)
                                {
                                    //method declaration
                                    methods.add(name);
                                    //cypher.add(new Cypher(name));
                                    addCypher(name);
                                    writeLog("METHOD: " + name + " (" + word + ")");
                                }
                                else
                                {
                                    writeLog("IGNORE METHOD: " + name + " (" + word + ")");
                                    ignore--;
                                }
                                figPos++;
                            }
                            else
                            {
                                if (ignore <= 0)
                                {
                                    //variable declaration
                                    variables.add(name);
                                    //cypher.add(new Cypher(name));
                                    addCypher(name);
                                    writeLog("VARIABLE: " + name + " (" + word + ")");
                                }
                                else
                                {
                                    writeLog("IGNORE VARIABLE: " + name + " (" + word + ")");
                                    ignore--;
                                }
                                figPos++;
                            }
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
                    writeLog("CLASS: " + name);
                    while (simp.get(figPos).getType() != Figure.FigureType.SYMBOL)
                        figPos++;
                }
                else if (word.equals("enum"))
                {
                    figPos++;
                    String name = simp.get(figPos).getText();
                    dataTypes.add(name);
                    addCypher(name);
                    writeLog("ENUM: " + name);
                    figPos+=2;
                    
                    if (!simp.get(figPos).getText().equals("}"))
                    {
                        name = simp.get(figPos).getText();
                        variables.add(name);
                        addCypher(name);
                        figPos++;
                        
                        Figure fig = simp.get(figPos);
                        while (!fig.getText().equals("}"))
                        {
                            if (fig.getText().equals(","))
                            {
                                figPos++;
                                name = simp.get(figPos).getText();
                                variables.add(name);
                                addCypher(name);
                                figPos++;
                            }
                            else
                            {
                                //this should not occur, should throw error
                            }
                            fig = simp.get(figPos);
                        }
                    }
                }
                else
                {
                    figPos++;
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
                            //checks for manual ignore
                            String rem = activeLine.substring(linePos+2);
                            //checks if remaining line is long enough 
                            if (rem.length() >= 15)
                            {
                                //start of the ignore signal
                                int start = rem.indexOf("<PCJO_IGNORE_"); //15+ long
                                if (start >= 0)
                                {
                                    //end of the ignore signal
                                    int end = rem.indexOf(">");
                                    if (start < end)
                                    {
                                        //attempts to read the number
                                        try
                                        {
                                            int count = Integer.parseInt(rem.substring(start + 13,end));
                                            simp.add(new Figure("<ignore" + count + ">",Figure.FigureType.IGNORE));
                                        }
                                        catch (Exception e)
                                        {
                                            //does nothing if the number count not be read
                                        }
                                    }
                                }
                            }
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
     * version of readword that doesnt ignore quotes
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
