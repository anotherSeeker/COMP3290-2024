package SymbolTable;

import Tokeniser.Token;

public class Symbol 
{
    private Token token;
    private String scope;


    public Symbol(Token _token, String _scope)
    {
        token = _token;
        scope = _scope;
    }
}
