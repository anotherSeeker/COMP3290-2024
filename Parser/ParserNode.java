package Parser;
import Tokeniser.Token;
import java.util.ArrayList;

public class ParserNode 
{
    //node ruleType, if terminal [token], [Child Nodes]
    private Token token = null;
    private String name = "Uninit";
    private Rule ruleType = null;
    private boolean isToken = false;
    private boolean isErr = false;
    private String errorDesc; 
    private ParserNode parent;
    public final ArrayList<ParserNode> children = new ArrayList<>();

    public ParserNode(Token _token, Rule _ruleType, ParserNode _parentNode)
    {
        token = _token;
        ruleType = _ruleType;
        parent = _parentNode;

        if (ruleType == null)
        {
            isToken = true;
            name = token.getTypeString();
        }
        else
            name = ruleType.getName();
    }

    public ParserNode(Token _token, Rule _ruleType, String _errorDesc, ParserNode _parentNode)
    {
        token = _token;
        ruleType = _ruleType;
        isErr = true;
        errorDesc = _errorDesc;
        parent = _parentNode;

        if (ruleType == null)
        {
            isToken = true;
            name = token.getTypeString();
        }
        else
            name = ruleType.getName();
    }

    public void setParent(ParserNode parentNode)
    {
        parent = parentNode;
    }

    public void addChild(ParserNode childNode)
    {
        children.add(childNode);
    }

    public void printSelf(int printDepth)
    {
        handleIndent(printDepth);

        if (isErr())
            System.out.print("Parse Error: " + errorDesc);
        else
        {
            if (isToken)
            {
                String par = " : parent: ";
                String str = name;

                if (token.isValueToken())
                    str = str+"="+ token.getLexeme();

                if (parent != null)
                    par = par + parent.getName();
                else
                    par = "";

                System.out.print(str+par);
            }
            else
                System.out.print(name);  
        }

        System.out.print("\n");
    }

    public void printChildren(int printDepth)
    {
        for (ParserNode child : children)
        {
            child.printSelf(printDepth);
        }
    }

    public boolean isErr()
    {
        return isErr;
    }

    public String getName()
    {
        return name;
    }

    private void handleIndent(int printDepth)
    {
        int count = 0;
        while (count < printDepth)
        {
            count++;
            if (count == printDepth)
                System.out.print("-");
            else if (count == 1)
                System.out.print("|");
            else
                System.out.print(" ");
        }
    }
}
