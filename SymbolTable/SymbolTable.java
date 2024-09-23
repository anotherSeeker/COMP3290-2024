package SymbolTable;
import Tokeniser.Token;
import Tokeniser.TokenTypes;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SymbolTable 
{
    private ArrayList<Token> tokenList;
    private ArrayList<Symbol> table;
    private boolean symtError = false;

    public SymbolTable(ArrayList<Token> _tokenList)
    {
        tokenList = _tokenList;

        populateTable();
    }

    private void populateTable()
    {
        String state = "Neutral";
        Token currentToken = tokenList.get(0);
        String currentScope = findTokScope(currentToken);

        boolean run = true;
        try
        {
            for (int i = 0; i<tokenList.size(); i++)
            {
                currentToken = tokenList.get(i);
                switch (currentToken.getType()) {
                    case TokenTypes.TCD24 -> currentScope = "global";
                    case TokenTypes.TCNST -> {
                        boolean runCnst = true;
                        while (runCnst)
                        {
                            i++;
                            Token SymbolToken = tokenList.get(i);
                            i+=2;
                            ArrayList<Token> values = new ArrayList<>();
                            values.add(tokenList.get(i));
                            String type = "Constant";
                            SymbolTable.add(SymbolToken, values, type, currentScope);

                            //we have made a const symbol, if we don't have a comma we're done with consts and so we should move on.
                            if (tokenList.get(i+1).getType() != TokenTypes.TCOMA)
                            {
                                break;
                            }
                        }


                    }
                    case TokenTypes.TTYPD -> state = "TypeDef";
                    case TokenTypes.TFUNC -> state = "Func";
                    default -> {}
                }
            }
        }
        catch(Exception e)
        {
            System.err.println("Symbol Table errored" + e);
            symtError = true;
        }
    }

    private String findTokScope(Token inputToken)
    {
        String scope = "undefined";
        for (int i = 0; i < tokenList.size(); i++)
        {
            Token listToken = tokenList.get(i);
            switch(listToken.getType())
            {
                case TokenTypes.TCD24:
                     scope="Global";
                     break;
                case TokenTypes.TFUNC:
                    scope=tokenList.get(i+1).getLexeme(); //get the name of the function as the scope
                    break;
                case TokenTypes.TMAIN:
                    scope="Main";
                    break;
                case TokenTypes.TTEND:
                    if (scope.equalsIgnoreCase("Global") || scope.equalsIgnoreCase("Main"))
                    {
                        break;
                    }
                    //if we're not in global or main, then we saw TFUNC which means we're defining functions, 
                    //which means we reset to global but honestly shouldn't matter
                    scope = "Global";
                    break;
                default:
                    break;
            }


            if (inputToken.getLine() == listToken.getLine() && inputToken.getColumn() == listToken.getColumn())
            {
                return scope;
            }
        }

        System.err.print("Undefined scope for token: "+ inputToken.toStringWithLocation());
        return scope;
    }

    public boolean add(Token _token, ArrayList<Token> _values, String _type, String _scope)
    {
       


        return true;
    }

    public boolean addOccurance(Token _token)
    {
        Symbol existingSymbol = symbolIsInTable(_token);
        if (existingSymbol != null)
        {
            //symbol is in the table, add an occurance
            existingSymbol.addOccurance(_token);
            return true;
        }
        System.err.println("Adding occurance to symbol that does not exist: "+ _token.toStringWithLocation());
        return false;
    }

    public Symbol symbolIsInTable(Token inputToken, String scope)
    {

        return null;
    }
    
    public boolean validateSymbol(Token inputToken, String expectedType)
    {
        return true;
    }

}
