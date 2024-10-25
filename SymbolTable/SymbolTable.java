package SymbolTable;
import SymbolTable.Symbol.symTypes;
import Tokeniser.Token;
import Tokeniser.TokenTypes;
import java.util.ArrayList;

public class SymbolTable 
{
    private final String RESET = "\u001B[0m";
    private final String RED = "\u001B[31m";
    private final String GREEN = "\u001B[32m";
    //private final String BLUE =  "\u001B[34m";
    //private final String CYAN = "\u001B[36m";
    private final String bCYAN = "\u001B[96m";
    private final String MAGENTA = "\u001B[35m";
    //private final String bMAGENTA = "\u001B[95m";
    private final String YELLOW = "\u001B[33m";
    //private final String bYELLOW = "\u001B[93m";
    //private final String bBLUE = "\u001B[94m";
    //private final String bGREEN = "\u001B[92m";
    //private final String bRED = "\u001B[91m";


    private final ArrayList<Token> tokenList; 
    private final ArrayList<Scope> scopeList = new ArrayList<>();
    private final Scope globalScope = new Scope(true);
    private final Scope mainScope = new Scope(false);
    private String progName = null;
    private boolean symtError = false;
    private final ArrayList<String> errorLog = new ArrayList<>();

    private int globalIterator = -1;
    
    private enum SymState {
        Global, Main, Func
    }

    public SymbolTable(ArrayList<Token> _tokenList) 
    {
        tokenList = _tokenList;
        populateTable();
    }

    public void printTable()
    {
        for (Scope currentScope : scopeList)
        {
            String name = currentScope.getName();
            symTypes returnType = currentScope.getReturnType();

            switch (name) {
                case "Global" -> System.out.println(GREEN+"Scope: "+bCYAN+name+RESET+" : "+currentScope.occurancesToString());
                case "Main" -> System.out.println(GREEN+"Scope: "+MAGENTA+name+RESET+" : "+currentScope.occurancesToString());
                default -> System.out.println(GREEN+"Scope: "+YELLOW+name+RESET+" : "+currentScope.occurancesToString());
            }

            if (returnType != null)
            {
                System.out.println(GREEN+"ReturnType: "+YELLOW+returnType+RESET);
            }

            for (Symbol sym : currentScope.getSymbolList())
            {
                String outString = "";

                outString += sym.toString();
                System.out.println(outString);
            }
        }
    }

    private void populateTable()
    {
        //scopes 0 and 1 are globalScope and mainScope
        setupScopes();

        stepThroughTokens();
        for (Scope scope : scopeList)
        {
            if (scope.getReturnType() == symTypes.ID)
            {
                validateReturnTypes(mainScope);
            }
        }

        if (symtError)
        {
           logError("Symbol Table has failed to initialise correctly");
        }
    }

    private void setupScopes()
    {
        //these are always the first two scopes
        addScope(globalScope);
        addScope(mainScope);

        Token scopeToken;
        for (int i = 0; i < tokenList.size(); i++)
        {
            Token listToken = tokenList.get(i);
            if (listToken.getType() == TokenTypes.TMAIN)
            {
                //mainscope was setup with no token, we fix that here
                mainScope.updateMainScopeToken(listToken);
            }

            //each function will become it's own scope and we will later add the locals for our scopes
            if (listToken.getType() == TokenTypes.TFUNC) 
            {
                try {
                    i = stepIterator(i, 1);
                    scopeToken = tokenList.get(i);
                    
                    Token returnToken = getNextReturnToken(i);
                    symTypes returnType = standardiseReturnType(returnToken);

                    Scope newScope;
                    if (returnToken == null)
                    {
                        logError("Symbol Table Error: No return type found for "+scopeToken.getLexeme()+" at "+scopeToken.getLocationString());
                        newScope = new Scope(scopeToken);
                    }
                    else
                    {
                        newScope = new Scope(scopeToken, returnType);
                    }

                    addScope(newScope);
                } catch (Exception e) {
                    //e.printStackTrace();
                    logError("Scope setup at: "+listToken.getLocationStringErr()+" has failed");
                }
            }
        }
    }

    private Token getNextReturnToken(int index)
    {
        try
        {
            for (int i = index; i < tokenList.size(); i++)
            {
                Token listToken = tokenList.get(i);
                if (listToken.getType() == TokenTypes.TRPAR)
                {
                    //step forward
                    listToken = tokenList.get(i+1);
                    if (listToken.getType() == TokenTypes.TCOLN)
                    {
                        return tokenList.get(i+2);
                    }
                }
            }
        } 
        catch(Exception e) 
        {
            //e.printStackTrace();
            logError("Symbol Table Error: Finding function return type has failed");
        }
        return null;
    }

    private void stepThroughTokens()
    {
        for (int i = 0; i<tokenList.size();i++)
        {
            Token listToken = tokenList.get(i);

            //will move through the loop depending on how many consts there are but will only start after seeing keyword constants
            i = stepHandleConsts(listToken, i);
            if (i >= tokenList.size())
                break;
            listToken = tokenList.get(i);

            i = stepHandleTypes(listToken, i);
            if (i >= tokenList.size())
                break;
            listToken = tokenList.get(i);

            i = stepHandleLooseIds(listToken, i);

            i = stepHandleCD24(listToken, i);
        }
    }

    private int stepHandleCD24(Token cd24, int index)
    {
        if (cd24.getType() == TokenTypes.TCD24)
        {
            index = stepIterator(index, 1);
            if (index < tokenList.size())
            {
                if (progName == null)
                {
                    progName = tokenList.get(index).getLexeme();
                    symTypes type = symTypes.programName;
                    Symbol newSymbol = new Symbol(tokenList.get(index), type, globalScope);
                    addToScope(newSymbol, globalScope, true);
                }
                else if ( !progName.equals(tokenList.get(index).getLexeme()) )
                {
                    String log = "Error: recevived \""+tokenList.get(index).getLexeme()+"\" expecting cd24 program name at"+tokenList.get(index).getLocationStringErr();
                    logError(log);
                }
                else
                {
                    progName = tokenList.get(index).getLexeme();
                    Symbol.symTypes type = symTypes.programName;
                    Symbol newSymbol = new Symbol(tokenList.get(index), type, globalScope);
                    addToScope(newSymbol, globalScope, false);
                }
            }
        }

        return index;
    }

    private int stepHandleLooseIds(Token token, int i)
    {
        //scopes are already setup so the scope will check if this is a duplicate (line col comparison) when it's asked to addToScope()
        if (token.getType() == TokenTypes.TIDEN)
        {
            if (tokenIsScopeName(token))
            {
                getScopeFromTokenName(token).addScopeOccurance(token);
            }
            else
            {
                Scope _scope = getScopeAtTokenLoc(token);
                Symbol.symTypes type;
                String subtype = null;

                if (setupAssignOrDefine(token, i) == 0)
                {
                    //is not followed by def, =, :, if a struct or array is not followed by those it's referencing the whole object
                    type = getSymTypeFromScopeLookup(token, _scope);

                    if (type == symTypes.structID || type == symTypes.typeID)
                    {
                        Symbol newSymbol = new Symbol(token, type, _scope);
                        addToScope(newSymbol, _scope, token.isDefinition);

                        int structOrArrayState = isStructOrArrayReference(token, _scope, i, type);
                        if (structOrArrayState != 0)
                        {
                            return globalIterator;
                        }
                    }                    
                }
                else
                {
                    int k = stepIterator(i, 2);
                    Token newToken = tokenList.get(k);
                    if (tokenIsScopeName(newToken))
                    {
                        type = getScopeFromTokenName(newToken).getReturnType();
                    }
                    else
                    {
                        type = getSymTypeFromTokenType(newToken);
                    }

                    if (type == symTypes.typeID || type == symTypes.structID)
                    {
                        //name : type
                        i = stepIterator(i, 2);
                        subtype = tokenList.get(i).getLexeme();
                    }

                    Symbol newSymbol = new Symbol(token, type, _scope);
                    addToScope(newSymbol, _scope, token.isDefinition);
                    if (subtype!=null)
                        newSymbol.setSubType(subtype);
                }
            }
        }
        return i; 
    }

    private int stepHandleTypes(Token token, int i)
    {
        Token identToken;

        if (token.getType() == TokenTypes.TTYPD)
        {
            for (int k = i+1; k < tokenList.size();k++)
            {
                Token testTok = tokenList.get(k);
                if (testTok.getType() == TokenTypes.TIDEN)
                {
                    identToken = testTok;

                    //is always name def [stuff]
                    if (setupAssignOrDefine(testTok, k) != 1)
                    {
                        String log = "Symbol Table Error: expecting \"name def ...\" for type setup"+testTok.getLocationStringErr();
                        logError(log);
                    }

                    //is always name def [stuff]
                        //we skip two to bypass def, simply hard coding this cause it's faster and if you made a mistake it'll be flagged as an error anyway
                    k=stepIterator(k,2);
                    k=structOrTypeHandler(identToken, k);
                }
                if (testTok.getType() == TokenTypes.TMAIN  
                ||  testTok.getType() == TokenTypes.TARRD 
                ||  testTok.getType() == TokenTypes.TFUNC)
                {
                    return k;
                }

                i = k;
            }
        }

        return i;
    }

    private int structOrTypeHandler(Token identToken, int i)
    {
        Token valueToken;
        symTypes type;

        Token stepForwardToken = tokenList.get(i);

        if (stepForwardToken.getType() == TokenTypes.TARAY)
        {
            type = symTypes.typeID;
            Symbol newSymbol = new Symbol(identToken, type, globalScope);
            globalScope.addSymbol(newSymbol);

            identToken.isDefinition = true;

            //array [ <expr> ] of <structid> end
            //skip 2 here to bypass the left bracket
            int k = stepIterator(i, 2);
            
            //add <expr> then add <structid>
            valueToken = tokenList.get(k);
            newSymbol.addValue(valueToken);
            
            //<structid>
            k = stepIterator(k, 3);
            valueToken = tokenList.get(k);
            newSymbol.addValue(valueToken);
            newSymbol.setSubType(valueToken.getLexeme());

            if (!lookupStructExistsBefore(valueToken))
            {
                String log = "Symbol Table Error: Arrays must be defined with a StructID, that StructID has to be defined before them "+valueToken.getLocationStringErr();
                logError(log);
                return k;
            }

            k = stepIterator(k, 1);
            valueToken = tokenList.get(k);
            if (!(valueToken.getType() == TokenTypes.TTEND))
            {
                String log = "Symbol Table Error: Array definition does not end"+valueToken.getLocationStringErr();
                logError(log);
                return k;
            }

            return k;
        }
        else if (stepForwardToken.getType() == TokenTypes.TIDEN) 
        {
            //accepts int, float, bool, structID
            //id:type, id:type,... end

            //setup and addto scope newSymbol
            type = symTypes.structID;
            Symbol newSymbol = new Symbol(identToken, type, globalScope);
            globalScope.addSymbol(newSymbol);

            //add initial value to struct
            newSymbol.addValue(stepForwardToken);
            i=stepIterator(i, 2);
            valueToken = tokenList.get(i);
            newSymbol.addValue(valueToken);

            //see if there are additional values to add
            for (int k = i+1; k<tokenList.size(); k++)
            {
                valueToken = tokenList.get(k);
                if (valueToken.getType() == TokenTypes.TCOMA)
                {
                    //fields is sdecl
                        //sdcel is always teh form <id> : int|float|bool|structid
                    k=stepIterator(k, 1);
                    valueToken = tokenList.get(k);
                    newSymbol.addValue(valueToken);
                    
                    k=stepIterator(k, 2);
                    valueToken = tokenList.get(k);
                    newSymbol.addValue(valueToken);

                    //isn't intlit, floatlit, bool or struct id
                    if (valueToken.getType() == TokenTypes.TIDEN)
                    {
                        if (!lookupStructExists(valueToken))
                        {
                            String log = "Symbol Table Error: Structs do not accept Non-Struct identifiers as a valid type "+valueToken.getLocationStringErr();
                            logError(log);
                            return k;
                        }
                    }
                }
                else
                {
                    return k;
                }
            }
        }
        return i;
    }

    private int stepHandleConsts(Token token, int i)
    {
        Token identToken;
        Token valueToken;
        Symbol.symTypes type;

        if (token.getType() == TokenTypes.TCONS)
        {
            for (int k = i+1; k < tokenList.size();k++)
            {
                Token constTok = tokenList.get(k);
                if (constTok.getType() == TokenTypes.TIDEN)
                {
                    identToken = constTok;
                    
                    //constants are defined when they're assigned
                    if (setupAssignOrDefine(constTok, k) == -1)
                        constTok.isDefinition = true;

                    k+=2;
                    valueToken = tokenList.get(k);
                    type = getSymTypeFromTokenType(valueToken);
                    if (type == symTypes.undf)
                    {
                        String errStr = "Symbol Table Error: Assigned Illegal Value: "+valueToken.getTypeString()+" to constant: "+identToken.getLexeme()
                                        +"\n"+identToken.getLocationStringErr();
                        logError(errStr);
                    }

                    Symbol newSymbol = new Symbol(identToken, type, globalScope);
                    newSymbol.addValue(valueToken);
                    newSymbol.setSubType("const");

                    globalScope.addSymbol(newSymbol);
                }
                if (constTok.getType() == TokenTypes.TMAIN 
                ||  constTok.getType() == TokenTypes.TTYPD 
                ||  constTok.getType() == TokenTypes.TARRD 
                ||  constTok.getType() == TokenTypes.TFUNC)
                {
                    return k;
                }

                i = k;
            }
        }
        return i;
    }

    //-------------------------------------
    private void addScope(Scope scope)
    {
        scopeList.add(scope);
    }

    private void addToScope(Symbol sym, Scope scope, boolean isDefinition)
    {
        scope.addSymbol(sym);
        if (!isDefinition)
        {
            if ( scope != globalScope && isInGlobal(sym.getToken()) )
            {
                globalScope.addSymbol(sym);
            }
        }
    }
    //-------------------------------------
    //return -1 for assignment, return 1 for definition, 0 else
    public int setupAssignOrDefine(Token testTok, int index)
    {
        if (tokenList.get(index+1).getType() == TokenTypes.TCOLN)
        {
            //is definition
            testTok.isDefinition = true;
            return 1;
        }
        if (tokenList.get(index+1).getType() == TokenTypes.TTDEF)
        {
            //is definition
            testTok.isDefinition = true;
            return 1;
        }
        if (tokenList.get(index+1).getType() == TokenTypes.TEQUL || 
            tokenList.get(index+1).getType() == TokenTypes.TPLEQ || 
            tokenList.get(index+1).getType() == TokenTypes.TMNEQ || 
            tokenList.get(index+1).getType() == TokenTypes.TSTEQ || 
            tokenList.get(index+1).getType() == TokenTypes.TDVEQ) 
        {
            //is assignment
            testTok.isAssignment = true;
            return -1;
        }
        
        return 0;
    }

    //structName.var returns 1, arrayName[index] returns 2, arrayName[index].var returns 3, else return 0
    //structName.var adds var token as a value
    //arrayName[index]      adds index as a value,   then adds null as a value
    //arrayName[index].var  add index as a value,    then adds var as a value
    private int isStructOrArrayReference(Token token, Scope scope, int i, symTypes type)
    {
        //TODO: cannot handle struct values as array indexes, fix either internally or externally
        globalIterator = stepIterator(i, 1);
        
        if (tokenList.get(globalIterator).getType() == TokenTypes.TDOTT)
        {
            //is struct, step forward one and add name to value, we'll use struct
            globalIterator = stepIterator(globalIterator, 1);
            if (type == symTypes.typeID)
            {
                scope.getSymbolByTokenName(token).addValue(tokenList.get(globalIterator));
                scope.getSymbolByTokenName(token).addValue(null);
            }
            scope.getSymbolByTokenName(token).addValue(tokenList.get(globalIterator));
            return 1;
        }
        else if (tokenList.get(globalIterator).getType() == TokenTypes.TLBRK)
        {
            //is struct, step forward one and add name to value, we'll use struct
            globalIterator = stepIterator(globalIterator, 1);
            scope.getSymbolByTokenName(token).addValue(tokenList.get(globalIterator));

            //step two so we go past the right bracket name[index].var
            globalIterator = stepIterator(globalIterator, 2);
            if (tokenList.get(globalIterator).getType() == TokenTypes.TDOTT)
            {
                //is name[index].var
                globalIterator = stepIterator(globalIterator, 1);
                scope.getSymbolByTokenName(token).addValue(tokenList.get(globalIterator));
                scope.getSymbolByTokenName(token).addValue(null);
                return 3;
            }
            //is name[index]
            scope.getSymbolByTokenName(token).addValue(null);
            return 2;
        }

        return 0;
    }
    
    public boolean tokenIsStructName(Token _token)
    {
        Symbol sym = globalScope.getSymbolByTokenName(_token);
        if (sym != null)
            return sym.getType() == Symbol.symTypes.structID;

        return false;
    }

    public boolean tokenIsTypeName(Token _token)
    {
        Symbol sym = globalScope.getSymbolByTokenName(_token);
        if (sym != null)
            return sym.getType() == Symbol.symTypes.typeID;
        
        return false;
    }

    public boolean tokenIsScopeName(Token _token)
    {
        for (Scope scope : scopeList) 
        {
            if (scope.tokenIsScopeName(_token))
                return true;
        }
        return false;
    }

    public boolean tokenIsExactScope(Token _token)
    {
        for (Scope scope : scopeList) 
        {
            if (scope.tokenIsExactScope(_token))
                return true;
        }
        return false;
    }

    private Symbol.symTypes getSymTypeFromScopeLookup(Token token, Scope _scope)
    {
        symTypes type;
        if (isInScope(token, _scope))
        {
            type = _scope.getSymbolByTokenName(token).getType();
            if (type == symTypes.typeID || type == symTypes.structID)
            {

            }
            return type;
        }
        if (isInGlobal(token))
        {
            type = globalScope.getSymbolByTokenName(token).getType();
            return type;
        }

        //String log = "Symbol Table Error: Failed to find symbol definition, defaulting to \"ID\" "+token.getLocationStringErr();
        //logError(log);

        return symTypes.ID;
    }

    private Symbol.symTypes getSymTypeFromTokenType(Token token)
    {
        switch (token.getType()) {
            case TIDEN -> {
                if (tokenIsScopeName(token))
                    return Symbol.symTypes.funcID;
                else if (tokenIsStructName(token))
                    return Symbol.symTypes.structID;
                else if (tokenIsTypeName(token))
                    return Symbol.symTypes.typeID;
                else
                    return Symbol.symTypes.ID;
            }
            case TILIT -> {return Symbol.symTypes.intg;}
            case TFLIT -> {return Symbol.symTypes.flot;}
            case TBOOL -> {return Symbol.symTypes.bool;}
            case TINTG -> {return Symbol.symTypes.intg;}
            case TFLOT -> {return Symbol.symTypes.flot;}
            default -> {return Symbol.symTypes.undf;}
        }
    }

    public Scope getScopeAtTokenLoc(Token inputToken)
    {
        SymState tempState = SymState.Global;
        Token scopeToken = null;
        for (int i = 0; i < tokenList.size(); i++)
        {
            Token listToken = tokenList.get(i);

            switch (listToken.getType()) {
                case TMAIN -> tempState = SymState.Main;
                case TFUNC -> {
                    tempState = SymState.Func;
                    try {
                        scopeToken = tokenList.get(i+1);
                    } catch (Exception e) {
                        System.err.print(e);
                    }
                }
                default -> {}
            }
            //do nothing, does not change our scope

            if (listToken.matchLocation(inputToken))
            {
                Scope outScope = globalScope;
                if (tempState == SymState.Main)
                {
                    outScope = mainScope;
                }
                else if (tempState == SymState.Func)
                {
                    try { outScope = getScopeFromTokenName(scopeToken); } 
                    catch (Exception e) {System.err.print(e);}
                }
                return outScope;
            }
        }
        return null;
    }

    public Scope getScopeFromTokenName(Token inputToken)
    {
        Scope out = null;
        for (Scope scope : scopeList)
        {
            if (scope.tokenIsScopeName(inputToken))
            {
                out = scope;
                break;
            }
        }

        return out;
    }

    private Symbol lookup(Token _token, Symbol.symTypes inputType)
    {
        Symbol out;
        for (Scope scope : scopeList)
        {
            out = scope.lookupSymbol(_token, inputType);
            if (out != null)
                return out;
        }
        return null;
    }

    private Symbol lookup(String name, Symbol.symTypes inputType)
    {
        Symbol out;
        for (Scope scope : scopeList)
        {
            out = scope.lookupSymbolbyName(name, inputType);
            if (out != null)
                return out;
        }
        return null;
    }

    private Symbol lookupBefore(Token _token, Symbol.symTypes inputType)
    {
        int line=_token.getLine(); 
        int col=_token.getColumn();
        Symbol out;
        for (Scope scope : scopeList)
        {
            out = scope.lookupSymbol(_token, inputType);
            if (out != null)
            {
                if (out.isBefore(line, col))
                    return out;
            }
        }
        return null;
    }

    public boolean lookupStructExists(Token _token)
    {
        return (lookup(_token, Symbol.symTypes.structID) != null);
    }

    public boolean lookupStructExists(String name)
    {
        return (lookup(name, Symbol.symTypes.structID) != null);
    }
    
    public boolean lookupStructExistsBefore(Token _token)
    {
        return (lookupBefore(_token, Symbol.symTypes.structID) != null);
    }

    public boolean lookupTypeExists(Token _token)
    {
        return (lookup(_token, Symbol.symTypes.typeID) != null);
    }

    public boolean lookupTypeExists(String name)
    {
        return (lookup(name, Symbol.symTypes.typeID) != null);
    }

    public boolean lookupTypeExistsBefore(Token _token)
    {
        return (lookupBefore(_token, Symbol.symTypes.typeID) != null);
    }
    

    public boolean lookupFuncExists(Token _token)
    {
        return (getScopeAtTokenLoc(_token) != null);
    }


    public boolean isInGlobal(Token _token)
    {
        return isInScope(_token, globalScope); 
    }

    public boolean isInMain(Token _token)
    {
        return isInScope(_token, mainScope); 
    }

    public boolean isInScope(Token _token, Scope scope)
    {
        return scope.isInScope(_token); 
    }

    private void logError(String log)
    {
        symtError = true;
        errorLog.add(log);
    }

    private int stepIterator(int i, int stepSize)
    {
        if (i+stepSize > tokenList.size())
        {
            logError("Symbol Table Error: Table is attempting to access index larger than tokenList.size()");
            return i;
        }
        return i+stepSize;
    }


    public void printErrorLog()
    {
        if (symtError == false)
        {
            System.out.println("No Symbol Table Errors");
            return;
        }

        for (String error : errorLog)
        {
            System.out.println(RED+error+RESET);
        }
    }

    private symTypes standardiseReturnType(Token returnToken)
    {
        boolean isIden = false;
        String returnString = returnToken.getTypeString();

        //if we're using the symType name directly we can get away with this
        if (returnToken.getType() == TokenTypes.TIDEN)
        {
            isIden = true;
        }

        symTypes type = Symbol.typeFromString(returnString);

        if (isIden)
        {
            if (lookupStructExists(returnString))
            {
                type = symTypes.structID;
            }
            else if (lookupTypeExists(returnString))
            {
                type = symTypes.typeID;
            }
            else
            {
                type = symTypes.ID;
            }
        }

        return type;
    }

    private void validateReturnTypes(Scope scope)
    {
        Token scopeToken = scope.getToken();
        
        Token returnToken = getNextReturnToken(scopeToken.getIndex());
        String returnString = returnToken.getLexeme();
        if (returnString != null)
        {
            if (lookupStructExists(returnString))
            {
                scope.setReturnType(symTypes.structID);
            }
            else if (lookupTypeExists(returnString))
            {
                scope.setReturnType(symTypes.typeID);
            }
            else
            {
                logError("Symbol Table Error: assigned return type is not a Struct or Array");
            }
        }
    } 

    private Symbol getProgramNameSymbol(Scope scope)
    {
        ArrayList<Symbol> syms;
        symTypes type = symTypes.programName;

        syms = scope.getAllOfType(type);
        if (!syms.isEmpty() && syms.size()==1)
        {
            Symbol sym = syms.get(0);

            return sym;
        }

        return null;
    }

    public boolean programNameIsValid()
    {
        boolean isValid = false;
        for (Scope scope : scopeList)
        {
            Symbol sym = getProgramNameSymbol(scope);
            if (sym != null)
            {
                if (isValid == false)
                {
                    isValid = matchProgNameOccurances(sym);
                    if (isValid == false)
                        return isValid;//if name ever doesn't match here, we can immediately exit
                }
                else
                {
                    //we found a second instance of program name, this shouldn't ever happen
                    isValid = false;
                    return isValid;
                }
            }
        }

        return isValid;
    }

    private boolean matchProgNameOccurances(Symbol sym)
    {
        ArrayList<Token> toks = sym.getOccurances();
        String initialName = toks.get(0).getLexeme();

        boolean isValid = false;

        for (int i = 1; i<toks.size();i++)
        {
            String name = toks.get(i).getLexeme();

            isValid = name.equalsIgnoreCase(initialName);
        }

        return isValid;
    }

    public boolean validateVariableUses()
    {
        boolean isValid = false;

        for (Scope scope : scopeList)
        {
            for (Symbol sym : scope.getSymbolList())
            {
                for (Token occurance : sym.getOccurances())
                {

                }
            }
        }



        return false;
    }


    

}
