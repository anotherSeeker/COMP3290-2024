package SymbolTable;

import Tokeniser.Token;
import java.util.ArrayList;

public class Symbol 
{
    public static enum symTypes
    {
        symID, ilit, flit, bool, structID, typeID, funcID, undf
    }

    private final Token firstToken;
    private final symTypes type;
    private final ArrayList<Token> values = new ArrayList<>(); 
    private final ArrayList<Token> occurances = new ArrayList<>();
    private final Scope parentScope;

    public Symbol(Token _token, symTypes _type, Scope scope)
    {
        firstToken = _token;
        type = _type;
        occurances.add(_token);
        parentScope = scope;
    }

    public boolean exists(String inputName)
    {
        return (firstToken.getLexeme().equals(inputName));
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
