import Tokeniser.LexemeTokeniser;
import Tokeniser.Token;
import java.util.ArrayList;

public class A1
{
    private static ArrayList<Token> tokenList;

    public static void main(String[] args)
    {
        LexemeTokeniser tokeniser = new LexemeTokeniser();
        String filePathString = "";

        if (args.length > 0)
        {
            filePathString = args[0];
            tokenList = tokeniser.run(filePathString);
            
            printTokens();
        }
        else
            System.out.println("please provide filepath");
    }

    private static void printTokens()
    {
        int currentLineLength = 0;

        for (Token tok : tokenList)
        {
            String out = padString(tok.toString());

            currentLineLength += out.length();

            if (currentLineLength > 60)
            {
                currentLineLength = 0;
                out = out + "\n";
            }

            System.out.print(out);
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