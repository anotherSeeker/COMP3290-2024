package SymbolTable;

import Tokeniser.Token;
import java.util.ArrayList;

public class Symbol 
{
    private Token token;
    private ArrayList<Token> values = new ArrayList<>();
    private String type;
    private String scope;
    private ArrayList<Token> occurances = new ArrayList<>();


    public Symbol(Token _token, String _type, String _scope)
    {
        token = _token;
        type = _type;
        scope = _scope;
        occurances.add(token);
    }

    public Symbol(Token _token, ArrayList<Token> _values, String _type, String _scope)
    {
        token = _token;
        values = _values;
        type = _type;
        scope = _scope;
        occurances.add(token);
    }

    public void addOccurance(Token _token)
    {
        occurances.add(_token);
    }

    public void addValue(Token _token)
    {
        values.add(_token);
    }
}
