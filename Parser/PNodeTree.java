package Parser;

public class PNodeTree 
{
    //root holds children, children hold their parents and their children all the way down
    private ParserNode treeRoot = null; 
    private int printDepth = -1;
    private static int currentTraversalLineLength = 0;

    public PNodeTree(ParserNode root) 
    {
        treeRoot = root;
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

    private void printNodeAndChildren(ParserNode root)
    {
        printDepth++;
        root.printSelfOld(printDepth);

        for (ParserNode child : root.children)
        {
            printNodeAndChildren(child);
        }
        printDepth--;
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
    }

    public void traversalStep(ParserNode node)
    {
        String RESET = "\u001B[0m";
        String GREEN = "\u001B[32m";
        String BLUE =  "\u001B[34m";

        String travString = node.getTraversalString();

        try {
            travString = padString(travString);
        } catch (Exception e) {
            System.err.println(e);
        }

        currentTraversalLineLength += travString.length();

        if (node.isToken())
            travString = GREEN+travString+RESET;
        else
            travString = BLUE+travString;

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
}
