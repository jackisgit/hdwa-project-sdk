// $ANTLR 3.1 AdvancedExpression.g 2021-07-30 15:32:47

package com.hdwa.sdk.expression;


import org.antlr.runtime.*;

public class AdvancedExpressionLexer extends Lexer {
    public static final int T__29 = 29;
    public static final int T__28 = 28;
    public static final int T__27 = 27;
    public static final int T__26 = 26;
    public static final int T__25 = 25;
    public static final int T__24 = 24;
    public static final int T__23 = 23;
    public static final int T__22 = 22;
    public static final int T__21 = 21;
    public static final int T__20 = 20;
    public static final int ID = 11;
    public static final int EOF = -1;
    public static final int STRINGID = 15;
    public static final int STRINGWRAPPER = 5;
    public static final int F1 = 17;
    public static final int F0 = 16;
    public static final int DOUBLE = 8;
    public static final int TAILSTRING = 13;
    public static final int F2 = 18;
    public static final int T__42 = 42;
    public static final int INTEGER = 9;
    public static final int T__43 = 43;
    public static final int T__40 = 40;
    public static final int T__41 = 41;
    public static final int T__44 = 44;
    public static final int T__45 = 45;
    public static final int NULL = 7;
    public static final int SUBSTRING = 12;
    public static final int STRINGVALUE = 14;
    public static final int T__30 = 30;
    public static final int T__31 = 31;
    public static final int T__32 = 32;
    public static final int WS = 19;
    public static final int T__33 = 33;
    public static final int T__34 = 34;
    public static final int T__35 = 35;
    public static final int NEWLINE = 6;
    public static final int T__36 = 36;
    public static final int T__37 = 37;
    public static final int T__38 = 38;
    public static final int T__39 = 39;
    public static final int CONSTANT = 10;
    public static final int IFWRAPPER = 4;
    static final String DFA1_eotS =
            "\13\uffff\1\15\5\uffff";
    static final String DFA1_eofS =
            "\21\uffff";
    static final String DFA1_minS =
            "\1\144\1\uffff\1\151\1\141\6\uffff\1\171\1\157\1\146\1\uffff\1" +
                    "\167\2\uffff";


    // delegates
    // delegators
    static final String DFA1_maxS =
            "\1\171\1\uffff\1\157\1\141\6\uffff\1\171\1\157\1\146\1\uffff\1" +
                    "\171\2\uffff";
    static final String DFA1_acceptS =
            "\1\uffff\1\1\2\uffff\1\4\1\6\1\11\1\12\1\2\1\5\3\uffff\1\3\1\uffff" +
                    "\1\7\1\10";
    static final String DFA1_specialS =
            "\21\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\3\3\uffff\1\4\4\uffff\1\2\4\uffff\1\7\1\5\1\6\4\uffff\1" +
                    "\1",
            "",
            "\1\11\5\uffff\1\10",
            "\1\12",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\13",
            "\1\14",
            "\1\16",
            "",
            "\1\17\1\uffff\1\20",
            "",
            ""
    };
    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    // $ANTLR end "T__20"
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    // $ANTLR end "T__21"
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    // $ANTLR end "T__22"
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    // $ANTLR end "T__23"
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    // $ANTLR end "T__24"
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    // $ANTLR end "T__25"
    static final short[][] DFA1_transition;
    // $ANTLR end "T__26"
    static final String DFA2_eotS =
            "\22\uffff\1\27\1\31\1\33\1\35\10\uffff";
    // $ANTLR end "T__27"
    static final String DFA2_eofS =
            "\36\uffff";
    // $ANTLR end "T__28"
    static final String DFA2_minS =
            "\1\141\1\142\1\145\2\uffff\1\157\1\151\1\141\5\uffff\1\163\1\147" +
                    "\1\156\1\uffff\1\156\1\150\1\61\2\150\10\uffff";
    // $ANTLR end "T__29"
    static final String DFA2_maxS =
            "\2\164\1\157\2\uffff\1\157\1\161\1\141\5\uffff\1\163\1\147\1\156" +
                    "\1\uffff\1\156\1\150\1\61\2\150\10\uffff";
    // $ANTLR end "T__30"
    static final String DFA2_acceptS =
            "\3\uffff\1\10\1\11\3\uffff\1\1\1\2\1\3\1\4\1\5\3\uffff\1\16\5\uffff" +
                    "\1\7\1\6\1\13\1\12\1\15\1\14\1\20\1\17";
    // $ANTLR end "T__31"
    static final String DFA2_specialS =
            "\36\uffff}>";
    // $ANTLR end "T__32"
    static final String[] DFA2_transitionS = {
            "\1\1\1\uffff\1\2\1\uffff\1\3\1\4\5\uffff\1\5\6\uffff\1\6\1" +
                    "\7",
            "\1\10\1\11\17\uffff\1\12\1\13",
            "\1\14\11\uffff\1\15",
            "",
            "",
            "\1\16",
            "\1\17\7\uffff\1\20",
            "\1\21",
            "",
            "",
            "",
            "",
            "",
            "\1\22",
            "\1\23",
            "\1\24",
            "",
            "\1\25",
            "\1\26",
            "\1\30",
            "\1\32",
            "\1\34",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };
    // $ANTLR end "T__33"
    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    // $ANTLR end "T__34"
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    // $ANTLR end "T__35"
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    // $ANTLR end "T__36"
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    // $ANTLR end "T__37"
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    // $ANTLR end "T__38"
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    // $ANTLR end "T__39"
    static final short[][] DFA2_transition;
    // $ANTLR end "T__40"
    static final String DFA14_eotS =
            "\13\uffff\1\46\2\uffff\1\46\2\uffff\1\56\1\uffff\1\60\1\62\15\46" +
                    "\1\114\1\46\2\116\4\uffff\1\121\2\46\6\uffff\30\46\1\114\1\uffff" +
                    "\1\46\2\uffff\1\116\1\uffff\1\46\1\161\1\46\1\161\2\46\1\166\1\46" +
                    "\1\166\1\46\1\173\2\46\1\161\4\46\1\161\2\46\1\161\5\46\1\161\1" +
                    "\166\1\46\1\u008e\1\uffff\1\46\2\161\1\46\1\uffff\2\46\1\173\1\46" +
                    "\1\uffff\1\173\1\46\2\161\3\46\1\161\2\46\3\161\1\46\1\166\1\46" +
                    "\1\u009d\1\46\1\uffff\1\46\1\u00a0\1\173\10\46\1\166\2\161\1\uffff" +
                    "\1\u00aa\1\46\1\uffff\1\173\2\46\1\173\4\46\1\173\1\uffff\7\46\1" +
                    "\u00b9\3\46\1\u00bd\2\46\1\uffff\2\173\1\u00c1\1\uffff\1\u00bd\2" +
                    "\46\1\uffff\1\46\1\u00c5\1\173\1\uffff";
    // $ANTLR end "T__41"
    static final String DFA14_eofS =
            "\u00c6\uffff";
    // $ANTLR end "T__42"
    static final String DFA14_minS =
            "\1\11\12\uffff\1\146\2\uffff\1\154\2\uffff\1\75\1\uffff\2\75\1" +
                    "\145\1\141\1\145\1\141\1\157\1\145\2\141\1\142\1\154\2\157\1\111" +
                    "\1\60\1\165\2\56\4\uffff\1\60\1\163\1\160\6\uffff\1\156\1\151\1" +
                    "\164\2\156\1\141\1\171\1\165\1\143\1\156\1\162\1\142\1\162\1\164" +
                    "\1\151\1\156\1\163\1\157\1\151\1\141\2\157\1\147\1\167\1\60\1\uffff" +
                    "\1\154\2\uffff\1\56\1\uffff\1\145\1\60\1\164\1\60\1\154\1\143\1" +
                    "\60\1\164\1\60\1\162\1\60\1\162\1\157\1\60\1\164\1\163\1\151\1\141" +
                    "\1\60\1\154\1\144\1\60\1\163\2\156\1\157\1\144\2\60\1\154\1\60\1" +
                    "\uffff\1\141\2\60\1\150\1\uffff\1\150\1\164\1\60\1\146\1\uffff\1" +
                    "\60\1\156\2\60\1\164\1\156\1\154\1\60\1\163\1\157\3\60\1\162\3\60" +
                    "\1\146\1\uffff\1\151\2\60\1\145\1\167\1\144\1\162\1\147\1\163\1" +
                    "\164\1\155\3\60\1\uffff\1\60\1\156\1\uffff\1\60\2\145\1\60\1\151" +
                    "\1\137\1\145\1\162\1\60\1\uffff\1\163\1\145\1\141\1\156\1\101\1" +
                    "\143\1\151\1\60\1\153\1\162\1\147\1\60\1\157\1\156\1\uffff\3\60" +
                    "\1\uffff\1\60\1\156\1\147\1\uffff\1\144\2\60\1\uffff";
    // $ANTLR end "T__43"
    static final String DFA14_maxS =
            "\1\175\12\uffff\1\146\2\uffff\1\170\2\uffff\1\75\1\uffff\2\75\2" +
                    "\157\1\145\1\141\1\157\1\165\1\157\1\141\1\164\1\155\2\157\1\111" +
                    "\1\172\1\165\1\56\1\71\4\uffff\1\172\1\163\1\160\6\uffff\1\163\1" +
                    "\151\1\170\2\156\1\141\1\171\1\165\1\143\1\156\1\162\1\142\1\162" +
                    "\1\164\2\156\1\163\1\157\1\151\1\141\2\157\1\147\1\167\1\172\1\uffff" +
                    "\1\154\2\uffff\1\71\1\uffff\1\145\1\172\1\164\1\172\1\154\1\143" +
                    "\1\172\1\164\1\172\1\162\1\172\1\162\1\157\1\172\1\164\1\163\1\151" +
                    "\1\141\1\172\1\154\1\144\1\172\1\163\2\156\1\157\1\144\2\172\1\154" +
                    "\1\172\1\uffff\1\141\2\172\1\150\1\uffff\1\150\1\164\1\172\1\146" +
                    "\1\uffff\1\172\1\156\2\172\1\164\1\156\1\154\1\172\1\163\1\157\3" +
                    "\172\1\162\1\172\1\60\1\172\1\146\1\uffff\1\151\2\172\1\145\1\171" +
                    "\1\144\1\162\1\147\1\163\1\164\1\155\3\172\1\uffff\1\172\1\156\1" +
                    "\uffff\1\172\2\145\1\172\1\151\1\137\1\145\1\162\1\172\1\uffff\1" +
                    "\163\1\145\1\141\1\156\1\172\1\143\1\151\1\172\1\153\1\162\1\147" +
                    "\1\172\1\157\1\156\1\uffff\3\172\1\uffff\1\172\1\156\1\147\1\uffff" +
                    "\1\144\2\172\1\uffff";
    // $ANTLR end "T__44"
    static final String DFA14_acceptS =
            "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\uffff\1\14" +
                    "\1\15\1\uffff\1\20\1\21\1\uffff\1\23\23\uffff\1\45\1\46\1\47\1\50" +
                    "\3\uffff\1\24\1\22\1\26\1\25\1\30\1\27\31\uffff\1\36\1\uffff\1\41" +
                    "\1\40\1\uffff\1\13\37\uffff\1\34\4\uffff\1\35\4\uffff\1\33\22\uffff" +
                    "\1\17\16\uffff\1\37\2\uffff\1\32\11\uffff\1\16\16\uffff\1\31\3\uffff" +
                    "\1\44\3\uffff\1\42\3\uffff\1\43";
    // $ANTLR end "T__45"
    static final String DFA14_specialS =
            "\u00c6\uffff}>";
    // $ANTLR end "F0"
    static final String[] DFA14_transitionS = {
            "\2\51\2\uffff\1\51\22\uffff\1\51\1\21\1\47\1\uffff\1\50\1\5" +
                    "\1\20\1\uffff\1\6\1\7\1\3\1\1\1\10\1\2\1\uffff\1\4\1\44\11\45" +
                    "\2\uffff\1\23\1\22\1\24\2\uffff\4\46\1\42\12\46\1\41\12\46\1" +
                    "\11\1\uffff\1\12\1\uffff\1\46\1\uffff\1\35\1\46\1\25\1\30\1" +
                    "\16\1\36\1\46\1\31\1\13\2\46\1\37\1\26\1\43\1\46\1\40\1\46\1" +
                    "\34\1\32\1\33\4\46\1\27\1\46\1\14\1\17\1\15",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\52",
            "",
            "",
            "\1\53\13\uffff\1\54",
            "",
            "",
            "\1\55",
            "",
            "\1\57",
            "\1\61",
            "\1\64\11\uffff\1\63",
            "\1\65\7\uffff\1\67\5\uffff\1\66",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73\3\uffff\1\74\7\uffff\1\75\2\uffff\1\77\1\76",
            "\1\101\15\uffff\1\100",
            "\1\102",
            "\1\103\1\104\17\uffff\1\105\1\106",
            "\1\107\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\115",
            "\1\117",
            "\1\117\1\uffff\12\120",
            "",
            "",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\122",
            "\1\123",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\124\4\uffff\1\125",
            "\1\126",
            "\1\127\3\uffff\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\145\4\uffff\1\144",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
            "\1\155",
            "\1\156",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\157",
            "",
            "",
            "\1\117\1\uffff\12\120",
            "",
            "\1\160",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\162",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\7\46\1\163\22\46",
            "\1\164",
            "\1\165",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\167",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\24\46\1\170\5\46",
            "\1\171",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\16\46\1\172\13\46",
            "\1\174",
            "\1\175",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\7\46\1\176\22\46",
            "\1\177",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\7\46\1\u0083\22" +
                    "\46",
            "\1\u0084",
            "\1\u0085",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0086",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\46\1\u008b\10\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32" +
                    "\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u008c",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\10\46\1\u008d\21" +
                    "\46",
            "",
            "\1\u008f",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0090",
            "",
            "\1\u0091",
            "\1\u0092",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0093",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0094",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0095",
            "\1\u0096",
            "\1\u0097",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0098",
            "\1\u0099",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\2\46\1\u009a\7\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u009b",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u009c",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u009e",
            "",
            "\1\u009f",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u00a1",
            "\1\u00a2\1\uffff\1\u00a3",
            "\1\u00a4",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u00ab",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u00ac",
            "\1\u00ad",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u00ae",
            "\1\u00af",
            "\1\u00b0",
            "\1\u00b1",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\1\u00b5",
            "\32\u00b6\4\uffff\1\u00b6\1\uffff\32\u00b6",
            "\1\u00b7",
            "\1\u00b8",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u00ba",
            "\1\u00bb",
            "\1\u00bc",
            "\12\u00be\7\uffff\32\u00be\4\uffff\1\u00be\1\uffff\32\u00be",
            "\1\u00bf",
            "\1\u00c0",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\12\u00be\7\uffff\32\u00be\4\uffff\1\u00be\1\uffff\32\u00be",
            "\1\u00c2",
            "\1\u00c3",
            "",
            "\1\u00c4",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            ""
    };
    // $ANTLR end "F1"
    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    // $ANTLR end "F2"
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    // $ANTLR end "CONSTANT"
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    // $ANTLR end "NULL"
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    // $ANTLR end "DOUBLE"
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    // $ANTLR end "INTEGER"
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    // $ANTLR end "SUBSTRING"
    static final short[][] DFA14_transition;
    // $ANTLR end "TAILSTRING"

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }
    // $ANTLR end "STRINGID"

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }
    // $ANTLR end "ID"

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }
    // $ANTLR end "STRINGVALUE"

    protected DFA1 dfa1 = new DFA1(this);
    // $ANTLR end "NEWLINE"
    protected DFA2 dfa2 = new DFA2(this);
    // $ANTLR end "WS"
    protected DFA14 dfa14 = new DFA14(this);


    public AdvancedExpressionLexer() {
        ;
    }
    public AdvancedExpressionLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public AdvancedExpressionLexer(CharStream input, RecognizerSharedState state) {
        super(input, state);

    }

    public Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException {
        throw new MismatchedTokenException(ttype, input);
    }

    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException {
        throw e;
    }

    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        throw new RuntimeException(hdr + ":" + msg);
    }

    public String getGrammarFileName() {
        return "AdvancedExpression.g";
    }

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:29:7: ( '+' )
            // AdvancedExpression.g:29:9: '+'
            {
                match('+');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:30:7: ( '-' )
            // AdvancedExpression.g:30:9: '-'
            {
                match('-');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:31:7: ( '*' )
            // AdvancedExpression.g:31:9: '*'
            {
                match('*');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:32:7: ( '/' )
            // AdvancedExpression.g:32:9: '/'
            {
                match('/');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:33:7: ( '%' )
            // AdvancedExpression.g:33:9: '%'
            {
                match('%');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:34:7: ( '(' )
            // AdvancedExpression.g:34:9: '('
            {
                match('(');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:35:7: ( ')' )
            // AdvancedExpression.g:35:9: ')'
            {
                match(')');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:36:7: ( ',' )
            // AdvancedExpression.g:36:9: ','
            {
                match(',');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:37:7: ( '[' )
            // AdvancedExpression.g:37:9: '['
            {
                match('[');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:38:7: ( ']' )
            // AdvancedExpression.g:38:9: ']'
            {
                match(']');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:39:7: ( 'if' )
            // AdvancedExpression.g:39:9: 'if'
            {
                match("if");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:40:7: ( '{' )
            // AdvancedExpression.g:40:9: '{'
            {
                match('{');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:41:7: ( '}' )
            // AdvancedExpression.g:41:9: '}'
            {
                match('}');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:42:7: ( 'elseif' )
            // AdvancedExpression.g:42:9: 'elseif'
            {
                match("elseif");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:43:7: ( 'else' )
            // AdvancedExpression.g:43:9: 'else'
            {
                match("else");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:44:7: ( '||' )
            // AdvancedExpression.g:44:9: '||'
            {
                match("||");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:45:7: ( '&&' )
            // AdvancedExpression.g:45:9: '&&'
            {
                match("&&");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:46:7: ( '!' )
            // AdvancedExpression.g:46:9: '!'
            {
                match('!');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:47:7: ( '==' )
            // AdvancedExpression.g:47:9: '=='
            {
                match("==");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:48:7: ( '!=' )
            // AdvancedExpression.g:48:9: '!='
            {
                match("!=");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:49:7: ( '<' )
            // AdvancedExpression.g:49:9: '<'
            {
                match('<');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:50:7: ( '<=' )
            // AdvancedExpression.g:50:9: '<='
            {
                match("<=");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:51:7: ( '>' )
            // AdvancedExpression.g:51:9: '>'
            {
                match('>');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:52:7: ( '>=' )
            // AdvancedExpression.g:52:9: '>='
            {
                match(">=");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:53:7: ( 'contains' )
            // AdvancedExpression.g:53:9: 'contains'
            {
                match("contains");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:54:7: ( 'match' )
            // AdvancedExpression.g:54:9: 'match'
            {
                match("match");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "F0"
    public final void mF0() throws RecognitionException {
        try {
            int _type = F0;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:160:4: ( ( 'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'dayofweek' | 'dayofyear' | 'totalsecond' | 'random' ) )
            // AdvancedExpression.g:160:6: ( 'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'dayofweek' | 'dayofyear' | 'totalsecond' | 'random' )
            {
                // AdvancedExpression.g:160:6: ( 'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'dayofweek' | 'dayofyear' | 'totalsecond' | 'random' )
                int alt1 = 10;
                alt1 = dfa1.predict(input);
                switch (alt1) {
                    case 1:
                        // AdvancedExpression.g:160:7: 'year'
                    {
                        match("year");


                    }
                    break;
                    case 2:
                        // AdvancedExpression.g:160:14: 'month'
                    {
                        match("month");


                    }
                    break;
                    case 3:
                        // AdvancedExpression.g:160:22: 'day'
                    {
                        match("day");


                    }
                    break;
                    case 4:
                        // AdvancedExpression.g:160:28: 'hour'
                    {
                        match("hour");


                    }
                    break;
                    case 5:
                        // AdvancedExpression.g:160:35: 'minute'
                    {
                        match("minute");


                    }
                    break;
                    case 6:
                        // AdvancedExpression.g:160:44: 'second'
                    {
                        match("second");


                    }
                    break;
                    case 7:
                        // AdvancedExpression.g:160:53: 'dayofweek'
                    {
                        match("dayofweek");


                    }
                    break;
                    case 8:
                        // AdvancedExpression.g:160:65: 'dayofyear'
                    {
                        match("dayofyear");


                    }
                    break;
                    case 9:
                        // AdvancedExpression.g:160:77: 'totalsecond'
                    {
                        match("totalsecond");


                    }
                    break;
                    case 10:
                        // AdvancedExpression.g:160:91: 'random'
                    {
                        match("random");


                    }
                    break;

                }


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "F1"
    public final void mF1() throws RecognitionException {
        try {
            int _type = F1;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:161:4: ( ( 'abs' | 'acos' | 'asin' | 'atan' | 'ceil' | 'cos' | 'cosh' | 'exp' | 'floor' | 'log' | 'log10' | 'sin' | 'sinh' | 'sqrt' | 'tan' | 'tanh' ) )
            // AdvancedExpression.g:161:6: ( 'abs' | 'acos' | 'asin' | 'atan' | 'ceil' | 'cos' | 'cosh' | 'exp' | 'floor' | 'log' | 'log10' | 'sin' | 'sinh' | 'sqrt' | 'tan' | 'tanh' )
            {
                // AdvancedExpression.g:161:6: ( 'abs' | 'acos' | 'asin' | 'atan' | 'ceil' | 'cos' | 'cosh' | 'exp' | 'floor' | 'log' | 'log10' | 'sin' | 'sinh' | 'sqrt' | 'tan' | 'tanh' )
                int alt2 = 16;
                alt2 = dfa2.predict(input);
                switch (alt2) {
                    case 1:
                        // AdvancedExpression.g:161:7: 'abs'
                    {
                        match("abs");


                    }
                    break;
                    case 2:
                        // AdvancedExpression.g:161:13: 'acos'
                    {
                        match("acos");


                    }
                    break;
                    case 3:
                        // AdvancedExpression.g:161:20: 'asin'
                    {
                        match("asin");


                    }
                    break;
                    case 4:
                        // AdvancedExpression.g:161:27: 'atan'
                    {
                        match("atan");


                    }
                    break;
                    case 5:
                        // AdvancedExpression.g:161:34: 'ceil'
                    {
                        match("ceil");


                    }
                    break;
                    case 6:
                        // AdvancedExpression.g:161:41: 'cos'
                    {
                        match("cos");


                    }
                    break;
                    case 7:
                        // AdvancedExpression.g:161:47: 'cosh'
                    {
                        match("cosh");


                    }
                    break;
                    case 8:
                        // AdvancedExpression.g:161:54: 'exp'
                    {
                        match("exp");


                    }
                    break;
                    case 9:
                        // AdvancedExpression.g:161:60: 'floor'
                    {
                        match("floor");


                    }
                    break;
                    case 10:
                        // AdvancedExpression.g:161:68: 'log'
                    {
                        match("log");


                    }
                    break;
                    case 11:
                        // AdvancedExpression.g:161:74: 'log10'
                    {
                        match("log10");


                    }
                    break;
                    case 12:
                        // AdvancedExpression.g:161:82: 'sin'
                    {
                        match("sin");


                    }
                    break;
                    case 13:
                        // AdvancedExpression.g:161:88: 'sinh'
                    {
                        match("sinh");


                    }
                    break;
                    case 14:
                        // AdvancedExpression.g:161:95: 'sqrt'
                    {
                        match("sqrt");


                    }
                    break;
                    case 15:
                        // AdvancedExpression.g:161:102: 'tan'
                    {
                        match("tan");


                    }
                    break;
                    case 16:
                        // AdvancedExpression.g:161:108: 'tanh'
                    {
                        match("tanh");


                    }
                    break;

                }


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "F2"
    public final void mF2() throws RecognitionException {
        try {
            int _type = F2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:162:4: ( ( 'atan2' | 'fmod' | 'pow' | 'max' | 'min' ) )
            // AdvancedExpression.g:162:6: ( 'atan2' | 'fmod' | 'pow' | 'max' | 'min' )
            {
                // AdvancedExpression.g:162:6: ( 'atan2' | 'fmod' | 'pow' | 'max' | 'min' )
                int alt3 = 5;
                switch (input.LA(1)) {
                    case 'a': {
                        alt3 = 1;
                    }
                    break;
                    case 'f': {
                        alt3 = 2;
                    }
                    break;
                    case 'p': {
                        alt3 = 3;
                    }
                    break;
                    case 'm': {
                        int LA3_4 = input.LA(2);

                        if ((LA3_4 == 'a')) {
                            alt3 = 4;
                        } else if ((LA3_4 == 'i')) {
                            alt3 = 5;
                        } else {
                            NoViableAltException nvae =
                                    new NoViableAltException("", 3, 4, input);

                            throw nvae;
                        }
                    }
                    break;
                    default:
                        NoViableAltException nvae =
                                new NoViableAltException("", 3, 0, input);

                        throw nvae;
                }

                switch (alt3) {
                    case 1:
                        // AdvancedExpression.g:162:7: 'atan2'
                    {
                        match("atan2");


                    }
                    break;
                    case 2:
                        // AdvancedExpression.g:162:15: 'fmod'
                    {
                        match("fmod");


                    }
                    break;
                    case 3:
                        // AdvancedExpression.g:162:22: 'pow'
                    {
                        match("pow");


                    }
                    break;
                    case 4:
                        // AdvancedExpression.g:162:28: 'max'
                    {
                        match("max");


                    }
                    break;
                    case 5:
                        // AdvancedExpression.g:162:34: 'min'
                    {
                        match("min");


                    }
                    break;

                }


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "CONSTANT"
    public final void mCONSTANT() throws RecognitionException {
        try {
            int _type = CONSTANT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:163:10: ( ( 'PI' | 'E' ) )
            // AdvancedExpression.g:163:12: ( 'PI' | 'E' )
            {
                // AdvancedExpression.g:163:12: ( 'PI' | 'E' )
                int alt4 = 2;
                int LA4_0 = input.LA(1);

                if ((LA4_0 == 'P')) {
                    alt4 = 1;
                } else if ((LA4_0 == 'E')) {
                    alt4 = 2;
                } else {
                    NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                    throw nvae;
                }
                switch (alt4) {
                    case 1:
                        // AdvancedExpression.g:163:13: 'PI'
                    {
                        match("PI");


                    }
                    break;
                    case 2:
                        // AdvancedExpression.g:163:18: 'E'
                    {
                        match('E');

                    }
                    break;

                }


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:164:6: ( 'null' )
            // AdvancedExpression.g:164:8: 'null'
            {
                match("null");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "DOUBLE"
    public final void mDOUBLE() throws RecognitionException {
        try {
            int _type = DOUBLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:165:8: ( ( '0' | ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) ) ) ( '.' ( ( '0' .. '9' )* ) ) )
            // AdvancedExpression.g:165:10: ( '0' | ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) ) ) ( '.' ( ( '0' .. '9' )* ) )
            {
                // AdvancedExpression.g:165:10: ( '0' | ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) ) )
                int alt6 = 2;
                int LA6_0 = input.LA(1);

                if ((LA6_0 == '0')) {
                    alt6 = 1;
                } else if (((LA6_0 >= '1' && LA6_0 <= '9'))) {
                    alt6 = 2;
                } else {
                    NoViableAltException nvae =
                            new NoViableAltException("", 6, 0, input);

                    throw nvae;
                }
                switch (alt6) {
                    case 1:
                        // AdvancedExpression.g:165:11: '0'
                    {
                        match('0');

                    }
                    break;
                    case 2:
                        // AdvancedExpression.g:165:15: ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) )
                    {
                        // AdvancedExpression.g:165:15: ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) )
                        // AdvancedExpression.g:165:16: ( '1' .. '9' ) ( ( '0' .. '9' )* )
                        {
                            // AdvancedExpression.g:165:16: ( '1' .. '9' )
                            // AdvancedExpression.g:165:17: '1' .. '9'
                            {
                                matchRange('1', '9');

                            }

                            // AdvancedExpression.g:165:26: ( ( '0' .. '9' )* )
                            // AdvancedExpression.g:165:27: ( '0' .. '9' )*
                            {
                                // AdvancedExpression.g:165:27: ( '0' .. '9' )*
                                loop5:
                                do {
                                    int alt5 = 2;
                                    int LA5_0 = input.LA(1);

                                    if (((LA5_0 >= '0' && LA5_0 <= '9'))) {
                                        alt5 = 1;
                                    }


                                    switch (alt5) {
                                        case 1:
                                            // AdvancedExpression.g:165:27: '0' .. '9'
                                        {
                                            matchRange('0', '9');

                                        }
                                        break;

                                        default:
                                            break loop5;
                                    }
                                } while (true);


                            }


                        }


                    }
                    break;

                }

                // AdvancedExpression.g:165:39: ( '.' ( ( '0' .. '9' )* ) )
                // AdvancedExpression.g:165:40: '.' ( ( '0' .. '9' )* )
                {
                    match('.');
                    // AdvancedExpression.g:165:43: ( ( '0' .. '9' )* )
                    // AdvancedExpression.g:165:44: ( '0' .. '9' )*
                    {
                        // AdvancedExpression.g:165:44: ( '0' .. '9' )*
                        loop7:
                        do {
                            int alt7 = 2;
                            int LA7_0 = input.LA(1);

                            if (((LA7_0 >= '0' && LA7_0 <= '9'))) {
                                alt7 = 1;
                            }


                            switch (alt7) {
                                case 1:
                                    // AdvancedExpression.g:165:44: '0' .. '9'
                                {
                                    matchRange('0', '9');

                                }
                                break;

                                default:
                                    break loop7;
                            }
                        } while (true);


                    }


                }


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "INTEGER"
    public final void mINTEGER() throws RecognitionException {
        try {
            int _type = INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:166:9: ( ( '0' | ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) ) ) )
            // AdvancedExpression.g:166:11: ( '0' | ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) ) )
            {
                // AdvancedExpression.g:166:11: ( '0' | ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) ) )
                int alt9 = 2;
                int LA9_0 = input.LA(1);

                if ((LA9_0 == '0')) {
                    alt9 = 1;
                } else if (((LA9_0 >= '1' && LA9_0 <= '9'))) {
                    alt9 = 2;
                } else {
                    NoViableAltException nvae =
                            new NoViableAltException("", 9, 0, input);

                    throw nvae;
                }
                switch (alt9) {
                    case 1:
                        // AdvancedExpression.g:166:12: '0'
                    {
                        match('0');

                    }
                    break;
                    case 2:
                        // AdvancedExpression.g:166:16: ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) )
                    {
                        // AdvancedExpression.g:166:16: ( ( '1' .. '9' ) ( ( '0' .. '9' )* ) )
                        // AdvancedExpression.g:166:17: ( '1' .. '9' ) ( ( '0' .. '9' )* )
                        {
                            // AdvancedExpression.g:166:17: ( '1' .. '9' )
                            // AdvancedExpression.g:166:18: '1' .. '9'
                            {
                                matchRange('1', '9');

                            }

                            // AdvancedExpression.g:166:27: ( ( '0' .. '9' )* )
                            // AdvancedExpression.g:166:28: ( '0' .. '9' )*
                            {
                                // AdvancedExpression.g:166:28: ( '0' .. '9' )*
                                loop8:
                                do {
                                    int alt8 = 2;
                                    int LA8_0 = input.LA(1);

                                    if (((LA8_0 >= '0' && LA8_0 <= '9'))) {
                                        alt8 = 1;
                                    }


                                    switch (alt8) {
                                        case 1:
                                            // AdvancedExpression.g:166:28: '0' .. '9'
                                        {
                                            matchRange('0', '9');

                                        }
                                        break;

                                        default:
                                            break loop8;
                                    }
                                } while (true);


                            }


                        }


                    }
                    break;

                }


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "SUBSTRING"
    public final void mSUBSTRING() throws RecognitionException {
        try {
            int _type = SUBSTRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:167:11: ( ( 's' ) ( 'u' ) ( 'b' ) ( 's' ) ( 't' ) ( 'r' ) ( 'i' ) ( 'n' ) ( 'g' ) )
            // AdvancedExpression.g:167:13: ( 's' ) ( 'u' ) ( 'b' ) ( 's' ) ( 't' ) ( 'r' ) ( 'i' ) ( 'n' ) ( 'g' )
            {
                // AdvancedExpression.g:167:13: ( 's' )
                // AdvancedExpression.g:167:14: 's'
                {
                    match('s');

                }

                // AdvancedExpression.g:167:18: ( 'u' )
                // AdvancedExpression.g:167:19: 'u'
                {
                    match('u');

                }

                // AdvancedExpression.g:167:23: ( 'b' )
                // AdvancedExpression.g:167:24: 'b'
                {
                    match('b');

                }

                // AdvancedExpression.g:167:28: ( 's' )
                // AdvancedExpression.g:167:29: 's'
                {
                    match('s');

                }

                // AdvancedExpression.g:167:33: ( 't' )
                // AdvancedExpression.g:167:34: 't'
                {
                    match('t');

                }

                // AdvancedExpression.g:167:38: ( 'r' )
                // AdvancedExpression.g:167:39: 'r'
                {
                    match('r');

                }

                // AdvancedExpression.g:167:43: ( 'i' )
                // AdvancedExpression.g:167:44: 'i'
                {
                    match('i');

                }

                // AdvancedExpression.g:167:48: ( 'n' )
                // AdvancedExpression.g:167:49: 'n'
                {
                    match('n');

                }

                // AdvancedExpression.g:167:53: ( 'g' )
                // AdvancedExpression.g:167:54: 'g'
                {
                    match('g');

                }


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "TAILSTRING"
    public final void mTAILSTRING() throws RecognitionException {
        try {
            int _type = TAILSTRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:168:12: ( ( 't' ) ( 'a' ) ( 'i' ) ( 'l' ) ( 's' ) ( 't' ) ( 'r' ) ( 'i' ) ( 'n' ) ( 'g' ) )
            // AdvancedExpression.g:168:14: ( 't' ) ( 'a' ) ( 'i' ) ( 'l' ) ( 's' ) ( 't' ) ( 'r' ) ( 'i' ) ( 'n' ) ( 'g' )
            {
                // AdvancedExpression.g:168:14: ( 't' )
                // AdvancedExpression.g:168:15: 't'
                {
                    match('t');

                }

                // AdvancedExpression.g:168:19: ( 'a' )
                // AdvancedExpression.g:168:20: 'a'
                {
                    match('a');

                }

                // AdvancedExpression.g:168:24: ( 'i' )
                // AdvancedExpression.g:168:25: 'i'
                {
                    match('i');

                }

                // AdvancedExpression.g:168:29: ( 'l' )
                // AdvancedExpression.g:168:30: 'l'
                {
                    match('l');

                }

                // AdvancedExpression.g:168:34: ( 's' )
                // AdvancedExpression.g:168:35: 's'
                {
                    match('s');

                }

                // AdvancedExpression.g:168:39: ( 't' )
                // AdvancedExpression.g:168:40: 't'
                {
                    match('t');

                }

                // AdvancedExpression.g:168:44: ( 'r' )
                // AdvancedExpression.g:168:45: 'r'
                {
                    match('r');

                }

                // AdvancedExpression.g:168:49: ( 'i' )
                // AdvancedExpression.g:168:50: 'i'
                {
                    match('i');

                }

                // AdvancedExpression.g:168:54: ( 'n' )
                // AdvancedExpression.g:168:55: 'n'
                {
                    match('n');

                }

                // AdvancedExpression.g:168:59: ( 'g' )
                // AdvancedExpression.g:168:60: 'g'
                {
                    match('g');

                }


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "STRINGID"
    public final void mSTRINGID() throws RecognitionException {
        try {
            int _type = STRINGID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:169:10: ( ( 's' ) ( 't' ) ( 'r' ) ( 'i' ) ( 'n' ) ( 'g' ) ( '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // AdvancedExpression.g:169:12: ( 's' ) ( 't' ) ( 'r' ) ( 'i' ) ( 'n' ) ( 'g' ) ( '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
                // AdvancedExpression.g:169:12: ( 's' )
                // AdvancedExpression.g:169:13: 's'
                {
                    match('s');

                }

                // AdvancedExpression.g:169:17: ( 't' )
                // AdvancedExpression.g:169:18: 't'
                {
                    match('t');

                }

                // AdvancedExpression.g:169:22: ( 'r' )
                // AdvancedExpression.g:169:23: 'r'
                {
                    match('r');

                }

                // AdvancedExpression.g:169:27: ( 'i' )
                // AdvancedExpression.g:169:28: 'i'
                {
                    match('i');

                }

                // AdvancedExpression.g:169:32: ( 'n' )
                // AdvancedExpression.g:169:33: 'n'
                {
                    match('n');

                }

                // AdvancedExpression.g:169:37: ( 'g' )
                // AdvancedExpression.g:169:38: 'g'
                {
                    match('g');

                }

                // AdvancedExpression.g:169:42: ( '_' )
                // AdvancedExpression.g:169:43: '_'
                {
                    match('_');

                }

                if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }

                // AdvancedExpression.g:169:70: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
                loop10:
                do {
                    int alt10 = 2;
                    int LA10_0 = input.LA(1);

                    if (((LA10_0 >= '0' && LA10_0 <= '9') || (LA10_0 >= 'A' && LA10_0 <= 'Z') || LA10_0 == '_' || (LA10_0 >= 'a' && LA10_0 <= 'z'))) {
                        alt10 = 1;
                    }


                    switch (alt10) {
                        case 1:
                            // AdvancedExpression.g:
                        {
                            if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                                input.consume();

                            } else {
                                MismatchedSetException mse = new MismatchedSetException(null, input);
                                recover(mse);
                                throw mse;
                            }


                        }
                        break;

                        default:
                            break loop10;
                    }
                } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:170:4: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // AdvancedExpression.g:170:6: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
                if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }

                // AdvancedExpression.g:170:29: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
                loop11:
                do {
                    int alt11 = 2;
                    int LA11_0 = input.LA(1);

                    if (((LA11_0 >= '0' && LA11_0 <= '9') || (LA11_0 >= 'A' && LA11_0 <= 'Z') || LA11_0 == '_' || (LA11_0 >= 'a' && LA11_0 <= 'z'))) {
                        alt11 = 1;
                    }


                    switch (alt11) {
                        case 1:
                            // AdvancedExpression.g:
                        {
                            if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                                input.consume();

                            } else {
                                MismatchedSetException mse = new MismatchedSetException(null, input);
                                recover(mse);
                                throw mse;
                            }


                        }
                        break;

                        default:
                            break loop11;
                    }
                } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "STRINGVALUE"
    public final void mSTRINGVALUE() throws RecognitionException {
        try {
            int _type = STRINGVALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:171:13: ( '\"' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '-' | '0' .. '9' )* '\"' )
            // AdvancedExpression.g:171:15: '\"' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '-' | '0' .. '9' )* '\"'
            {
                match('\"');
                // AdvancedExpression.g:171:19: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '-' | '0' .. '9' )*
                loop12:
                do {
                    int alt12 = 2;
                    int LA12_0 = input.LA(1);

                    if ((LA12_0 == '-' || (LA12_0 >= '0' && LA12_0 <= '9') || (LA12_0 >= 'A' && LA12_0 <= 'Z') || LA12_0 == '_' || (LA12_0 >= 'a' && LA12_0 <= 'z'))) {
                        alt12 = 1;
                    }


                    switch (alt12) {
                        case 1:
                            // AdvancedExpression.g:
                        {
                            if (input.LA(1) == '-' || (input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                                input.consume();

                            } else {
                                MismatchedSetException mse = new MismatchedSetException(null, input);
                                recover(mse);
                                throw mse;
                            }


                        }
                        break;

                        default:
                            break loop12;
                    }
                } while (true);

                match('\"');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:172:9: ( '$' )
            // AdvancedExpression.g:172:11: '$'
            {
                match('$');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // AdvancedExpression.g:173:4: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // AdvancedExpression.g:173:6: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
                // AdvancedExpression.g:173:6: ( ' ' | '\\t' | '\\r' | '\\n' )+
                int cnt13 = 0;
                loop13:
                do {
                    int alt13 = 2;
                    int LA13_0 = input.LA(1);

                    if (((LA13_0 >= '\t' && LA13_0 <= '\n') || LA13_0 == '\r' || LA13_0 == ' ')) {
                        alt13 = 1;
                    }


                    switch (alt13) {
                        case 1:
                            // AdvancedExpression.g:
                        {
                            if ((input.LA(1) >= '\t' && input.LA(1) <= '\n') || input.LA(1) == '\r' || input.LA(1) == ' ') {
                                input.consume();

                            } else {
                                MismatchedSetException mse = new MismatchedSetException(null, input);
                                recover(mse);
                                throw mse;
                            }


                        }
                        break;

                        default:
                            if (cnt13 >= 1) break loop13;
                            EarlyExitException eee =
                                    new EarlyExitException(13, input);
                            throw eee;
                    }
                    cnt13++;
                } while (true);

                _channel = HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }

    public void mTokens() throws RecognitionException {
        // AdvancedExpression.g:1:8: ( T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | F0 | F1 | F2 | CONSTANT | NULL | DOUBLE | INTEGER | SUBSTRING | TAILSTRING | STRINGID | ID | STRINGVALUE | NEWLINE | WS )
        int alt14 = 40;
        alt14 = dfa14.predict(input);
        switch (alt14) {
            case 1:
                // AdvancedExpression.g:1:10: T__20
            {
                mT__20();

            }
            break;
            case 2:
                // AdvancedExpression.g:1:16: T__21
            {
                mT__21();

            }
            break;
            case 3:
                // AdvancedExpression.g:1:22: T__22
            {
                mT__22();

            }
            break;
            case 4:
                // AdvancedExpression.g:1:28: T__23
            {
                mT__23();

            }
            break;
            case 5:
                // AdvancedExpression.g:1:34: T__24
            {
                mT__24();

            }
            break;
            case 6:
                // AdvancedExpression.g:1:40: T__25
            {
                mT__25();

            }
            break;
            case 7:
                // AdvancedExpression.g:1:46: T__26
            {
                mT__26();

            }
            break;
            case 8:
                // AdvancedExpression.g:1:52: T__27
            {
                mT__27();

            }
            break;
            case 9:
                // AdvancedExpression.g:1:58: T__28
            {
                mT__28();

            }
            break;
            case 10:
                // AdvancedExpression.g:1:64: T__29
            {
                mT__29();

            }
            break;
            case 11:
                // AdvancedExpression.g:1:70: T__30
            {
                mT__30();

            }
            break;
            case 12:
                // AdvancedExpression.g:1:76: T__31
            {
                mT__31();

            }
            break;
            case 13:
                // AdvancedExpression.g:1:82: T__32
            {
                mT__32();

            }
            break;
            case 14:
                // AdvancedExpression.g:1:88: T__33
            {
                mT__33();

            }
            break;
            case 15:
                // AdvancedExpression.g:1:94: T__34
            {
                mT__34();

            }
            break;
            case 16:
                // AdvancedExpression.g:1:100: T__35
            {
                mT__35();

            }
            break;
            case 17:
                // AdvancedExpression.g:1:106: T__36
            {
                mT__36();

            }
            break;
            case 18:
                // AdvancedExpression.g:1:112: T__37
            {
                mT__37();

            }
            break;
            case 19:
                // AdvancedExpression.g:1:118: T__38
            {
                mT__38();

            }
            break;
            case 20:
                // AdvancedExpression.g:1:124: T__39
            {
                mT__39();

            }
            break;
            case 21:
                // AdvancedExpression.g:1:130: T__40
            {
                mT__40();

            }
            break;
            case 22:
                // AdvancedExpression.g:1:136: T__41
            {
                mT__41();

            }
            break;
            case 23:
                // AdvancedExpression.g:1:142: T__42
            {
                mT__42();

            }
            break;
            case 24:
                // AdvancedExpression.g:1:148: T__43
            {
                mT__43();

            }
            break;
            case 25:
                // AdvancedExpression.g:1:154: T__44
            {
                mT__44();

            }
            break;
            case 26:
                // AdvancedExpression.g:1:160: T__45
            {
                mT__45();

            }
            break;
            case 27:
                // AdvancedExpression.g:1:166: F0
            {
                mF0();

            }
            break;
            case 28:
                // AdvancedExpression.g:1:169: F1
            {
                mF1();

            }
            break;
            case 29:
                // AdvancedExpression.g:1:172: F2
            {
                mF2();

            }
            break;
            case 30:
                // AdvancedExpression.g:1:175: CONSTANT
            {
                mCONSTANT();

            }
            break;
            case 31:
                // AdvancedExpression.g:1:184: NULL
            {
                mNULL();

            }
            break;
            case 32:
                // AdvancedExpression.g:1:189: DOUBLE
            {
                mDOUBLE();

            }
            break;
            case 33:
                // AdvancedExpression.g:1:196: INTEGER
            {
                mINTEGER();

            }
            break;
            case 34:
                // AdvancedExpression.g:1:204: SUBSTRING
            {
                mSUBSTRING();

            }
            break;
            case 35:
                // AdvancedExpression.g:1:214: TAILSTRING
            {
                mTAILSTRING();

            }
            break;
            case 36:
                // AdvancedExpression.g:1:225: STRINGID
            {
                mSTRINGID();

            }
            break;
            case 37:
                // AdvancedExpression.g:1:234: ID
            {
                mID();

            }
            break;
            case 38:
                // AdvancedExpression.g:1:237: STRINGVALUE
            {
                mSTRINGVALUE();

            }
            break;
            case 39:
                // AdvancedExpression.g:1:249: NEWLINE
            {
                mNEWLINE();

            }
            break;
            case 40:
                // AdvancedExpression.g:1:257: WS
            {
                mWS();

            }
            break;

        }

    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }

        public String getDescription() {
            return "160:6: ( 'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'dayofweek' | 'dayofyear' | 'totalsecond' | 'random' )";
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }

        public String getDescription() {
            return "161:6: ( 'abs' | 'acos' | 'asin' | 'atan' | 'ceil' | 'cos' | 'cosh' | 'exp' | 'floor' | 'log' | 'log10' | 'sin' | 'sinh' | 'sqrt' | 'tan' | 'tanh' )";
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }

        public String getDescription() {
            return "1:1: Tokens : ( T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | F0 | F1 | F2 | CONSTANT | NULL | DOUBLE | INTEGER | SUBSTRING | TAILSTRING | STRINGID | ID | STRINGVALUE | NEWLINE | WS );";
        }
    }


}