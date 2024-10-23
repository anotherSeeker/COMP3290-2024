package Parser;

import java.util.ArrayList;

public class PNodeTree 
{
    //root holds children, children hold their parents and their children all the way down
    private ParserNode treeRoot = null; 
    private int printDepth = -1;
    private static int currentTraversalLineLength = 0;
    private static final ArrayList<String> errorStrings = new ArrayList<>();

    public PNodeTree(ParserNode root) 
    {
        treeRoot = root;
    }

    public void addError(String errString)
    {
        errorStrings.add(errString);
    }

    public ParserNode getRoot()
    {
        if (treeRoot == null)
        {
            System.out.println("Warning root is null"); 
        }
        return treeRoot;
    }

    public void printTree()
    {
        //bookkeeping for the print
        boolean depthChange = false;
        int parentDepth = 0;
        //true only prints tokens, false prints all nodes
        boolean onlyTokens = false;

        printNodeAndChildren(treeRoot, parentDepth, depthChange, onlyTokens);
    }

    private void printNodeAndChildren(ParserNode node, int parentDepth, boolean depthChange, boolean onlyTokens)
    {
        printDepth++;
        node.printSelf(printDepth, parentDepth, depthChange, onlyTokens);

        depthChange = true;
        for (int i = 0; i < node.children.size(); i++)
        {
            ParserNode child = node.children.get(i);

            printNodeAndChildren(child, printDepth, depthChange, onlyTokens);
            depthChange = false;
        }
        
        printDepth--;
    }

    public void printTreeTraversal()
    {
        traversalStep(treeRoot);
        System.out.println("\n");
        errorPrint();
    }

    public String stringTreeTraversal()
    {
        String travString = traversalStepString(treeRoot)+"\n";

        travString += errorStrings();

        return travString;
    }

    public String stringErrors()
    {
        String travString = "";

        travString += errorStrings();

        return travString;
    }

    public void traversalStep(ParserNode node)
    {
        String RESET = "\u001B[0m"; 
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";
        String BLUE =  "\u001B[34m";

        String travString = node.getTraversalString();

        try {
            travString = padString(travString);
        } catch (Exception e) {
            System.err.println(e);
        }

        currentTraversalLineLength += travString.length();

        if (node.isErr())
            travString = RED+travString+RESET;
        else if (node.isToken())
            travString = GREEN+travString+RESET;
        else
            travString = BLUE+travString+RESET;

        if (currentTraversalLineLength >= 70)
        {
            currentTraversalLineLength = 0;
            travString = travString + "\n";
        }
        System.out.print(travString);

        for (ParserNode child : node.children)
        {
            traversalStep(child);
        }
    }

    public String traversalStepString(ParserNode node)
    {
        String travString = node.getTraversalString();

        try {
            travString = padString(travString);
        } catch (Exception e) {
            System.err.println(e);
        }

        currentTraversalLineLength += travString.length();

        if (currentTraversalLineLength >= 70)
        {
            currentTraversalLineLength = 0;
            travString = travString + "\n";
        }

        for (ParserNode child : node.children)
        {
            travString = travString + traversalStepString(child);
        }

        return travString;
    }

    private static String padString(String str)
    {
        int padLen = 7;
        int padding = str.length()%padLen;
        
        for (int i = 0; i < (padLen - padding); i++)
        {
            str = str + " ";
        }

        return str;
    }

    private void errorPrint()
    {
        if (errorStrings.isEmpty())
        {
            System.out.println("No Parser Errors Found");
            return;
        }

        for (String err : errorStrings)
        {
            System.out.println(err);
        }
        
    }

    private String errorStrings()
    {
        String out = "";

        for (String err : errorStrings)
        {
            out += err;
        }

        return out;
    }

    public void printErrors()
    {
        errorPrint();
    }
}
