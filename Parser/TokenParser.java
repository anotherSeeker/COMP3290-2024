package Parser;
import Tokeniser.Token;
import java.util.*;

public class TokenParser 
{
    private static PNodeTree nodeTree;
    private static ArrayList<Rule> ruleList;
    private static ArrayList<Token> tokenList;
    private static int currentTokenIndex = 0;
    private static Token currentToken;
    private static boolean isFirstNode = true;

    public TokenParser(ArrayList<Token> tokList)
    {
        tokenList = tokList;
        currentToken = getNextToken();

        ruleList = ParserRules.initialiseRules();
    }

    public PNodeTree run()
    {
        matchRule(currentToken, ruleList.get(0), null);

        return nodeTree;
    }

    //---------------------------

    private ParserNode matchRule(Token inputToken, Rule inputRule, ParserNode ParentNode)
    {
        boolean hasMultipleMatchSets = inputRule.hasMultipleMatchSets();
        //if we don't have multiple match sets we always use set 0 regardless of epsilon status

        int matchSetAddress = compareFirstSet(inputToken, inputRule.getFirstSet(), hasMultipleMatchSets);
        boolean valid = matchSetExists(matchSetAddress, inputToken, inputRule, ParentNode);
        if (valid)
        {
            ParentNode = executeRules(inputToken, inputRule, ParentNode, matchSetAddress);
            return ParentNode;
        }

        return null;
    }


    //---------------------------

    private int compareFirstSet(Token inputToken, String[] firstSet, boolean hasMultipleMatchSets)
    {
        String name = inputToken.getTypeString();

        for (int i = 0; i < firstSet.length; i++)
        {
            //if we matched with the epsilon symbol we have something weird going on but we catch it here anyway
            if (!(firstSet[i].equalsIgnoreCase("nEPS")))
            {
                if (name.equalsIgnoreCase(firstSet[i]))
                {
                    //if we have multiple sets we return the number that corressponds to that array entry, 
                    //else we return 0 because only one set
                    if (hasMultipleMatchSets)
                    {
                        return i;
                    }
                    return 0;
                }
            }
        }
        return -1;
    }

    private boolean matchSetExists(int matchSetAddress, Token inputToken, Rule inputRule, ParserNode ParentNode)
    {
        boolean isEpsilonRule = inputRule.isEpsilonRule();
        boolean hasMultipleMatchSets = inputRule.hasMultipleMatchSets();
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
                //if we're not an epsilon rule we've received illegal input and should give semantic error
                addErrorNode(inputToken, inputRule, ParentNode);
                return false;
            }
        }

        //return true if the adress is inbounds
        if (isEpsilonRule && hasMultipleMatchSets)
        {
            //if we are an epsilon rule with multiple sets, nEPS is always the first entry so we reduce our address because eps has no matchset, 
                //this setup is kinda silly but fixing it isn't worth the effort right now, it lets us check isEpsilon easily 
                //but is overall pretty braindead
            matchSetAddress--;
        }
        return (matchSetAddress+1 <= inputRule.getMatchSets().length);
    }

    private boolean checkIsSubRule(String input)
    {
        //rule nodes are denoted as nRuleName
        //tokens are TTOKN as setup in A1
        //symbols (specified id's) are symName
        return input.charAt(0) == 'n';
    }

    private ParserNode executeRules(Token inputToken, Rule inputRule, ParserNode ParentNode, int matchSetAddress)
    {
        String[] matchSet = inputRule.getMatchSet(matchSetAddress);
        for (String rule : matchSet)
        {
            boolean isSubRule = checkIsSubRule(rule);
            if (isSubRule)
            {
                Rule subRule = getRule(rule);
                ParserNode oldParent = ParentNode;

                //if not a tail rule we add to the parse tree
                if (!subRule.getIsTailRule())
                {
                    ParentNode = addNode(inputToken, inputRule, oldParent);
                    //TODO: remove test prints
                    oldParent.printSelf();
                    ParentNode.printSelf();
                }

                matchRule(inputToken, subRule, ParentNode);

                ParentNode = oldParent;
            }
            else
            {
                //we are matching a specific token type or entry in the symbol table
                //if our inputtoken matches our rule we can add a Node
                if (Token.compareTypeAsString(inputToken, rule))
                {
                    ParentNode = addNode(inputToken, inputRule, ParentNode);
                }
                else
                {
                    
                }

            }
        }

        return ParentNode;
    }


    private void addErrorNode(Token token, Rule rule, ParserNode parentNode)
    {
        String errorDesc = "Received Token: "+token.toString()+"Expected token of possible types: "+rule.firstSetToString();

        ParserNode newNode = new ParserNode(token, "ERROR: "+rule.getName(), errorDesc, parentNode);
        parentNode.addChild(newNode);
    }

    private ParserNode addNode(Token token, Rule rule, ParserNode parentNode)
    {
        ParserNode newNode = new ParserNode(token, rule.getName(), parentNode);

        if (isFirstNode) {
            isFirstNode = false;
            nodeTree = new PNodeTree(newNode);   
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
        System.err.println("Failed to find Rule named: "+Name);
        return null;
    }

    private static Token getNextToken() 
    {
        Token tok = tokenList.get(currentTokenIndex);
        currentTokenIndex++;
        return tok;
    }
}
