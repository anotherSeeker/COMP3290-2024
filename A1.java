
import Tokeniser.LexemeTokeniser;
import Tokeniser.Token;
import java.util.ArrayList;

public class A1
{
    public static void main(String[] args)
    {
        LexemeTokeniser tokeniser = new LexemeTokeniser();
        String filePathString = "";

        if (args.length > 0)
        {
            if (args[0].equals("a1Test.cd24"))
            {
                filePathString = args[0];
            }
        }
        else
            filePathString = "";
        
        ArrayList<Token> tokenList = tokeniser.run(filePathString);
    }
}