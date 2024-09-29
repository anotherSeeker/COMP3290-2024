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

    public void printSelfOld(int printDepth)
    {
        String RESET = "\u001B[0m";
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";
        String BLUE =  "\u001B[34m";

        handleIndent(printDepth);

        if (isErr())
            System.out.print(RED+"Syntax Error: " + errorDesc+RESET);
        else
        {
            if (isToken)
            {
                String par = " : parent: ";
                String str = name;

                if (token.isValueToken())
                    str = str+" = "+ token.getLexeme();

                if (parent != null)
                    par = par + parent.getName();
                else
                    par = "";

                System.out.print(GREEN+str/*+par+*/+RESET);
            }
            else
                System.out.print(BLUE+name+RESET);  
        }

        System.out.print("\n");
    }

    public void printSelf(int printDepth, int parentDepth, boolean depthChange, boolean onlyTokens)
    {
        String RESET = "\u001B[0m";
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";
        String BLUE =  "\u001B[34m";

        if (isErr())
        {
            System.out.print(RED+"syntax error: " + errorDesc +RESET+"\n");
            System.out.print(RED+"Occured At: "+token.getLocationStringErr()+RESET);
            System.out.print("\n");
        }
        else
        {
            if (isToken)
            {
                handleIndent(printDepth, parentDepth, depthChange);
                String par = " : parent: ";
                String str = name;

                if (token.isValueToken())
                    str = str+" = "+ token.getLexeme();
                if (parent != null)
                    par = par + parent.getName();
                else
                    par = "";

                System.out.print(GREEN+str/*+par+*/+RESET);
                System.out.print("\n");
            }
            else if (!onlyTokens)
            {
                handleIndent(printDepth, parentDepth, depthChange);
                System.out.print(BLUE+name+RESET);
                System.out.print("\n");  
            }
        }
    }

    public String getTraversalString()
    {
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
