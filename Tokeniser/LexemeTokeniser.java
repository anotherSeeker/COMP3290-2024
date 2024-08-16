package Tokeniser;
import java.io.File;

import FileReader.*;
import java.util.*;


public class LexemeTokeniser 
{
    private ArrayList<Token> tokenList = new ArrayList<Token>();
    private ArrayList<Token> errorList = new ArrayList<Token>();

    //private String lexemeBuffer = "";
    private ArrayList<LexChar> lexemeBuffer = new ArrayList<LexChar>();
    private FileReader reader;

    private String legalSymbols = ", [ ] ( ) = + - * \" / % ^ < > : ; .";
    private String[] legalMultiCharSymbols = {"<=", ">=", "!=", "==", "+=","-=", "*=", "/="};

    private Dictionary<String, TokenTypes> symbolDict = new Hashtable<>();

    private enum tokeniserState
    {
        none,
        identifier, //not confirmed as keyword but legal as ident
        keyword, //confirmed keyword
        number, //not confirmed as int or float
        floatlit,
        ilit,
        symbol, //single or multi character symbol
        string, //" ... "
        comment, // /--
        multicomment, // /**     **/ can go across lines unlike string
        undefined //illegal input, tokenise the largest legal thing in the buffer and repeat erroring if it's not legal
    }

    private tokeniserState state = tokeniserState.none;
    private String errorStateDesc = "N/A";
    private boolean bufferIsLegal = false;

    public ArrayList<Token> run(String filePath)
    {
        File sourceFile = new File(filePath);
        reader = new FileReader(sourceFile);

        setupDictionary();

        runTokenise();

        return tokenList;
    }

    private void runTokenise()
    {
        while (reader.hasNext())
        {
            LexChar current = reader.step();

            boolean shouldTokenise = isReadyToTokenise(current);
            if (shouldTokenise)
            {
                tokeniseBuffer(lexemeBuffer);
                wipeBuffer();
                resetState();
            }
        }
    }

    private void tokeniseBuffer(ArrayList<LexChar> lexBuffer)
    {
        if (getBufferString(lexBuffer).length() > 0)
        {
            int line   = lexBuffer.get(0).getLine(); 
            int column = lexBuffer.get(0).getCol();
            TokenTypes key;

            if (bufferIsLegal)
            {
                switch (state) 
                {
                    case identifier:
                        key = checkForKeyToken(lexBuffer);
                        if (key == null)
                        {
                            key = TokenTypes.TIDEN;
                        }
                       
                        buildAndPutTokenInList(key, lexBuffer, line, column);
                        break;
                    case number:
                        buildAndPutTokenInList(TokenTypes.TILIT, Double.parseDouble(getBufferString(lexBuffer)), line, column);
                        break;
                    case floatlit:
                        buildAndPutTokenInList(TokenTypes.TFLIT, Double.parseDouble(getBufferString(lexBuffer)), line, column);
                        break;
                    case string:
                        buildAndPutTokenInList(TokenTypes.TSTRG, lexBuffer, line, column);
                        break;
                    case comment:
                        //do nothing
                        break;
                    case multicomment:
                        //do nothing
                        break;
                    case symbol:
                        key = checkForKeyToken(lexBuffer);
                        buildAndPutTokenInList(key, lexBuffer, line, column);
                        break;
                    case undefined:
                        buildAndPutTokenInList(errorStateDesc, lexBuffer, line, column);
                        break;
                    default:
                        //we shouldn't be here we're gonna handle it like an undefined for now
                        buildAndPutTokenInList("Unhandled State", lexBuffer, line, column);
                        break;
                }
            }
            else
            {
                tokeniseIllegalBuffer(lexBuffer);
            }
        }
    }

    private boolean isReadyToTokenise(LexChar newChar)
    {
        boolean shouldTokenise = true;
        String charStr = newChar.getCharacter();
        boolean isWhitespaceChar = Character.isWhitespace(charStr.charAt(0));

        if(state == tokeniserState.string)
        {
            shouldTokenise = shouldTokeniseStringState(newChar);
            return shouldTokenise;
        }

        if (isCommentState())
        {
            lexemeBuffer.add(newChar);
            shouldTokenise = handleValidationState(newChar, lexemeBuffer);
            return shouldTokenise;
        }

        if (isWhitespaceChar)
        {
            shouldTokenise = true;
            return shouldTokenise;
        }
        
        lexemeBuffer.add(newChar);

        boolean isIllegalChar = isCharIllegal(charStr);
        if (!isIllegalChar)
        {
            shouldTokenise = handleValidationState(newChar, lexemeBuffer);
        }
        else 
        {
            //illegal character, if buffer already exists we will tokenise buffer and then tokenise current char as undf
            bufferIsLegal = false;
            shouldTokenise = true;
        }

        return shouldTokenise;
    }

    private boolean handleValidationState(LexChar lexChar, ArrayList<LexChar> buffer)
    {
        boolean shouldTokenise;

        String strChar = lexChar.getCharacter();

        switch (state) 
        {
            case none:
                shouldTokenise = statelessValidate(lexChar, buffer);
                break;
            case identifier:
                shouldTokenise = identifierValidate(strChar);
                break;
            case number:
                shouldTokenise = numberValidate(strChar);
                break;
            case floatlit:
                shouldTokenise = flitValidate(strChar);
                break;
            case comment:
                shouldTokenise = commentValidate(strChar);
                break;
            case multicomment:
                shouldTokenise = multiCommentValidate(strChar, buffer);
                break;
            case string:
                shouldTokenise = stringValidate(lexChar, buffer);
                break;
            case symbol:
                shouldTokenise = validateSymbol(lexChar, buffer);
                break;
            default:
                throw new AssertionError();
        }

        return shouldTokenise;
    }

    private boolean isCharIllegal(String currChar)
    {
        boolean isIllegal = false;
        if (Character.isAlphabetic(currChar.charAt(0)))
        {
            return isIllegal;
        }
        else if (Character.isDigit(currChar.charAt(0)))
        {
            return isIllegal;
        }

        isIllegal = !(legalSymbols.contains(currChar));
        return isIllegal;
    }

    private boolean statelessValidate(LexChar newChar, ArrayList<LexChar> buffer)
    {
        //We should never tokenise as our input character was legal and this is the only char in hte buffer
        boolean shouldTokenise = false;
        String currChar = newChar.getCharacter();

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
        else if(isSymbolLegal(buffer))
        {
            state = tokeniserState.symbol;
            
        }

        return shouldTokenise;
    }

    private boolean identifierValidate(String currChar)
    {
        //some of our symbols count as letters/numbers for some reason,
        boolean shouldTokenise;
        boolean isSymbol = legalSymbols.contains(currChar);
        if (isSymbol)
        {
            shouldTokenise = true;
            bufferIsLegal = false;
            return shouldTokenise;
        }

        boolean isAlphaNumeric = Character.isLetterOrDigit(currChar.charAt(0));
        if (isAlphaNumeric)
        {
            shouldTokenise = false;
            bufferIsLegal = true;
            return shouldTokenise;
        }

        shouldTokenise = true;
        bufferIsLegal = false;
        return shouldTokenise;
    }

    private boolean numberValidate(String currChar)
    {
        boolean shouldTokenise = false;

        if (Character.isDigit(currChar.charAt(0)))
        {
            bufferIsLegal = true;
        }
        else if (currChar.equals("."))
        {
            state = tokeniserState.floatlit;
            bufferIsLegal = false;
        }
        else
        {
            bufferIsLegal = false;
            shouldTokenise = true;
        }

        return shouldTokenise;
    }

    private boolean flitValidate(String currChar)
    {
        boolean shouldTokenise;

        if (Character.isDigit(currChar.charAt(0)))
        {
            shouldTokenise = false;
            bufferIsLegal = true;
        }
        else
        {
            shouldTokenise = true;
            bufferIsLegal = false;
        }

        return shouldTokenise;
    }

    private boolean commentValidate(String currChar)
    {
        boolean endComment = currChar.equals("\n");
        if (endComment)
        {
            wipeBuffer();
            resetState();
        }

        return endComment;
    }

    private boolean shouldTokeniseStringState(LexChar current)
    {
        boolean shouldTokenise = false;
        if (current.getCharacter().equals("\n"))
        {
            state = tokeniserState.undefined;
            errorStateDesc = "New line in String Prohibited";
            bufferIsLegal = false;
            shouldTokenise = true;

            return shouldTokenise;
        }

        shouldTokenise = handleValidationState(current, lexemeBuffer);
        return shouldTokenise;
    }

    private boolean multiCommentValidate(String currChar, ArrayList<LexChar> buffer)
    {
        return endMultiLineComment(buffer);
    }

    private boolean stringValidate(LexChar newChar, ArrayList<LexChar> buffer)
    {
        String currChar = newChar.getCharacter();
        boolean shouldTokenise = false;
        bufferIsLegal = false;

        buffer.add(newChar);

        if (currChar.equals("\""))
        {
            bufferIsLegal  = true;
            shouldTokenise = true;
            return shouldTokenise;
        }

        return shouldTokenise;
    }



    private void tokeniseIllegalBuffer(ArrayList<LexChar> buffer)
    {
        boolean shouldTokenise = true;
        ArrayList<LexChar> subBuffer = new ArrayList<LexChar>();
        //TODO: delete these strings
        String oldBufferString = getBufferString(buffer);
        String subBufferString = "";

        state = tokeniserState.none;

        for (int i = 0; i < buffer.size(); i++)
        {
            LexChar newChar = buffer.get(i);
            if (isCharIllegal(newChar.getCharacter()))
            {
                bufferIsLegal = true;
                tokeniseBuffer(subBuffer);
                subBuffer = new ArrayList<LexChar>();
                subBuffer.add(newChar);

                state = tokeniserState.undefined;
                errorStateDesc = "Illegal Character";
                tokeniseBuffer(subBuffer);
                subBuffer = new ArrayList<LexChar>();
                resetState();
            }
            else
            {
                shouldTokenise = handleValidationState(newChar, subBuffer);
                if (shouldTokenise)
                {
                    bufferIsLegal = true;
                    subBufferString = getBufferString(subBuffer);
                    tokeniseBuffer(subBuffer);
                    subBuffer = new ArrayList<LexChar>();
                    resetState();

                    subBuffer.add(newChar);
                    shouldTokenise = handleValidationState(newChar, subBuffer);
                }
                else
                {
                    subBuffer.add(newChar);
                }
            }
        }

        subBufferString = getBufferString(subBuffer);
        lexemeBuffer = subBuffer;
    }


    

    private boolean endMultiLineComment(ArrayList<LexChar> buffer)
    {
        boolean shouldTokenise = false;
        String bufferString = getBufferString(buffer);
        if (bufferString.contains("**/"))
        {
            bufferIsLegal = true;
            shouldTokenise = true;
            return shouldTokenise;
        }
        return shouldTokenise;
    }

    private boolean validateSymbol(LexChar newChar, ArrayList<LexChar> buffer)
    {
        boolean shouldTokenise = false;
        
        if (isSymbolBuildingComment(buffer))
        {
            return shouldTokenise;
        }

        boolean isLegal = isSymbolLegal(buffer);

        if (!isLegal)
        {
            shouldTokenise = true;
            bufferIsLegal = false;
        }

        return shouldTokenise;
    }

    private boolean isSymbolLegal(ArrayList<LexChar> buffer)
    {
        boolean symbolIsLegal = false;

        String bufferString = getBufferString(buffer);
        boolean isInLegalSymbols = legalSymbols.contains(bufferString);
        boolean isLegalMultiCharSymbol;

        if (buffer.size() > 1)
        {
            for (String symbol : legalMultiCharSymbols)
            {
                isLegalMultiCharSymbol = symbol.contains(bufferString);
                if (isLegalMultiCharSymbol)
                {
                    bufferIsLegal = true;
                    symbolIsLegal = true;
                    return symbolIsLegal;
                }
            }
        }
        else if (isInLegalSymbols)
        {
            bufferIsLegal = true;
            symbolIsLegal = true;
        }

        return symbolIsLegal;
    }

    private boolean isSymbolLegal(String inputSymbol)
    {
        boolean symbolIsLegal = legalSymbols.contains(inputSymbol);
        boolean isLegalMultiCharSymbol;

        if (symbolIsLegal)
        {
            bufferIsLegal = true;
            return symbolIsLegal;
        }

        if (inputSymbol.length() > 1)
        {
            for (String symbol : legalMultiCharSymbols)
            {
                isLegalMultiCharSymbol = symbol.contains(inputSymbol);
                if (isLegalMultiCharSymbol)
                {
                    bufferIsLegal = true;
                    symbolIsLegal = true;
                    return symbolIsLegal;
                }
            }
        }

        return symbolIsLegal;
    }

    private void wipeBuffer()
    {
        lexemeBuffer = new ArrayList<LexChar>();
    }

    private void resetState()
    {
        state = tokeniserState.none;
        errorStateDesc = "N/A";
    }
    
    private String getBufferString(ArrayList<LexChar> lexBuffer)
    {
        String out = "";
        for (LexChar lexChar : lexBuffer) 
        {
            out += lexChar.getCharacter();
        }

        return out;
    }

    private boolean isCommentState()
    {   
        boolean isComment = (state == tokeniserState.multicomment || state == tokeniserState.comment);
        return isComment;
    }
    
    private boolean isSymbolBuildingComment(ArrayList<LexChar> buffer)
    {
        boolean isBuildingComment = false;
        String bufferString = getBufferString(buffer);

        if (bufferString.equals("/--"))
        {
            state = tokeniserState.comment;
            bufferIsLegal = true;
            isBuildingComment = true;
            return isBuildingComment;
        }
        if (bufferString.equals("/**"))
        {
            state = tokeniserState.multicomment;
            bufferIsLegal = false;
            isBuildingComment = true;
            return isBuildingComment;
        }
        if (bufferString.equals("/-"))
        {
            isBuildingComment = true;
            return isBuildingComment;
        }
        if (bufferString.equals("/*"))
        {
            isBuildingComment = true;
            return isBuildingComment;
        }

        return isBuildingComment;
    }

    private TokenTypes checkForKeyToken(ArrayList<LexChar> buffer)
    {
        TokenTypes type = null;
        String bufferString = getBufferString(buffer);

        try 
        {
            type = symbolDict.get(bufferString);
        } catch (Exception e) {
            System.err.println(e);
        }

        return type;
    }

    private void buildAndPutTokenInList(TokenTypes type, ArrayList<LexChar> buffer, int line, int column)
    {
        Token outToken = new Token(type, getBufferString(buffer), line, column);
        tokenList.add(outToken);
    }

    private void buildAndPutTokenInList(TokenTypes type, double number, int line, int column)
    {
        Token outToken = new Token(type, number, line, column);
        tokenList.add(outToken);
    }

    private void buildAndPutTokenInList(String errorDescription, ArrayList<LexChar> buffer, int line, int column)
    {
        Token outToken = new Token(errorDescription, getBufferString(buffer), line, column);
        tokenList.add(outToken);
        errorList.add(outToken);
    }

    private void setupDictionary()
    {
        TokenTypes[] typeList = TokenTypes.values();
        for (int i = 0; i < typeList.length; i++)
        {
            String key = Token.tokenInputStrings[i];
            //"TIDEN ", "TILIT ", "TFLIT ", "TSTRG ", "TUNDF " are all marked with an empty string "" so we skip those here
            if (!key.equals(""))
            {
                symbolDict.put(key, typeList[i]);
            }
        }
    }
}
