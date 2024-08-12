package Tokeniser;
import java.util.ArrayList;


public class LexemeTokeniser 
{
    private ArrayList<Token> tokenList = new ArrayList<Token>();
    private ArrayList<Token> errorList = new ArrayList<Token>();

    public LexemeTokeniser()
    {}

    public ArrayList<Token> run(String filePath)
    {
        String path = "";
        if (filePath.isEmpty())
        {
            System.out.println("filePath is empty, using default path");
            path = "setthisupcorrectly";
        }
        else
            path = filePath;

        return tokenList;
    }


}
