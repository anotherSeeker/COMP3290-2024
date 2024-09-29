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
        "nVARTAILTAIL",
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
        "nFACT",
        "nFACTTAIL",
        "nEXPONENT",
        "nVARORFNCALL",
        "nVARORFNCALLTAIL",
        "nFNCALLTAIL",
        "nPRLIST",
        "nPRLISTTAIL",
        "nPRINTITEM"};

    //"nEPS" meaning epsiolon or empty
    //if first entry in first table is "nEPS" we can ignore this node if we do not meet any other firsts
        //if first entry is neps we have to receive match sets offset by 1 for each input, can handle this fine
    private static final String[][] first = {
        {"TCD24"},//program
        {"nEPS", "TCONS", "TTYPD", "TARRD"},//globals
        {"nEPS", "TCONS"},//constants
        {"TIDEN"},//initlist
        {"nEPS","TCOMA"},//initlistTail
        {"TIDEN"},//init
        {"nEPS", "TTYPD"},//types
        {"symSTRUCTID", "symTYPEID"},//typelist
        {"nEPS", "symSTRUCTID", "symTYPEID"},//typelisttail
        {"symSTRUCTID", "symTYPEID"},//type
        {"TIDEN"},//fields
        {"nEPS", "TCOMA"},//fieldsTail
        {"nEPS", "TARRD"},//arrays
        {"TIDEN"},//arrdecls
        {"nEPS", "TCOMA"},//arrdeclstail
        {"TIDEN"},//arrdecl
        {"symTYPEID"},//arrdeclTail
        {"nEPS", "TFUNC"},//funcs
        {"nEPS", "TFUNC"},//funcstail
        {"TFUNC"},//func
        {"TINTG", "TFLOT", "TBOOL", "TVOID"},//rtype
        {"NEPS", "TIDEN", "TCONS"},//plist
        {"TIDEN", "TCONS"},//params
        {"nEPS", "TCOMA"},//paramsTail
        {"TIDEN", "TCONS"},//param
        {"TIDEN"},//paramDecl
        {"TINTG", "TFLOT", "TBOOL", "symSTRUCTID", "symTYPEID"},//paramDeclTail
        {"TIDEN", "TBEGIN"},//funcbody
        {"nEPS", "TIDEN"},//locals
        {"TIDEN"},//dlist
        {"nEPS", "TCOMA"},//dlisttail
        {"TIDEN"},//decl
        {"TINTG", "TFLOT", "TBOOL", "symSTRUCTID", "symTYPEID"},//decltail
        {""},//mainbody
        {""},//slist
        {""},//slistTail
        {""},//sdecl
        {"TINTG", "TFLOT", "TBOOL", "symSTRUCTID"},//sdecltail
        {"TIDEN"},//initdecl
        {"TINTG", "TFLOT", "TBOOL"},//stype
        {""},//stats
        {""},//statstail
        {""},//strstat
        {""},//stat
        {""},//forstat
        {""},//repstat
        {""},//dostat
        {""},//asgnlist
        {""},//asgnlisttail
        {""},//ifstat
        {""},//ifstattail
        {""},//switchstat
        {""},//caselist
        {""},//caselisttail
        {""},//asgnstat
        {""},//asgnop
        {""},//iostat
        {""},//callstat
        {""},//callstattail
        {""},//returnstat
        {""},//returnstattail
        {""},//vlist
        {""},//vlisttail
        {""},//var
        {""},//vartail
        {""},//vartailtail
        {"TTNOT", "TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//elist
        {"nEPS", "TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//elisttail
        {""},//bool
        {""},//booltail
        {""},//rel
        {""},//reltail
        {"TTAND", "TTTOR", "TTXOR"},//logop
        {"TEQEQ", "TTNEQ", "TGRTR", "TGEQL", "TLESS", "TLEQL"},//relop
        {"TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//expr
        {"nEPS", "TPLUS", "TMINS"},//exprTail
        {"TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//term
        {"nEPS", "TSTAR", "TDIVID", "TPERC"},//termtail
        {"TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//fact
        {"nEPS", "TCART"},//facttail
        {"TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//exponent
        {"TIDEN"},//varorfncall
        {"TLPAR"},//varorfncalltail
        {""},//fncalltail
        {""},//prlist
        {""},//prlisttail
        {""}//printitem
    };

    private static final String[][] follow = {
        {"TTEOF"},//prog
        {"TFUNC", "TMAIN"},//glob
        {"TTYPD", "TARRD", "TFUNC", "TMAIN"},//consts
        {"TTYPD", "TARRD", "TFUNC", "TMAIN"},//initlist
        {"TTYPD", "TARRD", "TFUNC", "TMAIN", "TCOMA"},//initlisttail
        {"TCOMA", "TTYPD", "TARRD", "TFUNC", "TMAIN"},//init
        {"TTYPD", "TARRD", "TFUNC", "TMAIN"},//types
        {""},//typelist
        {""}
    };

    private static final String[][][] matchSets = {
        {{"TCD24", "TIDEN", "nGLOB", "nMAINBODY", "TTEOF"}
        },//nprog
        {{"nCONST", "nTYPES", "nARRAYS"}
        },//nglob
        {{"TCONS", "nINITLIST"}
        },//cnsts
        {{"nINIT", "nINITLISTTAIL"}
        },//initlist
        {{"TCOMA", "nINITLIST"}
        },//initlisttail
        {{"TIDEN","TEQUL", "nEXPR"}
        },//init
        {{"TTYPD", "nTYPELIST"}
        },//types
        {{"nTYPE", "nTYPELISTTAIL"}
        },//typelist
        {{"nTYPELIST"}
        },//typelisttail
        {{"symSTRUCTID", "TTDEF", "nFIELDS", "TTEND"}, {"symTYPEID", "TTDEF", "TARAY", "TLBRK", "nEXPR", "TRBRK", "TTTOF", "symSTRUCTID", "TTEND"}
        },//type
        {{"nSDECL", "nFIELDSTAIL"}
        },//fields
        {{"TCOMA"}
        },//fieldsTail
        {{"TARRD", "nARRDECLS"}
        },//arrays
        {{"nARRDECL", "nARRDECLSTAIL"}
        },//arrdecls
        {{"TCOMA", "nARRDECLS"}
        },//arrdeclstail
        {{"TIDEN", "TCOLN", "nARRDECLTAIL"}
        },//arrdecl
        {{"symTYPEID"}
        },//arrdeclTail
        {{"nFUNC", "nFUNCSTAIL"}
        },//funcs
        {{"nFUNCS"}
        },//funcstail
        {{"TFUNC", }
        },//func
        {{"nSTYPE"}, {"nSTYPE"}, {"nSTYPE"}, {"TVOID"}
        },//rtype
        {{"nPARAMS"}
        },//plist
        {{"nPARAM", "NPARAMSTAIL"}
        },//params
        {{"TCOMA", "nPARAMS"}
        },//paramstail
        {{"nPARAMDECL"}, {"TCNST", "nARRDECL"}
        },//param
        {{"nINITDECL", "nPARAMDECLTAIL"}
        },//paramDecl
        {{"nSDECLTAIL"}, {"symTYPEID"}
        },//paramDeclTail
        {{"nLOCALS", "TBEGN", "nSTATS", "TTEND"}
        },//funcbody
        {{"nDLIST"}
        },//locals
        {{"nDECL", "nDLISTTAIL"}
        },//dlist
        {{"TCOMA", "nDLIST"}
        },//dlisttail
        {{"nINITDECL", "nDECLTAIL"}
        },//decl
        {{"nSDECLTAIL"}, {"nARRDECLTAIL"}
        },//decltail
        {{"TMAIN", "nSLIST", "TBEGN", "nSTATS", "TTEND", "TCD24", "TIDEN"}
        },//mainbody
        {{"nSDECL", "nSLISTTAIL"}
        },//slist
        {{"TCOMA", "nSLIST"}
        },//slistTail
        {{"nINITDECL", "nSDECLTAIL"}
        },//sdecl
        {{"nSTYPE"}, {"nSDECLTAIL"}, {"nSDECLTAIL"}, {"nSDECLTAIL"},{"symSTRUCTID"}
        },//sdecltail
        {{"TIDEN", "TCOLN"}
        },//initdecl
        {{"TINTG"}, {"TFLOT"}, {"TBOOL"}
        },//stype
        {{"nSTAT", "TSEMI", "nSTATSTAIL"}, {"nSTRSTAT", "nSTATSTAIL"}
        },//stats
        {{"nSTATS"}
        },//statstail
        {{"nFORSTAT"}, {"nIFSTAT"}, {"nSWITCHSTAT"}, {"nDOSTAT"}
        },//strstat
        {{"nREPTSTAT"}, {"nASGNSTAT"}, {"nIOSTAT"}, {"nCALLSTAT"}, {"nRETURNSTAT"}
        },//stat
        {{"TTFOR", "TLPAR", "nASGNLIST", "TSEMI", "nBOOL", "TRPAR", "nSTATS", "TTEND"}
        },//forstat
        {{"TREPT", "TLPAR", "nASGNLIST", "TRPAR", "nSTATS", "TUNTL", "nBOOL"}
        },//repstat
        {{"TTTDO", "nSTATS", "TWHIL", "TLPAR", "nBOOL", "TRPAR", "TTEND"}
        },//dostat
        {{"nASGNSTAT", "nASGNLISTTAIL"}
        },//asgnlist
        {{"TCOMA", "nASGNLIST"}
        },//asgnlisttail
        {{"TTTIF", "TLPAR", "nBOOL", "TRPAR", "nSTATS", "nIFSTATTAIL"}
        },//ifstat
        {{"TTEND"}, {"TELSE", "nSTATS", "TTEND"}, {"TELIF", "TLPAR", "nBOOL", "nSTATS", "TTEND"}
        },//ifstattail
        {{"TSWTH", "TLPAR", "nEXPR", "TRPAR", "TBEGN", "nCASELIST", "TTEND"}
        },//switchstat
        {{"TCASE", "nEXPR", "TCOLN", "nSTATS", "TBREK", "TSEMI", "nCASELISTTAIL"}, {"TDFLT", "TCOLN", "TSTATS"}
        },//caselist
        {{"nCASELIST"}, {"TDFLT", "TCOLN", "TSTATS"}
        },//caselisttail
        {{"nVAR", "nASGNOP", "nBOOL"}
        },//asgnstat
        {{"TEQUL"}, {"TPLEQ"}, {"TMNEQ"}, {"TSTEQ"}, {"TDVEQ"}
        },//asgnop
        {{"TINPT", "nVLIST"}, {"TPRNT", "nPRLIST"}, {"TPRLN", "nPRLIST"}
        },//iostat
        {{"TIDEN", "TLPAR", "nCALLSTATTAIL"}
        },//callstat
        {{"nELIST", "TRPAR"}, {"TRPAR"}
        },//callstattail
        {{"TRETN", "nRETURNSTATTAIL"}
        },//returnstat
        {{"TVOID"}, {"nEXPR"}
        },//returnstattail
        {{"nVAR", "nVLISTTAIL"}
        },//vlist
        {{"TCOMA", "nVLIST"}
        },//vlisttail
        {{"TIDEN", "nVARTAIL"}
        },//var
        {{"TLBRK", "nEXPR", "nVARTAILTAIL"}
        },//vartail
        {{"TDOTT", "TIDEN"}
        },//vartailtail
        {{"nBOOL", "nELISTTAIL"}
        },//elist
        {{"TCOMA", "nELIST"}
        },//elisttail
        {{"TTNOT", "nBOOLTAIL"}, {"nBOOLTAIL"}
        },//bool
        {{"nREL"}, {"nBOOL", "nLOGOP", "nREL"}
        },//booltail
        {{"nEXPR", "nRELTAIL"}
        },//rel
        {{"nRELOP", "nEXPR"}
        },//reltail
        {{"TTAND"}, {"TTTOR"}, {"TTXOR"}, 
        },//logop
        {{"TEQEQ"}, {"TTNEQ"}, {"TGRTR"}, {"TGEQL"}, {"TLESS"}, {"TLEQL"}
        },//relop
        {{"nTERM", "nEXPRTAIL"}
        },//expr
        {{"TPLUS", "nEXPR"}, {"TMINS", "nEXPR"}
        },//exprTail
        {{"nFACT", "nTERMTAIL"}
        },//term
        {{"TSTAR", "nTERM"}, {"TDIVD", "nTERM"}, {"TPERC", "nTERM"}
        },//termtail
        {{"nEXPONENT", "nFACTTAIL"}
        },//fact
        {{"TCART", "nFACT"}
        },//facttail
        {{"nVARORFNCALL"}, {"TILIT"}, {"TFLIT"}, {"TTRUE"}, {"TFALS"}, {"TLPAR", "nBOOL", "TRPAR"}
        },//exponent
        {{"TIDEN", "nVARORFNCALLTAIL"}
        },//varorfncall
        {{"TLPAR", "nFNCALLTAIL"}
        },//varorfncalltail
        {{"nELIST", "TRPAR"}, {"TRPAR"}
        },//fncalltail
        {{"nPRINTITEM", "nPRLISTTAIL"}
        },//prlist
        {{"TCOMA", "PRLIST"}
        },//prlisttail
        {{"nEXPR"}, {"TSTRG"}
        }//printitem
    }; 

    //----------------------------------------------
    //----------------------------------------------
    //----------------------------------------------
    //----------------------------------------------

    private static final boolean[] isRecoveryRule = {
        true,
        true,
        true,
        false
    };
    
    public static ArrayList<Rule> initialiseRules()
    {
        for (int i = 0; i < first.length; i++) 
        {
            boolean isTailRule = checkIsTailRule(names[i]);
            //TODO: fix recovery and follow
            rules.add(new Rule(names[i], first[i], new String[0], matchSets[i], isTailRule, false));
        }

        return rules;
    }   

    private static boolean checkIsTailRule(String name)
    {
        //tail rules do not make nodes
        return name.toUpperCase().endsWith("TAIL");   
    }
}
    //----------------------------------------------
    //----------------------------------------------
    //----------------------------------------------
    //----------------------------------------------