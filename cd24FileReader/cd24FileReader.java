package cd24FileReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;

public class cd24FileReader 
{
    private FileReader reader;

    //location in the file
    private int line = 1;
    private int column = 0;
    private String fileString = "";


    public cd24FileReader(File filePath)
    { 
        try {
            reader = new FileReader(filePath);
            try {
                boolean run = true;
                while (run)
                {
                    int raw = reader.read();
                    char newChar = (char) raw;
                    if (raw == -1)
                    {
                        run = false;
                        fileString = fileString + "\u001a";
                    }
                    else
                    {
                        fileString = fileString + newChar;
                    }
                }
            } catch (Exception e) {
                System.err.println(e);
            }
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
        out = new LexChar(line, column, temp);

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
        if (inputChar.equals("\n"))
        {
            line++; column = 0;
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
