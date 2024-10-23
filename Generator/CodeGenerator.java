package Generator;

public class CodeGenerator 
{
    //OP_ codes
    private final String OP_HALT = "00";
    private final String OP_NOOP_ = "01";
    private final String OP_TRAP = "02";

    private final String OP_ZERO = "03";
    private final String OP_FALSE = "04";
    private final String OP_TRUE = "05";

    private final String OP_TYPE = "07";
    private final String OP_ITYPE = "08";
    private final String OP_FTYPE = "09";

    private final String OP_ADD = "11";
    private final String OP_SUB = "12";
    private final String OP_MUL = "13";
    private final String OP_DIV = "14";
    private final String OP_REM = "15";
    
    private final String OP_POW = "16";

    private final String OP_CHS = "17";
    private final String OP_ABS = "18";

    private final String OP_GT = "21";
    private final String OP_GE = "22";
    private final String OP_LT = "23";
    private final String OP_LE = "24";
    private final String OP_EQ = "25";
    private final String OP_NE = "26";

    private final String OP_AND = "31";
    private final String OP_OR = "32";
    private final String OP_XOR = "33";
    private final String OP_NOT = "34";

    private final String OP_BT = "35";
    private final String OP_BF = "36";

    private final String OP_BR = "37";

    private final String OP_L = "40";
    private final String OP_LB = "41";
    private final String OP_LH = "42";
    private final String OP_ST = "43";

    private final String OP_STEP = "51";
    private final String OP_ALLOC = "52";
    private final String OP_ARRAY = "53";
    private final String OP_INDEX = "54";
    private final String OP_SIZE = "55";
    private final String OP_DUP = "56";

    private final String OP_READF = "60";
    private final String OP_READI = "61";
    private final String OP_VALPR = "62";
    private final String OP_STRPR = "63";
    private final String OP_CHRPR = "64";
    private final String OP_NEWLN = "65";
    private final String OP_SPACE = "66";

    private final String OP_RVAL = "70";
    private final String OP_RETN = "71";
    private final String OP_JS2 = "72";

    private final String OP_LV0 = "80";
    private final String OP_LV1 = "81";
    private final String OP_LV2 = "82";

    private final String OP_LA0 = "90";
    private final String OP_LA1 = "91";
    private final String OP_LA2 = "92";
    //done with OP_ codes




}
