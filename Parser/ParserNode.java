package Parser;
import Tokeniser.Token;
import Tokeniser.TokenTypes;
import java.util.ArrayList;

public class ParserNode 
{
    //node ruleType, if terminal [nodeToken], [Child Nodes]
    private Token nodeToken = null;
    private String name = "Uninit";
    private Rule ruleType = null;
    private boolean isToken = false;
    private boolean isErr = false;
    private String errorDesc; 
    private ParserNode parent;
    public final ArrayList<ParserNode> children = new ArrayList<>();

    public ParserNode(Token _token, Rule _ruleType, ParserNode _parentNode)
    {
        nodeToken = _token;
        ruleType = _ruleType;
        parent = _parentNode;

        if (ruleType == null)
        {
            isToken = true;
            name = nodeToken.getTypeString();
        }
        else
            name = ruleType.getName();
    }

    public ParserNode(Token _token, Rule _ruleType, String _errorDesc, ParserNode _parentNode)
    {
        nodeToken = _token;
        ruleType = _ruleType;
        isErr = true;
        errorDesc = _errorDesc;
        parent = _parentNode;

        if (ruleType == null)
        {
            isToken = true;
            name = nodeToken.getTypeString();
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

    public void printSelfOld(int printDepth)
    {
        String RESET = "\u001B[0m";
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";
        String BLUE =  "\u001B[34m";

        handleIndent(printDepth);

        if (isErr())
            System.out.print("Syntax Error: " + errorDesc);
        else
        {
            if (isToken)
            {
                String par = " : parent: ";
                String str = name;

                if (nodeToken.isValueToken())
                    str = str+" = "+ nodeToken.getLexeme();

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

    public void printSelf(int printDepth, int parentDepth, boolean depthChange, boolean onlyTokens)
    {
        if (isErr())
        {
            System.out.print(errorDesc+"\n");
            //System.out.println("Occured At: "+nodeToken.getLocationStringErr());
        }
        else
        {
            if (isToken)
            {
                handleIndent(printDepth, parentDepth, depthChange);
                String str = name;

                if (nodeToken.getType() == TokenTypes.TIDEN)
                    str = str+" : "+nodeToken.getLexeme();

                System.out.print(str);
                System.out.print("\n");
            }
            else if (!onlyTokens)
            {
                handleIndent(printDepth, parentDepth, depthChange);
                String str = name;

                System.out.print(str);
                System.out.print("\n");  
            }
        }
    }

    public String getTraversalString()
    {
        if (isToken())
        {
            if (nodeToken.getType() == TokenTypes.TIDEN || nodeToken.getType() == TokenTypes.TILIT  || nodeToken.getType() == TokenTypes.TFLIT  || nodeToken.getType() == TokenTypes.TBOOL)
            {
                return getName()+" "+nodeToken.getLexeme();
            }
        }   
        return getName();
    }

    public boolean isToken()
    {
        return isToken;
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

    private void handleIndent(int printDepth, int parentDepth, boolean depthChange)
    {
        int count = 0;
        while (count < printDepth)
        {
            count++;
            if (count == parentDepth)
                System.out.print("│");
            else if (!depthChange && count > parentDepth)
                System.out.print("├");
            else if (count > parentDepth)
                System.out.print("┌");
            else
                System.out.print("│");
        }
    }
}
