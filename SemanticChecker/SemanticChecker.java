package SemanticChecker;

import SymbolTable.SymbolTable;
import Tokeniser.Token;
import java.util.ArrayList;

public final class SemanticChecker 
{
    private final ArrayList<Token> tokenList;
    private final ArrayList<String> errorLog = new ArrayList<>();
    private final SymbolTable symt;
    //private int listIndex = 0;
    private boolean errorFree = true;

    public SemanticChecker(ArrayList<Token> _tokenList, SymbolTable _symt) 
    {
        tokenList = _tokenList;
        symt = _symt;

        errorFree = runSemCheck();
    }


    public final boolean runSemCheck()
    {
        boolean isValid = false;

        if(validateProgramName())
        {
            if (validateDeclarationBeforeUse())
            {
                if (validateArraySizesAreDeclared())
                {
                   isValid = true; 
                }
                
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
        ArrayList<String> errors = symt.validateVariableUses();
        logError(errors);

        return errors.isEmpty();
    }

    private boolean validateArraySizesAreDeclared()
    {
        ArrayList<String> errors = symt.validateArraySizing();
        logError(errors);

        return errors.isEmpty();
    }

    private boolean validateExpressions()
    {
        ArrayList<String> errors = symt.validateExpressions();
        logError(errors);

        return errors.isEmpty();
    }















    public boolean hasErrors()
    {
        return !errorFree;
    }

    private void logError(String log)
    {
        errorFree = false;
        errorLog.add(log);
    }

    private void logError(ArrayList<String> logs)
    {
        errorFree = false;
        for (String log : logs)
        {
            errorLog.add(log);
        }
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
