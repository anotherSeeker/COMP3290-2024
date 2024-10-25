package SymbolTable;
import Tokeniser.Token;
import java.util.ArrayList;

public class Symbol 
{
    public static enum symTypes{
        ID, intg, flot, bool, symVoid,
        structID, typeID, typeVar, structVar, funcID, undf, programName
    }

    private static final String RESET = /*""//*/"\u001B[0m";
    private static final String RED = /*""//*/"\u001B[31m";
    private static final String B_RED = /*""//*/"\u001B[91m";
    private static final String GREEN = /*""//*/"\u001B[32m";
    private static final String B_GREEN = /*""//*/"\u001B[92m";
    private static final String BLUE =  /*""//*/"\u001B[34m";
    private static final String B_BLUE = /*""//*/"\u001B[94m";
    private static final String CYAN = /*""//*/"\u001B[36m";
    private static final String B_CYAN = /*""//*/"\u001B[96m";
    private static final String MAGENTA = /*""//*/"\u001B[35m";
    private static final String B_MAGENTA = /*""//*/"\u001B[95m";
    private static final String YELLOW = /*""//*/"\u001B[33m";
    private static final String B_YELLOW = /*""//*/"\u001B[93m";
    

    private final Token firstToken;
    private symTypes type;
    private String subtype = null;
    private final ArrayList<Token> values = new ArrayList<>(); 
    private final ArrayList<Token> occurances = new ArrayList<>();
    private final Scope parentScope;

    public static symTypes typeFromString(String returnString)
    {
        symTypes outType = null;
        //ID, intg, flot, bool, symVoid, structID, typeID, funcID,
        switch (returnString) {
            case "TINTG" -> {outType = symTypes.intg;}
            case "TFLOT" -> {outType = symTypes.flot;}
            case "TBOOL" -> {outType = symTypes.bool;}
            case "TVOID" -> {outType = symTypes.symVoid;}
            //if we get an integer that's what the next step is for
        }

        return outType;
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
            case symTypes.structVar -> outString+=CYAN;
            case symTypes.typeID -> outString+=B_MAGENTA;
            case symTypes.typeVar -> outString+=B_MAGENTA;
            case symTypes.funcID -> outString+=B_YELLOW;
            case symTypes.intg -> outString+=RED;
            case symTypes.flot -> outString+=B_BLUE;
            case symTypes.bool -> outString+=BLUE;
            case symTypes.undf -> outString+=B_RED;
            case symTypes.programName -> outString+=B_CYAN;
            case symTypes.symVoid -> outString+=YELLOW;
        }
        outString+=type.toString()+RESET+" : ";

        if (subtype != null)
            if (subtype.equals("const"))
                outString+=GREEN+"Subtype: "+B_MAGENTA+subtype+RESET+" : ";
            else
                outString+=GREEN+"Subtype: "+B_YELLOW+subtype+RESET+" : ";

        switch (parentScope.getName()) {
            case "Global" -> outString+=GREEN+"Scope: "+B_CYAN+parentScope.getName()+RESET+" : ";
            case "Main" -> outString+=GREEN+"Scope: "+MAGENTA+parentScope.getName()+RESET+" : ";
            default -> outString+=GREEN+"Scope: "+YELLOW+parentScope.getName()+RESET+" : ";
        }

        outString+=B_GREEN+"\n\tOccurances: ";

        for (int i = 0;i<occurances.size();i++)
        {
            Token occurance = occurances.get(i);

            outString+="\n\t\t"+B_BLUE+i+": ";
            outString+=occurance.getLocationStringCols()+RESET;

            if (occurance.isDefinition)
                outString+=BLUE+" : Definition"+RESET;
            if (occurance.isAssignment)
                outString+=B_BLUE+" : Assignment"+RESET;
        }

        int counter = 0;
        if (!values.isEmpty())
            outString+=B_GREEN+"\n\tValues: "+RESET;

        for (Token tok : values)
        {
            if (tok == null)
                outString+="\n\t\t"+counter+": "+B_RED+"null"+RESET;
            else
                outString+="\n\t\t"+counter+": "+B_RED+tok.getLexeme()+" "+tok.getLocationStringCols()+RESET;

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
            return inputType == type;
        }

        return false;
    }

    public boolean matchSymbolByType(symTypes inputType)
    {
        return inputType==type;
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

    public void addValues(ArrayList<Token> _values)
    {
        for (Token value : _values)
        {
            values.add(value);
        }
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

    public ArrayList<String> occurancesAreDefined(Scope globalScope, Scope mainScope)
    {
        ArrayList<String> errorList = new ArrayList<>();
        Token definitionToken;

        definitionToken = searchSelfForDefinition();

        if (definitionToken == null)
        {
            definitionToken = searchGlobalAndMainForDefinition(globalScope, mainScope);
        }


        if (definitionToken == null)
        {
            String log = "Semantic Error: symbol "+this.firstToken.getLexeme()+" failed to find a definition token in scope "+parentScope.getName()+" at "+firstToken.getLocationStringCols();
            errorList.add(log);
            return errorList;
        }

        for (Token occurance : occurances)
        {
            //if occurance is the definition token we're fine
            if (occurance != definitionToken)
            {
                if (!occurance.isAfter(definitionToken))
                {
                    //we have used this variable before its definition, log and error
                    String log = "Semantic Error: symbol "+occurance.getLexeme()+" used at "+occurance.getLocationString()+" before definition at "+definitionToken.getLocationString();
                    errorList.add(log);
                }
            }
        }

        return errorList;
    }

    private Token searchGlobalAndMainForDefinition(Scope globalScope, Scope mainScope)
    {
        Token definitionToken = null;

        if (this.parentScope == mainScope)
        {
            definitionToken = searchScopeForDef(mainScope);
        }

        if (definitionToken == null)
        {
            definitionToken = searchScopeForDef(globalScope);
        }

        return definitionToken;
    }


    public Token searchSelfForDefinition()
    {
        for (Token occurance : occurances)
        {
            if (occurance.isDefinition)
            {
                return occurance;
            }
        }
        return null;
    }

    private Token searchScopeForDef(Scope scope)
    {
        Token definitionToken = null;
        Symbol sym = findSymbolInScope(scope);

        if (sym!=null)
        {
            for (Token occurance : sym.getOccurances())
            {
                if (occurance.isDefinition)
                {
                    definitionToken = occurance;
                    break;
                }
            }
        }
        return definitionToken;
    }

    private Symbol findSymbolInScope(Scope scope)
    {
        for (Symbol sym : scope.getSymbolList())
        {
            if (sym.firstToken.matchIdentifier(this.firstToken))
            {
                return sym;
            }
        }
        return null;
    }

    public ArrayList<String> validateArraySizing()
    {
        ArrayList<String> errorList = new ArrayList<>();
        Token definitionToken;

        definitionToken = searchSelfForDefinition();
        if (definitionToken == null)
        {
            //does not contain definition, must be defined elsewhere
            return errorList;
        }

        //arrdef is always tokname def array [integerExpression] of structid end
        //always just two values
        boolean sizeValid = false;
        
        for (Token arrSizeToken : values)
        {
            if (arrSizeToken != values.getLast())
            {
                if (arrSizeToken.getType()!=null)
                {
                    switch (arrSizeToken.getType()) 
                    {
                        case TILIT -> sizeValid = true;
                        case TIDEN -> {
                            Symbol newSym = this.parentScope.lookupSymbolbyName(arrSizeToken.getLexeme(), symTypes.intg);
                            sizeValid = (newSym != null && newSym.subtype.equals("const"));
                        }
                        default -> sizeValid = false;
                    }
                }
            }
        }

        if (sizeValid == false)
        {
            String log = "Semantic Error: Array must have valid integer expression for sizing";
            errorList.add(log);
        }

        return errorList;
    }
}
