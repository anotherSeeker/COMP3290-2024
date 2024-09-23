package Parser;
import Tokeniser.Token;
import java.util.ArrayList;

public class ParserNode 
{
    //node type, if terminal [token], [Child Nodes]
    private Token token = null;
    private String type = "Uninit";
    private String errorDesc; 
    private ParserNode parent;
    private final ArrayList<ParserNode> children = new ArrayList<>();

    public ParserNode(Token _token, String _type, ParserNode _parentNode)
    {
        token = _token;
        type = _type;
        parent = _parentNode;
    }

    public ParserNode(Token _token, String _type, String _errorDesc, ParserNode _parentNode)
    {
        token = _token;
        type = "err";
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

    public void printSelf()
    {
        System.out.println("TODO: flesh this out, type is: "+type);   
    }

    public void printChildren()
    {
        for (ParserNode child : children)
        {
            child.printSelf();
        }
    }
}
