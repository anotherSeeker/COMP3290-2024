package Parser;

public class Rule 
{
    private final String Name;
    private final String[] FirstSet;
    private final String[] FollowSet;
    private final String[][] MatchSets;
    private final boolean isTailRule;
    private final boolean isRecoveryRule; 

    public Rule(String _Name, String[] _FirstSet, String[] _FollowSet, String[][] _MatchSets, boolean _isTailRule, boolean _isRecoveryRule)
    {
        Name = _Name;
        FirstSet = _FirstSet;
        FollowSet = _FollowSet;
        MatchSets = _MatchSets;
        isTailRule = _isTailRule;
        isRecoveryRule = _isRecoveryRule;
    }

    public String[] getFirstSet() {
        return FirstSet;
    }
    public String[] getFollowSet() {
        return FollowSet;
    }
    public String[][] getMatchSets() {
        return MatchSets;
    }
    public String[] getMatchSet(int address)
    {
        return MatchSets[address];
    }
    public String getName() {
        return Name;
    }
    public boolean getIsTailRule(){
        return isTailRule;
    }
    public boolean getIsRecoveryRule() {
        return isRecoveryRule;
    }
    



    public boolean isEpsilonRule()
    {
        return FirstSet[0].equalsIgnoreCase("nEPS");
    }

    public boolean hasMultipleMatchSets()
    {
        return (MatchSets.length > 1);
    }
    public String firstSetToString()
    {
        String out = "";
        for (String fSet : FirstSet)
        {
            out = out + fSet + " ";
        }

        return out;
    }
}
