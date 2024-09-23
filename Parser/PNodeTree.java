package Parser;

public class PNodeTree 
{
    private ParserNode treeRoot = null; 

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
        printNodeAndChildren(treeRoot);
    }

    private void printNodeAndChildren(ParserNode root)
    {
        System.out.print("TODO");
    }

    
    



}
