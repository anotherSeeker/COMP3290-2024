package Tokeniser;
import java.io.File;
import java.util.ArrayList;
import FileReader.*;
import java.io.Reader;


public class LexemeTokeniser 
{
    private ArrayList<Token> tokenList = new ArrayList<Token>();
    private ArrayList<Token> errorList = new ArrayList<Token>();

    private String lexemeBuffer = "";
    private FileReader reader;

    public LexemeTokeniser()
    {}

    public ArrayList<Token> run(String filePath)
    {
        File sourceFile = new File(filePath);
        reader = new FileReader(sourceFile);
        
        while (reader.hasNext())
        {
            tokenise();
        }

        return tokenList;
    }

    private void tokenise()
    {
        LexChar current = reader.step();
    }


}
