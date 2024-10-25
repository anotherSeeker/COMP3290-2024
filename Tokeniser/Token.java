package Tokeniser;

import cd24FileReader.LexChar;
import java.util.ArrayList;

public class Token
{
    private final String RESET = "\u001B[0m";
    private final String RED = "\u001B[31m";
    private final String GREEN = "\u001B[32m";
    private final String BLUE =  "\u001B[34m";
    //private final String CYAN = "\u001B[36m";
    private final String bCYAN = "\u001B[96m";
    //private final String MAGENTA = "\u001B[35m";
    private final String bMAGENTA = "\u001B[95m";
    //private final String YELLOW = "\u001B[33m";
    //private final String bYELLOW = "\u001B[93m";
    //private final String bBLUE = "\u001B[94m";
    //private final String bGREEN = "\u001B[92m";
    //private final String bRED = "\u001B[91m";


    public final String[] tokenOutputStrings = {
        "TTEOF ", 
        "TCD24 ", "TCONS ", "TTYPD ", "TTDEF ", "TARRD ", "TMAIN ", "TBEGN ", "TTEND ", "TARAY ", "TTTOF ", "TFUNC ", "TVOID ", 
        "TCNST ", "TINTG ", "TFLOT ", "TBOOL ", "TTFOR ", "TREPT ", "TUNTL ", "TTTDO ", "TWHIL ", "TIFTH ", "TELSE ", "TELIF ",
        "TSWTH ", "TCASE ", "TDFLT ", "TBREK ", "TINPT ", "TPRNT ", "TPRLN ", "TRETN ", "TNOTT ", "TTAND ", "TTTOR ", "TTXOR ", 
        "TTRUE ", "TFALS ", "TCOMA ", "TLBRK ", "TRBRK ", "TLPAR ", "TRPAR ", "TEQUL ", "TPLUS ", "TMINS ", "TSTAR ", "TDIVD ",  
        "TPERC ", "TCART ", "TLESS ", "TGRTR ", "TCOLN ", "TSEMI ", "TDOTT ", "TLEQL ", "TGEQL ", "TNEQL ", "TEQEQ ", "TPLEQ ", 
        "TMNEQ ", "TSTEQ ", "TDVEQ ", "TIDEN ", "TILIT ", "TFLIT ", "TSTRG ", 
        "TUNDF "
    };
    public static final String[] keyTokenStrings = {
        "\u001a", 
        "cd24", "constants", "typedef", "def", "arraydef", "main", "begin", "end", "array", "of", "func", "void",
        "const", "int", "float", "bool", "for", "repeat", "until", "do", "while", "if", "else" , "elif",
        "switch", "case", "default", "break", "input", "print", "printline", "return", "not", "and", "or", "xor",
        "true", "false", ",", "[", "]", "(", ")", "=", "+", "-", "*", "/",
        "%", "^", "<", ">", ":", ";", ".", "<=", ">=", "!=", "==", "+=", 
        "-=", "*=", "/="    
    }; 
    
    private TokenTypes type = TokenTypes.TUNDF;

    //for idents, strings, keys and operators
    private ArrayList<LexChar> lexBuffer = null;

    //for flit and ilit
    private double tokenNumber = 0;

    //short description for undefined tokens
    private String tokenError = "N/A";

    //used and set by symboltable
    public boolean isAssignment;
    public boolean isDefinition;

    private final int line;
    private final int column;

    private int index = -1;

    //Public

    public Token(TokenTypes inputType, ArrayList<LexChar> _lexBuffer, int _line, int _column)
    {
        type = inputType;
        lexBuffer = _lexBuffer;
        line = _line;
        column = _column;
    }

    public Token(TokenTypes inputType, ArrayList<LexChar> _lexBuffer, double inputNumber, int _line, int _column)
    {
        type = inputType;
        tokenNumber = inputNumber;
        lexBuffer = _lexBuffer;
        line = _line;
        column = _column;
    }

    public Token(String errorDescription, ArrayList<LexChar> _lexBuffer, int _line, int _column)
    {
        type = TokenTypes.TUNDF;
        lexBuffer = _lexBuffer;
        tokenError = errorDescription;
        line = _line;
        column = _column;
    }

    public TokenTypes getType()
    {
        return type;
    }

    public String getTypeString()
    {
        return type.toString();
    }

    public String getLexeme()
    {
        return getBufferString(lexBuffer);
    }

    public String getError()
    {
        return tokenError;
    }

    public @Override String toString()
    {
        return switch (type) 
        {
            case TIDEN -> getIDENString();
            case TSTRG -> getIDENString();
            case TFLIT -> getNumberString();
            case TILIT -> getNumberString();
            case TUNDF -> getIDENString();
            default -> getTypeString(type);
        };
    }

    public String toStringWithLocation()
    {
        return switch (type) 
        {
            case TIDEN -> getIDENString()   + getLocationString();
            case TSTRG -> getIDENString()   + getLocationString();
            case TFLIT -> getNumberString() + getLocationString();
            case TILIT -> getNumberString() + getLocationString();
            case TUNDF -> getIDENString()   + getLocationString();
            default -> getTypeString(type)  + getLocationString();
        };
    }

    public String toStringError() 
    {
        return switch (type) 
        {
            case TUNDF -> getIDENString()   + getLocationString() + "\n\t: " + getError();
            default -> "There is no error you shouldn't see this";
        };
    }

    //Private

    private String getTypeString(TokenTypes type)
    {
        return tokenOutputStrings[type.ordinal()];
    }

    private String getIDENString()
    {
        String out = getTypeString(type) + getLexeme(); 
        return out;
    }

    private String getNumberString() 
    {
        String num;
        if (type == TokenTypes.TILIT)
            num = ""+(int)tokenNumber;
        else
            num = ""+tokenNumber;

        String out = getTypeString(type) + num;
        return out;
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return column;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int _index)
    {
        index = _index;
    }

    public boolean matchLocation(Token inputToken)
    {
        //if lines and columns are identical tokens match, technically this could be wrong but you'd have to be messing with the tokens after they're output so just don't do that
        return (inputToken.getLine() == this.line && inputToken.getColumn() == this.column);
    }

    public String getLocationString()
    {
        return "(Line: "+line+", Column: "+column+")";
    }

    public String getLocationStringCols()
    {
        return GREEN+"(Line: "+bMAGENTA+line+GREEN+", Column: "+bCYAN+column+GREEN+")"+RESET;
    }

    public String getLocationStringErr()
    {
        return RED+"("+BLUE+"Line: "+BLUE+line+RED+", "+GREEN+"Column: "+GREEN+column+RED+")"+RESET;
    }

    private String getBufferString(ArrayList<LexChar> lexBuffer)
    {
        String out = "";
        for (LexChar lexChar : lexBuffer) 
        {
            out += lexChar.getCharacter();
        }

        return out;
    }

    public static boolean compareTypeAsString(Token tok, String strTok)
    {
        return tok.getType().toString().strip().equalsIgnoreCase(strTok);
    }

    public boolean isValueToken()
    {
        return (type == TokenTypes.TILIT || type == TokenTypes.TFLIT || type == TokenTypes.TSTRG || type == TokenTypes.TIDEN);
    }

    public boolean matchIdentifier(Token inputToken)
    {
        //idents are case sensitive
        return this.getLexeme().equals(inputToken.getLexeme());
    }

    public boolean isAfter(Token otherToken)
    {
        if (getLine() > otherToken.getLine())
        {
            return true;
        }
        else if  (getLine() == otherToken.getLine())
        {
            if (getColumn() > otherToken.getColumn())
            {
                return true;
            }
        }

        return false;
    }

}