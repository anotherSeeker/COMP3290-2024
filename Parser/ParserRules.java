package Parser;
import java.util.ArrayList;

public class ParserRules 
{
    //each rule needs, { Name, [[First1], [First2], ...], [[Follow1], [Follow2], ...], [[ruleSet1], [ruleSet2], ...] }

    //getRule(Name)
    private String[] names = {
        "NPROG",
        "NGLOB"
    };

    private String[][] first = {
        {"TCD24"},
        {"TCNST", "TTYPD", "TARRD"}
    };

    private String[][] follow = {
        {"TTEOF"}
    };

    private String[][] matchSets = {
        {"TCD24", "TIDEN", "NGLOB", "NMAIN", "TTEOF"}
    };

    private ArrayList<Rule> rules = new ArrayList<>();   
    
    public void initialiseRules()
    {
        for (int i = 0; i < first.length; i++)
        {
            rules.add(new Rule(names[i], first[i], follow[i], matchSets[i]));
        }
    }   
}
