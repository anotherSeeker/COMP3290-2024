package Parser;
import Tokeniser.Token;
import java.util.ArrayList;

public class ParserNode 
{
    //node type, if terminal [token], [Child Nodes]
    private Token token = null;
    private String nodeName = "Uninitialised";
    private String errorDesc;
    private ParserNode parent;
    private final ArrayList<ParserNode> children = new ArrayList<>();

    public ParserNode(Token _token, String _nodeName, ParserNode _parentNode)
    {
        token = _token;
        nodeName = _nodeName;
        parent = _parentNode;
    }

    public ParserNode(Token _token, String _nodeName, String _errorDesc, ParserNode _parentNode)
    {
        token = _token;
        nodeName = _nodeName;
        errorDesc = _errorDesc;
        parent = _parentNode;
    }

    public void setParent(ParserNode parentNode)
    {
        parent = parentNode;
    }

    public void addChild(ParserNode childNode)
    {
        children.add(childNode);
    }

    public void printChildren()
    {
        System.out.println("TODO");
    }
}
