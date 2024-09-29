package SymbolTable;
import Tokeniser.Token;
import java.util.ArrayList;

public class SymbolTable 
{
    private final ArrayList<Token> tokenList; 
    private ArrayList<Scope> table;
    private boolean symtError = false;

    public SymbolTable(ArrayList<Token> _tokenList) 
    {
        tokenList = _tokenList;

        populateTable();
    }

    private void populateTable()
    {

    }
}
