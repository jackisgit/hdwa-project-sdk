// $ANTLR 3.1 AdvancedExpressionScanner.g 2021-07-30 15:32:47

package com.hdwa.sdk.entity.expression;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;

import java.util.HashMap;
import java.util.Map;

public class AdvancedExpressionScanner extends TreeParser {
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
    public static final BitSet FOLLOW_expr_in_prog60 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fourexpr_in_expr76 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_expr83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRINGWRAPPER_in_expr90 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_expr94 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ifcondition_in_expr103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_fourexpr116 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr120 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr124 = new BitSet(new long[]{0x0000000000000008L});
    // $ANTLR end "prog"
    public static final BitSet FOLLOW_21_in_fourexpr131 = new BitSet(new long[]{0x0000000000000004L});
    // $ANTLR end "expr"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr135 = new BitSet(new long[]{0x0000000001F70F10L});
    // $ANTLR end "fourexpr"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr139 = new BitSet(new long[]{0x0000000000000008L});
    // $ANTLR end "string_fourexpr"
    public static final BitSet FOLLOW_22_in_fourexpr146 = new BitSet(new long[]{0x0000000000000004L});
    // $ANTLR end "ifcondition"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr150 = new BitSet(new long[]{0x0000000001F70F10L});
    // $ANTLR end "elseifcondition"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr154 = new BitSet(new long[]{0x0000000000000008L});
    // $ANTLR end "condition"
    public static final BitSet FOLLOW_23_in_fourexpr161 = new BitSet(new long[]{0x0000000000000004L});
    // $ANTLR end "comparecondition"
    public static final BitSet FOLLOW_fourexpr_in_fourexpr165 = new BitSet(new long[]{0x0000000001F70F10L});
    // $ANTLR end "string_comparecondition"

    // Delegated rules
    public static final BitSet FOLLOW_fourexpr_in_fourexpr169 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_24_in_fourexpr176 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr180 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr184 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_fourexpr190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_F0_in_fourexpr197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_F1_in_fourexpr203 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr207 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_F2_in_fourexpr214 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr218 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_fourexpr222 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONSTANT_in_fourexpr228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_fourexpr233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_fourexpr238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IFWRAPPER_in_fourexpr244 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ifcondition_in_fourexpr248 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_20_in_string_fourexpr261 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_fourexpr265 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_fourexpr269 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUBSTRING_in_string_fourexpr276 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_fourexpr280 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_string_fourexpr284 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_string_fourexpr288 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TAILSTRING_in_string_fourexpr295 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_fourexpr299 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_string_fourexpr303 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRINGID_in_string_fourexpr309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRINGVALUE_in_string_fourexpr316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IFWRAPPER_in_string_fourexpr322 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ifcondition_in_string_fourexpr326 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_30_in_ifcondition340 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_ifcondition344 = new BitSet(new long[]{0x0000000041F70FB0L});
    public static final BitSet FOLLOW_expr_in_ifcondition348 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_elseifcondition_in_ifcondition352 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_33_in_elseifcondition365 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_elseifcondition369 = new BitSet(new long[]{0x0000000041F70FB0L});
    public static final BitSet FOLLOW_expr_in_elseifcondition373 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_elseifcondition_in_elseifcondition377 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_34_in_elseifcondition384 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expr_in_elseifcondition388 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_36_in_condition401 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition405 = new BitSet(new long[]{0x00000FF800000020L});
    public static final BitSet FOLLOW_condition_in_condition409 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_35_in_condition416 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition420 = new BitSet(new long[]{0x00000FF800000020L});
    public static final BitSet FOLLOW_condition_in_condition424 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_37_in_condition431 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition435 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_comparecondition_in_condition444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_comparecondition457 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NULL_in_comparecondition459 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition463 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_39_in_comparecondition470 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NULL_in_comparecondition472 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition476 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_40_in_comparecondition483 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition487 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition491 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_41_in_comparecondition498 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition502 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition506 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_42_in_comparecondition513 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition517 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition521 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_43_in_comparecondition528 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition532 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition536 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_comparecondition543 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition547 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition551 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_39_in_comparecondition558 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition562 = new BitSet(new long[]{0x0000000001F70F10L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition566 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRINGWRAPPER_in_comparecondition573 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_comparecondition_in_comparecondition577 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_string_comparecondition590 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NULL_in_string_comparecondition592 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition596 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_39_in_string_comparecondition603 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NULL_in_string_comparecondition605 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition609 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_40_in_string_comparecondition616 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition620 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition624 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_41_in_string_comparecondition631 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition635 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition639 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_42_in_string_comparecondition646 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition650 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition654 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_43_in_string_comparecondition661 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition665 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition669 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_38_in_string_comparecondition676 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition680 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition684 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_39_in_string_comparecondition691 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition695 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition699 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_44_in_string_comparecondition706 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition710 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition714 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_45_in_string_comparecondition721 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition725 = new BitSet(new long[]{0x000000000010F010L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition729 = new BitSet(new long[]{0x0000000000000008L});
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

    public Map<String, Boolean> varDict = new HashMap<String, Boolean>();
    public Map<String, Boolean> varStringDict = new HashMap<String, Boolean>();
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA7 dfa7 = new DFA7(this);

    public AdvancedExpressionScanner(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }

    public AdvancedExpressionScanner(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);

    }

    public String[] getTokenNames() {
        return AdvancedExpressionScanner.tokenNames;
    }

    public String getGrammarFileName() {
        return "AdvancedExpressionScanner.g";
    }

    private void AddVar(String var) {
        if (!this.varDict.containsKey(var)) {
            this.varDict.put(var, true);
        }
    }

    private void AddStringVar(String var) {
        if (!this.varStringDict.containsKey(var)) {
            this.varStringDict.put(var, true);
        }
    }

    // $ANTLR start "prog"
    // AdvancedExpressionScanner.g:37:1: prog : (a= expr ) ;
    public final void prog() throws RecognitionException {
        try {
            // AdvancedExpressionScanner.g:38:2: ( (a= expr ) )
            // AdvancedExpressionScanner.g:38:4: (a= expr )
            {
                // AdvancedExpressionScanner.g:38:4: (a= expr )
                // AdvancedExpressionScanner.g:38:5: a= expr
                {
                    pushFollow(FOLLOW_expr_in_prog60);
                    expr();

                    state._fsp--;


                }


            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return;
    }

    // $ANTLR start "expr"
    // AdvancedExpressionScanner.g:41:1: expr : ( (a= fourexpr ) | ( NULL ) | ^( STRINGWRAPPER a= string_fourexpr ) | (a= ifcondition ) );
    public final void expr() throws RecognitionException {
        try {
            // AdvancedExpressionScanner.g:42:2: ( (a= fourexpr ) | ( NULL ) | ^( STRINGWRAPPER a= string_fourexpr ) | (a= ifcondition ) )
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
                    // AdvancedExpressionScanner.g:42:4: (a= fourexpr )
                {
                    // AdvancedExpressionScanner.g:42:4: (a= fourexpr )
                    // AdvancedExpressionScanner.g:42:5: a= fourexpr
                    {
                        pushFollow(FOLLOW_fourexpr_in_expr76);
                        fourexpr();

                        state._fsp--;


                    }


                }
                break;
                case 2:
                    // AdvancedExpressionScanner.g:43:4: ( NULL )
                {
                    // AdvancedExpressionScanner.g:43:4: ( NULL )
                    // AdvancedExpressionScanner.g:43:5: NULL
                    {
                        match(input, NULL, FOLLOW_NULL_in_expr83);

                    }


                }
                break;
                case 3:
                    // AdvancedExpressionScanner.g:44:4: ^( STRINGWRAPPER a= string_fourexpr )
                {
                    match(input, STRINGWRAPPER, FOLLOW_STRINGWRAPPER_in_expr90);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_expr94);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 4:
                    // AdvancedExpressionScanner.g:45:4: (a= ifcondition )
                {
                    // AdvancedExpressionScanner.g:45:4: (a= ifcondition )
                    // AdvancedExpressionScanner.g:45:5: a= ifcondition
                    {
                        pushFollow(FOLLOW_ifcondition_in_expr103);
                        ifcondition();

                        state._fsp--;


                    }


                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return;
    }

    // $ANTLR start "fourexpr"
    // AdvancedExpressionScanner.g:48:1: fourexpr : ( ^( '+' a= fourexpr b= fourexpr ) | ^( '-' a= fourexpr b= fourexpr ) | ^( '*' a= fourexpr b= fourexpr ) | ^( '/' a= fourexpr b= fourexpr ) | ^( '%' a= fourexpr b= fourexpr ) | ID | F0 | ^( F1 a= fourexpr ) | ^( F2 a= fourexpr b= fourexpr ) | CONSTANT | DOUBLE | INTEGER | ^( IFWRAPPER a= ifcondition ) );
    public final void fourexpr() throws RecognitionException {
        CommonTree ID1 = null;

        try {
            // AdvancedExpressionScanner.g:49:2: ( ^( '+' a= fourexpr b= fourexpr ) | ^( '-' a= fourexpr b= fourexpr ) | ^( '*' a= fourexpr b= fourexpr ) | ^( '/' a= fourexpr b= fourexpr ) | ^( '%' a= fourexpr b= fourexpr ) | ID | F0 | ^( F1 a= fourexpr ) | ^( F2 a= fourexpr b= fourexpr ) | CONSTANT | DOUBLE | INTEGER | ^( IFWRAPPER a= ifcondition ) )
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
                    // AdvancedExpressionScanner.g:49:4: ^( '+' a= fourexpr b= fourexpr )
                {
                    match(input, 20, FOLLOW_20_in_fourexpr116);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr120);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr124);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 2:
                    // AdvancedExpressionScanner.g:50:4: ^( '-' a= fourexpr b= fourexpr )
                {
                    match(input, 21, FOLLOW_21_in_fourexpr131);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr135);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr139);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 3:
                    // AdvancedExpressionScanner.g:51:4: ^( '*' a= fourexpr b= fourexpr )
                {
                    match(input, 22, FOLLOW_22_in_fourexpr146);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr150);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr154);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 4:
                    // AdvancedExpressionScanner.g:52:4: ^( '/' a= fourexpr b= fourexpr )
                {
                    match(input, 23, FOLLOW_23_in_fourexpr161);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr165);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr169);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 5:
                    // AdvancedExpressionScanner.g:53:4: ^( '%' a= fourexpr b= fourexpr )
                {
                    match(input, 24, FOLLOW_24_in_fourexpr176);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr180);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr184);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 6:
                    // AdvancedExpressionScanner.g:54:4: ID
                {
                    ID1 = (CommonTree) match(input, ID, FOLLOW_ID_in_fourexpr190);
                    this.AddVar((ID1 != null ? ID1.getText() : null));

                }
                break;
                case 7:
                    // AdvancedExpressionScanner.g:55:4: F0
                {
                    match(input, F0, FOLLOW_F0_in_fourexpr197);

                }
                break;
                case 8:
                    // AdvancedExpressionScanner.g:56:4: ^( F1 a= fourexpr )
                {
                    match(input, F1, FOLLOW_F1_in_fourexpr203);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr207);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 9:
                    // AdvancedExpressionScanner.g:57:4: ^( F2 a= fourexpr b= fourexpr )
                {
                    match(input, F2, FOLLOW_F2_in_fourexpr214);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_fourexpr218);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_fourexpr222);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 10:
                    // AdvancedExpressionScanner.g:58:4: CONSTANT
                {
                    match(input, CONSTANT, FOLLOW_CONSTANT_in_fourexpr228);

                }
                break;
                case 11:
                    // AdvancedExpressionScanner.g:59:4: DOUBLE
                {
                    match(input, DOUBLE, FOLLOW_DOUBLE_in_fourexpr233);

                }
                break;
                case 12:
                    // AdvancedExpressionScanner.g:60:4: INTEGER
                {
                    match(input, INTEGER, FOLLOW_INTEGER_in_fourexpr238);

                }
                break;
                case 13:
                    // AdvancedExpressionScanner.g:61:4: ^( IFWRAPPER a= ifcondition )
                {
                    match(input, IFWRAPPER, FOLLOW_IFWRAPPER_in_fourexpr244);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_ifcondition_in_fourexpr248);
                    ifcondition();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return;
    }

    // $ANTLR start "string_fourexpr"
    // AdvancedExpressionScanner.g:64:1: string_fourexpr : ( ^( '+' a= string_fourexpr b= string_fourexpr ) | ^( SUBSTRING a= string_fourexpr b= fourexpr c= fourexpr ) | ^( TAILSTRING a= string_fourexpr b= fourexpr ) | STRINGID | STRINGVALUE | ^( IFWRAPPER a= ifcondition ) );
    public final void string_fourexpr() throws RecognitionException {
        CommonTree STRINGID2 = null;

        try {
            // AdvancedExpressionScanner.g:65:2: ( ^( '+' a= string_fourexpr b= string_fourexpr ) | ^( SUBSTRING a= string_fourexpr b= fourexpr c= fourexpr ) | ^( TAILSTRING a= string_fourexpr b= fourexpr ) | STRINGID | STRINGVALUE | ^( IFWRAPPER a= ifcondition ) )
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
                    // AdvancedExpressionScanner.g:65:4: ^( '+' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 20, FOLLOW_20_in_string_fourexpr261);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_fourexpr265);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_fourexpr269);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 2:
                    // AdvancedExpressionScanner.g:66:4: ^( SUBSTRING a= string_fourexpr b= fourexpr c= fourexpr )
                {
                    match(input, SUBSTRING, FOLLOW_SUBSTRING_in_string_fourexpr276);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_fourexpr280);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_string_fourexpr284);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_string_fourexpr288);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 3:
                    // AdvancedExpressionScanner.g:67:4: ^( TAILSTRING a= string_fourexpr b= fourexpr )
                {
                    match(input, TAILSTRING, FOLLOW_TAILSTRING_in_string_fourexpr295);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_fourexpr299);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_string_fourexpr303);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 4:
                    // AdvancedExpressionScanner.g:68:4: STRINGID
                {
                    STRINGID2 = (CommonTree) match(input, STRINGID, FOLLOW_STRINGID_in_string_fourexpr309);
                    this.AddStringVar((STRINGID2 != null ? STRINGID2.getText() : null));

                }
                break;
                case 5:
                    // AdvancedExpressionScanner.g:69:4: STRINGVALUE
                {
                    match(input, STRINGVALUE, FOLLOW_STRINGVALUE_in_string_fourexpr316);

                }
                break;
                case 6:
                    // AdvancedExpressionScanner.g:70:4: ^( IFWRAPPER a= ifcondition )
                {
                    match(input, IFWRAPPER, FOLLOW_IFWRAPPER_in_string_fourexpr322);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_ifcondition_in_string_fourexpr326);
                    ifcondition();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return;
    }

    // $ANTLR start "ifcondition"
    // AdvancedExpressionScanner.g:73:1: ifcondition : ^( 'if' con= condition a= expr b= elseifcondition ) ;
    public final void ifcondition() throws RecognitionException {
        try {
            // AdvancedExpressionScanner.g:74:2: ( ^( 'if' con= condition a= expr b= elseifcondition ) )
            // AdvancedExpressionScanner.g:74:4: ^( 'if' con= condition a= expr b= elseifcondition )
            {
                match(input, 30, FOLLOW_30_in_ifcondition340);

                match(input, Token.DOWN, null);
                pushFollow(FOLLOW_condition_in_ifcondition344);
                condition();

                state._fsp--;

                pushFollow(FOLLOW_expr_in_ifcondition348);
                expr();

                state._fsp--;

                pushFollow(FOLLOW_elseifcondition_in_ifcondition352);
                elseifcondition();

                state._fsp--;


                match(input, Token.UP, null);

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return;
    }

    // $ANTLR start "elseifcondition"
    // AdvancedExpressionScanner.g:77:1: elseifcondition : ( ^( 'elseif' con= condition a= expr b= elseifcondition ) | ^( 'else' a= expr ) );
    public final void elseifcondition() throws RecognitionException {
        try {
            // AdvancedExpressionScanner.g:78:2: ( ^( 'elseif' con= condition a= expr b= elseifcondition ) | ^( 'else' a= expr ) )
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
                    // AdvancedExpressionScanner.g:78:4: ^( 'elseif' con= condition a= expr b= elseifcondition )
                {
                    match(input, 33, FOLLOW_33_in_elseifcondition365);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_condition_in_elseifcondition369);
                    condition();

                    state._fsp--;

                    pushFollow(FOLLOW_expr_in_elseifcondition373);
                    expr();

                    state._fsp--;

                    pushFollow(FOLLOW_elseifcondition_in_elseifcondition377);
                    elseifcondition();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 2:
                    // AdvancedExpressionScanner.g:79:4: ^( 'else' a= expr )
                {
                    match(input, 34, FOLLOW_34_in_elseifcondition384);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_expr_in_elseifcondition388);
                    expr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return;
    }

    // $ANTLR start "condition"
    // AdvancedExpressionScanner.g:82:1: condition : ( ^( '&&' a= condition b= condition ) | ^( '||' a= condition b= condition ) | ^( '!' a= condition ) | (a= comparecondition ) );
    public final void condition() throws RecognitionException {
        try {
            // AdvancedExpressionScanner.g:83:2: ( ^( '&&' a= condition b= condition ) | ^( '||' a= condition b= condition ) | ^( '!' a= condition ) | (a= comparecondition ) )
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
                    // AdvancedExpressionScanner.g:83:4: ^( '&&' a= condition b= condition )
                {
                    match(input, 36, FOLLOW_36_in_condition401);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_condition_in_condition405);
                    condition();

                    state._fsp--;

                    pushFollow(FOLLOW_condition_in_condition409);
                    condition();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 2:
                    // AdvancedExpressionScanner.g:84:4: ^( '||' a= condition b= condition )
                {
                    match(input, 35, FOLLOW_35_in_condition416);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_condition_in_condition420);
                    condition();

                    state._fsp--;

                    pushFollow(FOLLOW_condition_in_condition424);
                    condition();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 3:
                    // AdvancedExpressionScanner.g:85:4: ^( '!' a= condition )
                {
                    match(input, 37, FOLLOW_37_in_condition431);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_condition_in_condition435);
                    condition();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 4:
                    // AdvancedExpressionScanner.g:86:4: (a= comparecondition )
                {
                    // AdvancedExpressionScanner.g:86:4: (a= comparecondition )
                    // AdvancedExpressionScanner.g:86:5: a= comparecondition
                    {
                        pushFollow(FOLLOW_comparecondition_in_condition444);
                        comparecondition();

                        state._fsp--;


                    }


                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return;
    }

    // $ANTLR start "comparecondition"
    // AdvancedExpressionScanner.g:89:1: comparecondition : ( ^( '==' NULL b= fourexpr ) | ^( '!=' NULL b= fourexpr ) | ^( '<' a= fourexpr b= fourexpr ) | ^( '<=' a= fourexpr b= fourexpr ) | ^( '>' a= fourexpr b= fourexpr ) | ^( '>=' a= fourexpr b= fourexpr ) | ^( '==' a= fourexpr b= fourexpr ) | ^( '!=' a= fourexpr b= fourexpr ) | ^( STRINGWRAPPER c= string_comparecondition ) );
    public final void comparecondition() throws RecognitionException {
        try {
            // AdvancedExpressionScanner.g:90:2: ( ^( '==' NULL b= fourexpr ) | ^( '!=' NULL b= fourexpr ) | ^( '<' a= fourexpr b= fourexpr ) | ^( '<=' a= fourexpr b= fourexpr ) | ^( '>' a= fourexpr b= fourexpr ) | ^( '>=' a= fourexpr b= fourexpr ) | ^( '==' a= fourexpr b= fourexpr ) | ^( '!=' a= fourexpr b= fourexpr ) | ^( STRINGWRAPPER c= string_comparecondition ) )
            int alt6 = 9;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1:
                    // AdvancedExpressionScanner.g:90:4: ^( '==' NULL b= fourexpr )
                {
                    match(input, 38, FOLLOW_38_in_comparecondition457);

                    match(input, Token.DOWN, null);
                    match(input, NULL, FOLLOW_NULL_in_comparecondition459);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition463);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 2:
                    // AdvancedExpressionScanner.g:91:4: ^( '!=' NULL b= fourexpr )
                {
                    match(input, 39, FOLLOW_39_in_comparecondition470);

                    match(input, Token.DOWN, null);
                    match(input, NULL, FOLLOW_NULL_in_comparecondition472);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition476);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 3:
                    // AdvancedExpressionScanner.g:92:4: ^( '<' a= fourexpr b= fourexpr )
                {
                    match(input, 40, FOLLOW_40_in_comparecondition483);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition487);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition491);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 4:
                    // AdvancedExpressionScanner.g:93:4: ^( '<=' a= fourexpr b= fourexpr )
                {
                    match(input, 41, FOLLOW_41_in_comparecondition498);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition502);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition506);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 5:
                    // AdvancedExpressionScanner.g:94:4: ^( '>' a= fourexpr b= fourexpr )
                {
                    match(input, 42, FOLLOW_42_in_comparecondition513);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition517);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition521);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 6:
                    // AdvancedExpressionScanner.g:95:4: ^( '>=' a= fourexpr b= fourexpr )
                {
                    match(input, 43, FOLLOW_43_in_comparecondition528);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition532);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition536);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 7:
                    // AdvancedExpressionScanner.g:96:4: ^( '==' a= fourexpr b= fourexpr )
                {
                    match(input, 38, FOLLOW_38_in_comparecondition543);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition547);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition551);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 8:
                    // AdvancedExpressionScanner.g:97:4: ^( '!=' a= fourexpr b= fourexpr )
                {
                    match(input, 39, FOLLOW_39_in_comparecondition558);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition562);
                    fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_fourexpr_in_comparecondition566);
                    fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 9:
                    // AdvancedExpressionScanner.g:98:4: ^( STRINGWRAPPER c= string_comparecondition )
                {
                    match(input, STRINGWRAPPER, FOLLOW_STRINGWRAPPER_in_comparecondition573);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_comparecondition_in_comparecondition577);
                    string_comparecondition();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return;
    }

    // $ANTLR start "string_comparecondition"
    // AdvancedExpressionScanner.g:101:1: string_comparecondition : ( ^( '==' NULL b= string_fourexpr ) | ^( '!=' NULL b= string_fourexpr ) | ^( '<' a= string_fourexpr b= string_fourexpr ) | ^( '<=' a= string_fourexpr b= string_fourexpr ) | ^( '>' a= string_fourexpr b= string_fourexpr ) | ^( '>=' a= string_fourexpr b= string_fourexpr ) | ^( '==' a= string_fourexpr b= string_fourexpr ) | ^( '!=' a= string_fourexpr b= string_fourexpr ) | ^( 'contains' a= string_fourexpr b= string_fourexpr ) | ^( 'match' a= string_fourexpr b= string_fourexpr ) );
    public final void string_comparecondition() throws RecognitionException {
        try {
            // AdvancedExpressionScanner.g:102:2: ( ^( '==' NULL b= string_fourexpr ) | ^( '!=' NULL b= string_fourexpr ) | ^( '<' a= string_fourexpr b= string_fourexpr ) | ^( '<=' a= string_fourexpr b= string_fourexpr ) | ^( '>' a= string_fourexpr b= string_fourexpr ) | ^( '>=' a= string_fourexpr b= string_fourexpr ) | ^( '==' a= string_fourexpr b= string_fourexpr ) | ^( '!=' a= string_fourexpr b= string_fourexpr ) | ^( 'contains' a= string_fourexpr b= string_fourexpr ) | ^( 'match' a= string_fourexpr b= string_fourexpr ) )
            int alt7 = 10;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1:
                    // AdvancedExpressionScanner.g:102:4: ^( '==' NULL b= string_fourexpr )
                {
                    match(input, 38, FOLLOW_38_in_string_comparecondition590);

                    match(input, Token.DOWN, null);
                    match(input, NULL, FOLLOW_NULL_in_string_comparecondition592);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition596);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 2:
                    // AdvancedExpressionScanner.g:103:4: ^( '!=' NULL b= string_fourexpr )
                {
                    match(input, 39, FOLLOW_39_in_string_comparecondition603);

                    match(input, Token.DOWN, null);
                    match(input, NULL, FOLLOW_NULL_in_string_comparecondition605);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition609);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 3:
                    // AdvancedExpressionScanner.g:104:4: ^( '<' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 40, FOLLOW_40_in_string_comparecondition616);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition620);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition624);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 4:
                    // AdvancedExpressionScanner.g:105:4: ^( '<=' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 41, FOLLOW_41_in_string_comparecondition631);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition635);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition639);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 5:
                    // AdvancedExpressionScanner.g:106:4: ^( '>' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 42, FOLLOW_42_in_string_comparecondition646);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition650);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition654);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 6:
                    // AdvancedExpressionScanner.g:107:4: ^( '>=' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 43, FOLLOW_43_in_string_comparecondition661);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition665);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition669);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 7:
                    // AdvancedExpressionScanner.g:108:4: ^( '==' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 38, FOLLOW_38_in_string_comparecondition676);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition680);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition684);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 8:
                    // AdvancedExpressionScanner.g:109:4: ^( '!=' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 39, FOLLOW_39_in_string_comparecondition691);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition695);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition699);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 9:
                    // AdvancedExpressionScanner.g:110:4: ^( 'contains' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 44, FOLLOW_44_in_string_comparecondition706);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition710);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition714);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;
                case 10:
                    // AdvancedExpressionScanner.g:111:4: ^( 'match' a= string_fourexpr b= string_fourexpr )
                {
                    match(input, 45, FOLLOW_45_in_string_comparecondition721);

                    match(input, Token.DOWN, null);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition725);
                    string_fourexpr();

                    state._fsp--;

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition729);
                    string_fourexpr();

                    state._fsp--;


                    match(input, Token.UP, null);

                }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        } finally {
        }
        return;
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
            return "89:1: comparecondition : ( ^( '==' NULL b= fourexpr ) | ^( '!=' NULL b= fourexpr ) | ^( '<' a= fourexpr b= fourexpr ) | ^( '<=' a= fourexpr b= fourexpr ) | ^( '>' a= fourexpr b= fourexpr ) | ^( '>=' a= fourexpr b= fourexpr ) | ^( '==' a= fourexpr b= fourexpr ) | ^( '!=' a= fourexpr b= fourexpr ) | ^( STRINGWRAPPER c= string_comparecondition ) );";
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
            return "101:1: string_comparecondition : ( ^( '==' NULL b= string_fourexpr ) | ^( '!=' NULL b= string_fourexpr ) | ^( '<' a= string_fourexpr b= string_fourexpr ) | ^( '<=' a= string_fourexpr b= string_fourexpr ) | ^( '>' a= string_fourexpr b= string_fourexpr ) | ^( '>=' a= string_fourexpr b= string_fourexpr ) | ^( '==' a= string_fourexpr b= string_fourexpr ) | ^( '!=' a= string_fourexpr b= string_fourexpr ) | ^( 'contains' a= string_fourexpr b= string_fourexpr ) | ^( 'match' a= string_fourexpr b= string_fourexpr ) );";
        }
    }

}