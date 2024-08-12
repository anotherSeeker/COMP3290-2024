
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
            filePathString = args[0];
            ArrayList<Token> tokenList = tokeniser.run(filePathString);
        }
        else
            System.out.println("please provide filepath");
        
    }
}