package cd24FileReader;

public class LexChar 
{
    int line;
    int col;
    String character = "";   
    
    public LexChar(int _line, int _col, String _character)
    {
        line = _line;
        col = _col;
        character = _character;
    }

    public String getCharacter() {
        return character;
    }

    public int getCol() {
        return col;
    }

    public int getLine() {
        return line;
    }
}
