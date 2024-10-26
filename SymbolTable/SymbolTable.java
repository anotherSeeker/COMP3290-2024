package SymbolTable;
import SymbolTable.Symbol.symTypes;
import Tokeniser.Token;
import Tokeniser.TokenTypes;
import java.util.ArrayList;



/*  The complexity of all the symbol table logic got away from me, this needs significant cleanup and to be designed 
    such that functions are more reusable than they are currently that is completely impractical with the timeline of 
    this and my other assignments especially while working on this alone I am frankly disgusted by the length of this file.
*/
public class SymbolTable 
{
    private static final String RESET = /*""//*/"\u001B[0m";
    private static final String RED = /*""//*/"\u001B[31m";
    //private static final String B_RED = /*""//*/"\u001B[91m";
    private static final String GREEN = /*""//*/"\u001B[32m";
    //private static final String B_GREEN = /*""//*/"\u001B[92m";
    //private static final String BLUE =  /*""//*/"\u001B[34m";
    //private static final String B_BLUE = /*""//*/"\u001B[94m";
    //private static final String CYAN = /*""//*/"\u001B[36m";
    private static final String B_CYAN = /*""//*/"\u001B[96m";
    private static final String MAGENTA = /*""//*/"\u001B[35m";
    //private static final String B_MAGENTA = /*""//*/"\u001B[95m";
    private static final String YELLOW = /*""//*/"\u001B[33m";
    //private static final String B_YELLOW = /*""//*/"\u001B[93m";


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
            String outString = "";


            switch (name) {
                case "Global" -> outString+=GREEN+"Scope: "+B_CYAN+name+RESET+" : ";
                case "Main" -> outString+=GREEN+"Scope: "+MAGENTA+name+RESET+" : ";
                default -> outString+=GREEN+"Scope: "+YELLOW+name+RESET+" : "+currentScope.occurancesToString();
            }

            System.out.println(outString);

            if (returnType != null)
            {
                System.out.println(GREEN+"ReturnType: "+YELLOW+returnType+RESET);
            }

            for (Symbol sym : currentScope.getSymbolList())
            {
                outString = "";

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
                    tokenList.get(index).isDefinition = true;
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

                    if (type == symTypes.structID || type == symTypes.typeID || type == symTypes.structVar || type == symTypes.typeVar)
                    {
                        Symbol newSymbol = new Symbol(token, type, _scope); 
                        addToScope(newSymbol, _scope, token.isDefinition);
                        int structOrArrayState = isStructOrArrayReference(i, _scope, token);
                        if (structOrArrayState != 0)
                        {
                            return globalIterator;
                        }
                    }                    
                }
                else
                {
                    i = handleLooseAssignOrDefine(i, subtype, _scope, token);
                }
            }
        }
        return i; 
    }

    private int handleLooseAssignOrDefine(int i, String subtype, Scope _scope, Token oldToken)
    {
        i = stepIterator(i, 2);
        Token newToken = tokenList.get(i);
        symTypes type;
        if (tokenIsScopeName(newToken))
        {
            type = getScopeFromTokenName(newToken).getReturnType();
        }
        else
        {
            type = getSymTypeFromTokenType(newToken);
        }

        symTypes newSymType = type;
        if (type == symTypes.typeID || type == symTypes.structID)
        {
            if (type == symTypes.typeID)
                newSymType = symTypes.typeVar;
            else if (type == symTypes.structID)
                newSymType = symTypes.structVar;

            //name : type, step past :
                //not a define as we already handled those in stepHandleTypes
            //i = stepIterator(i, 2);
            subtype = tokenList.get(i).getLexeme();
        }

        Symbol newSymbol = new Symbol(oldToken, newSymType, _scope);
        addToScope(newSymbol, _scope, oldToken.isDefinition);
        if (subtype!=null)
            newSymbol.setSubType(subtype);

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
            
            k = addExpressionValuesToArraySymbol(newSymbol, k);
            //breaks once reaching "]", returns index of "]"

            //<structid>
            k = stepIterator(k, 2);
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
            newSymbol = globalScope.lookupSymbolByTokenName(newSymbol.getToken());

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
        //symbol is not being defined, nor is it defined in this scope
        if (!isDefinition)
        {
            if (!isDefinitionInCurrentScope(scope, sym))
            {
                if ( scope != globalScope && isInGlobal(sym.getToken()) )
                {
                    globalScope.addSymbol(sym);
                }
            }
        }
    }

    private boolean isDefinitionInCurrentScope(Scope scope, Symbol _sym)
    {
        Symbol sym = scope.lookupSymbolByTokenName(_sym.getToken());

        Token symTok = sym.searchSelfForDefinition();
        
        return symTok!=null;
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

    private int addExpressionValuesToArraySymbol(Symbol newSymbol, int index)
    {
        //starts on first token in "index", in name[index]
        int i;
        for (i = index; i < tokenList.size(); i++)
        {
            Token tok = tokenList.get(i);
            if (tok.getType() == TokenTypes.TRBRK)
            {
                return i;
            }
            
            switch (tok.getType())
            {
                //int lits and constants, constant is checked for being an int const elsewhere
                case TokenTypes.TILIT -> newSymbol.addValue(tok);
                case TokenTypes.TIDEN -> newSymbol.addValue(tok);
                //arithmetic
                case TokenTypes.TPLUS -> newSymbol.addValue(tok);
                case TokenTypes.TMINS -> newSymbol.addValue(tok);
                case TokenTypes.TSTAR -> newSymbol.addValue(tok);
                case TokenTypes.TDIVD -> newSymbol.addValue(tok);
                case TokenTypes.TCART -> newSymbol.addValue(tok);
                default -> {
                    String log = "Symbol Table Error: Invalid token type in array definition "+tok.getTypeString()+" : "+tok.getLocationString();
                    logError(log);
                }
            }
        }

        return i;
    }

    private int addExpressionValuesToArrayVar(Symbol newSymbol, int index)
    {
        //starts on first token in "index", in name[index].var, ends on ]
        int i;
        for (i = index; i < tokenList.size(); i++)
        {
            Token tok = tokenList.get(i);
            if (tok.getType() == TokenTypes.TRBRK)
            {
                return i;
            }
            
            switch (tok.getType())
            {
                //int lits and constants, constant is checked for being an int const elsewhere
                case TokenTypes.TILIT -> newSymbol.addValue(tok);
                case TokenTypes.TIDEN -> newSymbol.addValue(tok);
                //arithmetic
                case TokenTypes.TPLUS -> newSymbol.addValue(tok);
                case TokenTypes.TMINS -> newSymbol.addValue(tok);
                case TokenTypes.TSTAR -> newSymbol.addValue(tok);
                case TokenTypes.TDIVD -> newSymbol.addValue(tok);
                case TokenTypes.TCART -> newSymbol.addValue(tok);
                default -> {
                    String log = "Symbol Table Error: Invalid token type in array index "+tok.getTypeString()+" : "+tok.getLocationString();
                    logError(log);
                }
            }
        }

        return i;
    }


    //structName.var returns 1, arrayName[index] returns 2, arrayName[index].var returns 3, else return 0
    //structName.var adds var token as a value
    //arrayName[index]      adds index as a value,   then adds null as a value
    //arrayName[index].var  add index as a value,    then adds var as a value
    private int isStructOrArrayReference(int i, Scope scope, Token _symbolToken)
    {
        globalIterator = stepIterator(i, 1);
        Token tok=tokenList.get(globalIterator);
        Symbol newSymbol = scope.lookupSymbolByTokenName(_symbolToken);

        int outValue = 0;

        if (tok.getType() == TokenTypes.TDOTT)
        {
            //is struct, step forward one and add name to value, we'll use struct
                //structName.var
            globalIterator = stepIterator(globalIterator, 1);
            tok = tokenList.get(globalIterator);
            newSymbol.addValue(tok);
            outValue = 1;
        }
        else if (tokenList.get(globalIterator).getType() == TokenTypes.TLBRK)
        {
            //is array ("type"), step forward one and add array expression to thingo
                ////name[expression] ...
            globalIterator = stepIterator(globalIterator, 1);
            globalIterator = addExpressionValuesToArrayVar(newSymbol, globalIterator);

            //step two so we go past the right bracket name[index].var
            globalIterator = stepIterator(globalIterator, 1);
            if (tokenList.get(globalIterator).getType() == TokenTypes.TDOTT)
            {
                //is name[index].var
                globalIterator = stepIterator(globalIterator, 1);
                tok = tokenList.get(globalIterator);
                newSymbol.addValue(tok);

                outValue = 3;
            }
            else
            {
                //is name[index]
                outValue = 2;
            }
        }
        else
        {
            //for struct refs or direct array refs
            newSymbol.addValue(null);
        }

        return outValue;
    }
    
    public boolean tokenIsStructName(Token _token)
    {
        Symbol sym = globalScope.lookupSymbolByTokenName(_token);
        if (sym != null)
            return sym.getType() == Symbol.symTypes.structID;

        return false;
    }

    public boolean tokenIsTypeName(Token _token)
    {
        Symbol sym = globalScope.lookupSymbolByTokenName(_token);
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
            type = _scope.lookupSymbolByTokenName(token).getType();
            if (type == symTypes.typeID || type == symTypes.structID)
            {

            }
            return type;
        }
        if (isInGlobal(token))
        {
            type = globalScope.lookupSymbolByTokenName(token).getType();
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

    public ArrayList<Scope> getScopeList()
    {
        return scopeList;
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
    private void logError(ArrayList<String> logs)
    {
        symtError = false;
        for (String log : logs)
        {
            errorLog.add(log);
        }
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

        symTypes type = Symbol.symTypeFromString(returnString);

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

    private int advanceUntil(TokenTypes[] types, int index)
    {
        int i;
        for (i = index; i<tokenList.size(); i++)
        {
            Token tok = tokenList.get(i);
            for (TokenTypes type : types)
            {
                if (type == tok.getType())
                {
                    return i;
                }
            }
        }
        return i;
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

    public ArrayList<String> validateVariableUses()
    {
        ArrayList<String> errorList = new ArrayList<>();
        ArrayList<String> newErrors;

        for (Scope scope : scopeList)
        {
            for (Symbol sym : scope.getSymbolList())
            {
                newErrors = sym.occurancesAreDefined(globalScope, mainScope);
                
                for (String error : newErrors)
                {
                    errorList.add(error);
                }
            }
        }
        return errorList;
    }

    public ArrayList<String> validateArraySizing()
    {
        ArrayList<String> errorList = new ArrayList<>();
        ArrayList<String> newErrors;

        for (Scope scope : scopeList)
        {
            for (Symbol sym : scope.getSymbolList())
            {
                if (sym.getType() == symTypes.typeID)
                {
                    //confirmed as array type
                    newErrors = sym.validateArraySizing();
                    errorList.addAll(newErrors);
                }
            }
        }
        return errorList;
    }
    
    public ArrayList<String> validateExpressions()
    {
        ArrayList<String> errorList = new ArrayList<>();
        ArrayList<String> newErrors;

        //TODO: AAAAAAAAAAAAAAA

        return errorList;
    }


    public ArrayList<String> validateFunctionReturns()
    {
        ArrayList<String> errorList = new ArrayList<>();

        for (Scope scope : scopeList)
        {
            if (scope.isFunc())
            {
                //is not global or main

                int index = scope.getToken().getIndex();

                boolean inBody = false;
                boolean hasReturn = false;

                Token currToken;
                for (int i = index; i<tokenList.size();i++)
                {
                    currToken = tokenList.get(i);
                    if (currToken.getType() == TokenTypes.TMAIN || currToken.getType() == TokenTypes.TFUNC)
                    {
                        break;
                    }

                    if (!inBody)
                    {
                        if (currToken.getType() == TokenTypes.TBEGN)
                        {
                            inBody = true;
                        }
                    }

                    if (inBody)
                    {
                        if (currToken.getType() == TokenTypes.TRETN)
                        {
                            hasReturn = true;
                            break;
                        }
                    }
                }

                if (!hasReturn)
                {
                    String log = "Semantic Error: Func "+scope.getName()+" "+scope.getScopeToken().getLocationStringCols()+" must include a return statement";
                    errorList.add(log);
                }
            }
        }

        return errorList;
    }

    public ArrayList<String> validateFunctionParameters()
    {
        ArrayList<String> errorList = new ArrayList<>();
        ArrayList<String> newErrors;

        for (Scope scope : scopeList)
        {
            if (scope.isFunc())
            {
                ArrayList<String> paramTypes = scope.getParameterTypes(tokenList);

                //for each time that scope is referenced in code
                for (Token occurance : scope.getOccurances())
                {
                    //for each time that scope is called
                    if (!occurance.isDefinition)
                    {
                        newErrors = validateParamTypes(occurance, paramTypes, scope);

                        errorList.addAll(newErrors);
                    }
                }
            }
        }

        return errorList;
    }

    public ArrayList<String> validateParamTypes(Token funcOccurance, ArrayList<String> declarationParamTypes, Scope functionScope)
    {
        ArrayList<String> errorList = new ArrayList<>();
        ArrayList<String> occuranceParamTypes = setupOccuranceParams(funcOccurance);

        if (occuranceParamTypes.size() != declarationParamTypes.size())
        {
            String log = "Semantic Error: Incorrect number of parameters in function reference "+funcOccurance.getLexeme()+" at "+funcOccurance.getLocationStringCols();
            errorList.add(log);
            return errorList;
        }

        for (int i = 0; i<occuranceParamTypes.size(); i++)
        {
            String occrType = occuranceParamTypes.get(i);
            String decType = declarationParamTypes.get(i);

            if (!occrType.equals(decType))
            {
                //if both types don't match we error
                String log = "Semantic Error: Incorrect parameter type \""+occrType+"\" expecting \""+decType+"\" in "+funcOccurance.getLexeme()+" at "+funcOccurance.getLocationStringCols();
                errorList.add(log);
            }
        }

        return errorList;
    }

    private ArrayList<String> setupOccuranceParams(Token funcOccurance)
    {
        ArrayList<String> occuranceParamTypes = new ArrayList<>();

        for (int i = funcOccurance.getIndex()+2; i<tokenList.size(); i++)
        {
            String strType;
            //starting at funcName of funcName( param1, ... , paramN);
                //getIndex()+2 skips the (
            
            Token paramTok = tokenList.get(i);

            if (paramTok.getType() == TokenTypes.TRPAR)
            {
                //we have hit ) and must be done with our parameters
                break;
            }

            Scope paramFunc = getScopeFromTokenName(paramTok);
            if (paramFunc != null)
            {
                strType = paramFunc.getReturnType().toString();
                occuranceParamTypes.add(strType);
                TokenTypes[] advanceTypes = {TokenTypes.TCOMA, TokenTypes.TRPAR};

                //done with consume, gonna advance till we pass it
                i = advanceUntil(advanceTypes, i); 
            }
            else
            {
                strType = consumeVarReturnTyping(funcOccurance);
                occuranceParamTypes.add(strType);
                TokenTypes[] advanceTypes = {TokenTypes.TCOMA, TokenTypes.TRPAR};

                //done with consume, gonna advance till we pass it
                i = advanceUntil(advanceTypes, i);

                //wasn't a function so if we're on TRPAR now we wanna break as we're done
                if (tokenList.get(i).getType() == TokenTypes.TRPAR)
                    break;
            }
                       
        }

        return occuranceParamTypes;
    }


    public String consumeVarReturnTyping(Token funcOccurance)
    {
        //funcName(var)
        //var == structName || structName.var || arrayName || arrayName[index] || arrayName[index].var || funcName(parameters) <- hate this >:(, also hate arrays esp when they can be inside a funcparameter

        //index of funcName, need to step forward 2 to get to the start of var
        int funcIndex = funcOccurance.getIndex()+2;
        String outString = null;
        Boolean varIsFunc;

        Scope callScope = getScopeAtTokenLoc(funcOccurance);
        varIsFunc = lookupFuncExists(funcOccurance);
        if (varIsFunc)
        {
            Scope funcScope = getScopeFromTokenName(funcOccurance);

            //if we're a function we're getting the function return type
            outString = funcScope.getReturnType().toString();
            return outString;
        }

        Symbol varSym = callScope.lookupSymbolByTokenName(funcOccurance);

 
        symTypes symType = varSym.getType();


        if (symType == symTypes.structVar)
        {
            //if we've received a structVar it is either done or followed by .varName
            
            //so we step index and check if the next token is TDOTT
            Token varTok;
            funcIndex++;
            varTok = tokenList.get(funcIndex);

            if (varTok.getType() == TokenTypes.TDOTT)
            {
                //is structName.varName to get var we step forward once more and reference the type in the declaration of startingSym
                funcIndex++;
                varTok = tokenList.get(funcIndex);

                Symbol structIdSymbol = getStructIdOf(varSym);
                Token structValueTok = structIdSymbol.getStructValueToken(varTok);

                if (structValueTok==null)
                    return null;

                //get the type as a string from our value token, convert this string to a symTypes type, then convert to a string to compare against the other values
                outString = Symbol.symTypeFromString(structValueTok.getTypeString()).toString();
                return outString;
            }
            else
            {
                //is varName and varName is a struct, so return the subtype of varName
                outString = varSym.getSubtype();
                return outString;
            }
        }
        else if (symType == symTypes.typeVar)
        {
            Token varTok;
            funcIndex++;
            varTok = tokenList.get(funcIndex);

            //khvkhlvkhbl
                //varTok is the first token after typeName in typeName[index].var
                //if we do not receive TLBRK we are using the raw typing of the array 
                    //e.g. locations from locations def array [20] of location end
                //if we do array indexes matter, if we have an index we're using the raw typing of the 
                    //e.g. location from locations def array [20] of location end
            if (varTok.getType() == TokenTypes.TLBRK)
            {
                //now what, because we can use struct/array.var as a legal input if it's an integer
                //not too bad in case of struct but arrays are little rat bastards incorporating their own []'s

                //so step forward and grab that first token
                boolean lastWasOperator = false;
                boolean handlingIndex = true;
                String arrayType;

                while (handlingIndex)
                {
                    //step token forward
                    funcIndex++;
                    varTok = tokenList.get(funcIndex);

                    if (varTok.getType() == TokenTypes.TRBRK)
                    {
                        //last input in the index was a maths operator and that's not legal
                        if (lastWasOperator)
                            return null;
                    }


                    if (varTok.getType() == TokenTypes.TINTG)
                    {
                        //this is legal but it's also only the index, so just do nothing move onto next step
                    }
                    else if (varTok.getType() == TokenTypes.TIDEN)
                    {
                        //if not ident, then we need it to be an int lit and if it is an ident it has to be an int symbol or struct.intVar

                        String newType = consumeVarReturnTyping(funcOccurance);
                        //we've consumed a function, so we have to step past that function. 
                            //TODO: This will break if we have to step past a function taking multiple parameters or a function as a parameter, I would fix this by returning typeString and the new index but it's late and I wanna turn this innnnnn
                        TokenTypes[] advanceTypes = {TokenTypes.TCOMA, TokenTypes.TRPAR};
                        funcIndex = advanceUntil(advanceTypes, funcIndex);

                        if (!newType.equals(symTypes.intg.toString()))
                        {
                            //we did not get an integer typed thingo and so we'll error by returning null
                            return null;
                        }
                    }

                    //we've consumed some form of int token, we step and look for end of index or a mathematical operator + - * / ^ 
                    //step token forward
                    funcIndex++;
                    varTok = tokenList.get(funcIndex);

                    //TODO: does not handle functions returning structs that you immediately reference eg getStruct().varName is illegal but I think that's just generally illegal? unclear.
                    switch (varTok.getType())
                    {
                        case TRBRK -> handlingIndex=false;
                        //these are legal operators and so we do nothing and keep looking for end or
                        case TPLUS -> {lastWasOperator = true;}
                        case TMINS -> {lastWasOperator = true;}
                        case TSTAR -> {lastWasOperator = true;}
                        case TDIVD -> {lastWasOperator = true;}
                        case TCART -> {lastWasOperator = true;}
                        default -> {
                            //not TLBRK or a maths op, err
                            return null;}
                    }
                }

                if (varTok.getType() == TokenTypes.TDOTT)
                {
                    //we now have to do all the same logic from struct but as if it were an Array
                }
                else
                {
                    //raw array Type ref, so return type is teh typeVar's subtype
                    outString = ;
                }

            }
            
            

        }
        else
        {
            //typing is intg, flot, etc and can just be grabbed from the symbol
            outString = symType.toString();
        }

        return outString;
    }


    private Symbol getStructIdOf(Symbol startingSym)
    {
        if (startingSym.getType() == symTypes.structVar)
        {
            return globalScope.lookupSymbolbyName(startingSym.getSubtype(), symTypes.structID);
        }
        else
        {
            if (startingSym.getType() == symTypes.typeVar)
            {
                return globalScope.lookupSymbolbyName(startingSym.getSubtype(), symTypes.typeID);
            }
        }
        //invalid typing
        return null;
    }
}

//The complexity of all the symbol table logic got away from me, this needs significant cleanup and to be designed 
//such that functions are more reusable than they are currently that is completely impractical with the timeline of 
//this and my other assignments especially while working on this alone I am frankly disgusted by the length of this file.