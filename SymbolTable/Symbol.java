package SymbolTable;
import Tokeniser.Token;
import java.util.ArrayList;

public class Symbol 
{
    public static enum symTypes{
        ID, intg, flot, bool, 
        structID, typeID, funcID, undf, programName
    }

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE =  "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String bCYAN = "\u001B[96m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String bMAGENTA = "\u001B[95m";
    private static final String YELLOW = "\u001B[33m";
    private static final String bYELLOW = "\u001B[93m";
    private static final String bBLUE = "\u001B[94m";
    private static final String bGREEN = "\u001B[92m";
    private static final String bRED = "\u001B[91m";

    private final Token firstToken;
    private symTypes type;
    private String subtype = null;
    private final ArrayList<Token> values = new ArrayList<>(); 
    private final ArrayList<Token> occurances = new ArrayList<>();
    private final Scope parentScope;

    public static symTypes typeFromString(String returnType)
    {
        try
        {
           return symTypes.valueOf(returnType);  
        }
        catch (Exception e)
        {
            //don't bother with error, we handle null cases outside of this
            return null;
        }
    }

    public Symbol(Token _token, symTypes _type, Scope scope)
    {
        firstToken = _token;
        type = _type;
        occurances.add(_token);
        parentScope = scope;
    }

    public Symbol(Token _token, symTypes _type, Scope scope, String _subtype)
    {
        firstToken = _token;
        type = _type;
        occurances.add(_token);
        parentScope = scope;
        subtype = _subtype;
    }

    @Override
    public String toString()
    {
        String outString = "\t";

        if (type!=symTypes.undf)
            outString+=GREEN+"Name: "+BLUE+firstToken.getLexeme()+RESET+" : ";
        else
            outString+=GREEN+"Name: "+RED+firstToken.getLexeme()+RESET+" : ";

        outString+=GREEN+"Type: ";
        switch(type)
        {
            case symTypes.ID -> outString+=RESET;
            case symTypes.structID -> outString+=CYAN;
            case symTypes.typeID -> outString+=bMAGENTA;
            case symTypes.funcID -> outString+=bYELLOW;
            case symTypes.intg -> outString+=RED;
            case symTypes.flot -> outString+=bBLUE;
            case symTypes.bool -> outString+=BLUE;
            case symTypes.undf -> outString+=bRED;
            case symTypes.programName -> outString+=bCYAN;
        }
        outString+=type.toString()+RESET+" : ";

        if (subtype != null)
            outString+=GREEN+"Subtype: "+bYELLOW+subtype+RESET+" : ";

        switch (parentScope.getName()) {
            case "Global" -> outString+=GREEN+"Scope: "+bCYAN+parentScope.getName()+RESET+" : ";
            case "Main" -> outString+=GREEN+"Scope: "+MAGENTA+parentScope.getName()+RESET+" : ";
            default -> outString+=GREEN+"Scope: "+YELLOW+parentScope.getName()+RESET+" : ";
        }

        outString+=bGREEN+"\n\tOccurances: ";

        for (int i = 0;i<occurances.size();i++)
        {
            Token occurance = occurances.get(i);

            outString+="\n\t\t"+bBLUE+i+": ";
            outString+=occurance.getLocationStringCols();

            if (occurance.isDefinition)
                outString+=BLUE+" : Definition"+RESET;
            if (occurance.isAssignment)
                outString+=bBLUE+" : Assignment"+RESET;
        }

        int counter = 1;
        if (!values.isEmpty())
            outString+=bGREEN+"\n\tValues: "+RESET;

        for (Token tok : values)
        {
            outString+="\n\t\t"+counter+": "+bRED+tok.getLexeme()+RESET;
            counter++;
        }

        return outString;
    }

    public boolean exists(String inputName)
    {
        return (firstToken.getLexeme().equals(inputName));
    }

    public boolean isBefore(int _line, int _col)
    {
        boolean earlierLine = firstToken.getLine()<_line;
        boolean sameLine = firstToken.getLine()==_line;
        boolean earlierCol = firstToken.getColumn()<_col;
        
        if (earlierLine)
            return true;
        else if (sameLine)
            return earlierCol;

        return false;
    }

    public boolean matchSymbolByType(String inputName, symTypes inputType)
    {
        boolean matchesLex = firstToken.getLexeme().equals(inputName);
        if (matchesLex)
        {
            if (inputType == type) 
            {
                return true;
            }
        }

        return false;
    }

    public boolean matchSymbolByName(String inputName)
    {
        boolean matchesLex = firstToken.getLexeme().equals(inputName);
        return matchesLex;
    }

    public void addOccurance(Token _token)
    {
        for (Token occurToken : occurances)
        {
            //if the token is already in our occurances we just don't add it, shrimple as
            if (occurToken.matchLocation(_token))
                return;
        }
        occurances.add(_token);
    }

    public void addValue(Token _token)
    {
        values.add(_token);
    }

    public void setType(symTypes _type)
    {
        type = _type;
    }

    public void setSubType(String _subtype)
    {
        subtype = _subtype;
    }

    public symTypes getType()
    {
        return type;
    }

    public Token getToken()
    {
        return firstToken;
    }

    public Scope getScope()
    {
        return parentScope;
    }

    public ArrayList<Token> getValues()
    {
        return values;
    }

    public ArrayList<Token> getOccurances()
    {
        return occurances;
    }
}
