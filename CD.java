import Generator.*;
import Parser.*;
import SemanticChecker.*;
import SymbolTable.*;
import Tokeniser.*;
import java.util.ArrayList;

public class CD
{
    private static ArrayList<ArrayList<Token>> lists;
    private static ArrayList<Token> tokenList;
    private static ArrayList<Token> A1errorList;
    private static ArrayList<String> loggedErrors = new ArrayList<>();

    private static SymbolTable symTable = null;
    private static PNodeTree parserTree = null;
    private static TokenParser parser = null;
    private static SemanticChecker semChecker = null;
    private static CodeGenerator gen = null;

    public static void main(String[] args)
    {      
        LexemeTokeniser tokeniser = new LexemeTokeniser();
        String filePathString = args[0];
        
        try 
        {
            if (args.length > 0)
            {
                lists = tokeniser.run(filePathString);
                tokenList = lists.get(0);
                A1errorList = lists.get(1);
                //adds errors internally
                //printLexErrors();
                //printTokens();

                listingGenerator.generateListingA1(tokenList, A1errorList, filePathString);
                //System.out.println(listing);

                if (A1errorList.isEmpty())
                {
                    symTable = new SymbolTable(tokenList);
                    //symTableErrors
                    //symTable.printTable();
                    //symTable.printErrorLog();

                    //print tree is the debug print, 
                    //traversal is the requested output print for A2
                    parser = new TokenParser(tokenList, symTable);
                    parserTree = parser.run();

                    //parserTree.printTreeTraversal();
                    //parserTree.printTree();
                    //parserTree.printErrors();

                    
                    listingGenerator.generateListingA2(parserTree, filePathString);
                    

                    if (!parserTree.hasErrors())
                    {
                        semChecker = new SemanticChecker(tokenList, symTable);
                        semChecker.runSemCheck();
                        //semChecker.printErrorLog();

                        if (!semChecker.hasErrors() && !symTable.hasErrors())
                        {
                            //TODO: generate code and cry
                            gen = new CodeGenerator();
                        }
                        else
                        {
                            System.out.println("\nAborting Code Gen due to Semantic Checker Errors - Note code gen is non functional");
                        }
                    }
                    else
                    {
                        System.out.println("\nAborting Semantic Checking due to Symbol Table and/or Parsing Error(s)");
                    }
                }
                else
                {
                    System.out.println("\nAborting Parsing due to Lexical Error(s)");
                }         
            }
            else
            {
                System.out.println("\nAborting Tokenising due to missing Filepath");
            }

            printLoggedErrors();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            printLoggedErrors();
        }
    }

    public static void printTokens()
    {
        int currentLineLength = 0;
        String out = "";
        
        for (Token tok : tokenList)
        {
            try {
                out = padString(tok.toString());
            } catch (Exception e) {
                System.err.println(e);
            }

            currentLineLength += out.length();

            if (currentLineLength > 60)
            {
                currentLineLength = 0;
                out = out + "\n";
            }

            System.out.print(out);
        }
    }

    public static void printLexErrors()
    {
        int currentLineLength = 0;
        if (A1errorList.size() < 1)
        {
            //loggedErrors.add("No Lexical errors found");
            System.out.println("No Lexical errors found");
        }
        else
        {
            System.out.println("\n\n" + A1errorList.size() + "Errors found");
        }
        for (Token tok : A1errorList)
        {
            String out = padString(tok.toStringError());

            currentLineLength += out.length();

            if (currentLineLength > 60)
            {
                currentLineLength = 0;
                out = out + "\n";
            }

            //loggedErrors.add(out);
            System.out.println(out);
        }
    }

    public static ArrayList<String> getErrorLog()
    {
        ArrayList<String> errLog = new ArrayList<>();
        int currentLineLength = 0;
        if (A1errorList.size() < 1)
        {
            errLog.add("No Lexical errors found");
            return errLog;
        }

        for (Token tok : A1errorList)
        {
            String out = padString(tok.toStringError());

            currentLineLength += out.length();

            if (currentLineLength > 60)
            {
                currentLineLength = 0;
                out = out + "\n";
            }

            errLog.add(out);
        }

        return errLog;
    }

    private static String padString(String str)
    {
        int padding = str.length()%6;

        if (str.length() != 6)
        {
            for (int i = 0; i < (6-padding); i++)
            {
                str = str + " ";
            }
        }

        return str;
    }

    private static void printLoggedErrors()
    {
        if (A1errorList != null)
            loggedErrors.addAll(getErrorLog());
        if (parserTree != null)
            loggedErrors.addAll(parserTree.getErrorLog());
        if (symTable != null)
            loggedErrors.addAll(symTable.getErrorLog());
        if (semChecker != null)
            loggedErrors.addAll(semChecker.getErrorLog());

        for (String err : loggedErrors)
        {
            System.out.println(err);
        }
    }
}