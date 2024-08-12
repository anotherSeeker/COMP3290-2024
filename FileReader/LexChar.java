package FileReader;

public class LexChar 
{
    int row;
    int col;
    String character = "";   
    
    public LexChar(int _row, int _col, String _character)
    {
        row = _row;
        col = _col;
        character = _character;
    }

    public String getCharacter() {
        return character;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
