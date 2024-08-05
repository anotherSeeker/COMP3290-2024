package FileReader;

import java.util.Scanner;

public class FileReader 
{
    private Scanner fileScanner;


    public FileReader(String PathString)
    { 
        fileScanner = new Scanner(PathString);
    }



}
