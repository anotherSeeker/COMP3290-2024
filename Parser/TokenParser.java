package Parser;
import SymbolTable.SymbolTable;
import Tokeniser.Token;
import java.util.*;

public class TokenParser 
{
    private static PNodeTree nodeTree;
    private static ArrayList<Rule> ruleList;
    private static ArrayList<Token> tokenList;
    private static SymbolTable symbolTable;
    private static int currentTokenIndex = 0;
    private static Token currentToken;
    private static boolean isFirstNode = true;

    private static String globErrDesc = null;

    public TokenParser(ArrayList<Token> tokList, SymbolTable symt)
    {
        tokenList = tokList;
        symbolTable = symt;
        advanceCurrentToken();

        ruleList = ParserRules.initialiseRules();
    }

    public PNodeTree run()
    {
        //we run with the first token from our lexical analysis, and the nProg ruleString no parent because this is the root
        //because isFirstNode is true, this will instantiate new PNodeTree
        matchRule(ruleList.get(0), null);

        return nodeTree;
    }

    //---------------------------

    private void matchRule(Rule inputRule, ParserNode ParentNode)
    {
        //setup nprog as root
        if (isFirstNode) {
            ParentNode = addNode(inputRule, ParentNode);
        }

        boolean hasMultipleMatchSets = inputRule.hasMultipleMatchSets();
        //if we don't have multiple match sets we always use set 0 regardless of epsilon status

        int matchSetAddress = compareFirstSet(inputRule, hasMultipleMatchSets);
        boolean valid = matchSetExists(matchSetAddress, inputRule, ParentNode);
        //if matchset exists we'll execute the rules in that matchset, making children for each new ruleString that isn't a tail and for each token
            //if it doesn't then matchSetExists has made an error node for us

        if (valid)
        {
            if (matchSetAddress != -1)
            {
                //we're valid and did not hit epsilon case
                    //if we hit the epsilon case there are no rules to run so don't it's just that shrimple
                //currentToken advances when we run a TNAME, or symNAME ruleString to consume specific token type or symbol type
                executeRules(inputRule, ParentNode, matchSetAddress);
            }
        }
    }

    private ParserNode executeRules(Rule inputRule, ParserNode ParentNode, int matchSetAddress)
    {
        String[] matchSet = inputRule.getMatchSet(matchSetAddress);
        for (String ruleString : matchSet)
        {
            Rule subRule = null;

            int isSubRule = 0;
            int isTokenType = 1;
            int isSymType = 2;

            int ruleType = checkRuleType(ruleString);
            if (ruleType != -1)
            {
                subRule = getRule(ruleString);
            }

            if (subRule != null)
            {
                System.out.print("");
            }

            if (isSubRule == ruleType)
            {
                
                ParserNode oldParent = ParentNode;

                //if not a tail ruleString we add to the parse tree
                if (!subRule.getIsTailRule())
                {
                    ParentNode = addNode(subRule, oldParent);
                }

                //subrule does not consume token
                matchRule(subRule, ParentNode);
                ParentNode = oldParent;
            }

            else if (isTokenType == ruleType)
            {
                //we consume the token at the end of this step
                    //we are matching a specific token type or entry in the symbol table
                    //if our currentToken matches our ruleString we can add a Node
                if (Token.compareTypeAsString(currentToken, ruleString))
                {
                    //token rules do not pass a ruleString
                    addNode(null, ParentNode);
                    advanceCurrentToken();
                }
                else
                {
                    //our token did not match correctly so we error,
                    //TODO: do better error stuff
                    addErrorNode(inputRule, ParentNode);
                    advanceCurrentToken();
                }
            }

            else if (isSymType == ruleType)
            {
                //we consume the token at the end of this step
                //TODO: work in symbol table

                if (Token.compareTypeAsString(currentToken, "TIDEN"))
                {
                    addNode(null, ParentNode);
                    advanceCurrentToken();
                }
                else
                {
                    //our token did not match correctly so we error,
                    //TODO: do better error stuff
                    addErrorNode(inputRule, ParentNode);
                    advanceCurrentToken();
                }
            }
        } //end for

        return ParentNode;
    }


    //---------------------------

    private int compareFirstSet(Rule inputRule, boolean hasMultipleMatchSets)
    {
        String[] firstSet = inputRule.getFirstSet();

        String name = currentToken.getTypeString();

        for (int i = 0; i < firstSet.length; i++)
        {
            //if we have multiple sets we return the number that corressponds to that array entry, 
                //else we return 0 because only one set
            if (name.equalsIgnoreCase(firstSet[i]))
            {
                if (hasMultipleMatchSets)
                    if (inputRule.isEpsilonRule())
                        return i-1;
                    else
                        return i;
                return 0;
            }
            
            //handle symbols TODO: lookup symbol table
            if (firstSet[i].startsWith("sym") && name.equalsIgnoreCase("TIDEN"))
            {
                if (hasMultipleMatchSets)
                    if (inputRule.isEpsilonRule())
                        return i-1;
                    else
                        return i;
                return 0;
            }
        }
        return -1;
    }

    private boolean matchSetExists(int matchSetAddress, Rule inputRule, ParserNode ParentNode)
    {
        boolean isEpsilonRule = inputRule.isEpsilonRule();
        //adress being -1 mean we did not match any of the values in the first set
        if (matchSetAddress == -1)
        {
            if (isEpsilonRule)
            {
                //epsilon is legal and our input is legal so everything is legal
                return true;
            }
            else
            {
                //if we're not an epsilon ruleString we've received illegal input and should give semantic error
                addErrorNode(inputRule, ParentNode);
                return false;
            }
        }

        //return true if the adress is inbounds
        return (matchSetAddress+1 <= inputRule.getMatchSets().length);
    }

    private int checkRuleType(String input)
    {
        //rule nodes are denoted as nRuleName
        //tokens are TTOKN as setup in A1
        //symbols (specified id's) are symNAME
        if (input.toUpperCase().startsWith("N"))
            return 0;
        if (input.toUpperCase().startsWith("T"))
            return 1;
        if (input.toUpperCase().startsWith("SYM"))
            return 2;

        //System.err.println("Invalid input ruleString: "+input);
        return -1;
    }

    private void addErrorNode(Rule rule, ParserNode parentNode)
    {
        String errorDesc = "Received Token: "+currentToken.toString()+" | "+rule.getName()+" expected token of possible types: "+rule.firstSetToString();

        if (globErrDesc != null)
        {
            errorDesc = globErrDesc;
        }

        ParserNode newNode = new ParserNode(currentToken, rule, errorDesc, parentNode);
        parentNode.addChild(newNode);
    }

    private ParserNode addNode(Rule rule, ParserNode parentNode)
    {
        ParserNode newNode = new ParserNode(currentToken, rule, parentNode);

        if (isFirstNode) {
            isFirstNode = false;
            nodeTree = new PNodeTree(newNode);   
            return newNode;
        }

        parentNode.addChild(newNode);
        return newNode;
    }


    private Rule getRule(String Name)
    {
        for (Rule rule : ruleList) {
            if (rule.getName().equalsIgnoreCase(Name))
            {
                return rule;
            }
        }
        //System.err.println("Failed to find Rule named: "+Name);
        return null;
    }

    private static void advanceCurrentToken() 
    {
        //returns the token and increments for next time
        if (currentTokenIndex < tokenList.size())
        {
            Token tok = tokenList.get(currentTokenIndex);
            currentTokenIndex++;
            currentToken = tok;
        }
    }
}
