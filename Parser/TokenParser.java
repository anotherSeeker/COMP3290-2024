package Parser;
import Tokeniser.Token;
import java.util.*;

public class TokenParser 
{
    private final static PNodeTree nodeTree = new PNodeTree();
    private static ArrayList<Rule> ruleList;
    private static ArrayList<Token> tokenList;
    private static int currentTokenIndex = 0;
    private static Token currentToken;

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

    private boolean matchRule(Token inputToken, Rule inputRule, ParserNode ParentNode)
    {
        boolean isEpsilonRule = inputRule.isEpsilonRule();
        boolean hasMultipleMatchSets = inputRule.hasMultipleMatchSets();
        //if we are an epsilon rule, we offset our matchset address by 1, 
            //we could get around this with empty matchsets think about it

        //if we don't have multiple match sets we always use set 0 regardless of epsilon status

        int matchSetAddress = compareFirstSet(inputToken.getTypeString(), inputRule.getFirstSet(), hasMultipleMatchSets);
        boolean valid = validateMatchAddress(matchSetAddress, inputToken, inputRule, isEpsilonRule, hasMultipleMatchSets, ParentNode);


        return false;
    }


    //---------------------------

    private int compareFirstSet(String inputName, String[] firstSet, boolean hasMultipleMatchSets)
    {
        for (int i = 0; i < firstSet.length; i++)
        {
            //if we matched with the epsilon symbol we have something weird going on but we catch it here anyway
            if (!(firstSet[i].equalsIgnoreCase("nEPS")))
            {
                if (inputName.equalsIgnoreCase(firstSet[i]))
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

    private boolean validateMatchAddress(int matchSetAddress, Token inputToken, Rule inputRule, boolean isEpsilonRule, boolean hasMultipleMatchSets, ParserNode ParentNode)
    {
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


    private void addErrorNode(Token token, Rule rule, ParserNode parentNode)
    {
        String errorDesc = "Received Token: "+token.toString()+"Expected token of types: "+rule.firstSetToString();
        ParserNode newNode = new ParserNode(token, "ERROR: "+rule.getName(), errorDesc, parentNode);

        parentNode.addChild(newNode);
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
