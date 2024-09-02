import Parser.*;
import Tokeniser.*;
import java.util.ArrayList;

public class A2
{
    private static ArrayList<ArrayList<Token>> lists;
    private static ArrayList<Token> tokenList;
    private static ArrayList<Token> errorList;

    public static void main(String[] args)
    {      
        LexemeTokeniser tokeniser = new LexemeTokeniser();
        String filePathString = args[0];

        if (args.length > 0)
        {
            lists = tokeniser.run(filePathString);
            tokenList = lists.get(0);
            errorList = lists.get(1);
            
            //printTokens();

            String listing = listingGenerator.generateListing(tokenList, errorList, filePathString);
            //System.out.println(listing);

            TokenParser.run(tokenList);

            


        }
        else
            System.out.println("please provide filepath");
    }

    private static void printTokens()
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

    private static void printErrors()
    {
        int currentLineLength = 0;
        if (errorList.size() < 1)
        {
            System.out.println("\n\nNo Lexical errors found");
        }
        else
        {
            System.out.println("\n\n" + errorList.size() + "Errors found");
        }
        for (Token tok : errorList)
        {
            String out = padString(tok.toStringError());

            currentLineLength += out.length();

            if (currentLineLength > 60)
            {
                currentLineLength = 0;
                out = out + "\n";
            }

            System.out.println(out);
        }
    }

    private static String padString(String str)
    {
        int padding = str.length()%6;

        if (padding != 0)
        {
            for (int i = 0; i < (6-padding); i++)
            {
                str = str + " ";
            }
        }

        return str;
    }
}