package Tokeniser;
import java.io.File;
import java.util.ArrayList;
import FileReader.*;


public class LexemeTokeniser 
{
    private ArrayList<Token> tokenList = new ArrayList<Token>();
    private ArrayList<Token> errorList = new ArrayList<Token>();

    private String lexemeBuffer = "";
    private ArrayList<LexChar> lexCharBuffer = new ArrayList<LexChar>();
    private FileReader reader;

    private String legalSymbols = ", [ ] ( ) = + - * / % ^ < > : ; .";

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

    public ArrayList<Token> run(String filePath)
    {
        File sourceFile = new File(filePath);
        reader = new FileReader(sourceFile);
        
        runTokenise();

        return tokenList;
    }

    private void runTokenise()
    {
        while (reader.hasNext())
        {
            LexChar current = reader.step();

            validateCharacter(current);
        }
    }

    private void tokeniseBuffer()
    {
        int line   = lexCharBuffer.get(0).getLine(); 
        int column = lexCharBuffer.get(0).getCol();

        Token outToken;
        if (bufferIsLegal)
        {
            switch (state) 
            {
                case identifier:
                    TokenTypes key = checkForKeyword();

                    if (key != TokenTypes.TIDEN)
                    {
                        outToken = new Token(key, lexemeBuffer, line, column);
                        tokenList.add(outToken);
                        wipeBuffers();
                        break;
                    }

                    outToken = new Token(TokenTypes.TIDEN, lexemeBuffer, line, column);
                    tokenList.add(outToken);
                    wipeBuffers();
                    break;

                case number:
                    outToken = new Token(TokenTypes.TILIT, lexemeBuffer, line, column);
                    tokenList.add(outToken);
                    wipeBuffers();
                    break;

                case flit:
                    outToken = new Token(TokenTypes.TFLIT, Double.parseDouble(lexemeBuffer), line, column);
                    break;

                case undefined:
                    outToken = new Token(TokenTypes.TUNDF, lexemeBuffer, line, column);
                    tokenList.add(outToken);
                    errorList.add(outToken);
                    wipeBuffers();
                    break;

                default:
                    //we shouldn't be here we're gonna handle it like a undefined
                    outToken = new Token(TokenTypes.TUNDF, lexemeBuffer, line, column);
                    tokenList.add(outToken);
                    errorList.add(outToken);
                    wipeBuffers();
            }
        }
    }

    private boolean validateCharacter(LexChar current)
    {
        boolean out = true;

        //don't add and tokenise everything when whitespace or do our valiation checks adding to buffer within
            //this is a string not a char so we make it a char
        if (Character.isWhitespace(current.getCharacter().charAt(0)))
        {
            tokeniseBuffer();
        }
        else
        {
            appendCharToBuffers(current);

            out = notIllegalChar(current.getCharacter());

            if (out == true)
            {
                out = handleValidationState(current.getCharacter());
            }
            else 
            {
                //illegal character, if buffer already exists tokenise and then tokenise as undf
                tokeniseBuffer();
            }
        }

        return out;
    }

    private boolean handleValidationState(String currChar)
    {
        boolean out;

        switch (state) 
        {
            case none:
                out = statelessValidate(currChar);
                break;
            case identifier:
                out = identifierValidate(currChar);
                break;
            case number:
                out = numberValidate(currChar);
                break;
            case flit:
                out = flitValidate(currChar);
                break;
            case comment:
                out = commentValidate(currChar);
                break;
            case multicomment:
                out = multiCommentValidate(currChar);
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
        return legalSymbols.contains(currChar);
    }

    private boolean statelessValidate(String currChar)
    {
        //this can't be false, we already determined that we have a legal character.
        if (Character.isAlphabetic(currChar.charAt(0)))
        {
            state = tokeniserState.identifier;
            bufferIsLegal = true;
        }
        else if (Character.isDigit(currChar.charAt(0)))
        {
            state = tokeniserState.number;
            bufferIsLegal = true;
        }
        else if (currChar.equals("\"")) 
        {
            state = tokeniserState.string;
            bufferIsLegal = false;    
        }
        else
        {
            state = tokeniserState.symbol;
            bufferIsLegal = isSymbolLegal();
        }

        return true;
    }

    private boolean identifierValidate(String currChar)
    {
        //we are already an ident so first char must be alphabetic
        return ( Character.isAlphabetic(currChar.charAt(0)) || Character.isDigit(currChar.charAt(0)) );
    }

    private boolean numberValidate(String currChar)
    {
        boolean out = false;

        if (Character.isDigit(currChar.charAt(0)))
        {
            out = true;
            bufferIsLegal = true;
        }
        else if (currChar.equals("."))
        {
            out = true;
            state = tokeniserState.flit;
            bufferIsLegal = false;
        }

        return out;
    }

    private boolean flitValidate(String currChar)
    {
        boolean out = false;

        if (Character.isDigit(currChar.charAt(0)))
        {
            out = true;
            bufferIsLegal = true;
        }

        return out;
    }

    private boolean commentValidate(String currChar)
    {
        return currChar.equals("\n");
    }

    private boolean multiCommentValidate(String currChar)
    {
        return endMultiLineComment();
    }

    private TokenTypes checkForKeyword()
    {
        //todo
        return TokenTypes.TIDEN;
    }



    private boolean endMultiLineComment()
    {
        if (lexemeBuffer.contains("**/"))
        {
            return true;
        }
        return false;
    }

    private boolean isSymbolLegal()
    {
        String[] multiCharSymbols = {"<=", ">=", "!=", "==", "+=","-=", "*=", "/=", "/**", "**/"};

        if ("/--".contains(lexemeBuffer) || "/**".contains(lexemeBuffer))
        {
            state = tokeniserState.comment;
            return true;
        }
        if (lexemeBuffer.equals("/**"))
        {
            state = tokeniserState.comment;
            return true;
        }

        if (lexemeBuffer.length() > 1)
        {
            for (String symbol : multiCharSymbols)
            {
                if (symbol.equals(lexemeBuffer))
                {
                    return true;
                }
            }
        }
        else if (legalSymbols.contains(lexemeBuffer))
        {
            return true;
        }

        return false;
    }

    private void appendCharToBuffers(LexChar lexChar)
    {
        lexemeBuffer += lexChar.getCharacter();
        lexCharBuffer.add(lexChar);
    }

    private void wipeBuffers()
    {
        lexemeBuffer = "";
        lexCharBuffer = new ArrayList<LexChar>();
    }
}
