package Tokeniser;
import cd24FileReader.*;
import java.io.File;
import java.util.*;

public class LexemeTokeniser 
{
    private final ArrayList<Token> TOKEN_LIST = new ArrayList<>();
    private final ArrayList<Token> ERROR_LIST = new ArrayList<>();

    //private String lexemeBuffer = "";
    private ArrayList<LexChar> lexemeBuffer = new ArrayList<>();
    private cd24FileReader reader;
    private final String LEGAL_SYMBOLS = "! , [ ] ( ) = + - * \" / % ^ < > : ; . \u001a";
    private final String[] LEGAL_MULTI_CHAR_SYMBOLS = {"<=", ">=", "!=", "==", "+=", "-=", "*=", "/="};

    private final HashMap<String, TokenTypes> SYMBOL_DICT = new HashMap<>();

    private enum tokeniserState
    {
        none, //the reset state for the system, will figure out what state it should be in after
        identifier, //not confirmed as keyword but legal as ident
        keyword, //confirmed keyword
        number, //not confirmed as int or float
        floatlit, //confirmed as float
        ilit, //irrelevant state, cannot confirm as int lit unless we're already tokenising
        symbol, //single or multi character symbol
        string, //" ... "
        comment, // /--
        multicomment, // /**     **/ can go across lines unlike string
        eof, //end of file char
        undefined //illegal input, tokenise the largest legal thing in the buffer and repeat erroring if it's not legal
    }

    private tokeniserState state = tokeniserState.none;
    private String errorStateDesc = "N/A";
    private boolean bufferIsLegal = false;
    private boolean keepBuffer = false;

    public ArrayList<ArrayList<Token>> run(String filePath)
    {
        File sourceFile = new File(filePath);
        reader = new cd24FileReader(sourceFile);

        setupDictionary();

        runTokenise();
        ArrayList<ArrayList<Token>> lists = new ArrayList<>();
        lists.add(TOKEN_LIST);
        lists.add(ERROR_LIST);

        for (int i = 0; i<TOKEN_LIST.size(); i++)
        {
            //to make it easier to reference tokens in our symbol table
            Token tok = TOKEN_LIST.get(i);
            tok.setIndex(i);
        }

        return lists;
    }

    private void runTokenise()
    {
        while (reader.hasNext())
        {
            LexChar current = reader.step();

            if (current.getCharacter().equals("\u001a"))
            {
                //eof character
                if (lexemeBuffer.size() > 1)
                {
                    tokeniseBuffer(lexemeBuffer);
                    wipeBuffer();
                    resetState();
                }
                state = tokeniserState.eof;
                lexemeBuffer.add(current);
                tokeniseBuffer(lexemeBuffer);
                wipeBuffer();
                resetState();
                break;
            }

            boolean shouldTokenise = isReadyToTokenise(current);
            if (shouldTokenise)
            {
                tokeniseBuffer(lexemeBuffer);
                if (keepBuffer)
                {
                    keepBuffer = false;
                }
                else
                {
                    wipeBuffer();
                    resetState();
                }
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
                    case identifier -> 
                    {
                        key = checkForKeyToken(lexBuffer);
                        if (key == null)
                        {
                            key = TokenTypes.TIDEN;
                        }
                       
                        buildAndPutTokenInList(key, lexBuffer, line, column);
                    }
                    case number -> 
                    {
                        double number = Double.parseDouble(getBufferString(lexBuffer));
                        buildAndPutTokenInList(TokenTypes.TILIT, lexBuffer, number, line, column);
                    }
                    case floatlit -> buildAndPutTokenInList(TokenTypes.TFLIT, lexBuffer, Double.parseDouble(getBufferString(lexBuffer)), line, column);
                    case string -> buildAndPutTokenInList(TokenTypes.TSTRG, lexBuffer, line, column);
                    case comment -> {/*Do nothing*/}
                    case multicomment -> {/*Do nothing*/}
                    case symbol -> 
                    {
                        key = checkForKeyToken(lexBuffer);
                        if (key == null)
                        {
                            //key = TokenTypes.TUNDF;
                            errorStateDesc = "Illegal symbol: "+getBufferString(lexBuffer);
                            buildAndPutTokenInList(errorStateDesc, lexBuffer, line, column);
                        }
                        else
                            buildAndPutTokenInList(key, lexBuffer, line, column);
                    }
                    case undefined -> buildAndPutTokenInList(errorStateDesc, lexBuffer, line, column);
                    case eof -> 
                    {
                        key = checkForKeyToken(lexBuffer);
                        buildAndPutTokenInList(key, lexBuffer, line, column);
                    }
                    default -> throw new AssertionError();//we shouldn't be here
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
        boolean shouldTokenise;
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
            case none -> shouldTokenise = statelessValidate(lexChar, buffer);
            case identifier -> shouldTokenise = identifierValidate(strChar);
            case number -> shouldTokenise = numberValidate(strChar, buffer);
            case floatlit -> shouldTokenise = flitValidate(strChar, buffer);
            case comment -> shouldTokenise = commentValidate(strChar);
            case multicomment -> shouldTokenise = multiCommentValidate(buffer);
            case string -> shouldTokenise = stringValidate(lexChar, buffer);
            case symbol -> shouldTokenise = validateSymbol(lexChar, buffer);
            default -> throw new AssertionError();
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

        isIllegal = !(LEGAL_SYMBOLS.contains(currChar));
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
        else if(isSymbolLegal(newChar, buffer))
        {
            state = tokeniserState.symbol;
        }

        return shouldTokenise;
    }

    private boolean identifierValidate(String currChar)
    {
        //some of our symbols count as letters/numbers for some reason,
        boolean shouldTokenise;
        boolean isSymbol = LEGAL_SYMBOLS.contains(currChar);
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

    private boolean numberValidate(String currChar, ArrayList<LexChar> buffer)
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
            shouldTokenise = false;
            return shouldTokenise;
        }
        else
        {
            bufferIsLegal = false;
            shouldTokenise = true;
            return shouldTokenise;
        }

        try {
            double number = Double.parseDouble(getBufferString(buffer));
            if (!(number <= Integer.MAX_VALUE && number >= 0))
            {
                bufferIsLegal = true;
                shouldTokenise = true;
                state = tokeniserState.undefined;
                errorStateDesc = "Integer is too large";
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return shouldTokenise;
    }

    private boolean flitValidate(String currChar, ArrayList<LexChar> buffer)
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
            return shouldTokenise;
        }

        try {
            double number = Double.parseDouble(getBufferString(buffer));
            if (!(number <= Double.MAX_VALUE && number >= 0))
            {
                bufferIsLegal = false;
                shouldTokenise = true;
                state = tokeniserState.undefined;
                errorStateDesc = "Float is too large";
            }
        } catch (Exception e) {
            System.err.println(e);
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
        boolean shouldTokenise;
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

    private boolean multiCommentValidate(ArrayList<LexChar> buffer)
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

    private boolean tokeniseIllegalBuffer(ArrayList<LexChar> buffer)
    {
        boolean shouldTokenise = true;
        ArrayList<LexChar> subBuffer = new ArrayList<>();

        state = tokeniserState.none;

        for (int i = 0; i < buffer.size(); i++)
        {
            LexChar newChar = buffer.get(i);
            if (isCharIllegal(newChar.getCharacter()))
            {
                bufferIsLegal = true;
                tokeniseBuffer(subBuffer);
                subBuffer = new ArrayList<>();
                subBuffer.add(newChar);

                state = tokeniserState.undefined;
                errorStateDesc = "Illegal Character";
                tokeniseBuffer(subBuffer);
                subBuffer = new ArrayList<>();
                resetState();
            }
            else
            {
                ArrayList<LexChar> savedSubBuffer =  new ArrayList<>(subBuffer);
                subBuffer.add(newChar);

                shouldTokenise = handleValidationState(newChar, subBuffer);
                if (shouldTokenise)
                {
                    bufferIsLegal = true;
                    if (state == tokeniserState.floatlit && savedSubBuffer.getLast().getCharacter().equals("."))
                    {
                        ArrayList<LexChar> subSubBuffer = new ArrayList<>();
                        for (int k = 0; k < savedSubBuffer.size()-1; k++)
                        {
                            subSubBuffer.add(savedSubBuffer.get(k));
                        }
                        state = tokeniserState.number;
                        tokeniseBuffer(subSubBuffer);

                        subSubBuffer = new ArrayList<>();
                        subSubBuffer.add(savedSubBuffer.getLast());
                        state = tokeniserState.symbol;
                        tokeniseBuffer(subSubBuffer);
                    }
                    else 
                    {
                        tokeniseBuffer(savedSubBuffer);                        
                    }
                    subBuffer = new ArrayList<>();
                    resetState();

                    shouldTokenise = handleValidationState(newChar, subBuffer);
                    subBuffer.add(newChar);
                }
            }
        }

        if (!lexemeBuffer.isEmpty())
        {
            keepBuffer = true;
            lexemeBuffer = new ArrayList<>(subBuffer);
        }
        return shouldTokenise;
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

        if (isSymbolBuildingComment(newChar, buffer))
        {
            return shouldTokenise;
        }

        boolean isLegal = isSymbolLegal(newChar, buffer);

        if (!isLegal)
        {
            shouldTokenise = true;
            bufferIsLegal = false;
        }

        return shouldTokenise;
    }

    private boolean isSymbolLegal(LexChar newChar, ArrayList<LexChar> buffer)
    {
        
        boolean symbolIsLegal = false;

        String bufferString = getBufferString(buffer);
        String currChar = newChar.getCharacter();
        boolean isInLegalSymbols = LEGAL_SYMBOLS.contains(currChar);
        boolean isLegalMultiCharSymbol;

        if (buffer.size() > 1)
        {
            for (String symbol : LEGAL_MULTI_CHAR_SYMBOLS)
            {
                isLegalMultiCharSymbol = symbol.contains(bufferString);
                if (isLegalMultiCharSymbol)
                {
                    bufferIsLegal = true;
                    symbolIsLegal = true;
                    return symbolIsLegal;
                }
            }
            bufferIsLegal = false;
            symbolIsLegal = false;
            return symbolIsLegal;
        }
        else if (isInLegalSymbols)
        {
            bufferIsLegal = true;
            symbolIsLegal = true;
        }

        return symbolIsLegal;
    }

    private void wipeBuffer()
    {
        lexemeBuffer = new ArrayList<>();
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
    
    private boolean isSymbolBuildingComment(LexChar newChar, ArrayList<LexChar> buffer)
    {
        boolean isBuildingComment = false;
        String bufferString = getBufferString(buffer)+newChar.getCharacter();

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
        String bufferString = getBufferString(buffer).toLowerCase();

        try 
        {
            type = SYMBOL_DICT.get(bufferString);
        } 
        catch (Exception e) 
        {
            System.err.println(e);
        }

        return type;
    }

    private void buildAndPutTokenInList(TokenTypes type, ArrayList<LexChar> buffer, int line, int column)
    {
        Token outToken = new Token(type, buffer, line, column);
        TOKEN_LIST.add(outToken);
    }

    private void buildAndPutTokenInList(TokenTypes type, ArrayList<LexChar> buffer, double number, int line, int column)
    {
        Token outToken = new Token(type, buffer, number, line, column);
        TOKEN_LIST.add(outToken);
    }

    private void buildAndPutTokenInList(String errorDescription, ArrayList<LexChar> buffer, int line, int column)
    {
        Token outToken = new Token(errorDescription, buffer, line, column);
        TOKEN_LIST.add(outToken);
        ERROR_LIST.add(outToken);
    }

    private void setupDictionary()
    {
        TokenTypes[] typeList = TokenTypes.values();
        for (int i = 0; i < Token.keyTokenStrings.length; i++)
        {
            String key = Token.keyTokenStrings[i];
            //"TIDEN ", "TILIT ", "TFLIT ", "TSTRG ", "TUNDF " are all marked with an empty string "" so we skip those here
            if (!key.equals(""))
            {
                SYMBOL_DICT.put(key, typeList[i]);
            }
        }
    }
}
