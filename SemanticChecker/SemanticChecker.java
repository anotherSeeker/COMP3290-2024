package SemanticChecker;

import SymbolTable.SymbolTable;
import Tokeniser.Token;
import java.util.ArrayList;

public class SemanticChecker 
{
    private ArrayList<Token> tokenList;
    private final ArrayList<String> errorLog = new ArrayList<>();
    private SymbolTable symt;
    private int listIndex = 0;
    private boolean errorFree = true;

    public SemanticChecker(ArrayList<Token> _tokenList, SymbolTable _symt) 
    {
        tokenList = _tokenList;
        symt = _symt;

        errorFree = runSemCheck();


        printErrorLog();
    }


    public boolean runSemCheck()
    {
        boolean isValid = false;

        if(validateProgramName())
        {
            if (validateDeclarationBeforeUse())
            {
                isValid = true;
            }
        }

        return isValid;
    }

    private boolean validateProgramName()
    {
        if (symt.programNameIsValid())
        {
            return true;
        }

        String log = "Semantic Error: CD24 Program Names do not match / start and end the program file";
        logError(log);
        return false;
    }


    private boolean validateDeclarationBeforeUse()
    {
        symt.validateVariableUses();

        return false;
    }















    public boolean getHasError()
    {
        return errorFree;
    }

    private void logError(String log)
    {
        errorFree = false;
        errorLog.add(log);
    }

    public void printErrorLog()
    {
        if (errorFree == true)
        {
            System.out.println("No Semantic Checker Errors");
            return;
        }

        for (String error : errorLog)
        {
            System.out.println(error);
        }
    }
}
