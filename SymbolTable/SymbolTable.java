package SymbolTable;
import SymbolTable.Symbol.symTypes;
import Tokeniser.Token;
import Tokeniser.TokenTypes;
import java.util.ArrayList;

public class SymbolTable 
{
    private final ArrayList<Token> tokenList; 
    private final ArrayList<Scope> scopeList = new ArrayList<>();
    private final Scope globalScope = new Scope(true);
    private final Scope mainScope = new Scope(false);
    private boolean symtError = false;
    private final ArrayList<String> errorLog = new ArrayList<>();
    
    private enum SymState {
        Global, Main, Func
    }

    public SymbolTable(ArrayList<Token> _tokenList) 
    {
        tokenList = _tokenList;
        populateTable();
    }

    private void populateTable()
    {
        //scopes 0 and 1 are globalScope and mainScope
        scopeSetup();

        stepThroughTokens();

        if (symtError)
        {
            System.out.println("Symbol Table has failed to initialise correctly");
        }
    }

    private void scopeSetup()
    {
        //these are always the first two scopes
        addScope(globalScope);
        addScope(mainScope);

        Token scopeToken;
        for (int i = 0; i < tokenList.size(); i++)
        {
            Token listToken = tokenList.get(i);

            //each function will become it's own scope and we will later add the locals for our scopes
            if (listToken.getType() == TokenTypes.TFUNC) 
            {
                try {
                    scopeToken = tokenList.get(i+1);

                    Scope newScope = new Scope(scopeToken);
                    addScope(newScope);
                } catch (Exception e) {
                    System.out.println("Scope setup at: "+listToken.getLocationStringErr()+" has failed");
                    symtError = true;
                }
                
            }
        }
    }

    private void stepThroughTokens()
    {
        for (int i = 0; i<tokenList.size();i++)
        {
            Token listToken = tokenList.get(i);

            //will move through the loop depending on how many consts there are but will only start after seeing keyword constants
            i = stepHandleConsts(listToken, i);
            listToken = tokenList.get(i);



            stepHandleLooseIds(listToken);
        }
    }

    private void stepHandleLooseIds(Token token)
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
                Scope _scope = getScopeFromTokenName(token);
                Symbol.symTypes type = getSymTypeFromToken(token);
                Symbol newSymbol = new Symbol(token, type, _scope);

                addToScope(newSymbol, _scope);
            }
        } 
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
                Token testTok = tokenList.get(k);
                if (testTok.getType() == TokenTypes.TIDEN)
                {
                    identToken = testTok;
                    k+=2;
                    valueToken = tokenList.get(k);
                    type = getSymTypeFromToken(valueToken);
                    if (type == symTypes.undf)
                    {
                        String errStr = "Symbol Table Error: Assigned Illegal Value: "+valueToken.getTypeString()+" to constant: "+identToken.getLexeme()
                                        +"\n"+identToken.getLocationStringErr();
                        logError(errStr);
                    }

                    Symbol newSymbol = new Symbol(identToken, type, globalScope);
                    newSymbol.addValue(valueToken);

                    addToScope(newSymbol, globalScope);
                }
                if (testTok.getType() == TokenTypes.TMAIN 
                ||  testTok.getType() == TokenTypes.TTYPD 
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

    //-------------------------------------
    private void addScope(Scope scope)
    {
        scopeList.add(scope);
    }

    private void addToScope(Symbol sym, Scope scope)
    {
        scope.addSymbol(sym);
    }
    //-------------------------------------
    public boolean tokenIsStructName(Token _token)
    {
        Symbol sym = globalScope.getSymbolByTokenName(_token);
        return sym.getType() == Symbol.symTypes.structID;
    }

    public boolean tokenIsTypeName(Token _token)
    {
        Symbol sym = globalScope.getSymbolByTokenName(_token);
        return sym.getType() == Symbol.symTypes.typeID;
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

    private Symbol.symTypes getSymTypeFromToken(Token token)
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
                    return Symbol.symTypes.symID;
            }
            case TILIT -> {return Symbol.symTypes.ilit;}
            case TFLIT -> {return Symbol.symTypes.flit;}
            case TBOOL -> {return Symbol.symTypes.bool;}
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

    public boolean lookupStructExists(Token _token)
    {
        return (lookup(_token, Symbol.symTypes.structID) != null);
    }

    public boolean lookupTypeExists(Token _token)
    {
        return (lookup(_token, Symbol.symTypes.typeID) != null);
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


    public void printErrorLog()
    {
        String RESET = "\u001B[0m";
        String RED = "\u001B[31m";

        for (String error : errorLog)
        {
            System.out.println(RED+error+RESET);
        }
    }
}
