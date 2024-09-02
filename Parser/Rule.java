package Parser;

public class Rule 
{
    private String Name = null;
    private String[] FirstSet;
    private String[] FollowSet;
    private String[] MatchSet;

    public Rule(String _Name, String[] _FirstSet, String[] _FollowSet, String[] _MatchSet)
    {
        Name = _Name;
        FirstSet = _FirstSet;
        FollowSet = _FollowSet;
        MatchSet = _MatchSet;
    }

    public String[] getFirstSet() {
        return FirstSet;
    }
    public String[] getFollowSet() {
        return FollowSet;
    }
    public String[] getMatchSet() {
        return MatchSet;
    }
    public String getName() {
        return Name;
    }
}
