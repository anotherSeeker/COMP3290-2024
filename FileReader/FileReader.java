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


    public FileReader(File filePath)
    { 
        try {
            FileScanner = new Scanner(filePath);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found exception");
            e.printStackTrace();
        }
    }



}
