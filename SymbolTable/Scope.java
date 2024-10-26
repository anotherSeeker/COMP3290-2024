package SymbolTable;

import SymbolTable.Symbol.symTypes;
import Tokeniser.Token;
import Tokeniser.TokenTypes;
import java.util.ArrayList;

public class Scope 
{
    private static Scope globalScope;
    private final ArrayList<Symbol> symbols = new ArrayList<>();
    private final ArrayList<Token> scopeOccurances = new ArrayList<>();
    private Token scopeToken;
    private symTypes returnType = null;
    private final boolean isFunc;
    private final String name;

    public Scope(Token _token)
    {
        scopeToken = _token;
        scopeToken.isDefinition = true;
        name = scopeToken.getLexeme();
        isFunc = true;
    }

    public Scope(Token _token, symTypes _returnType)
    {
        scopeToken = _token;
        scopeToken.isDefinition = true;
        name = scopeToken.getLexeme();
        isFunc = true;
        returnType = _returnType;
    }

    public Scope(boolean globOrMain)
    {
        isFunc = false;
        scopeToken = null;
        if (globOrMain)
        {
            name = "Global";
            globalScope = this;
        }
        else
            name = "Main";
    }

    public ArrayList<Symbol> getSymbolList()
    {
        return symbols;
    }

    public Token getScopeToken()
    {
        return scopeToken;
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
            addSymbolOccurance(_token, sym.getValues());
        else
            symbols.add(sym);
    }

    private void addSymbolOccurance(Token _token, ArrayList<Token> values)
    {
        Symbol entry = lookupSymbolByTokenName(_token);
        if (entry != null)
        {
            entry.addOccurance(_token);
            entry.addValues(values);
        }
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

    public Symbol lookupSymbol(Token _token, symTypes type)
    {
        String _name = _token.getLexeme();
        for (Symbol entry : symbols)
        {
            if (entry.matchSymbolByType(_name, type))
                return entry;
        }
        return null;
    }

    public Symbol lookupSymbolbyName(String name, symTypes type)
    {
        for (Symbol entry : symbols)
        {
            if (entry.matchSymbolByType(name, type))
                return entry;
        }
        return null;
    }

    public Symbol lookupSymbolByTokenName(Token _token)
    {
        String _name = _token.getLexeme();
        for (Symbol entry : symbols)
        {
            if (entry.matchSymbolByName(_name))
                return entry;
        }
        return null;
    }

    public ArrayList<Symbol> getAllOfType(symTypes type)
    {
        ArrayList<Symbol> syms = new ArrayList<>();

        for (Symbol entry : symbols)
        {
            if (entry.matchSymbolByType(type))
                syms.add(entry);
        }


        return syms;
    }

    public void setReturnType(symTypes _returnType)
    {
        returnType = _returnType;
    }

    public symTypes getReturnType()
    {
        return returnType;
    }

    public boolean isFunc()
    {
        return isFunc;
    }

    public String getName() 
    {
        return name;
    }

    public Token getToken()
    {
        return scopeToken;
    }

    public String occurancesToString()
    {
        String outString = "";
        int count = 0;
        for (Token occurance : scopeOccurances)
        {
            outString += "\n\t"+count+": "+occurance.getLocationStringCols();
            if (occurance.isDefinition)
                outString+=" : Definition";
            if (occurance.isAssignment)
                outString+=" : Assignment";
            count++;
        }


        return outString;
    }






    public ArrayList<String> getParameterTypes(ArrayList<Token> tokenList)
    {        
        //we'll add the required type to this list one by one
        ArrayList<String> paramTypes = new ArrayList<>();

        for (int i = scopeToken.getIndex(); i<tokenList.size(); i++)
        {
            //need to know if a symbol is a parameter
            Token tok = tokenList.get(i);
            if (tok.getType() == TokenTypes.TRPAR)
            {
                //we have reached the end of the parameters
                return paramTypes;
            }

            if (tok.getType() == TokenTypes.TIDEN)
            {
                Symbol sym = lookupSymbolByTokenName(tok);
                if (sym != null)
                {
                    symTypes symType = sym.getType();

                    if (symType == symTypes.structVar || symType == symTypes.typeVar)
                    {
                        paramTypes.add(sym.getSubtype());
                    }
                    else
                    {
                        paramTypes.add(symType.toString());
                    }
                    //we've grabbed the type from the symbol however we're still sitting at the first ident token
                    //we want to see either a , or a ) to end and we need to step past the type as that token can be an ident
                        //name : type, name : type)
                        //if we can't step two tokens forward in a definition here we have incorrect formatting, type is always one token
                    i+=2;
                }
            }          
        }

        return paramTypes;
    }

    private String getStructOrTypeRefSubtype(Token inputTok, Symbol sym, ArrayList<Token> tokenList)
    {
        String outString = null;

        //step through looking for matching parameters
        for (int i = inputTok.getIndex()+1; i<tokenList.size();i++)
        {
            Token tok = tokenList.get(i);
            switch (tok.getType()) 
            {
                case TLBRK -> {

                }
                case TDOTT -> {

                }
                default -> {
                    outString = sym.getSubtype();
                }
            }
        }


        return outString;//returns null on fail
    }
}
