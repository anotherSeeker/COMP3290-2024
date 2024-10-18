package SymbolTable;

import Tokeniser.Token;
import java.util.ArrayList;

public class Scope 
{
    private final ArrayList<Symbol> symbols = new ArrayList<>();
    private final ArrayList<Token> scopeOccurances = new ArrayList<>();
    private Token scopeToken;
    private final String name;

    public Scope(Token _token)
    {
        scopeToken = _token;
        name = scopeToken.getLexeme();
    }

    public Scope(boolean globOrMain)
    {
        scopeToken = null;
        if (globOrMain)
            name = "Global";
        else
            name = "Main";
    }

    public ArrayList<Symbol> getSymbolList()
    {
        return symbols;
    }

    public ArrayList<Token> getOccurances()
    {
        return scopeOccurances;
    }

    public void updateMainScopeToken(Token tok)
    {
        //this is here purely to set the main scope token after instantiating
        scopeToken = tok;
    }

    public void addSymbol(Symbol sym)
    {
        Token _token = sym.getToken();
        if (isInScope(_token))
            addSymbolOccurance(_token);
        else
            symbols.add(sym);
    }

    private void addSymbolOccurance(Token _token)
    {
        Symbol entry = getSymbolByTokenName(_token);
        if (entry != null)
            entry.addOccurance(_token);
    }

    public void addScopeOccurance(Token _token)
    {
        for (Token occurToken : scopeOccurances)
        {
            //if the token is already in our occurances we just don't add it, shrimple as
            if (occurToken.matchLocation(_token))
                return;
        }
        scopeOccurances.add(_token);
    }

    public boolean tokenIsExactScope(Token _token)
    {
        if (scopeToken != null)
            return (scopeToken.matchLocation(_token) && scopeToken.matchIdentifier(_token));
        return false;
    }

    public boolean tokenIsScopeName(Token _token)
    {
        if (scopeToken != null)
            return scopeToken.matchIdentifier(_token);

        return false;
    }

    public boolean isInScope(Token _token)
    {
        String inputLex = _token.getLexeme();

        int inputLine = _token.getLine();
        int inputCol = _token.getColumn();
        int scopeLine;
        int scopeCol;
        if (scopeToken != null)
        {
            scopeLine = scopeToken.getLine();
            scopeCol = scopeToken.getColumn();
        }
        else
        {
            scopeLine = 0;
            scopeCol = 0;
        }

        for (Symbol sym : symbols)
        {
            if (inputLex.equals(sym.getToken().getLexeme()))
            {
                if (inputLine > scopeLine)
                {
                    //the scopeToken is below our scope start, we ensure that we check the last scope before the scopeToken outside this function
                    return true;
                }
                else if (inputLine == scopeLine)
                {
                    //the scopeToken is inline with our scope start, we ensure that we check the last scope before the scopeToken outside this function
                    if (inputCol >= scopeCol)
                    {
                        //the scopeToken is after our scope start on the same line
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Symbol lookupSymbol(Token _token, Symbol.symTypes type)
    {
        String _name = _token.getLexeme();
        for (Symbol entry : symbols)
        {
            if (entry.matchSymbolByType(_name, type))
                return entry;
        }
        return null;
    }

    public Symbol getSymbolByTokenName(Token _token)
    {
        String _name = _token.getLexeme();
        for (Symbol entry : symbols)
        {
            if (entry.matchSymbolByName(_name))
                return entry;
        }
        return null;
    }

    public String getName() 
    {
        return name;
    }

    public Token getToken()
    {
        return scopeToken;
    }
}
