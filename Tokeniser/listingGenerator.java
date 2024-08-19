package Tokeniser;
import java.io.*;
import java.util.*;

public class listingGenerator 
{
    
   public static String generateListing(ArrayList<Token> tokenList, ArrayList<Token> errorList, String path)
   {
        String outString = path;

        int currLine = 0;
        boolean lineHasError = false;

        for (Token tok : tokenList)
        {
            if (currLine != tok.getLine())
            {
                if (lineHasError)
                {
                    outString += addErrors(currLine, errorList);
                    lineHasError = false;
                }

                currLine = tok.getLine();
                outString+="\n"+currLine+": ";
            }
            if (tok.getType() == TokenTypes.TUNDF)
            {
                lineHasError = true;
            }

            outString += padString(tok.toString());
        }
        if (lineHasError)
        {
            outString += addErrors(currLine, errorList);
            lineHasError = false;
        }

        writeFile(outString);

        return outString;
   } 

   private static String addErrors(int line, ArrayList<Token> errorList)
   {
        String outString = "\n\t";
        for (Token tok : errorList)
        {
            if (line == tok.getLine())
            {
                outString += padString(tok.toStringError()) + "\n\t";
            }
        }

        return outString;
   }

   private static void writeFile(String fileStr)
   {
        try  {
            FileWriter writer = new FileWriter("output/listingFile.txt");

            writer.write(fileStr);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
