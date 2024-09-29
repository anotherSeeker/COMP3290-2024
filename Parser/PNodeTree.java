package Parser;

public class PNodeTree 
{
    //root holds children, children hold their parents and their children all the way down
    private ParserNode treeRoot = null; 
    private int printDepth = -1;

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
        root.printSelf(printDepth);

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

    
    



}
