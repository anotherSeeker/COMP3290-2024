package Parser;
import java.util.ArrayList;

public class ParserRules 
{
    //each rule needs, { Name, [[First1], [First2], ...], [[Follow1], [Follow2], ...], [[ruleSet1], [ruleSet2], ...] }

    //getRule(Name)
    private String[] names = {
        "NPROG",
        "NGLOB",
        "NCONST"
    };

    //"NEPS" meaning epsiolon or empty
    //if first entry in first table is "NEPS" we can ignore this node if we do not meet any other firsts
        //if first entry is neps we have to receive match sets offset by 1 for each input, can handle this fine
    private String[][] first = {
        {"TCD24"},
        {"TCNST", "TTYPD", "TARRD"},
        {"NEPS", "TCNST"}
    };

    private String[][] follow = {
        {"TTEOF"},
        {},
        {}
    };

    private String[][][] matchSets = {
        {{"TCD24", "TIDEN", "NGLOB", "NMAIN", "TTEOF"}
        },
        {{"NCONSTS", "NTYPES", "NARRAYS"}
        },
        {{"TCNST", "NINITLIST"}
        }
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
