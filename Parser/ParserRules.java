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
        "nSTATS2TAIL",
        "nSTATSTAIL",
        "nSTRSTAT",
        "nSTAT",
        "nASGNORCALLSTAT",
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
        "nASGNSTATTAIL",
        "nASGNOP",
        "nIOSTAT",
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
        {"NEPS", "TIDEN", "TCNST"},//plist
        {"TIDEN", "TCNST"},//params
        {"nEPS", "TCOMA"},//paramsTail
        {"TIDEN", "TCNST"},//param
        {"TIDEN"},//paramDecl
        {"TINTG", "TFLOT", "TBOOL", "symSTRUCTID", "symTYPEID"},//paramDeclTail
        {"TIDEN", "TBEGN"},//funcbody
        {"nEPS", "TIDEN"},//locals
        {"TIDEN"},//dlist
        {"nEPS", "TCOMA"},//dlisttail
        {"TIDEN"},//decl
        {"TINTG", "TFLOT", "TBOOL", "symSTRUCTID", "symTYPEID"},//decltail
        {"TMAIN"},//mainbody
        {"TIDEN"},//slist
        {"nEPS", "TCOMA"},//slistTail
        {"TIDEN"},//sdecl
        {"TINTG", "TFLOT", "TBOOL", "symSTRUCTID"},//sdecltail
        {"TIDEN"},//initdecl
        {"TINTG", "TFLOT", "TBOOL"},//stype
        //stat up to TRETN, strstat from then on
        {        "TREPT", "TIDEN", "TINPT", "TPRNT", "TPRLN", "TRETN", "TTFOR", "TIFTH", "TSWTH", "TTTDO"},//stats
        //stat up to TRETN, strstat from then on
        {        "TREPT", "TIDEN", "TINPT", "TPRNT", "TPRLN", "TRETN", "TTFOR", "TIFTH", "TSWTH", "TTTDO"},//stats2tail
        {"nEPS", "TREPT", "TIDEN", "TINPT", "TPRNT", "TPRLN", "TRETN", "TTFOR", "TIFTH", "TSWTH", "TTTDO"},//statstail
        {"TTFOR", "TIFTH", "TSWTH", "TTTDO"},//strstat
        {"TREPT", "TIDEN", "TINPT", "TPRNT", "TPRLN", "TRETN"},//stat
        {"nEPS", "TDOTT", "TLBRK", "TEQUL", "TPLEQ", "TMNEQ", "TSTEQ", "TDVEQ", "TLPAR"},//asgnorcallstat
        {"TTFOR"},//forstat
        {"TREPT"},//repstat
        {"TTTDO"},//dostat
        {"nEPS", "TIDEN"},//asgnlist
        {"nEPS", "TCOMA"},//asgnlisttail
        {"TIFTH"},//ifstat
        {"TTEND", "TELSE", "TELIF"},//ifstattail
        {"TSWTH"},//switchstat
        {"TCASE", "TDFLT"},//caselist
        {"nEPS", "TCASE", "TDFLT"},//caselisttail
        {"TIDEN"},//asgnstat
        {"TDOTT", "TLBRK", "TEQUL", "TPLEQ", "TMNEQ", "TSTEQ", "TDVEQ"},//asgnstattail used in asgnorcallstat in place of asgnstat WE MISSED TDOTT IN THE FIRST SUBMISSION
        {"TEQUL", "TPLEQ", "TMNEQ", "TSTEQ", "TDVEQ"},//asgnop
        {"TINPT", "TPRNT", "TPRLN"},//iostat
        {"TTNOT", "TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR", "TRPAR"},//callstattail
        {"TRETN"},//returnstat
        {"TVOID", "TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//returnstattail
        {"TIDEN"},//vlist
        {"nEPS", "TCOMA"},//vlisttail
        {"TIDEN"},//var
        {"nEPS", "TLBRK", "TDOTT"},//vartail
        {"nEPS", "TDOTT"},//vartailtail
        {"TTNOT", "TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//elist
        {"nEPS", "TCOMA"},//elisttail

        {"TTNOT", "TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//bool
        {"nEPS", "TTAND", "TTTOR", "TTXOR"},//booltail

        {"TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//rel
        {"nEPS", "TEQEQ", "TTNEQ", "TGRTR", "TGEQL", "TLESS", "TLEQL"},//reltail
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
        {"nEPS", "TLPAR", "TLBRK", "TDOTT"},//varorfncalltail
        {"TRPAR", "TTNOT", "TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//fncalltail
        {"TSTRG", "TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"},//prlist
        {"nEPS", "TCOMA"},//prlisttail
        {"TSTRG", "TIDEN", "TILIT", "TFLIT", "TTRUE", "TFALS", "TLPAR"}//printitem
    };

    private static final String[][][] matchSets = {
        {{"TCD24", "TIDEN", "nGLOB", "nFUNCS", "nMAINBODY", "TTEOF"}
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
        {{"TCOMA", "nFIELDS"}
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
        {{"TFUNC", "TIDEN", "TLPAR", "nPLIST", "TRPAR", "TCOLN", "nRTYPE", "nFUNCBODY"}
        },//func
        {{"nSTYPE"}, {"nSTYPE"}, {"nSTYPE"}, {"TVOID"}
        },//rtype
        {{"nPARAMS"}
        },//plist
        {{"nPARAM", "nPARAMSTAIL"}
        },//params
        {{"TCOMA", "nPARAMS"}
        },//paramstail
        {{"nPARAMDECL"}, {"TCNST", "nARRDECL"}
        },//param
        {{"nINITDECL", "nPARAMDECLTAIL"}
        },//paramDecl
        {{"nSDECLTAIL"}, {"nSDECLTAIL"}, {"nSDECLTAIL"}, {"nSDECLTAIL"}, {"symTYPEID"}
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
        {{"nSDECLTAIL"}, {"nSDECLTAIL"}, {"nSDECLTAIL"}, {"nSDECLTAIL"}, {"nARRDECLTAIL"}
        },//decltail
        {{"TMAIN", "nSLIST", "TBEGN", "nSTATS", "TTEND", "TCD24", "TIDEN"}
        },//mainbody
        {{"nSDECL", "nSLISTTAIL"}
        },//slist
        {{"TCOMA", "nSLIST"}
        },//slistTail
        {{"nINITDECL", "nSDECLTAIL"}
        },//sdecl
        {{"nSTYPE"}, {"nSTYPE"}, {"nSTYPE"},{"symSTRUCTID"}
        },//sdecltail
        {{"TIDEN", "TCOLN"}
        },//initdecl
        {{"TINTG"}, {"TFLOT"}, {"TBOOL"}
        },//stype 
        {{"nSTAT", "TSEMI", "nSTATSTAIL"}, {"nSTAT", "TSEMI", "nSTATSTAIL"}, {"nSTAT", "TSEMI", "nSTATSTAIL"},{"nSTAT", "TSEMI", "nSTATSTAIL"}, {"nSTAT", "TSEMI", "nSTATSTAIL"}, {"nSTAT", "TSEMI", "nSTATSTAIL"},{"nSTRSTAT", "nSTATSTAIL"}, {"nSTRSTAT", "nSTATSTAIL"}, {"nSTRSTAT", "nSTATSTAIL"}, {"nSTRSTAT", "nSTATSTAIL"}
        },//stats
        {{"nSTAT", "TSEMI", "nSTATSTAIL"}, {"nSTAT", "TSEMI", "nSTATSTAIL"}, {"nSTAT", "TSEMI", "nSTATSTAIL"},{"nSTAT", "TSEMI", "nSTATSTAIL"}, {"nSTAT", "TSEMI", "nSTATSTAIL"}, {"nSTAT", "TSEMI", "nSTATSTAIL"},{"nSTRSTAT", "nSTATSTAIL"}, {"nSTRSTAT", "nSTATSTAIL"}, {"nSTRSTAT", "nSTATSTAIL"}, {"nSTRSTAT", "nSTATSTAIL"}
        },//stats2tail exists so we avoid indenting and printing stats with every new stats statement
        {{"nSTATS2TAIL"}
        },//statstail
        {{"nFORSTAT"}, {"nIFSTAT"}, {"nSWITCHSTAT"}, {"nDOSTAT"}
        },//strstat
        {{"nREPTSTAT"}, {"TIDEN", "nASGNORCALLSTAT"}, {"nIOSTAT"}, {"nIOSTAT"}, {"nIOSTAT"}, {"nRETURNSTAT"}
        },//stat
        {{"nASGNSTATTAIL"}, {"nASGNSTATTAIL"}, {"nASGNSTATTAIL"}, {"nASGNSTATTAIL"}, {"nASGNSTATTAIL"}, {"nASGNSTATTAIL"}, {"nASGNSTATTAIL"}, {"TLPAR", "nCALLSTATTAIL"},
        },//asgnorcallstat
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
        {{"TIFTH", "TLPAR", "nBOOL", "TRPAR", "nSTATS", "nIFSTATTAIL"}
        },//ifstat
        {{"TTEND"}, {"TELSE", "nSTATS", "TTEND"}, {"TELIF", "TLPAR", "nBOOL", "nSTATS", "TTEND"}
        },//ifstattail
        {{"TSWTH", "TLPAR", "nEXPR", "TRPAR", "TBEGN", "nCASELIST", "TTEND"}
        },//switchstat
        {{"TCASE", "nEXPR", "TCOLN", "nSTATS", "TBREK", "TSEMI", "nCASELISTTAIL"}, {"TDFLT", "TCOLN", "nSTATS"}
        },//caselist
        {{"nCASELIST"}
        },//caselisttail3
        {{"nVAR", "nASGNOP", "nBOOL"}
        },//asgnstat
        {{"nVARTAIL", "nASGNOP", "nBOOL"}
        },//asgnstattail
        {{"TEQUL"}, {"TPLEQ"}, {"TMNEQ"}, {"TSTEQ"}, {"TDVEQ"}
        },//asgnop
        {{"TINPT", "nVLIST"}, {"TPRNT", "nPRLIST"}, {"TPRLN", "nPRLIST"}
        },//iostat
        {{"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"TRPAR"}
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
        {{"TLBRK", "nEXPR", "TRBRK", "nVARTAILTAIL"}, {"nVARTAILTAIL"}
        },//vartail
        {{"TDOTT", "TIDEN"}
        },//vartailtail
        {{"nBOOL", "nELISTTAIL"}
        },//elist
        {{"TCOMA", "nELIST"}
        },//elisttail
        {{"TTNOT", "nREL", "nBOOLTAIL"}, {"nREL", "nBOOLTAIL"}, {"nREL", "nBOOLTAIL"},{"nREL", "nBOOLTAIL"},{"nREL", "nBOOLTAIL"},{"nREL", "nBOOLTAIL"},{"nREL", "nBOOLTAIL"},
        },//bool
        {{"nLOGOP", "nBOOL"},
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
        {{"TLPAR", "nFNCALLTAIL"}, {"nVARTAIL"}, {"nVARTAIL"}
        },//varorfncalltail
        {{"TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}, {"nELIST", "TRPAR"}
        },//fncalltail
        {{"nPRINTITEM", "nPRLISTTAIL"}
        },//prlist
        {{"TCOMA", "PRLIST"}
        },//prlisttail
        {{"TSTRG"}, {"nEXPR"}, {"nEXPR"}, {"nEXPR"}, {"nEXPR"}, {"nEXPR"}, {"nEXPR"},  
        }//printitem
    }; 

    //----------------------------------------------
    //----------------------------------------------
    //----------------------------------------------
    //----------------------------------------------

    private static final boolean[] isRecoveryRule = {
        true,//nprog
        true,//glob
        true,//const
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        true,//arrays
        false,
        false,
        false,
        false,
        true,//funcs
        false,
        true,//func
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        true,//funcbody
        false,
        false,
        false,
        false,
        false,
        true,//mainbody
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        true,//for
        true,//repeat
        true,//do
        false,
        false,
        true,//if
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false
    };
    
    public static ArrayList<Rule> initialiseRules()
    {
        for (int i = 0; i < first.length; i++) 
        {
            boolean isTailRule = checkIsTailRule(names[i]);
            
            rules.add(new Rule(names[i], first[i], new String[0], matchSets[i], isTailRule, isRecoveryRule[i]));
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