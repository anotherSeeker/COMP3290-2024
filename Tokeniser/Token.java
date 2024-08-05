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
    private String tokenString = null;

    //Strictly should probably use a hashmap instead of an enum but this is easier and won't matter
    public Token(TokenTypes InputType, String inputString)
    {
        if (InputType == TokenTypes.TIDEN || InputType == TokenTypes.TUNDF)
        {
            type = InputType;
            tokenString = inputString;
        }
        else
        {
            type = InputType;
            tokenString = "";
        }
    }

    private String getTypeString(TokenTypes type)
    {
        return tokenOutputStrings[type.ordinal()];
    }

    public TokenTypes getType()
    {
        return type;
    }

    public String getString()
    {
        return tokenString;
    }

    public String getFullIdenString()
    {
        if (type != TokenTypes.TIDEN || type != TokenTypes.TUNDF)
        {
            return getTypeString(type);
        }
        String out = getTypeString(type) + tokenString; 
        return out;
    }
    

}