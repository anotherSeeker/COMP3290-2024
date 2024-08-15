package FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner; 


public class FileReader 
{
    private Scanner FileScanner;

    //location in the file
    private int row = 0;
    private int column = 0;
    private String fileString = "";


    public FileReader(File filePath)
    { 
        try {
            FileScanner = new Scanner(filePath).useDelimiter("\\Z");
            fileString = FileScanner.next();
            FileScanner.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found exception");
            e.printStackTrace();
        }
    }

    public LexChar step()
    {
        LexChar out;
        String temp;

        temp = nextCharacter();
        out = new LexChar(row, column, temp);

        return out;
    }

    private String nextCharacter()
    {
        String out = Character.toString(fileString.charAt(0));
        fileString = fileString.substring(1);
        updateLocation(out);

       return out;
    }


    private void updateLocation(String inputChar)
    {
        if (inputChar.equals("\t"))
        {
            column+=4;
        }
        if (inputChar.equals("\\r?\\n|\\r"))
        {
            row++; column = 0;
        }
        else
        {
            column++;
        }
    }

    public boolean hasNext()
    {
        boolean out = fileString.length() != 0;

        return out;
    }


}
