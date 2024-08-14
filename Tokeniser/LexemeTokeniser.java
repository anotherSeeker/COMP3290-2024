package Tokeniser;
import java.io.File;
import java.util.ArrayList;
import FileReader.*;

public class LexemeTokeniser 
{
    private ArrayList<Token> tokenList = new ArrayList<Token>();
    private ArrayList<Token> errorList = new ArrayList<Token>();

    private String lexemeBuffer = "";
    private FileReader reader;

    private enum tokeniserState
    {
        none,
        identifier, //not confirmed as keyword but legal as ident
        keyword, //confirmed keyword
        number, //not confirmed as int or float
        flit,
        ilit,
        symbol, //single or multi character symbol
        string, //" ... "
        comment, // /--
        multicomment, // /**     **/ can go across lines unlike string
        undefined //illegal input, tokenise the largest legal thing in the buffer and repeat erroring if it's not legal
    }

    private tokeniserState state = tokeniserState.none;
    private boolean bufferIsLegal = false;


    public LexemeTokeniser()
    {}

    public ArrayList<Token> run(String filePath)
    {
        File sourceFile = new File(filePath);
        reader = new FileReader(sourceFile);
        
        while (reader.hasNext())
        {
            tokenise();
        }

        return tokenList;
    }

    private void tokenise()
    {
        LexChar current = reader.step();
        lexemeBuffer += current.getCharacter();

        validateCharacter(current.getCharacter());



    }

    private void tokeniseBuffer()
    {
        switch (state) {
            case tokeniserState.identifier:
                
                break;
            default:
                //buffer must be empty do nothing
        }
    }

    private boolean validateCharacter(String currChar)
    {
        boolean out = true;

        if (Character.isWhitespace(currChar.charAt(0)))
        {
            tokeniseBuffer();
        }

        out = notIllegalChar(currChar);

        switch (state) {
            case tokeniserState.none:
                baseValidation(currChar);
                break;
            case tokeniserState.identifier:
                out = identifierValidation(currChar);
                break;
            default:
                throw new AssertionError();
        }

        return out;
    }

    private boolean notIllegalChar(String currChar)
    {
        if (Character.isAlphabetic(currChar.charAt(0)))
        {
            return true;
        }
        else if (Character.isDigit(currChar.charAt(0)))
        {
            return true;
        }
        String legalSymbols = ",[]()=+-*/%^<>:;.";
        return legalSymbols.contains(currChar);
    }

    private void baseValidation(String currChar)
    {
        if (Character.isAlphabetic(currChar.charAt(0)))
        {
            state = tokeniserState.identifier;
        }
        else if (Character.isDigit(currChar.charAt(0)))
        {
            state = tokeniserState.number;
        }
        else if (currChar.equals("\"")) 
        {
            state = tokeniserState.string;    
        }
        else
        {
            state = tokeniserState.symbol;
        }
    }

    private boolean identifierValidation(String currChar)
    {
        return ( Character.isAlphabetic(currChar.charAt(0)) || Character.isDigit(currChar.charAt(0)) );
    }
}
