package Tokeniser;

public class Token
{
    private final String[] tokenOutputStrings = {
        "TTEOF ", 
        "TCD24 ", "TCONS ", "TTYPD ", "TTDEF ", "TARRD ", "TMAIN ", "TBEGN ", "TTEND ", "TARAY ", "TTTOF ", "TFUNC ", "TVOID ", 
        "TCNST ", "TINTG ", "TFLOT ", "TBOOL ", "TTFOR ", "TREPT ", "TUNTL ", "TTTDO ", "TWHIL ", "TIFTH ", "TELSE ", "TELIF ",
        "TSWTH ", "TCASE ", "TDFLT ", "TBREK ", "TINPT ", "TPRNT ", "TPRLN ", "TRETN ", "TNOTT ", "TTAND ", "TTTOR ", "TTXOR ", 
        "TTRUE ", "TFALS ", "TCOMA ", "TLBRK ", "TRBRK ", "TLPAR ", "TRPAR ", "TEQUL ", "TPLUS ", "TMINS ", "TSTAR ", "TDIVD ",  
        "TPERC ", "TCART ", "TLESS ", "TGRTR ", "TCOLN ", "TSEMI ", "TDOTT ", "TLEQL ", "TGEQL ", "TNEQL ", "TEQEQ ", "TPLEQ ", 
        "TMNEQ ", "TSTEQ ", "TDVEQ ", "TIDEN ", "TILIT ", "TFLIT ", "TSTRG ", 
        "TUNDF "
    };
    
    private TokenTypes type = TokenTypes.TUNDF;

    //for idents and strings
    private String tokenLexeme = null;

    //for flit and ilit
    private double tokenNumber = 0;

    private int line;
    private int column;

    //Public

    public Token(TokenTypes inputType, String inputString, int _line, int _column)
    {
        line = _line;
        column = _column;
        type = inputType;
        tokenLexeme = inputString;
    }

    public Token(TokenTypes inputType, double inputNumber, int _line, int _column)
    {
        line = _line;
        column = _column;
        type = inputType;
        tokenNumber = inputNumber;
    }

    public TokenTypes getType()
    {
        return type;
    }

    public String getLexeme()
    {
        return tokenLexeme;
    }

    public @Override String toString()
    {
        return switch (type) 
        {
            case TIDEN -> getIDENString();
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
            case TFLIT -> getNumberString() + getLocationString();
            case TILIT -> getNumberString() + getLocationString();
            case TUNDF -> getIDENString()   + getLocationString();
            default -> getTypeString(type)  + getLocationString();
        };
    }

    //Private

    private String getTypeString(TokenTypes type)
    {
        return tokenOutputStrings[type.ordinal()];
    }

    private String getIDENString()
    {
        String out = getTypeString(type) + tokenLexeme; 
        return out;
    }

    private String getNumberString()
    {
       String out = getTypeString(type) + tokenNumber;
       return out;
    }

    public String getLocationString()
    {
        return "(Line: "+line+", Column: "+column+")";
    }
}