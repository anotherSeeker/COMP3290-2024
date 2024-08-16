package Tokeniser;
import java.util.*;
import java.io.*;

public class listingGenerator 
{
    
   public static String generateListing(ArrayList<Token> tokenList, ArrayList<Token> errorList)
   {
        String outString = "";

        int currLine = 0;
        boolean lineHasError = false;

        for (Token tok : tokenList)
        {
            if (currLine != tok.getLine())
            {
                if (lineHasError)
                {
                    addErrors(currLine, errorList);
                    lineHasError = false;
                }

                currLine = tok.getLine();
                outString+="\n"+currLine+": ";
            }
            if (tok.getType() == TokenTypes.TUNDF)
            {
                lineHasError = true;
            }

            outString += tok.toString();
        }
        if (lineHasError)
        {
            addErrors(currLine, errorList);
            lineHasError = false;
        }

        writeFile(outString);

        return outString;
   } 

   private static String addErrors(int line, ArrayList<Token> errorList)
   {
        String outString = "";
        for (Token tok : errorList)
        {
            if (line == tok.getLine())
            {
                outString += tok.toStringError() + "\n";
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

}
