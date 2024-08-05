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
    private String tokenLexeme = null;
    private final TokenLocation location;

    //Public

    public Token(TokenTypes inputType, String inputString, int row, int column)
    {
        location = new TokenLocation(row, column);
        type = inputType;
        tokenLexeme = inputString;
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
            case TokenTypes.TIDEN -> getIDENString();
            case TokenTypes.TUNDF -> getUNDFString();
            default -> getTypeString(type);
        };
    }

    public String toStringWithLocation()
    {
        return switch (type) 
        {
            case TokenTypes.TIDEN -> getIDENString() + location.getLocationString();
            case TokenTypes.TUNDF -> getUNDFString();
            default -> getTypeString(type) + location.getLocationString();
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

    private String getUNDFString()
    {
        //TODO: Reformat this to be correct
        String out = getIDENString() + location.getLocationString();

        return out;
    }    

    private class TokenLocation
    {
        int row;
        int column;

        public TokenLocation(int _row, int _column)
        {
            row = _row;
            column = _column;
        }

        public String getLocationString()
        {
            //TODO: reformat this to be correct
            return ": At Row: "+row+", Column: "+column;
        }
    }
}