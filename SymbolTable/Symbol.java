package SymbolTable;

import Tokeniser.Token;
import java.util.ArrayList;

public class Symbol 
{
    private Token firstToken;
    private String type;
    private ArrayList<Token> values = new ArrayList<>(); 
    private ArrayList<Token> occurances = new ArrayList<>();


    public Symbol(Token _token, String _type)
    {
        firstToken = _token;
        type = _type;
        occurances.add(_token);
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
