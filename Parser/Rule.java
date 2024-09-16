package Parser;

public class Rule 
{
    private String Name = null;
    private String[] FirstSet;
    private String[] FollowSet;
    private String[][] MatchSets;

    public Rule(String _Name, String[] _FirstSet, String[] _FollowSet, String[][] _MatchSets)
    {
        Name = _Name;
        FirstSet = _FirstSet;
        FollowSet = _FollowSet;
        MatchSets = _MatchSets;
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
    public String getName() {
        return Name;
    }
}
