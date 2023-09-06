// $ANTLR 3.1 AdvancedExpressionWalker.g 2021-07-30 15:32:47

package com.hdwa.sdk.expression;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;

import java.util.HashMap;
import java.util.Map;

public class AdvancedExpressionWalker extends TreeParser {
    public static final String[] tokenNames = new String[]{
            "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IFWRAPPER", "STRINGWRAPPER", "NEWLINE", "NULL", "DOUBLE", "INTEGER", "CONSTANT", "ID", "SUBSTRING", "TAILSTRING", "STRINGVALUE", "STRINGID", "F0", "F1", "F2", "WS", "'+'", "'-'", "'*'", "'/'", "'%'", "'('", "')'", "','", "'['", "']'", "'if'", "'{'", "'}'", "'elseif'", "'else'", "'||'", "'&&'", "'!'", "'=='", "'!='", "'<'", "'<='", "'>'", "'>='", "'contains'", "'match'"
    };
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

    // delegates
    // delegators
    public static final BitSet FOLLOW_expr_in_prog64 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fourexpr_in_expr86 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_expr95 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRINGWRAPPER_in_expr104 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_expr108 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ifcondition_in_expr119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_fourexpr138 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr142 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr146 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_21_in_fourexpr155 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr159 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr163 = new BitSet(new long[]{0x0000000000000008L});
    // $ANTLR end "prog"
    public static final BitSet FOLLOW_22_in_fourexpr172 = new BitSet(new long[]{0x0000000000000004L});
    // $ANTLR end "expr"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr176 = new BitSet(new long[]{0x0000000001F70F10L});
    // $ANTLR end "fourexpr"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr180 = new BitSet(new long[]{0x0000000000000008L});
    // $ANTLR end "string_fourexpr"
    public static final BitSet FOLLOW_23_in_fourexpr189 = new BitSet(new long[]{0x0000000000000004L});
    // $ANTLR end "ifcondition"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr193 = new BitSet(new long[]{0x0000000001F70F10L});
    // $ANTLR end "elseifcondition"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr197 = new BitSet(new long[]{0x0000000000000008L});
    // $ANTLR end "condition"
    public static final BitSet FOLLOW_24_in_fourexpr206 = new BitSet(new long[]{0x0000000000000004L});
    // $ANTLR end "comparecondition"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr210 = new BitSet(new long[]{0x0000000001F70F10L});
    // $ANTLR end "string_comparecondition"

    // Delegated rules
    public static final BitSet FOLLOW_fourexpr_in_fourexpr214 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_fourexpr222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_F0_in_fourexpr229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_F1_in_fourexpr237 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr241 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_F2_in_fourexpr250 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr254 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr258 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONSTANT_in_fourexpr266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_fourexpr273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_fourexpr281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IFWRAPPER_in_fourexpr290 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ifcondition_in_fourexpr294 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_20_in_string_fourexpr313 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_fourexpr317 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_fourexpr321 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUBSTRING_in_string_fourexpr330 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_fourexpr334 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_string_fourexpr338 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_string_fourexpr342 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TAILSTRING_in_string_fourexpr351 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_fourexpr355 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_string_fourexpr359 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRINGID_in_string_fourexpr367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRINGVALUE_in_string_fourexpr374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IFWRAPPER_in_string_fourexpr383 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ifcondition_in_string_fourexpr387 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_30_in_ifcondition407 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_ifcondition411 = new BitSet(new long[]{0x0000000041F70FB0L});
    public static final BitSet FOLLOW_expr_in_ifcondition415 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_elseifcondition_in_ifcondition419 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_33_in_elseifcondition438 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_elseifcondition442 = new BitSet(new long[]{0x0000000041F70FB0L});
    public static final BitSet FOLLOW_expr_in_elseifcondition446 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_elseifcondition_in_elseifcondition450 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_34_in_elseifcondition459 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expr_in_elseifcondition463 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_36_in_condition482 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition486 = new BitSet(new long[]{0x00000FF800000020L});
    public static final BitSet FOLLOW_condition_in_condition490 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_35_in_condition499 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition503 = new BitSet(new long[]{0x00000FF800000020L});
    public static final BitSet FOLLOW_condition_in_condition507 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_37_in_condition516 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition520 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_comparecondition_in_condition532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_comparecondition551 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NULL_in_comparecondition553 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition557 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_39_in_comparecondition566 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NULL_in_comparecondition568 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition572 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_40_in_comparecondition581 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition585 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition589 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_41_in_comparecondition598 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition602 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition606 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_42_in_comparecondition615 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition619 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition623 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_43_in_comparecondition632 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition636 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition640 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_comparecondition649 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition653 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition657 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_39_in_comparecondition666 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition670 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition674 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRINGWRAPPER_in_comparecondition683 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_comparecondition_in_comparecondition687 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_string_comparecondition706 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NULL_in_string_comparecondition708 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition712 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_39_in_string_comparecondition721 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NULL_in_string_comparecondition723 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition727 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_40_in_string_comparecondition736 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition740 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition744 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_41_in_string_comparecondition753 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition757 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition761 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_42_in_string_comparecondition770 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition774 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition778 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_43_in_string_comparecondition787 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition791 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition795 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_string_comparecondition804 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition808 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition812 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_39_in_string_comparecondition821 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition825 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition829 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_44_in_string_comparecondition838 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition842 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition846 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_45_in_string_comparecondition855 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition859 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition863 = new BitSet(new long[]{0x0000000000000008L});
    static final String DFA6_eotS =
            "\16\uffff";
    static final String DFA6_eofS =
            "\16\uffff";
    static final String DFA6_minS =
            "\1\5\2\2\5\uffff\2\4\4\uffff";
    static final String DFA6_maxS =
            "\1\53\2\2\5\uffff\2\30\4\uffff";
    static final String DFA6_acceptS =
            "\3\uffff\1\3\1\4\1\5\1\6\1\11\2\uffff\1\1\1\7\1\2\1\10";
    static final String DFA6_specialS =
            "\16\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\7\40\uffff\1\1\1\2\1\3\1\4\1\5\1\6",
            "\1\10",
            "\1\11",
            "",
            "",
            "",
            "",
            "",
            "\1\13\2\uffff\1\12\4\13\4\uffff\3\13\1\uffff\5\13",
            "\1\15\2\uffff\1\14\4\15\4\uffff\3\15\1\uffff\5\15",
            "",
            "",
            "",
            ""
    };
    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;
    static final String DFA7_eotS =
            "\17\uffff";
    static final String DFA7_eofS =
            "\17\uffff";
    static final String DFA7_minS =
            "\1\46\2\2\6\uffff\2\4\4\uffff";
    static final String DFA7_maxS =
            "\1\55\2\2\6\uffff\2\24\4\uffff";
    static final String DFA7_acceptS =
            "\3\uffff\1\3\1\4\1\5\1\6\1\11\1\12\2\uffff\1\1\1\7\1\2\1\10";
    static final String DFA7_specialS =
            "\17\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10",
            "\1\11",
            "\1\12",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\14\2\uffff\1\13\4\uffff\4\14\4\uffff\1\14",
            "\1\16\2\uffff\1\15\4\uffff\4\16\4\uffff\1\16",
            "",
            "",
            "",
            ""
    };
    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    protected DFA6 dfa6 = new DFA6(this);
    protected DFA7 dfa7 = new DFA7(this);
    private Map<String, ValueObject> varValueMap = new HashMap<String, ValueObject>();
    private Map<String, ValueObject> varStringValueMap = new HashMap<String, ValueObject>();
    public AdvancedExpressionWalker(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public AdvancedExpressionWalker(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);

    }

    public String[] getTokenNames() {
        return AdvancedExpressionWalker.tokenNames;
    }

    public String getGrammarFileName() {
        return "AdvancedExpressionWalker.g";
    }

    public void clear() {
        this.varValueMap.clear();
        this.varStringValueMap.clear();
    }

    public void put(String var, Double value) {
        this.varValueMap.put(var, new ValueObject(1, value));
    }

    public void put(String var, Long value) {
        this.varValueMap.put(var, new ValueObject(0, value));
    }

    public void put_null(String var) {
        this.varValueMap.put(var, new ValueObject(null));
    }

    public void putString(String var, String value) {
        this.varStringValueMap.put(var, new ValueObject(value));
    }

    // $ANTLR start "prog"
    // AdvancedExpressionWalker.g:43:1: prog returns [ValueObject value] : (a= expr ) ;
    public final ValueObject prog() throws RecognitionException {
        ValueObject value = null;

        ValueObject a = null;


        try {
            // AdvancedExpressionWalker.g:44:2: ( (a= expr ) )
            // AdvancedExpressionWalker.g:44:4: (a= expr )
            {
                // AdvancedExpressionWalker.g:44:4: (a= expr )
                // AdvancedExpressionWalker.g:44:5: a= expr
                {
                    pushFollow(FOLLOW_expr_in_prog64);
                    a = expr();

                    state._fsp--;


                }

                value = a;

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return value;
    }

    // $ANTLR start "expr"
    // AdvancedExpressionWalker.g:47:1: expr returns [ValueObject value] : ( (a= fourexpr ) | ( NULL ) | ^( STRINGWRAPPER a= string_fourexpr ) | (a= ifcondition ) );
    public final ValueObject expr() throws RecognitionException {
        ValueObject value = null;

        ValueObject a = null;


        try {
            // AdvancedExpressionWalker.g:48:2: ( (a= fourexpr ) | ( NULL ) | ^( STRINGWRAPPER a= string_fourexpr ) | (a= ifcondition ) )
            int alt1 = 4;
            switch (input.LA(1)) {
                case IFWRAPPER:
                case DOUBLE:
                case INTEGER:
                case CONSTANT:
                case ID:
                case F0:
                case F1:
                case F2:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24: {
                    alt1 = 1;
                }
                break;
                case NULL: {
                    alt1 = 2;
                }
                break;
                case STRINGWRAPPER: {
                    alt1 = 3;
                }
                break;
                case 30: {
                    alt1 = 4;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 1, 0, input);

                    throw nvae;
            }

            switch (alt1) {
                case 1:
                    // AdvancedExpressionWalker.g:48:4: (a= fourexpr )
                {
                    // AdvancedExpressionWalker.g:48:4: (a= fourexpr )
                    // AdvancedExpressionWalker.g:48:5: a= fourexpr
                    {
                        pushFollow(FOLLOW_fourexpr_in_expr86);
                        a = fourexpr();

                        state._fsp--;


                    }

                    value = a;

                }
                break;
                case 2:
                    // AdvancedExpressionWalker.g:49:4: ( NULL )
                {
                    // AdvancedExpressionWalker.g:49:4: ( NULL )
                    // AdvancedExpressionWalker.g:49:5: NULL
                    {
                        match(input, NULL, FOLLOW_NULL_in_expr95);

                    }

                    value = new ValueObject(null);

                }
                break;
                case 3:
                    // AdvancedExpressionWalker.g:50:4: ^( STRINGWRAPPER a= string_fourexpr )
                {
                    match(input, STRINGWRAPPER, FOLLOW_STRINGWRAPPER_in_expr104);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_expr108);
                    a = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = a;

                }
                break;
                case 4:
                    // AdvancedExpressionWalker.g:51:4: (a= ifcondition )
                {
                    // AdvancedExpressionWalker.g:51:4: (a= ifcondition )
                    // AdvancedExpressionWalker.g:51:5: a= ifcondition
                    {
                        pushFollow(FOLLOW_ifcondition_in_expr119);
                        a = ifcondition();

                        state._fsp--;


                    }

                    value = a;

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return value;
    }

    // $ANTLR start "fourexpr"
    // AdvancedExpressionWalker.g:54:1: fourexpr returns [ValueObject value] : ( ^( '+' a= fourexpr b= fourexpr ) | ^( '-' a= fourexpr b= fourexpr ) | ^( '*' a= fourexpr b= fourexpr ) | ^( '/' a= fourexpr b= fourexpr ) | ^( '%' a= fourexpr b= fourexpr ) | ID | F0 | ^( F1 a= fourexpr ) | ^( F2 a= fourexpr b= fourexpr ) | CONSTANT | DOUBLE | INTEGER | ^( IFWRAPPER a= ifcondition ) );
    public final ValueObject fourexpr() throws RecognitionException {
        ValueObject value = null;

        CommonTree ID1 = null;
        CommonTree F02 = null;
        CommonTree F13 = null;
        CommonTree F24 = null;
        CommonTree CONSTANT5 = null;
        CommonTree DOUBLE6 = null;
        CommonTree INTEGER7 = null;
        ValueObject a = null;

        ValueObject b = null;


        try {
            // AdvancedExpressionWalker.g:55:2: ( ^( '+' a= fourexpr b= fourexpr ) | ^( '-' a= fourexpr b= fourexpr ) | ^( '*' a= fourexpr b= fourexpr ) | ^( '/' a= fourexpr b= fourexpr ) | ^( '%' a= fourexpr b= fourexpr ) | ID | F0 | ^( F1 a= fourexpr ) | ^( F2 a= fourexpr b= fourexpr ) | CONSTANT | DOUBLE | INTEGER | ^( IFWRAPPER a= ifcondition ) )
            int alt2 = 13;
            switch (input.LA(1)) {
                case 20: {
                    alt2 = 1;
                }
                break;
                case 21: {
                    alt2 = 2;
                }
                break;
                case 22: {
                    alt2 = 3;
                }
                break;
                case 23: {
                    alt2 = 4;
                }
                break;
                case 24: {
                    alt2 = 5;
                }
                break;
                case ID: {
                    alt2 = 6;
                }
                break;
                case F0: {
                    alt2 = 7;
                }
                break;
                case F1: {
                    alt2 = 8;
                }
                break;
                case F2: {
                    alt2 = 9;
                }
                break;
                case CONSTANT: {
                    alt2 = 10;
                }
                break;
                case DOUBLE: {
                    alt2 = 11;
                }
                break;
                case INTEGER: {
                    alt2 = 12;
                }
                break;
                case IFWRAPPER: {
                    alt2 = 13;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 2, 0, input);

                    throw nvae;
            }

            switch (alt2) {
                case 1:
                    // AdvancedExpressionWalker.g:55:4: ^( '+' a= fourexpr b= fourexpr )
                {
                    match(input, 20, FOLLOW_20_in_fourexpr138);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr142);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr146);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compute("+", a, b);

                }
                break;
                case 2:
                    // AdvancedExpressionWalker.g:56:4: ^( '-' a= fourexpr b= fourexpr )
                {
                    match(input, 21, FOLLOW_21_in_fourexpr155);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr159);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr163);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compute("-", a, b);

                }
                break;
                case 3:
                    // AdvancedExpressionWalker.g:57:4: ^( '*' a= fourexpr b= fourexpr )
                {
                    match(input, 22, FOLLOW_22_in_fourexpr172);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr176);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr180);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compute("*", a, b);

                }
                break;
                case 4:
                    // AdvancedExpressionWalker.g:58:4: ^( '/' a= fourexpr b= fourexpr )
                {
                    match(input, 23, FOLLOW_23_in_fourexpr189);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr193);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr197);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compute("/", a, b);

                }
                break;
                case 5:
                    // AdvancedExpressionWalker.g:59:4: ^( '%' a= fourexpr b= fourexpr )
                {
                    match(input, 24, FOLLOW_24_in_fourexpr206);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr210);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr214);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compute("%", a, b);

                }
                break;
                case 6:
                    // AdvancedExpressionWalker.g:60:4: ID
                {
                    ID1 = (CommonTree) match(input, ID, FOLLOW_ID_in_fourexpr222);
                    value = this.varValueMap.get((ID1 != null ? ID1.getText() : null));

                }
                break;
                case 7:
                    // AdvancedExpressionWalker.g:61:4: F0
                {
                    F02 = (CommonTree) match(input, F0, FOLLOW_F0_in_fourexpr229);
                    value = FunctionUtil.compute((F02 != null ? F02.getText() : null));

                }
                break;
                case 8:
                    // AdvancedExpressionWalker.g:62:4: ^( F1 a= fourexpr )
                {
                    F13 = (CommonTree) match(input, F1, FOLLOW_F1_in_fourexpr237);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr241);
                    a = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = FunctionUtil.compute((F13 != null ? F13.getText() : null), a);

                }
                break;
                case 9:
                    // AdvancedExpressionWalker.g:63:4: ^( F2 a= fourexpr b= fourexpr )
                {
                    F24 = (CommonTree) match(input, F2, FOLLOW_F2_in_fourexpr250);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr254);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr258);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = FunctionUtil.compute((F24 != null ? F24.getText() : null), a, b);

                }
                break;
                case 10:
                    // AdvancedExpressionWalker.g:64:4: CONSTANT
                {
                    CONSTANT5 = (CommonTree) match(input, CONSTANT, FOLLOW_CONSTANT_in_fourexpr266);
                    value = FunctionUtil.constant((CONSTANT5 != null ? CONSTANT5.getText() : null));

                }
                break;
                case 11:
                    // AdvancedExpressionWalker.g:65:4: DOUBLE
                {
                    DOUBLE6 = (CommonTree) match(input, DOUBLE, FOLLOW_DOUBLE_in_fourexpr273);
                    value = new ValueObject(1, Double.parseDouble((DOUBLE6 != null ? DOUBLE6.getText() : null)));

                }
                break;
                case 12:
                    // AdvancedExpressionWalker.g:66:4: INTEGER
                {
                    INTEGER7 = (CommonTree) match(input, INTEGER, FOLLOW_INTEGER_in_fourexpr281);
                    value = new ValueObject(0, Long.parseLong((INTEGER7 != null ? INTEGER7.getText() : null)));

                }
                break;
                case 13:
                    // AdvancedExpressionWalker.g:67:4: ^( IFWRAPPER a= ifcondition )
                {
                    match(input, IFWRAPPER, FOLLOW_IFWRAPPER_in_fourexpr290);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_ifcondition_in_fourexpr294);
                    a = ifcondition();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = a;

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return value;
    }

    // $ANTLR start "string_fourexpr"
    // AdvancedExpressionWalker.g:70:1: string_fourexpr returns [ValueObject value] : ( ^( '+' a= string_fourexpr b= string_fourexpr ) | ^( SUBSTRING a= string_fourexpr b= fourexpr c= fourexpr ) | ^( TAILSTRING a= string_fourexpr b= fourexpr ) | STRINGID | STRINGVALUE | ^( IFWRAPPER a= ifcondition ) );
    public final ValueObject string_fourexpr() throws RecognitionException {
        ValueObject value = null;

        CommonTree STRINGID8 = null;
        CommonTree STRINGVALUE9 = null;
        ValueObject a = null;

        ValueObject b = null;

        ValueObject c = null;


        try {
            // AdvancedExpressionWalker.g:71:2: ( ^( '+' a= string_fourexpr b= string_fourexpr ) | ^( SUBSTRING a= string_fourexpr b= fourexpr c= fourexpr ) | ^( TAILSTRING a= string_fourexpr b= fourexpr ) | STRINGID | STRINGVALUE | ^( IFWRAPPER a= ifcondition ) )
            int alt3 = 6;
            switch (input.LA(1)) {
                case 20: {
                    alt3 = 1;
                }
                break;
                case SUBSTRING: {
                    alt3 = 2;
                }
                break;
                case TAILSTRING: {
                    alt3 = 3;
                }
                break;
                case STRINGID: {
                    alt3 = 4;
                }
                break;
                case STRINGVALUE: {
                    alt3 = 5;
                }
                break;
                case IFWRAPPER: {
                    alt3 = 6;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 3, 0, input);

                    throw nvae;
            }

            switch (alt3) {
                case 1:
                    // AdvancedExpressionWalker.g:71:4: ^( '+' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 20, FOLLOW_20_in_string_fourexpr313);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_fourexpr317);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_fourexpr321);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = new ValueObject(a.stringValue + b.stringValue);

                }
                break;
                case 2:
                    // AdvancedExpressionWalker.g:72:4: ^( SUBSTRING a= string_fourexpr b= fourexpr c= fourexpr )
                {
                    match(input, SUBSTRING, FOLLOW_SUBSTRING_in_string_fourexpr330);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_fourexpr334);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_string_fourexpr338);
                    b = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_string_fourexpr342);
                    c = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = new ValueObject(a.stringValue.substring(b.intValue.intValue(), c.intValue.intValue()));

                }
                break;
                case 3:
                    // AdvancedExpressionWalker.g:73:4: ^( TAILSTRING a= string_fourexpr b= fourexpr )
                {
                    match(input, TAILSTRING, FOLLOW_TAILSTRING_in_string_fourexpr351);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_fourexpr355);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_string_fourexpr359);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = new ValueObject(a.stringValue.substring(b.intValue.intValue()));

                }
                break;
                case 4:
                    // AdvancedExpressionWalker.g:74:4: STRINGID
                {
                    STRINGID8 = (CommonTree) match(input, STRINGID, FOLLOW_STRINGID_in_string_fourexpr367);
                    value = this.varStringValueMap.get((STRINGID8 != null ? STRINGID8.getText() : null));

                }
                break;
                case 5:
                    // AdvancedExpressionWalker.g:75:4: STRINGVALUE
                {
                    STRINGVALUE9 = (CommonTree) match(input, STRINGVALUE, FOLLOW_STRINGVALUE_in_string_fourexpr374);
                    value = new ValueObject((STRINGVALUE9 != null ? STRINGVALUE9.getText() : null).substring(1, (STRINGVALUE9 != null ? STRINGVALUE9.getText() : null).length() - 1));

                }
                break;
                case 6:
                    // AdvancedExpressionWalker.g:76:4: ^( IFWRAPPER a= ifcondition )
                {
                    match(input, IFWRAPPER, FOLLOW_IFWRAPPER_in_string_fourexpr383);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_ifcondition_in_string_fourexpr387);
                    a = ifcondition();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = a;

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return value;
    }

    // $ANTLR start "ifcondition"
    // AdvancedExpressionWalker.g:79:1: ifcondition returns [ValueObject value] : ^( 'if' con= condition a= expr b= elseifcondition ) ;
    public final ValueObject ifcondition() throws RecognitionException {
        ValueObject value = null;

        boolean con = false;

        ValueObject a = null;

        ValueObject b = null;


        try {
            // AdvancedExpressionWalker.g:80:2: ( ^( 'if' con= condition a= expr b= elseifcondition ) )
            // AdvancedExpressionWalker.g:80:4: ^( 'if' con= condition a= expr b= elseifcondition )
            {
                match(input, 30, FOLLOW_30_in_ifcondition407);

                match(input, Token.DOWN, null);
                pushFollow(FOLLOW_condition_in_ifcondition411);
                con = condition();

                state._fsp--;

                pushFollow(FOLLOW_expr_in_ifcondition415);
                a = expr();

                state._fsp--;

                pushFollow(FOLLOW_elseifcondition_in_ifcondition419);
                b = elseifcondition();

                state._fsp--;


                match(input, Token.UP, null);
                if (con) value = a;
                else value = b;

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return value;
    }

    // $ANTLR start "elseifcondition"
    // AdvancedExpressionWalker.g:83:1: elseifcondition returns [ValueObject value] : ( ^( 'elseif' con= condition a= expr b= elseifcondition ) | ^( 'else' a= expr ) );
    public final ValueObject elseifcondition() throws RecognitionException {
        ValueObject value = null;

        boolean con = false;

        ValueObject a = null;

        ValueObject b = null;


        try {
            // AdvancedExpressionWalker.g:84:2: ( ^( 'elseif' con= condition a= expr b= elseifcondition ) | ^( 'else' a= expr ) )
            int alt4 = 2;
            int LA4_0 = input.LA(1);

            if ((LA4_0 == 33)) {
                alt4 = 1;
            } else if ((LA4_0 == 34)) {
                alt4 = 2;
            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1:
                    // AdvancedExpressionWalker.g:84:4: ^( 'elseif' con= condition a= expr b= elseifcondition )
                {
                    match(input, 33, FOLLOW_33_in_elseifcondition438);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_condition_in_elseifcondition442);
                    con = condition();

                    state._fsp--;

                    pushFollow(FOLLOW_expr_in_elseifcondition446);
                    a = expr();

                    state._fsp--;

                    pushFollow(FOLLOW_elseifcondition_in_elseifcondition450);
                    b = elseifcondition();

                    state._fsp--;


                    match(input, Token.UP, null);
                    if (con) value = a;
                    else value = b;

                }
                break;
                case 2:
                    // AdvancedExpressionWalker.g:85:4: ^( 'else' a= expr )
                {
                    match(input, 34, FOLLOW_34_in_elseifcondition459);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_expr_in_elseifcondition463);
                    a = expr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = a;

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return value;
    }

    // $ANTLR start "condition"
    // AdvancedExpressionWalker.g:88:1: condition returns [boolean value] : ( ^( '&&' a= condition b= condition ) | ^( '||' a= condition b= condition ) | ^( '!' a= condition ) | (a= comparecondition ) );
    public final boolean condition() throws RecognitionException {
        boolean value = false;

        boolean a = false;

        boolean b = false;


        try {
            // AdvancedExpressionWalker.g:89:2: ( ^( '&&' a= condition b= condition ) | ^( '||' a= condition b= condition ) | ^( '!' a= condition ) | (a= comparecondition ) )
            int alt5 = 4;
            switch (input.LA(1)) {
                case 36: {
                    alt5 = 1;
                }
                break;
                case 35: {
                    alt5 = 2;
                }
                break;
                case 37: {
                    alt5 = 3;
                }
                break;
                case STRINGWRAPPER:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43: {
                    alt5 = 4;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 5, 0, input);

                    throw nvae;
            }

            switch (alt5) {
                case 1:
                    // AdvancedExpressionWalker.g:89:4: ^( '&&' a= condition b= condition )
                {
                    match(input, 36, FOLLOW_36_in_condition482);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_condition_in_condition486);
                    a = condition();

                    state._fsp--;

                    pushFollow(FOLLOW_condition_in_condition490);
                    b = condition();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = a && b;

                }
                break;
                case 2:
                    // AdvancedExpressionWalker.g:90:4: ^( '||' a= condition b= condition )
                {
                    match(input, 35, FOLLOW_35_in_condition499);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_condition_in_condition503);
                    a = condition();

                    state._fsp--;

                    pushFollow(FOLLOW_condition_in_condition507);
                    b = condition();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = a || b;

                }
                break;
                case 3:
                    // AdvancedExpressionWalker.g:91:4: ^( '!' a= condition )
                {
                    match(input, 37, FOLLOW_37_in_condition516);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_condition_in_condition520);
                    a = condition();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = !a;

                }
                break;
                case 4:
                    // AdvancedExpressionWalker.g:92:4: (a= comparecondition )
                {
                    // AdvancedExpressionWalker.g:92:4: (a= comparecondition )
                    // AdvancedExpressionWalker.g:92:5: a= comparecondition
                    {
                        pushFollow(FOLLOW_comparecondition_in_condition532);
                        a = comparecondition();

                        state._fsp--;


                    }

                    value = a;

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return value;
    }

    // $ANTLR start "comparecondition"
    // AdvancedExpressionWalker.g:95:1: comparecondition returns [boolean value] : ( ^( '==' NULL b= fourexpr ) | ^( '!=' NULL b= fourexpr ) | ^( '<' a= fourexpr b= fourexpr ) | ^( '<=' a= fourexpr b= fourexpr ) | ^( '>' a= fourexpr b= fourexpr ) | ^( '>=' a= fourexpr b= fourexpr ) | ^( '==' a= fourexpr b= fourexpr ) | ^( '!=' a= fourexpr b= fourexpr ) | ^( STRINGWRAPPER c= string_comparecondition ) );
    public final boolean comparecondition() throws RecognitionException {
        boolean value = false;

        ValueObject b = null;

        ValueObject a = null;

        boolean c = false;


        try {
            // AdvancedExpressionWalker.g:96:2: ( ^( '==' NULL b= fourexpr ) | ^( '!=' NULL b= fourexpr ) | ^( '<' a= fourexpr b= fourexpr ) | ^( '<=' a= fourexpr b= fourexpr ) | ^( '>' a= fourexpr b= fourexpr ) | ^( '>=' a= fourexpr b= fourexpr ) | ^( '==' a= fourexpr b= fourexpr ) | ^( '!=' a= fourexpr b= fourexpr ) | ^( STRINGWRAPPER c= string_comparecondition ) )
            int alt6 = 9;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1:
                    // AdvancedExpressionWalker.g:96:4: ^( '==' NULL b= fourexpr )
                {
                    match(input, 38, FOLLOW_38_in_comparecondition551);

                    match(input, Token.DOWN, null);
                    match(input, NULL, FOLLOW_NULL_in_comparecondition553);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition557);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compare("==", b);

                }
                break;
                case 2:
                    // AdvancedExpressionWalker.g:97:4: ^( '!=' NULL b= fourexpr )
                {
                    match(input, 39, FOLLOW_39_in_comparecondition566);

                    match(input, Token.DOWN, null);
                    match(input, NULL, FOLLOW_NULL_in_comparecondition568);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition572);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compare("!=", b);

                }
                break;
                case 3:
                    // AdvancedExpressionWalker.g:98:4: ^( '<' a= fourexpr b= fourexpr )
                {
                    match(input, 40, FOLLOW_40_in_comparecondition581);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition585);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition589);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compare("<", a, b);

                }
                break;
                case 4:
                    // AdvancedExpressionWalker.g:99:4: ^( '<=' a= fourexpr b= fourexpr )
                {
                    match(input, 41, FOLLOW_41_in_comparecondition598);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition602);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition606);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compare("<=", a, b);

                }
                break;
                case 5:
                    // AdvancedExpressionWalker.g:100:4: ^( '>' a= fourexpr b= fourexpr )
                {
                    match(input, 42, FOLLOW_42_in_comparecondition615);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition619);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition623);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compare(">", a, b);

                }
                break;
                case 6:
                    // AdvancedExpressionWalker.g:101:4: ^( '>=' a= fourexpr b= fourexpr )
                {
                    match(input, 43, FOLLOW_43_in_comparecondition632);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition636);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition640);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compare(">=", a, b);

                }
                break;
                case 7:
                    // AdvancedExpressionWalker.g:102:4: ^( '==' a= fourexpr b= fourexpr )
                {
                    match(input, 38, FOLLOW_38_in_comparecondition649);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition653);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition657);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compare("==", a, b);

                }
                break;
                case 8:
                    // AdvancedExpressionWalker.g:103:4: ^( '!=' a= fourexpr b= fourexpr )
                {
                    match(input, 39, FOLLOW_39_in_comparecondition666);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition670);
                    a = fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition674);
                    b = fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.compare("!=", a, b);

                }
                break;
                case 9:
                    // AdvancedExpressionWalker.g:104:4: ^( STRINGWRAPPER c= string_comparecondition )
                {
                    match(input, STRINGWRAPPER, FOLLOW_STRINGWRAPPER_in_comparecondition683);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_comparecondition_in_comparecondition687);
                    c = string_comparecondition();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = c;

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return value;
    }

    // $ANTLR start "string_comparecondition"
    // AdvancedExpressionWalker.g:107:1: string_comparecondition returns [boolean value] : ( ^( '==' NULL b= string_fourexpr ) | ^( '!=' NULL b= string_fourexpr ) | ^( '<' a= string_fourexpr b= string_fourexpr ) | ^( '<=' a= string_fourexpr b= string_fourexpr ) | ^( '>' a= string_fourexpr b= string_fourexpr ) | ^( '>=' a= string_fourexpr b= string_fourexpr ) | ^( '==' a= string_fourexpr b= string_fourexpr ) | ^( '!=' a= string_fourexpr b= string_fourexpr ) | ^( 'contains' a= string_fourexpr b= string_fourexpr ) | ^( 'match' a= string_fourexpr b= string_fourexpr ) );
    public final boolean string_comparecondition() throws RecognitionException {
        boolean value = false;

        ValueObject b = null;

        ValueObject a = null;


        try {
            // AdvancedExpressionWalker.g:108:2: ( ^( '==' NULL b= string_fourexpr ) | ^( '!=' NULL b= string_fourexpr ) | ^( '<' a= string_fourexpr b= string_fourexpr ) | ^( '<=' a= string_fourexpr b= string_fourexpr ) | ^( '>' a= string_fourexpr b= string_fourexpr ) | ^( '>=' a= string_fourexpr b= string_fourexpr ) | ^( '==' a= string_fourexpr b= string_fourexpr ) | ^( '!=' a= string_fourexpr b= string_fourexpr ) | ^( 'contains' a= string_fourexpr b= string_fourexpr ) | ^( 'match' a= string_fourexpr b= string_fourexpr ) )
            int alt7 = 10;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1:
                    // AdvancedExpressionWalker.g:108:4: ^( '==' NULL b= string_fourexpr )
                {
                    match(input, 38, FOLLOW_38_in_string_comparecondition706);

                    match(input, Token.DOWN, null);
                    match(input, NULL, FOLLOW_NULL_in_string_comparecondition708);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition712);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare("==", b);

                }
                break;
                case 2:
                    // AdvancedExpressionWalker.g:109:4: ^( '!=' NULL b= string_fourexpr )
                {
                    match(input, 39, FOLLOW_39_in_string_comparecondition721);

                    match(input, Token.DOWN, null);
                    match(input, NULL, FOLLOW_NULL_in_string_comparecondition723);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition727);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare("!=", b);

                }
                break;
                case 3:
                    // AdvancedExpressionWalker.g:110:4: ^( '<' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 40, FOLLOW_40_in_string_comparecondition736);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition740);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition744);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare("<", a, b);

                }
                break;
                case 4:
                    // AdvancedExpressionWalker.g:111:4: ^( '<=' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 41, FOLLOW_41_in_string_comparecondition753);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition757);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition761);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare("<=", a, b);

                }
                break;
                case 5:
                    // AdvancedExpressionWalker.g:112:4: ^( '>' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 42, FOLLOW_42_in_string_comparecondition770);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition774);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition778);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare(">", a, b);

                }
                break;
                case 6:
                    // AdvancedExpressionWalker.g:113:4: ^( '>=' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 43, FOLLOW_43_in_string_comparecondition787);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition791);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition795);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare(">=", a, b);

                }
                break;
                case 7:
                    // AdvancedExpressionWalker.g:114:4: ^( '==' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 38, FOLLOW_38_in_string_comparecondition804);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition808);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition812);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare("==", a, b);

                }
                break;
                case 8:
                    // AdvancedExpressionWalker.g:115:4: ^( '!=' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 39, FOLLOW_39_in_string_comparecondition821);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition825);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition829);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare("!=", a, b);

                }
                break;
                case 9:
                    // AdvancedExpressionWalker.g:116:4: ^( 'contains' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 44, FOLLOW_44_in_string_comparecondition838);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition842);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition846);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare("contains", a, b);

                }
                break;
                case 10:
                    // AdvancedExpressionWalker.g:117:4: ^( 'match' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 45, FOLLOW_45_in_string_comparecondition855);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition859);
                    a = string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition863);
                    b = string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);
                    value = ValueObjectUtil.stringcompare("match", a, b);

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return value;
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }

        public String getDescription() {
            return "95:1: comparecondition returns [boolean value] : ( ^( '==' NULL b= fourexpr ) | ^( '!=' NULL b= fourexpr ) | ^( '<' a= fourexpr b= fourexpr ) | ^( '<=' a= fourexpr b= fourexpr ) | ^( '>' a= fourexpr b= fourexpr ) | ^( '>=' a= fourexpr b= fourexpr ) | ^( '==' a= fourexpr b= fourexpr ) | ^( '!=' a= fourexpr b= fourexpr ) | ^( STRINGWRAPPER c= string_comparecondition ) );";
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }

        public String getDescription() {
            return "107:1: string_comparecondition returns [boolean value] : ( ^( '==' NULL b= string_fourexpr ) | ^( '!=' NULL b= string_fourexpr ) | ^( '<' a= string_fourexpr b= string_fourexpr ) | ^( '<=' a= string_fourexpr b= string_fourexpr ) | ^( '>' a= string_fourexpr b= string_fourexpr ) | ^( '>=' a= string_fourexpr b= string_fourexpr ) | ^( '==' a= string_fourexpr b= string_fourexpr ) | ^( '!=' a= string_fourexpr b= string_fourexpr ) | ^( 'contains' a= string_fourexpr b= string_fourexpr ) | ^( 'match' a= string_fourexpr b= string_fourexpr ) );";
        }
    }

}