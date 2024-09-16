package Parser;
import java.util.ArrayList;

public class ParserRules 
{
    private static final ArrayList<Rule> rules = new ArrayList<>();  
    //each rule needs, { Name, [First1, First2, ...], [Follow1, Follow2, ...], [[ruleSet1], [ruleSet2], ...] }

    //getRule(Name)
    private static final String[] names = {
        "nPROG",
        "nGLOB",
        "nCONST",
        "nINITLIST",
        "nINITLISTTAIL",
        "nINIT",
        "nTYPES",
        "nTYPELIST",
        "nTYPELISTTAIL",
        "nTYPE",
        "nFIELDS",
        "nFIELDSTAIL",
        "nARRAYS",
        "nARRDECLS",
        "nARRDECLSTAIL",
        "nARRDECL",
        "nARRDECLTAIL",
        "nFUNCS",
        "nFUNCSTAIL",
        "nFUNC",
        "nRTYPE",
        "nPLIST",
        "nPARAMS",
        "nPARAMSTAIL",
        "nPARAM",
        "nPARAMDECL",
        "nPARAMDECLTAIL",
        "nFUNCBODY",
        "nLOCALS",
        "nDLIST",
        "nDLISTTAIL",
        "nDECL",
        "nDECLTAIL",
        "nMAINBODY",
        "nSLIST",
        "nSLISTTAIL",
        "nSDECL",
        "nSDECLTAIL",
        "nINITDECL",
        "nSTYPE",
        "nSTATS",
        "nSTATSTAIL",
        "nSTRSTAT",
        "nSTAT",
        "nFORSTAT",
        "nREPSTAT",
        "nDOSTAT",
        "nASGNLIST",
        "nASGNLISTTAIL",
        "nIFSTAT",
        "nIFSTATTAIL",
        "nSWITCHSTAT",
        "nCASELIST",
        "nCASELISTTAIL",
        "nASGNSTAT",
        "nASGNOP",
        "nIOSTAT",
        "nCALLSTAT",
        "nCALLSTATTAIL",
        "nRETURNSTAT",
        "nRETURNSTATTAIL",
        "nVLIST",
        "nVLISTTAIL",
        "nVAR",
        "nVARTAIL",
        "nELIST",
        "nELISTTAIL",
        "nBOOL",
        "nBOOLTAIL",
        "nREL",
        "nRELTAIL",
        "nLOGOP",
        "nRELOP",
        "nEXPR",
        "nEXPRTAIL",
        "nTERM",
        "nTERMTAIL",
        "nFACTTAIL",
        "nEXPONENT",
        "nVARORFNCALL",
        "nVARORFNCALLTAIL",
        "nFNCALL",
        "nFNCALLTAIL",
        "nPRLIST",
        "nPRLISTTAIL",
        "nPRINTITEM"
    };

    //"nEPS" meaning epsiolon or empty
    //if first entry in first table is "nEPS" we can ignore this node if we do not meet any other firsts
        //if first entry is neps we have to receive match sets offset by 1 for each input, can handle this fine
    private static final String[][] first = {
        {"TCD24"},//program
        {"nEPS", "TCNST", "TTYPD", "TARRD"},//globals
        {"nEPS", "TCNST"},//constants
        {"TIDEN"},//initlist
        {"nEPS","TCOMA"},//initlistTail
        {"TIDEN"},//init
        {"TTYPD"},//types
        {"symSTRUCTID", "symTYPEID"},//typelist
        {"nEPS", "symSTRUCTID", "symTYPEID"},//typelisttail
        {"symSTRUCTID", "symTYPEID"},//type
        {"TIDEN"},//fields
        {"nEPS", "TCOMA"},//fieldsTail
        {"nEPS", "TARRD"},//arrays
        {""}
    };

    private static final String[][] follow = {
        {"TTEOF"},//prog
        {"TFUNC", "TMAIN"},//glob
        {"TTYPD", "TARRD", "TFUNC", "TMAIN"},//consts
        {"TTYPD", "TARRD", "TFUNC", "TMAIN"},//initlist
        {"TTYPD", "TARRD", "TFUNC", "TMAIN", "TCOMA"},//initlisttail
        {""},//init
        {}
    };

    private static final String[][][] matchSets = {
        {{"TCD24", "TIDEN", "nGLOB", "nMAIN", "TTEOF"}//nprog
        },
        {{"nCONSTS", "nTYPES", "nARRAYS"}//nglob
        },
        {{"TCNST", "nINITLIST"}//cnsts
        },
        {{"nINIT", "nINITLISTTAIL"}//initlist
        },
        {{"TCOMA", "nINITLIST"}//initlisttail
        },
        {{"TIDEN","TEQUL", "nEXPR"}//init
        }

    }; 

    private static final boolean[] isRecoveryRule = {
        true,
        true,
        true
    };
    
    public static ArrayList<Rule> initialiseRules()
    {
        for (int i = 0; i < first.length; i++)
        {
            boolean isTailRule = checkIsTailRule(names[i]);
            rules.add(new Rule(names[i], first[i], follow[i], matchSets[i], isTailRule, isRecoveryRule[i]));
        }

        return rules;
    }   

    private static boolean checkIsTailRule(String name)
    {
        //tail rules do not make nodes
        return name.toUpperCase().endsWith("TAIL");   
    }
}
