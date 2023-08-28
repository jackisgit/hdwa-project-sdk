// $ANTLR 3.1 AdvancedExpression.g 2021-07-30 15:32:47

package com.hdwa.sdk.expression;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class AdvancedExpressionParser extends Parser {
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
    public static final int DOUBLE = 8;
    public static final int F0 = 16;
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
    public static final BitSet FOLLOW_expr_in_prog93 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NEWLINE_in_prog95 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fourexpr_in_expr107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_expr112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_fourexpr_in_expr117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ifcondition_in_expr130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multexpr_in_fourexpr141 = new BitSet(new long[]{0x0000000000300002L});
    public static final BitSet FOLLOW_20_in_fourexpr145 = new BitSet(new long[]{0x0000000002070F00L});
    public static final BitSet FOLLOW_21_in_fourexpr148 = new BitSet(new long[]{0x0000000002070F00L});
    public static final BitSet FOLLOW_multexpr_in_fourexpr152 = new BitSet(new long[]{0x0000000000300002L});
    public static final BitSet FOLLOW_atom_in_multexpr166 = new BitSet(new long[]{0x0000000001C00002L});

    ;
    public static final BitSet FOLLOW_22_in_multexpr170 = new BitSet(new long[]{0x0000000002070F00L});
    // $ANTLR end "prog"
    public static final BitSet FOLLOW_23_in_multexpr173 = new BitSet(new long[]{0x0000000002070F00L});

    ;
    public static final BitSet FOLLOW_24_in_multexpr176 = new BitSet(new long[]{0x0000000002070F00L});
    // $ANTLR end "expr"
    public static final BitSet FOLLOW_atom_in_multexpr180 = new BitSet(new long[]{0x0000000001C00002L});

    ;
    public static final BitSet FOLLOW_DOUBLE_in_atom194 = new BitSet(new long[]{0x0000000000000002L});
    // $ANTLR end "fourexpr"
    public static final BitSet FOLLOW_INTEGER_in_atom199 = new BitSet(new long[]{0x0000000000000002L});

    ;
    public static final BitSet FOLLOW_CONSTANT_in_atom204 = new BitSet(new long[]{0x0000000000000002L});
    // $ANTLR end "multexpr"
    public static final BitSet FOLLOW_ID_in_atom209 = new BitSet(new long[]{0x0000000000000002L});

    ;
    public static final BitSet FOLLOW_func_in_atom214 = new BitSet(new long[]{0x0000000000000002L});
    // $ANTLR end "atom"
    public static final BitSet FOLLOW_25_in_atom220 = new BitSet(new long[]{0x0000000002070F00L});

    ;
    public static final BitSet FOLLOW_fourexpr_in_atom223 = new BitSet(new long[]{0x0000000004000000L});
    // $ANTLR end "string_fourexpr"
    public static final BitSet FOLLOW_26_in_atom225 = new BitSet(new long[]{0x0000000000000002L});

    ;
    public static final BitSet FOLLOW_25_in_atom232 = new BitSet(new long[]{0x0000000040000000L});
    // $ANTLR end "string_itom"
    public static final BitSet FOLLOW_ifcondition_in_atom234 = new BitSet(new long[]{0x0000000004000000L});

    ;
    public static final BitSet FOLLOW_26_in_atom236 = new BitSet(new long[]{0x0000000000000002L});
    // $ANTLR end "ifcondition"
    public static final BitSet FOLLOW_string_itom_in_string_fourexpr255 = new BitSet(new long[]{0x0000000000100002L});

    ;
    public static final BitSet FOLLOW_20_in_string_fourexpr259 = new BitSet(new long[]{0x000000001000C000L});
    // $ANTLR end "elseifcondition"
    public static final BitSet FOLLOW_string_itom_in_string_fourexpr263 = new BitSet(new long[]{0x0000000000100002L});

    ;
    public static final BitSet FOLLOW_SUBSTRING_in_string_fourexpr270 = new BitSet(new long[]{0x0000000002000000L});
    // $ANTLR end "elsecondition"
    public static final BitSet FOLLOW_25_in_string_fourexpr273 = new BitSet(new long[]{0x000000001000C000L});

    ;
    public static final BitSet FOLLOW_string_itom_in_string_fourexpr276 = new BitSet(new long[]{0x0000000008000000L});
    // $ANTLR end "condition"
    public static final BitSet FOLLOW_27_in_string_fourexpr278 = new BitSet(new long[]{0x0000000002070F00L});

    ;
    public static final BitSet FOLLOW_fourexpr_in_string_fourexpr281 = new BitSet(new long[]{0x0000000008000000L});
    // $ANTLR end "andcondition"
    public static final BitSet FOLLOW_27_in_string_fourexpr283 = new BitSet(new long[]{0x0000000002070F00L});

    ;
    public static final BitSet FOLLOW_fourexpr_in_string_fourexpr286 = new BitSet(new long[]{0x0000000004000000L});
    // $ANTLR end "notcondition"
    public static final BitSet FOLLOW_26_in_string_fourexpr288 = new BitSet(new long[]{0x0000000000000002L});

    ;
    public static final BitSet FOLLOW_TAILSTRING_in_string_fourexpr295 = new BitSet(new long[]{0x0000000002000000L});
    // $ANTLR end "itemcondition"
    public static final BitSet FOLLOW_25_in_string_fourexpr298 = new BitSet(new long[]{0x000000001000C000L});

    ;
    public static final BitSet FOLLOW_string_itom_in_string_fourexpr301 = new BitSet(new long[]{0x0000000008000000L});
    // $ANTLR end "comparecondition"
    public static final BitSet FOLLOW_27_in_string_fourexpr303 = new BitSet(new long[]{0x0000000002070F00L});

    ;
    public static final BitSet FOLLOW_fourexpr_in_string_fourexpr306 = new BitSet(new long[]{0x0000000004000000L});
    // $ANTLR end "string_comparecondition"
    public static final BitSet FOLLOW_26_in_string_fourexpr308 = new BitSet(new long[]{0x0000000000000002L});

    ;
    public static final BitSet FOLLOW_STRINGVALUE_in_string_itom321 = new BitSet(new long[]{0x0000000000000002L});
    // $ANTLR end "func"

    // Delegated rules
    public static final BitSet FOLLOW_STRINGID_in_string_itom326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_string_itom332 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_itom336 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_fourexpr_in_string_itom338 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_string_itom341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_string_itom348 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_ifcondition_in_string_itom350 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_string_itom352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_ifcondition371 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_ifcondition374 = new BitSet(new long[]{0x0000002012000000L});
    public static final BitSet FOLLOW_condition_in_ifcondition377 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_ifcondition379 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_ifcondition382 = new BitSet(new long[]{0x000000005207FF80L});
    public static final BitSet FOLLOW_expr_in_ifcondition385 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_ifcondition387 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_elseifcondition_in_ifcondition391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elsecondition_in_ifcondition393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_elseifcondition405 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_elseifcondition408 = new BitSet(new long[]{0x0000002012000000L});
    public static final BitSet FOLLOW_condition_in_elseifcondition411 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_elseifcondition413 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_elseifcondition416 = new BitSet(new long[]{0x000000005207FF80L});
    public static final BitSet FOLLOW_expr_in_elseifcondition419 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_elseifcondition421 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_elseifcondition_in_elseifcondition425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elsecondition_in_elseifcondition427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_elsecondition439 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_elsecondition442 = new BitSet(new long[]{0x000000005207FF80L});
    public static final BitSet FOLLOW_expr_in_elsecondition445 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_elsecondition447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andcondition_in_condition459 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_35_in_condition463 = new BitSet(new long[]{0x0000002012000000L});
    public static final BitSet FOLLOW_andcondition_in_condition467 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_notcondition_in_andcondition480 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_andcondition484 = new BitSet(new long[]{0x0000002012000000L});
    public static final BitSet FOLLOW_notcondition_in_andcondition488 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_37_in_notcondition502 = new BitSet(new long[]{0x0000002012000000L});
    public static final BitSet FOLLOW_itemcondition_in_notcondition506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_itemcondition_in_notcondition511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_itemcondition522 = new BitSet(new long[]{0x0000002012000000L});
    public static final BitSet FOLLOW_condition_in_itemcondition525 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_itemcondition527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparecondition_in_itemcondition533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_comparecondition544 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_NULL_in_comparecondition547 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_38_in_comparecondition550 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_39_in_comparecondition553 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition557 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_comparecondition559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_comparecondition565 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition568 = new BitSet(new long[]{0x00000FC000000000L});
    public static final BitSet FOLLOW_40_in_comparecondition571 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_41_in_comparecondition574 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_42_in_comparecondition577 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_43_in_comparecondition580 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_38_in_comparecondition583 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_39_in_comparecondition586 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_fourexpr_in_comparecondition590 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_comparecondition592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_comparecondition_in_comparecondition598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_string_comparecondition617 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_NULL_in_string_comparecondition620 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_38_in_string_comparecondition623 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_39_in_string_comparecondition626 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition630 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_string_comparecondition632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_string_comparecondition638 = new BitSet(new long[]{0x0000300000000000L});
    public static final BitSet FOLLOW_44_in_string_comparecondition642 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_45_in_string_comparecondition645 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_string_comparecondition649 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition652 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_string_comparecondition654 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition657 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_string_comparecondition659 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_string_comparecondition662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_string_comparecondition668 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition671 = new BitSet(new long[]{0x00000FC000000000L});
    public static final BitSet FOLLOW_40_in_string_comparecondition674 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_41_in_string_comparecondition677 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_42_in_string_comparecondition680 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_43_in_string_comparecondition683 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_38_in_string_comparecondition686 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_39_in_string_comparecondition689 = new BitSet(new long[]{0x000000001000F000L});
    public static final BitSet FOLLOW_string_fourexpr_in_string_comparecondition693 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_string_comparecondition695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_F0_in_func707 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_func710 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_func713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_F1_in_func719 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_func722 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_fourexpr_in_func725 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_func727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_F2_in_func733 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_func736 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_fourexpr_in_func739 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_func741 = new BitSet(new long[]{0x000000001207FF00L});
    public static final BitSet FOLLOW_fourexpr_in_func744 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_func746 = new BitSet(new long[]{0x0000000000000002L});
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();
    public AdvancedExpressionParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public AdvancedExpressionParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);

    }

    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    public String[] getTokenNames() {
        return AdvancedExpressionParser.tokenNames;
    }

    public String getGrammarFileName() {
        return "AdvancedExpression.g";
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

    // $ANTLR start "prog"
    // AdvancedExpression.g:70:1: prog : expr NEWLINE ;
    public final prog_return prog() throws RecognitionException {
        prog_return retval = new prog_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token NEWLINE2 = null;
        expr_return expr1 = null;


        CommonTree NEWLINE2_tree = null;

        try {
            // AdvancedExpression.g:71:2: ( expr NEWLINE )
            // AdvancedExpression.g:71:4: expr NEWLINE
            {
                root_0 = (CommonTree) adaptor.nil();

                pushFollow(FOLLOW_expr_in_prog93);
                expr1 = expr();

                state._fsp--;

                adaptor.addChild(root_0, expr1.getTree());
                NEWLINE2 = (Token) match(input, NEWLINE, FOLLOW_NEWLINE_in_prog95);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "expr"
    // AdvancedExpression.g:74:1: expr : ( fourexpr | NULL | string_fourexpr -> ^( STRINGWRAPPER string_fourexpr ) | ifcondition );
    public final expr_return expr() throws RecognitionException {
        expr_return retval = new expr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token NULL4 = null;
        fourexpr_return fourexpr3 = null;

        string_fourexpr_return string_fourexpr5 = null;

        ifcondition_return ifcondition6 = null;


        CommonTree NULL4_tree = null;
        RewriteRuleSubtreeStream stream_string_fourexpr = new RewriteRuleSubtreeStream(adaptor, "rule string_fourexpr");
        try {
            // AdvancedExpression.g:75:2: ( fourexpr | NULL | string_fourexpr -> ^( STRINGWRAPPER string_fourexpr ) | ifcondition )
            int alt1 = 4;
            switch (input.LA(1)) {
                case DOUBLE:
                case INTEGER:
                case CONSTANT:
                case ID:
                case F0:
                case F1:
                case F2:
                case 25: {
                    alt1 = 1;
                }
                break;
                case NULL: {
                    alt1 = 2;
                }
                break;
                case SUBSTRING:
                case TAILSTRING:
                case STRINGVALUE:
                case STRINGID:
                case 28: {
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
                    // AdvancedExpression.g:75:4: fourexpr
                {
                    root_0 = (CommonTree) adaptor.nil();

                    pushFollow(FOLLOW_fourexpr_in_expr107);
                    fourexpr3 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr3.getTree());

                }
                break;
                case 2:
                    // AdvancedExpression.g:76:4: NULL
                {
                    root_0 = (CommonTree) adaptor.nil();

                    NULL4 = (Token) match(input, NULL, FOLLOW_NULL_in_expr112);
                    NULL4_tree = (CommonTree) adaptor.create(NULL4);
                    adaptor.addChild(root_0, NULL4_tree);


                }
                break;
                case 3:
                    // AdvancedExpression.g:77:4: string_fourexpr
                {
                    pushFollow(FOLLOW_string_fourexpr_in_expr117);
                    string_fourexpr5 = string_fourexpr();

                    state._fsp--;

                    stream_string_fourexpr.add(string_fourexpr5.getTree());


                    // AST REWRITE
                    // elements: string_fourexpr
                    // token labels:
                    // rule labels: retval
                    // token list labels:
                    // rule list labels:
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "token retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) adaptor.nil();
                    // 77:20: -> ^( STRINGWRAPPER string_fourexpr )
                    {
                        // AdvancedExpression.g:77:23: ^( STRINGWRAPPER string_fourexpr )
                        {
                            CommonTree root_1 = (CommonTree) adaptor.nil();
                            root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(STRINGWRAPPER, "STRINGWRAPPER"), root_1);

                            adaptor.addChild(root_1, stream_string_fourexpr.nextTree());

                            adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
                break;
                case 4:
                    // AdvancedExpression.g:78:4: ifcondition
                {
                    root_0 = (CommonTree) adaptor.nil();

                    pushFollow(FOLLOW_ifcondition_in_expr130);
                    ifcondition6 = ifcondition();

                    state._fsp--;

                    adaptor.addChild(root_0, ifcondition6.getTree());

                }
                break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "fourexpr"
    // AdvancedExpression.g:81:1: fourexpr : multexpr ( ( '+' | '-' ) multexpr )* ;
    public final fourexpr_return fourexpr() throws RecognitionException {
        fourexpr_return retval = new fourexpr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal8 = null;
        Token char_literal9 = null;
        multexpr_return multexpr7 = null;

        multexpr_return multexpr10 = null;


        CommonTree char_literal8_tree = null;
        CommonTree char_literal9_tree = null;

        try {
            // AdvancedExpression.g:82:2: ( multexpr ( ( '+' | '-' ) multexpr )* )
            // AdvancedExpression.g:82:4: multexpr ( ( '+' | '-' ) multexpr )*
            {
                root_0 = (CommonTree) adaptor.nil();

                pushFollow(FOLLOW_multexpr_in_fourexpr141);
                multexpr7 = multexpr();

                state._fsp--;

                adaptor.addChild(root_0, multexpr7.getTree());
                // AdvancedExpression.g:82:13: ( ( '+' | '-' ) multexpr )*
                loop3:
                do {
                    int alt3 = 2;
                    int LA3_0 = input.LA(1);

                    if (((LA3_0 >= 20 && LA3_0 <= 21))) {
                        alt3 = 1;
                    }


                    switch (alt3) {
                        case 1:
                            // AdvancedExpression.g:82:14: ( '+' | '-' ) multexpr
                        {
                            // AdvancedExpression.g:82:14: ( '+' | '-' )
                            int alt2 = 2;
                            int LA2_0 = input.LA(1);

                            if ((LA2_0 == 20)) {
                                alt2 = 1;
                            } else if ((LA2_0 == 21)) {
                                alt2 = 2;
                            } else {
                                NoViableAltException nvae =
                                        new NoViableAltException("", 2, 0, input);

                                throw nvae;
                            }
                            switch (alt2) {
                                case 1:
                                    // AdvancedExpression.g:82:15: '+'
                                {
                                    char_literal8 = (Token) match(input, 20, FOLLOW_20_in_fourexpr145);
                                    char_literal8_tree = (CommonTree) adaptor.create(char_literal8);
                                    root_0 = (CommonTree) adaptor.becomeRoot(char_literal8_tree, root_0);


                                }
                                break;
                                case 2:
                                    // AdvancedExpression.g:82:20: '-'
                                {
                                    char_literal9 = (Token) match(input, 21, FOLLOW_21_in_fourexpr148);
                                    char_literal9_tree = (CommonTree) adaptor.create(char_literal9);
                                    root_0 = (CommonTree) adaptor.becomeRoot(char_literal9_tree, root_0);


                                }
                                break;

                            }

                            pushFollow(FOLLOW_multexpr_in_fourexpr152);
                            multexpr10 = multexpr();

                            state._fsp--;

                            adaptor.addChild(root_0, multexpr10.getTree());

                        }
                        break;

                        default:
                            break loop3;
                    }
                } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "multexpr"
    // AdvancedExpression.g:85:1: multexpr : atom ( ( '*' | '/' | '%' ) atom )* ;
    public final multexpr_return multexpr() throws RecognitionException {
        multexpr_return retval = new multexpr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal12 = null;
        Token char_literal13 = null;
        Token char_literal14 = null;
        atom_return atom11 = null;

        atom_return atom15 = null;


        CommonTree char_literal12_tree = null;
        CommonTree char_literal13_tree = null;
        CommonTree char_literal14_tree = null;

        try {
            // AdvancedExpression.g:86:2: ( atom ( ( '*' | '/' | '%' ) atom )* )
            // AdvancedExpression.g:86:4: atom ( ( '*' | '/' | '%' ) atom )*
            {
                root_0 = (CommonTree) adaptor.nil();

                pushFollow(FOLLOW_atom_in_multexpr166);
                atom11 = atom();

                state._fsp--;

                adaptor.addChild(root_0, atom11.getTree());
                // AdvancedExpression.g:86:9: ( ( '*' | '/' | '%' ) atom )*
                loop5:
                do {
                    int alt5 = 2;
                    int LA5_0 = input.LA(1);

                    if (((LA5_0 >= 22 && LA5_0 <= 24))) {
                        alt5 = 1;
                    }


                    switch (alt5) {
                        case 1:
                            // AdvancedExpression.g:86:10: ( '*' | '/' | '%' ) atom
                        {
                            // AdvancedExpression.g:86:10: ( '*' | '/' | '%' )
                            int alt4 = 3;
                            switch (input.LA(1)) {
                                case 22: {
                                    alt4 = 1;
                                }
                                break;
                                case 23: {
                                    alt4 = 2;
                                }
                                break;
                                case 24: {
                                    alt4 = 3;
                                }
                                break;
                                default:
                                    NoViableAltException nvae =
                                            new NoViableAltException("", 4, 0, input);

                                    throw nvae;
                            }

                            switch (alt4) {
                                case 1:
                                    // AdvancedExpression.g:86:11: '*'
                                {
                                    char_literal12 = (Token) match(input, 22, FOLLOW_22_in_multexpr170);
                                    char_literal12_tree = (CommonTree) adaptor.create(char_literal12);
                                    root_0 = (CommonTree) adaptor.becomeRoot(char_literal12_tree, root_0);


                                }
                                break;
                                case 2:
                                    // AdvancedExpression.g:86:16: '/'
                                {
                                    char_literal13 = (Token) match(input, 23, FOLLOW_23_in_multexpr173);
                                    char_literal13_tree = (CommonTree) adaptor.create(char_literal13);
                                    root_0 = (CommonTree) adaptor.becomeRoot(char_literal13_tree, root_0);


                                }
                                break;
                                case 3:
                                    // AdvancedExpression.g:86:21: '%'
                                {
                                    char_literal14 = (Token) match(input, 24, FOLLOW_24_in_multexpr176);
                                    char_literal14_tree = (CommonTree) adaptor.create(char_literal14);
                                    root_0 = (CommonTree) adaptor.becomeRoot(char_literal14_tree, root_0);


                                }
                                break;

                            }

                            pushFollow(FOLLOW_atom_in_multexpr180);
                            atom15 = atom();

                            state._fsp--;

                            adaptor.addChild(root_0, atom15.getTree());

                        }
                        break;

                        default:
                            break loop5;
                    }
                } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "atom"
    // AdvancedExpression.g:89:1: atom : ( DOUBLE | INTEGER | CONSTANT | ID | func | '(' fourexpr ')' | '(' ifcondition ')' -> ^( IFWRAPPER ifcondition ) );
    public final atom_return atom() throws RecognitionException {
        atom_return retval = new atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DOUBLE16 = null;
        Token INTEGER17 = null;
        Token CONSTANT18 = null;
        Token ID19 = null;
        Token char_literal21 = null;
        Token char_literal23 = null;
        Token char_literal24 = null;
        Token char_literal26 = null;
        func_return func20 = null;

        fourexpr_return fourexpr22 = null;

        ifcondition_return ifcondition25 = null;


        CommonTree DOUBLE16_tree = null;
        CommonTree INTEGER17_tree = null;
        CommonTree CONSTANT18_tree = null;
        CommonTree ID19_tree = null;
        CommonTree char_literal21_tree = null;
        CommonTree char_literal23_tree = null;
        CommonTree char_literal24_tree = null;
        CommonTree char_literal26_tree = null;
        RewriteRuleTokenStream stream_25 = new RewriteRuleTokenStream(adaptor, "token 25");
        RewriteRuleTokenStream stream_26 = new RewriteRuleTokenStream(adaptor, "token 26");
        RewriteRuleSubtreeStream stream_ifcondition = new RewriteRuleSubtreeStream(adaptor, "rule ifcondition");
        try {
            // AdvancedExpression.g:90:2: ( DOUBLE | INTEGER | CONSTANT | ID | func | '(' fourexpr ')' | '(' ifcondition ')' -> ^( IFWRAPPER ifcondition ) )
            int alt6 = 7;
            switch (input.LA(1)) {
                case DOUBLE: {
                    alt6 = 1;
                }
                break;
                case INTEGER: {
                    alt6 = 2;
                }
                break;
                case CONSTANT: {
                    alt6 = 3;
                }
                break;
                case ID: {
                    alt6 = 4;
                }
                break;
                case F0:
                case F1:
                case F2: {
                    alt6 = 5;
                }
                break;
                case 25: {
                    int LA6_6 = input.LA(2);

                    if ((LA6_6 == 30)) {
                        alt6 = 7;
                    } else if (((LA6_6 >= DOUBLE && LA6_6 <= ID) || (LA6_6 >= F0 && LA6_6 <= F2) || LA6_6 == 25)) {
                        alt6 = 6;
                    } else {
                        NoViableAltException nvae =
                                new NoViableAltException("", 6, 6, input);

                        throw nvae;
                    }
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 6, 0, input);

                    throw nvae;
            }

            switch (alt6) {
                case 1:
                    // AdvancedExpression.g:90:4: DOUBLE
                {
                    root_0 = (CommonTree) adaptor.nil();

                    DOUBLE16 = (Token) match(input, DOUBLE, FOLLOW_DOUBLE_in_atom194);
                    DOUBLE16_tree = (CommonTree) adaptor.create(DOUBLE16);
                    adaptor.addChild(root_0, DOUBLE16_tree);


                }
                break;
                case 2:
                    // AdvancedExpression.g:91:4: INTEGER
                {
                    root_0 = (CommonTree) adaptor.nil();

                    INTEGER17 = (Token) match(input, INTEGER, FOLLOW_INTEGER_in_atom199);
                    INTEGER17_tree = (CommonTree) adaptor.create(INTEGER17);
                    adaptor.addChild(root_0, INTEGER17_tree);


                }
                break;
                case 3:
                    // AdvancedExpression.g:92:4: CONSTANT
                {
                    root_0 = (CommonTree) adaptor.nil();

                    CONSTANT18 = (Token) match(input, CONSTANT, FOLLOW_CONSTANT_in_atom204);
                    CONSTANT18_tree = (CommonTree) adaptor.create(CONSTANT18);
                    adaptor.addChild(root_0, CONSTANT18_tree);


                }
                break;
                case 4:
                    // AdvancedExpression.g:93:4: ID
                {
                    root_0 = (CommonTree) adaptor.nil();

                    ID19 = (Token) match(input, ID, FOLLOW_ID_in_atom209);
                    ID19_tree = (CommonTree) adaptor.create(ID19);
                    adaptor.addChild(root_0, ID19_tree);


                }
                break;
                case 5:
                    // AdvancedExpression.g:94:4: func
                {
                    root_0 = (CommonTree) adaptor.nil();

                    pushFollow(FOLLOW_func_in_atom214);
                    func20 = func();

                    state._fsp--;

                    adaptor.addChild(root_0, func20.getTree());

                }
                break;
                case 6:
                    // AdvancedExpression.g:95:5: '(' fourexpr ')'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    char_literal21 = (Token) match(input, 25, FOLLOW_25_in_atom220);
                    pushFollow(FOLLOW_fourexpr_in_atom223);
                    fourexpr22 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr22.getTree());
                    char_literal23 = (Token) match(input, 26, FOLLOW_26_in_atom225);

                }
                break;
                case 7:
                    // AdvancedExpression.g:96:5: '(' ifcondition ')'
                {
                    char_literal24 = (Token) match(input, 25, FOLLOW_25_in_atom232);
                    stream_25.add(char_literal24);

                    pushFollow(FOLLOW_ifcondition_in_atom234);
                    ifcondition25 = ifcondition();

                    state._fsp--;

                    stream_ifcondition.add(ifcondition25.getTree());
                    char_literal26 = (Token) match(input, 26, FOLLOW_26_in_atom236);
                    stream_26.add(char_literal26);


                    // AST REWRITE
                    // elements: ifcondition
                    // token labels:
                    // rule labels: retval
                    // token list labels:
                    // rule list labels:
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "token retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) adaptor.nil();
                    // 96:25: -> ^( IFWRAPPER ifcondition )
                    {
                        // AdvancedExpression.g:96:28: ^( IFWRAPPER ifcondition )
                        {
                            CommonTree root_1 = (CommonTree) adaptor.nil();
                            root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(IFWRAPPER, "IFWRAPPER"), root_1);

                            adaptor.addChild(root_1, stream_ifcondition.nextTree());

                            adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
                break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "string_fourexpr"
    // AdvancedExpression.g:99:1: string_fourexpr : ( string_itom ( ( '+' ) string_itom )* | SUBSTRING '(' string_itom ',' fourexpr ',' fourexpr ')' | TAILSTRING '(' string_itom ',' fourexpr ')' );
    public final string_fourexpr_return string_fourexpr() throws RecognitionException {
        string_fourexpr_return retval = new string_fourexpr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal28 = null;
        Token SUBSTRING30 = null;
        Token char_literal31 = null;
        Token char_literal33 = null;
        Token char_literal35 = null;
        Token char_literal37 = null;
        Token TAILSTRING38 = null;
        Token char_literal39 = null;
        Token char_literal41 = null;
        Token char_literal43 = null;
        string_itom_return string_itom27 = null;

        string_itom_return string_itom29 = null;

        string_itom_return string_itom32 = null;

        fourexpr_return fourexpr34 = null;

        fourexpr_return fourexpr36 = null;

        string_itom_return string_itom40 = null;

        fourexpr_return fourexpr42 = null;


        CommonTree char_literal28_tree = null;
        CommonTree SUBSTRING30_tree = null;
        CommonTree char_literal31_tree = null;
        CommonTree char_literal33_tree = null;
        CommonTree char_literal35_tree = null;
        CommonTree char_literal37_tree = null;
        CommonTree TAILSTRING38_tree = null;
        CommonTree char_literal39_tree = null;
        CommonTree char_literal41_tree = null;
        CommonTree char_literal43_tree = null;

        try {
            // AdvancedExpression.g:100:2: ( string_itom ( ( '+' ) string_itom )* | SUBSTRING '(' string_itom ',' fourexpr ',' fourexpr ')' | TAILSTRING '(' string_itom ',' fourexpr ')' )
            int alt8 = 3;
            switch (input.LA(1)) {
                case STRINGVALUE:
                case STRINGID:
                case 28: {
                    alt8 = 1;
                }
                break;
                case SUBSTRING: {
                    alt8 = 2;
                }
                break;
                case TAILSTRING: {
                    alt8 = 3;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 8, 0, input);

                    throw nvae;
            }

            switch (alt8) {
                case 1:
                    // AdvancedExpression.g:100:4: string_itom ( ( '+' ) string_itom )*
                {
                    root_0 = (CommonTree) adaptor.nil();

                    pushFollow(FOLLOW_string_itom_in_string_fourexpr255);
                    string_itom27 = string_itom();

                    state._fsp--;

                    adaptor.addChild(root_0, string_itom27.getTree());
                    // AdvancedExpression.g:100:16: ( ( '+' ) string_itom )*
                    loop7:
                    do {
                        int alt7 = 2;
                        int LA7_0 = input.LA(1);

                        if ((LA7_0 == 20)) {
                            alt7 = 1;
                        }


                        switch (alt7) {
                            case 1:
                                // AdvancedExpression.g:100:17: ( '+' ) string_itom
                            {
                                // AdvancedExpression.g:100:17: ( '+' )
                                // AdvancedExpression.g:100:18: '+'
                                {
                                    char_literal28 = (Token) match(input, 20, FOLLOW_20_in_string_fourexpr259);
                                    char_literal28_tree = (CommonTree) adaptor.create(char_literal28);
                                    root_0 = (CommonTree) adaptor.becomeRoot(char_literal28_tree, root_0);


                                }

                                pushFollow(FOLLOW_string_itom_in_string_fourexpr263);
                                string_itom29 = string_itom();

                                state._fsp--;

                                adaptor.addChild(root_0, string_itom29.getTree());

                            }
                            break;

                            default:
                                break loop7;
                        }
                    } while (true);


                }
                break;
                case 2:
                    // AdvancedExpression.g:101:4: SUBSTRING '(' string_itom ',' fourexpr ',' fourexpr ')'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    SUBSTRING30 = (Token) match(input, SUBSTRING, FOLLOW_SUBSTRING_in_string_fourexpr270);
                    SUBSTRING30_tree = (CommonTree) adaptor.create(SUBSTRING30);
                    root_0 = (CommonTree) adaptor.becomeRoot(SUBSTRING30_tree, root_0);

                    char_literal31 = (Token) match(input, 25, FOLLOW_25_in_string_fourexpr273);
                    pushFollow(FOLLOW_string_itom_in_string_fourexpr276);
                    string_itom32 = string_itom();

                    state._fsp--;

                    adaptor.addChild(root_0, string_itom32.getTree());
                    char_literal33 = (Token) match(input, 27, FOLLOW_27_in_string_fourexpr278);
                    pushFollow(FOLLOW_fourexpr_in_string_fourexpr281);
                    fourexpr34 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr34.getTree());
                    char_literal35 = (Token) match(input, 27, FOLLOW_27_in_string_fourexpr283);
                    pushFollow(FOLLOW_fourexpr_in_string_fourexpr286);
                    fourexpr36 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr36.getTree());
                    char_literal37 = (Token) match(input, 26, FOLLOW_26_in_string_fourexpr288);

                }
                break;
                case 3:
                    // AdvancedExpression.g:102:4: TAILSTRING '(' string_itom ',' fourexpr ')'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    TAILSTRING38 = (Token) match(input, TAILSTRING, FOLLOW_TAILSTRING_in_string_fourexpr295);
                    TAILSTRING38_tree = (CommonTree) adaptor.create(TAILSTRING38);
                    root_0 = (CommonTree) adaptor.becomeRoot(TAILSTRING38_tree, root_0);

                    char_literal39 = (Token) match(input, 25, FOLLOW_25_in_string_fourexpr298);
                    pushFollow(FOLLOW_string_itom_in_string_fourexpr301);
                    string_itom40 = string_itom();

                    state._fsp--;

                    adaptor.addChild(root_0, string_itom40.getTree());
                    char_literal41 = (Token) match(input, 27, FOLLOW_27_in_string_fourexpr303);
                    pushFollow(FOLLOW_fourexpr_in_string_fourexpr306);
                    fourexpr42 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr42.getTree());
                    char_literal43 = (Token) match(input, 26, FOLLOW_26_in_string_fourexpr308);

                }
                break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "string_itom"
    // AdvancedExpression.g:105:1: string_itom : ( STRINGVALUE | STRINGID | '[' ( string_fourexpr | fourexpr ) ']' | '[' ifcondition ']' -> ^( IFWRAPPER ifcondition ) );
    public final string_itom_return string_itom() throws RecognitionException {
        string_itom_return retval = new string_itom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token STRINGVALUE44 = null;
        Token STRINGID45 = null;
        Token char_literal46 = null;
        Token char_literal49 = null;
        Token char_literal50 = null;
        Token char_literal52 = null;
        string_fourexpr_return string_fourexpr47 = null;

        fourexpr_return fourexpr48 = null;

        ifcondition_return ifcondition51 = null;


        CommonTree STRINGVALUE44_tree = null;
        CommonTree STRINGID45_tree = null;
        CommonTree char_literal46_tree = null;
        CommonTree char_literal49_tree = null;
        CommonTree char_literal50_tree = null;
        CommonTree char_literal52_tree = null;
        RewriteRuleTokenStream stream_28 = new RewriteRuleTokenStream(adaptor, "token 28");
        RewriteRuleTokenStream stream_29 = new RewriteRuleTokenStream(adaptor, "token 29");
        RewriteRuleSubtreeStream stream_ifcondition = new RewriteRuleSubtreeStream(adaptor, "rule ifcondition");
        try {
            // AdvancedExpression.g:106:2: ( STRINGVALUE | STRINGID | '[' ( string_fourexpr | fourexpr ) ']' | '[' ifcondition ']' -> ^( IFWRAPPER ifcondition ) )
            int alt10 = 4;
            switch (input.LA(1)) {
                case STRINGVALUE: {
                    alt10 = 1;
                }
                break;
                case STRINGID: {
                    alt10 = 2;
                }
                break;
                case 28: {
                    int LA10_3 = input.LA(2);

                    if (((LA10_3 >= DOUBLE && LA10_3 <= F2) || LA10_3 == 25 || LA10_3 == 28)) {
                        alt10 = 3;
                    } else if ((LA10_3 == 30)) {
                        alt10 = 4;
                    } else {
                        NoViableAltException nvae =
                                new NoViableAltException("", 10, 3, input);

                        throw nvae;
                    }
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 10, 0, input);

                    throw nvae;
            }

            switch (alt10) {
                case 1:
                    // AdvancedExpression.g:106:4: STRINGVALUE
                {
                    root_0 = (CommonTree) adaptor.nil();

                    STRINGVALUE44 = (Token) match(input, STRINGVALUE, FOLLOW_STRINGVALUE_in_string_itom321);
                    STRINGVALUE44_tree = (CommonTree) adaptor.create(STRINGVALUE44);
                    adaptor.addChild(root_0, STRINGVALUE44_tree);


                }
                break;
                case 2:
                    // AdvancedExpression.g:107:4: STRINGID
                {
                    root_0 = (CommonTree) adaptor.nil();

                    STRINGID45 = (Token) match(input, STRINGID, FOLLOW_STRINGID_in_string_itom326);
                    STRINGID45_tree = (CommonTree) adaptor.create(STRINGID45);
                    adaptor.addChild(root_0, STRINGID45_tree);


                }
                break;
                case 3:
                    // AdvancedExpression.g:108:5: '[' ( string_fourexpr | fourexpr ) ']'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    char_literal46 = (Token) match(input, 28, FOLLOW_28_in_string_itom332);
                    // AdvancedExpression.g:108:10: ( string_fourexpr | fourexpr )
                    int alt9 = 2;
                    int LA9_0 = input.LA(1);

                    if (((LA9_0 >= SUBSTRING && LA9_0 <= STRINGID) || LA9_0 == 28)) {
                        alt9 = 1;
                    } else if (((LA9_0 >= DOUBLE && LA9_0 <= ID) || (LA9_0 >= F0 && LA9_0 <= F2) || LA9_0 == 25)) {
                        alt9 = 2;
                    } else {
                        NoViableAltException nvae =
                                new NoViableAltException("", 9, 0, input);

                        throw nvae;
                    }
                    switch (alt9) {
                        case 1:
                            // AdvancedExpression.g:108:11: string_fourexpr
                        {
                            pushFollow(FOLLOW_string_fourexpr_in_string_itom336);
                            string_fourexpr47 = string_fourexpr();

                            state._fsp--;

                            adaptor.addChild(root_0, string_fourexpr47.getTree());

                        }
                        break;
                        case 2:
                            // AdvancedExpression.g:108:27: fourexpr
                        {
                            pushFollow(FOLLOW_fourexpr_in_string_itom338);
                            fourexpr48 = fourexpr();

                            state._fsp--;

                            adaptor.addChild(root_0, fourexpr48.getTree());

                        }
                        break;

                    }

                    char_literal49 = (Token) match(input, 29, FOLLOW_29_in_string_itom341);

                }
                break;
                case 4:
                    // AdvancedExpression.g:109:5: '[' ifcondition ']'
                {
                    char_literal50 = (Token) match(input, 28, FOLLOW_28_in_string_itom348);
                    stream_28.add(char_literal50);

                    pushFollow(FOLLOW_ifcondition_in_string_itom350);
                    ifcondition51 = ifcondition();

                    state._fsp--;

                    stream_ifcondition.add(ifcondition51.getTree());
                    char_literal52 = (Token) match(input, 29, FOLLOW_29_in_string_itom352);
                    stream_29.add(char_literal52);


                    // AST REWRITE
                    // elements: ifcondition
                    // token labels:
                    // rule labels: retval
                    // token list labels:
                    // rule list labels:
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "token retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) adaptor.nil();
                    // 109:25: -> ^( IFWRAPPER ifcondition )
                    {
                        // AdvancedExpression.g:109:28: ^( IFWRAPPER ifcondition )
                        {
                            CommonTree root_1 = (CommonTree) adaptor.nil();
                            root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(IFWRAPPER, "IFWRAPPER"), root_1);

                            adaptor.addChild(root_1, stream_ifcondition.nextTree());

                            adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
                break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "ifcondition"
    // AdvancedExpression.g:112:1: ifcondition : 'if' '(' condition ')' '{' expr '}' ( elseifcondition | elsecondition ) ;
    public final ifcondition_return ifcondition() throws RecognitionException {
        ifcondition_return retval = new ifcondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal53 = null;
        Token char_literal54 = null;
        Token char_literal56 = null;
        Token char_literal57 = null;
        Token char_literal59 = null;
        condition_return condition55 = null;

        expr_return expr58 = null;

        elseifcondition_return elseifcondition60 = null;

        elsecondition_return elsecondition61 = null;


        CommonTree string_literal53_tree = null;
        CommonTree char_literal54_tree = null;
        CommonTree char_literal56_tree = null;
        CommonTree char_literal57_tree = null;
        CommonTree char_literal59_tree = null;

        try {
            // AdvancedExpression.g:113:2: ( 'if' '(' condition ')' '{' expr '}' ( elseifcondition | elsecondition ) )
            // AdvancedExpression.g:113:4: 'if' '(' condition ')' '{' expr '}' ( elseifcondition | elsecondition )
            {
                root_0 = (CommonTree) adaptor.nil();

                string_literal53 = (Token) match(input, 30, FOLLOW_30_in_ifcondition371);
                string_literal53_tree = (CommonTree) adaptor.create(string_literal53);
                root_0 = (CommonTree) adaptor.becomeRoot(string_literal53_tree, root_0);

                char_literal54 = (Token) match(input, 25, FOLLOW_25_in_ifcondition374);
                pushFollow(FOLLOW_condition_in_ifcondition377);
                condition55 = condition();

                state._fsp--;

                adaptor.addChild(root_0, condition55.getTree());
                char_literal56 = (Token) match(input, 26, FOLLOW_26_in_ifcondition379);
                char_literal57 = (Token) match(input, 31, FOLLOW_31_in_ifcondition382);
                pushFollow(FOLLOW_expr_in_ifcondition385);
                expr58 = expr();

                state._fsp--;

                adaptor.addChild(root_0, expr58.getTree());
                char_literal59 = (Token) match(input, 32, FOLLOW_32_in_ifcondition387);
                // AdvancedExpression.g:113:45: ( elseifcondition | elsecondition )
                int alt11 = 2;
                int LA11_0 = input.LA(1);

                if ((LA11_0 == 33)) {
                    alt11 = 1;
                } else if ((LA11_0 == 34)) {
                    alt11 = 2;
                } else {
                    NoViableAltException nvae =
                            new NoViableAltException("", 11, 0, input);

                    throw nvae;
                }
                switch (alt11) {
                    case 1:
                        // AdvancedExpression.g:113:46: elseifcondition
                    {
                        pushFollow(FOLLOW_elseifcondition_in_ifcondition391);
                        elseifcondition60 = elseifcondition();

                        state._fsp--;

                        adaptor.addChild(root_0, elseifcondition60.getTree());

                    }
                    break;
                    case 2:
                        // AdvancedExpression.g:113:62: elsecondition
                    {
                        pushFollow(FOLLOW_elsecondition_in_ifcondition393);
                        elsecondition61 = elsecondition();

                        state._fsp--;

                        adaptor.addChild(root_0, elsecondition61.getTree());

                    }
                    break;

                }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "elseifcondition"
    // AdvancedExpression.g:116:1: elseifcondition : 'elseif' '(' condition ')' '{' expr '}' ( elseifcondition | elsecondition ) ;
    public final elseifcondition_return elseifcondition() throws RecognitionException {
        elseifcondition_return retval = new elseifcondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal62 = null;
        Token char_literal63 = null;
        Token char_literal65 = null;
        Token char_literal66 = null;
        Token char_literal68 = null;
        condition_return condition64 = null;

        expr_return expr67 = null;

        elseifcondition_return elseifcondition69 = null;

        elsecondition_return elsecondition70 = null;


        CommonTree string_literal62_tree = null;
        CommonTree char_literal63_tree = null;
        CommonTree char_literal65_tree = null;
        CommonTree char_literal66_tree = null;
        CommonTree char_literal68_tree = null;

        try {
            // AdvancedExpression.g:117:2: ( 'elseif' '(' condition ')' '{' expr '}' ( elseifcondition | elsecondition ) )
            // AdvancedExpression.g:117:4: 'elseif' '(' condition ')' '{' expr '}' ( elseifcondition | elsecondition )
            {
                root_0 = (CommonTree) adaptor.nil();

                string_literal62 = (Token) match(input, 33, FOLLOW_33_in_elseifcondition405);
                string_literal62_tree = (CommonTree) adaptor.create(string_literal62);
                root_0 = (CommonTree) adaptor.becomeRoot(string_literal62_tree, root_0);

                char_literal63 = (Token) match(input, 25, FOLLOW_25_in_elseifcondition408);
                pushFollow(FOLLOW_condition_in_elseifcondition411);
                condition64 = condition();

                state._fsp--;

                adaptor.addChild(root_0, condition64.getTree());
                char_literal65 = (Token) match(input, 26, FOLLOW_26_in_elseifcondition413);
                char_literal66 = (Token) match(input, 31, FOLLOW_31_in_elseifcondition416);
                pushFollow(FOLLOW_expr_in_elseifcondition419);
                expr67 = expr();

                state._fsp--;

                adaptor.addChild(root_0, expr67.getTree());
                char_literal68 = (Token) match(input, 32, FOLLOW_32_in_elseifcondition421);
                // AdvancedExpression.g:117:49: ( elseifcondition | elsecondition )
                int alt12 = 2;
                int LA12_0 = input.LA(1);

                if ((LA12_0 == 33)) {
                    alt12 = 1;
                } else if ((LA12_0 == 34)) {
                    alt12 = 2;
                } else {
                    NoViableAltException nvae =
                            new NoViableAltException("", 12, 0, input);

                    throw nvae;
                }
                switch (alt12) {
                    case 1:
                        // AdvancedExpression.g:117:50: elseifcondition
                    {
                        pushFollow(FOLLOW_elseifcondition_in_elseifcondition425);
                        elseifcondition69 = elseifcondition();

                        state._fsp--;

                        adaptor.addChild(root_0, elseifcondition69.getTree());

                    }
                    break;
                    case 2:
                        // AdvancedExpression.g:117:66: elsecondition
                    {
                        pushFollow(FOLLOW_elsecondition_in_elseifcondition427);
                        elsecondition70 = elsecondition();

                        state._fsp--;

                        adaptor.addChild(root_0, elsecondition70.getTree());

                    }
                    break;

                }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "elsecondition"
    // AdvancedExpression.g:120:1: elsecondition : 'else' '{' expr '}' ;
    public final elsecondition_return elsecondition() throws RecognitionException {
        elsecondition_return retval = new elsecondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal71 = null;
        Token char_literal72 = null;
        Token char_literal74 = null;
        expr_return expr73 = null;


        CommonTree string_literal71_tree = null;
        CommonTree char_literal72_tree = null;
        CommonTree char_literal74_tree = null;

        try {
            // AdvancedExpression.g:121:2: ( 'else' '{' expr '}' )
            // AdvancedExpression.g:121:4: 'else' '{' expr '}'
            {
                root_0 = (CommonTree) adaptor.nil();

                string_literal71 = (Token) match(input, 34, FOLLOW_34_in_elsecondition439);
                string_literal71_tree = (CommonTree) adaptor.create(string_literal71);
                root_0 = (CommonTree) adaptor.becomeRoot(string_literal71_tree, root_0);

                char_literal72 = (Token) match(input, 31, FOLLOW_31_in_elsecondition442);
                pushFollow(FOLLOW_expr_in_elsecondition445);
                expr73 = expr();

                state._fsp--;

                adaptor.addChild(root_0, expr73.getTree());
                char_literal74 = (Token) match(input, 32, FOLLOW_32_in_elsecondition447);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "condition"
    // AdvancedExpression.g:124:1: condition : andcondition ( ( '||' ) andcondition )* ;
    public final condition_return condition() throws RecognitionException {
        condition_return retval = new condition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal76 = null;
        andcondition_return andcondition75 = null;

        andcondition_return andcondition77 = null;


        CommonTree string_literal76_tree = null;

        try {
            // AdvancedExpression.g:125:2: ( andcondition ( ( '||' ) andcondition )* )
            // AdvancedExpression.g:125:4: andcondition ( ( '||' ) andcondition )*
            {
                root_0 = (CommonTree) adaptor.nil();

                pushFollow(FOLLOW_andcondition_in_condition459);
                andcondition75 = andcondition();

                state._fsp--;

                adaptor.addChild(root_0, andcondition75.getTree());
                // AdvancedExpression.g:125:17: ( ( '||' ) andcondition )*
                loop13:
                do {
                    int alt13 = 2;
                    int LA13_0 = input.LA(1);

                    if ((LA13_0 == 35)) {
                        alt13 = 1;
                    }


                    switch (alt13) {
                        case 1:
                            // AdvancedExpression.g:125:18: ( '||' ) andcondition
                        {
                            // AdvancedExpression.g:125:18: ( '||' )
                            // AdvancedExpression.g:125:19: '||'
                            {
                                string_literal76 = (Token) match(input, 35, FOLLOW_35_in_condition463);
                                string_literal76_tree = (CommonTree) adaptor.create(string_literal76);
                                root_0 = (CommonTree) adaptor.becomeRoot(string_literal76_tree, root_0);


                            }

                            pushFollow(FOLLOW_andcondition_in_condition467);
                            andcondition77 = andcondition();

                            state._fsp--;

                            adaptor.addChild(root_0, andcondition77.getTree());

                        }
                        break;

                        default:
                            break loop13;
                    }
                } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "andcondition"
    // AdvancedExpression.g:128:1: andcondition : notcondition ( ( '&&' ) notcondition )* ;
    public final andcondition_return andcondition() throws RecognitionException {
        andcondition_return retval = new andcondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal79 = null;
        notcondition_return notcondition78 = null;

        notcondition_return notcondition80 = null;


        CommonTree string_literal79_tree = null;

        try {
            // AdvancedExpression.g:129:2: ( notcondition ( ( '&&' ) notcondition )* )
            // AdvancedExpression.g:129:4: notcondition ( ( '&&' ) notcondition )*
            {
                root_0 = (CommonTree) adaptor.nil();

                pushFollow(FOLLOW_notcondition_in_andcondition480);
                notcondition78 = notcondition();

                state._fsp--;

                adaptor.addChild(root_0, notcondition78.getTree());
                // AdvancedExpression.g:129:17: ( ( '&&' ) notcondition )*
                loop14:
                do {
                    int alt14 = 2;
                    int LA14_0 = input.LA(1);

                    if ((LA14_0 == 36)) {
                        alt14 = 1;
                    }


                    switch (alt14) {
                        case 1:
                            // AdvancedExpression.g:129:18: ( '&&' ) notcondition
                        {
                            // AdvancedExpression.g:129:18: ( '&&' )
                            // AdvancedExpression.g:129:19: '&&'
                            {
                                string_literal79 = (Token) match(input, 36, FOLLOW_36_in_andcondition484);
                                string_literal79_tree = (CommonTree) adaptor.create(string_literal79);
                                root_0 = (CommonTree) adaptor.becomeRoot(string_literal79_tree, root_0);


                            }

                            pushFollow(FOLLOW_notcondition_in_andcondition488);
                            notcondition80 = notcondition();

                            state._fsp--;

                            adaptor.addChild(root_0, notcondition80.getTree());

                        }
                        break;

                        default:
                            break loop14;
                    }
                } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "notcondition"
    // AdvancedExpression.g:132:1: notcondition : ( ( '!' ) itemcondition | itemcondition );
    public final notcondition_return notcondition() throws RecognitionException {
        notcondition_return retval = new notcondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal81 = null;
        itemcondition_return itemcondition82 = null;

        itemcondition_return itemcondition83 = null;


        CommonTree char_literal81_tree = null;

        try {
            // AdvancedExpression.g:133:2: ( ( '!' ) itemcondition | itemcondition )
            int alt15 = 2;
            int LA15_0 = input.LA(1);

            if ((LA15_0 == 37)) {
                alt15 = 1;
            } else if ((LA15_0 == 25 || LA15_0 == 28)) {
                alt15 = 2;
            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1:
                    // AdvancedExpression.g:133:4: ( '!' ) itemcondition
                {
                    root_0 = (CommonTree) adaptor.nil();

                    // AdvancedExpression.g:133:4: ( '!' )
                    // AdvancedExpression.g:133:5: '!'
                    {
                        char_literal81 = (Token) match(input, 37, FOLLOW_37_in_notcondition502);
                        char_literal81_tree = (CommonTree) adaptor.create(char_literal81);
                        root_0 = (CommonTree) adaptor.becomeRoot(char_literal81_tree, root_0);


                    }

                    pushFollow(FOLLOW_itemcondition_in_notcondition506);
                    itemcondition82 = itemcondition();

                    state._fsp--;

                    adaptor.addChild(root_0, itemcondition82.getTree());

                }
                break;
                case 2:
                    // AdvancedExpression.g:134:4: itemcondition
                {
                    root_0 = (CommonTree) adaptor.nil();

                    pushFollow(FOLLOW_itemcondition_in_notcondition511);
                    itemcondition83 = itemcondition();

                    state._fsp--;

                    adaptor.addChild(root_0, itemcondition83.getTree());

                }
                break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "itemcondition"
    // AdvancedExpression.g:137:1: itemcondition : ( '(' condition ')' | comparecondition );
    public final itemcondition_return itemcondition() throws RecognitionException {
        itemcondition_return retval = new itemcondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal84 = null;
        Token char_literal86 = null;
        condition_return condition85 = null;

        comparecondition_return comparecondition87 = null;


        CommonTree char_literal84_tree = null;
        CommonTree char_literal86_tree = null;

        try {
            // AdvancedExpression.g:138:2: ( '(' condition ')' | comparecondition )
            int alt16 = 2;
            int LA16_0 = input.LA(1);

            if ((LA16_0 == 25)) {
                alt16 = 1;
            } else if ((LA16_0 == 28)) {
                alt16 = 2;
            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1:
                    // AdvancedExpression.g:138:4: '(' condition ')'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    char_literal84 = (Token) match(input, 25, FOLLOW_25_in_itemcondition522);
                    pushFollow(FOLLOW_condition_in_itemcondition525);
                    condition85 = condition();

                    state._fsp--;

                    adaptor.addChild(root_0, condition85.getTree());
                    char_literal86 = (Token) match(input, 26, FOLLOW_26_in_itemcondition527);

                }
                break;
                case 2:
                    // AdvancedExpression.g:139:4: comparecondition
                {
                    root_0 = (CommonTree) adaptor.nil();

                    pushFollow(FOLLOW_comparecondition_in_itemcondition533);
                    comparecondition87 = comparecondition();

                    state._fsp--;

                    adaptor.addChild(root_0, comparecondition87.getTree());

                }
                break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "comparecondition"
    // AdvancedExpression.g:142:1: comparecondition : ( '[' NULL ( '==' | '!=' ) fourexpr ']' | '[' fourexpr ( '<' | '<=' | '>' | '>=' | '==' | '!=' ) fourexpr ']' | string_comparecondition -> ^( STRINGWRAPPER string_comparecondition ) );
    public final comparecondition_return comparecondition() throws RecognitionException {
        comparecondition_return retval = new comparecondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal88 = null;
        Token NULL89 = null;
        Token string_literal90 = null;
        Token string_literal91 = null;
        Token char_literal93 = null;
        Token char_literal94 = null;
        Token char_literal96 = null;
        Token string_literal97 = null;
        Token char_literal98 = null;
        Token string_literal99 = null;
        Token string_literal100 = null;
        Token string_literal101 = null;
        Token char_literal103 = null;
        fourexpr_return fourexpr92 = null;

        fourexpr_return fourexpr95 = null;

        fourexpr_return fourexpr102 = null;

        string_comparecondition_return string_comparecondition104 = null;


        CommonTree char_literal88_tree = null;
        CommonTree NULL89_tree = null;
        CommonTree string_literal90_tree = null;
        CommonTree string_literal91_tree = null;
        CommonTree char_literal93_tree = null;
        CommonTree char_literal94_tree = null;
        CommonTree char_literal96_tree = null;
        CommonTree string_literal97_tree = null;
        CommonTree char_literal98_tree = null;
        CommonTree string_literal99_tree = null;
        CommonTree string_literal100_tree = null;
        CommonTree string_literal101_tree = null;
        CommonTree char_literal103_tree = null;
        RewriteRuleSubtreeStream stream_string_comparecondition = new RewriteRuleSubtreeStream(adaptor, "rule string_comparecondition");
        try {
            // AdvancedExpression.g:143:2: ( '[' NULL ( '==' | '!=' ) fourexpr ']' | '[' fourexpr ( '<' | '<=' | '>' | '>=' | '==' | '!=' ) fourexpr ']' | string_comparecondition -> ^( STRINGWRAPPER string_comparecondition ) )
            int alt19 = 3;
            int LA19_0 = input.LA(1);

            if ((LA19_0 == 28)) {
                switch (input.LA(2)) {
                    case NULL: {
                        int LA19_2 = input.LA(3);

                        if ((LA19_2 == 38)) {
                            int LA19_5 = input.LA(4);

                            if (((LA19_5 >= SUBSTRING && LA19_5 <= STRINGID) || LA19_5 == 28)) {
                                alt19 = 3;
                            } else if (((LA19_5 >= DOUBLE && LA19_5 <= ID) || (LA19_5 >= F0 && LA19_5 <= F2) || LA19_5 == 25)) {
                                alt19 = 1;
                            } else {
                                NoViableAltException nvae =
                                        new NoViableAltException("", 19, 5, input);

                                throw nvae;
                            }
                        } else if ((LA19_2 == 39)) {
                            int LA19_6 = input.LA(4);

                            if (((LA19_6 >= SUBSTRING && LA19_6 <= STRINGID) || LA19_6 == 28)) {
                                alt19 = 3;
                            } else if (((LA19_6 >= DOUBLE && LA19_6 <= ID) || (LA19_6 >= F0 && LA19_6 <= F2) || LA19_6 == 25)) {
                                alt19 = 1;
                            } else {
                                NoViableAltException nvae =
                                        new NoViableAltException("", 19, 6, input);

                                throw nvae;
                            }
                        } else {
                            NoViableAltException nvae =
                                    new NoViableAltException("", 19, 2, input);

                            throw nvae;
                        }
                    }
                    break;
                    case DOUBLE:
                    case INTEGER:
                    case CONSTANT:
                    case ID:
                    case F0:
                    case F1:
                    case F2:
                    case 25: {
                        alt19 = 2;
                    }
                    break;
                    case SUBSTRING:
                    case TAILSTRING:
                    case STRINGVALUE:
                    case STRINGID:
                    case 28:
                    case 44:
                    case 45: {
                        alt19 = 3;
                    }
                    break;
                    default:
                        NoViableAltException nvae =
                                new NoViableAltException("", 19, 1, input);

                        throw nvae;
                }

            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1:
                    // AdvancedExpression.g:143:4: '[' NULL ( '==' | '!=' ) fourexpr ']'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    char_literal88 = (Token) match(input, 28, FOLLOW_28_in_comparecondition544);
                    NULL89 = (Token) match(input, NULL, FOLLOW_NULL_in_comparecondition547);
                    NULL89_tree = (CommonTree) adaptor.create(NULL89);
                    adaptor.addChild(root_0, NULL89_tree);

                    // AdvancedExpression.g:143:14: ( '==' | '!=' )
                    int alt17 = 2;
                    int LA17_0 = input.LA(1);

                    if ((LA17_0 == 38)) {
                        alt17 = 1;
                    } else if ((LA17_0 == 39)) {
                        alt17 = 2;
                    } else {
                        NoViableAltException nvae =
                                new NoViableAltException("", 17, 0, input);

                        throw nvae;
                    }
                    switch (alt17) {
                        case 1:
                            // AdvancedExpression.g:143:15: '=='
                        {
                            string_literal90 = (Token) match(input, 38, FOLLOW_38_in_comparecondition550);
                            string_literal90_tree = (CommonTree) adaptor.create(string_literal90);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal90_tree, root_0);


                        }
                        break;
                        case 2:
                            // AdvancedExpression.g:143:21: '!='
                        {
                            string_literal91 = (Token) match(input, 39, FOLLOW_39_in_comparecondition553);
                            string_literal91_tree = (CommonTree) adaptor.create(string_literal91);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal91_tree, root_0);


                        }
                        break;

                    }

                    pushFollow(FOLLOW_fourexpr_in_comparecondition557);
                    fourexpr92 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr92.getTree());
                    char_literal93 = (Token) match(input, 29, FOLLOW_29_in_comparecondition559);

                }
                break;
                case 2:
                    // AdvancedExpression.g:144:4: '[' fourexpr ( '<' | '<=' | '>' | '>=' | '==' | '!=' ) fourexpr ']'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    char_literal94 = (Token) match(input, 28, FOLLOW_28_in_comparecondition565);
                    pushFollow(FOLLOW_fourexpr_in_comparecondition568);
                    fourexpr95 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr95.getTree());
                    // AdvancedExpression.g:144:18: ( '<' | '<=' | '>' | '>=' | '==' | '!=' )
                    int alt18 = 6;
                    switch (input.LA(1)) {
                        case 40: {
                            alt18 = 1;
                        }
                        break;
                        case 41: {
                            alt18 = 2;
                        }
                        break;
                        case 42: {
                            alt18 = 3;
                        }
                        break;
                        case 43: {
                            alt18 = 4;
                        }
                        break;
                        case 38: {
                            alt18 = 5;
                        }
                        break;
                        case 39: {
                            alt18 = 6;
                        }
                        break;
                        default:
                            NoViableAltException nvae =
                                    new NoViableAltException("", 18, 0, input);

                            throw nvae;
                    }

                    switch (alt18) {
                        case 1:
                            // AdvancedExpression.g:144:19: '<'
                        {
                            char_literal96 = (Token) match(input, 40, FOLLOW_40_in_comparecondition571);
                            char_literal96_tree = (CommonTree) adaptor.create(char_literal96);
                            root_0 = (CommonTree) adaptor.becomeRoot(char_literal96_tree, root_0);


                        }
                        break;
                        case 2:
                            // AdvancedExpression.g:144:24: '<='
                        {
                            string_literal97 = (Token) match(input, 41, FOLLOW_41_in_comparecondition574);
                            string_literal97_tree = (CommonTree) adaptor.create(string_literal97);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal97_tree, root_0);


                        }
                        break;
                        case 3:
                            // AdvancedExpression.g:144:30: '>'
                        {
                            char_literal98 = (Token) match(input, 42, FOLLOW_42_in_comparecondition577);
                            char_literal98_tree = (CommonTree) adaptor.create(char_literal98);
                            root_0 = (CommonTree) adaptor.becomeRoot(char_literal98_tree, root_0);


                        }
                        break;
                        case 4:
                            // AdvancedExpression.g:144:35: '>='
                        {
                            string_literal99 = (Token) match(input, 43, FOLLOW_43_in_comparecondition580);
                            string_literal99_tree = (CommonTree) adaptor.create(string_literal99);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal99_tree, root_0);


                        }
                        break;
                        case 5:
                            // AdvancedExpression.g:144:41: '=='
                        {
                            string_literal100 = (Token) match(input, 38, FOLLOW_38_in_comparecondition583);
                            string_literal100_tree = (CommonTree) adaptor.create(string_literal100);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal100_tree, root_0);


                        }
                        break;
                        case 6:
                            // AdvancedExpression.g:144:47: '!='
                        {
                            string_literal101 = (Token) match(input, 39, FOLLOW_39_in_comparecondition586);
                            string_literal101_tree = (CommonTree) adaptor.create(string_literal101);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal101_tree, root_0);


                        }
                        break;

                    }

                    pushFollow(FOLLOW_fourexpr_in_comparecondition590);
                    fourexpr102 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr102.getTree());
                    char_literal103 = (Token) match(input, 29, FOLLOW_29_in_comparecondition592);

                }
                break;
                case 3:
                    // AdvancedExpression.g:145:4: string_comparecondition
                {
                    pushFollow(FOLLOW_string_comparecondition_in_comparecondition598);
                    string_comparecondition104 = string_comparecondition();

                    state._fsp--;

                    stream_string_comparecondition.add(string_comparecondition104.getTree());


                    // AST REWRITE
                    // elements: string_comparecondition
                    // token labels:
                    // rule labels: retval
                    // token list labels:
                    // rule list labels:
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "token retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) adaptor.nil();
                    // 145:28: -> ^( STRINGWRAPPER string_comparecondition )
                    {
                        // AdvancedExpression.g:145:31: ^( STRINGWRAPPER string_comparecondition )
                        {
                            CommonTree root_1 = (CommonTree) adaptor.nil();
                            root_1 = (CommonTree) adaptor.becomeRoot((CommonTree) adaptor.create(STRINGWRAPPER, "STRINGWRAPPER"), root_1);

                            adaptor.addChild(root_1, stream_string_comparecondition.nextTree());

                            adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
                break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "string_comparecondition"
    // AdvancedExpression.g:148:1: string_comparecondition : ( '[' NULL ( '==' | '!=' ) string_fourexpr ']' | '[' ( 'contains' | 'match' ) '(' string_fourexpr ',' string_fourexpr ')' ']' | '[' string_fourexpr ( '<' | '<=' | '>' | '>=' | '==' | '!=' ) string_fourexpr ']' );
    public final string_comparecondition_return string_comparecondition() throws RecognitionException {
        string_comparecondition_return retval = new string_comparecondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal105 = null;
        Token NULL106 = null;
        Token string_literal107 = null;
        Token string_literal108 = null;
        Token char_literal110 = null;
        Token char_literal111 = null;
        Token string_literal112 = null;
        Token string_literal113 = null;
        Token char_literal114 = null;
        Token char_literal116 = null;
        Token char_literal118 = null;
        Token char_literal119 = null;
        Token char_literal120 = null;
        Token char_literal122 = null;
        Token string_literal123 = null;
        Token char_literal124 = null;
        Token string_literal125 = null;
        Token string_literal126 = null;
        Token string_literal127 = null;
        Token char_literal129 = null;
        string_fourexpr_return string_fourexpr109 = null;

        string_fourexpr_return string_fourexpr115 = null;

        string_fourexpr_return string_fourexpr117 = null;

        string_fourexpr_return string_fourexpr121 = null;

        string_fourexpr_return string_fourexpr128 = null;


        CommonTree char_literal105_tree = null;
        CommonTree NULL106_tree = null;
        CommonTree string_literal107_tree = null;
        CommonTree string_literal108_tree = null;
        CommonTree char_literal110_tree = null;
        CommonTree char_literal111_tree = null;
        CommonTree string_literal112_tree = null;
        CommonTree string_literal113_tree = null;
        CommonTree char_literal114_tree = null;
        CommonTree char_literal116_tree = null;
        CommonTree char_literal118_tree = null;
        CommonTree char_literal119_tree = null;
        CommonTree char_literal120_tree = null;
        CommonTree char_literal122_tree = null;
        CommonTree string_literal123_tree = null;
        CommonTree char_literal124_tree = null;
        CommonTree string_literal125_tree = null;
        CommonTree string_literal126_tree = null;
        CommonTree string_literal127_tree = null;
        CommonTree char_literal129_tree = null;

        try {
            // AdvancedExpression.g:149:2: ( '[' NULL ( '==' | '!=' ) string_fourexpr ']' | '[' ( 'contains' | 'match' ) '(' string_fourexpr ',' string_fourexpr ')' ']' | '[' string_fourexpr ( '<' | '<=' | '>' | '>=' | '==' | '!=' ) string_fourexpr ']' )
            int alt23 = 3;
            int LA23_0 = input.LA(1);

            if ((LA23_0 == 28)) {
                switch (input.LA(2)) {
                    case NULL: {
                        alt23 = 1;
                    }
                    break;
                    case SUBSTRING:
                    case TAILSTRING:
                    case STRINGVALUE:
                    case STRINGID:
                    case 28: {
                        alt23 = 3;
                    }
                    break;
                    case 44:
                    case 45: {
                        alt23 = 2;
                    }
                    break;
                    default:
                        NoViableAltException nvae =
                                new NoViableAltException("", 23, 1, input);

                        throw nvae;
                }

            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1:
                    // AdvancedExpression.g:149:4: '[' NULL ( '==' | '!=' ) string_fourexpr ']'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    char_literal105 = (Token) match(input, 28, FOLLOW_28_in_string_comparecondition617);
                    NULL106 = (Token) match(input, NULL, FOLLOW_NULL_in_string_comparecondition620);
                    NULL106_tree = (CommonTree) adaptor.create(NULL106);
                    adaptor.addChild(root_0, NULL106_tree);

                    // AdvancedExpression.g:149:14: ( '==' | '!=' )
                    int alt20 = 2;
                    int LA20_0 = input.LA(1);

                    if ((LA20_0 == 38)) {
                        alt20 = 1;
                    } else if ((LA20_0 == 39)) {
                        alt20 = 2;
                    } else {
                        NoViableAltException nvae =
                                new NoViableAltException("", 20, 0, input);

                        throw nvae;
                    }
                    switch (alt20) {
                        case 1:
                            // AdvancedExpression.g:149:15: '=='
                        {
                            string_literal107 = (Token) match(input, 38, FOLLOW_38_in_string_comparecondition623);
                            string_literal107_tree = (CommonTree) adaptor.create(string_literal107);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal107_tree, root_0);


                        }
                        break;
                        case 2:
                            // AdvancedExpression.g:149:21: '!='
                        {
                            string_literal108 = (Token) match(input, 39, FOLLOW_39_in_string_comparecondition626);
                            string_literal108_tree = (CommonTree) adaptor.create(string_literal108);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal108_tree, root_0);


                        }
                        break;

                    }

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition630);
                    string_fourexpr109 = string_fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, string_fourexpr109.getTree());
                    char_literal110 = (Token) match(input, 29, FOLLOW_29_in_string_comparecondition632);

                }
                break;
                case 2:
                    // AdvancedExpression.g:150:4: '[' ( 'contains' | 'match' ) '(' string_fourexpr ',' string_fourexpr ')' ']'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    char_literal111 = (Token) match(input, 28, FOLLOW_28_in_string_comparecondition638);
                    // AdvancedExpression.g:150:9: ( 'contains' | 'match' )
                    int alt21 = 2;
                    int LA21_0 = input.LA(1);

                    if ((LA21_0 == 44)) {
                        alt21 = 1;
                    } else if ((LA21_0 == 45)) {
                        alt21 = 2;
                    } else {
                        NoViableAltException nvae =
                                new NoViableAltException("", 21, 0, input);

                        throw nvae;
                    }
                    switch (alt21) {
                        case 1:
                            // AdvancedExpression.g:150:10: 'contains'
                        {
                            string_literal112 = (Token) match(input, 44, FOLLOW_44_in_string_comparecondition642);
                            string_literal112_tree = (CommonTree) adaptor.create(string_literal112);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal112_tree, root_0);


                        }
                        break;
                        case 2:
                            // AdvancedExpression.g:150:22: 'match'
                        {
                            string_literal113 = (Token) match(input, 45, FOLLOW_45_in_string_comparecondition645);
                            string_literal113_tree = (CommonTree) adaptor.create(string_literal113);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal113_tree, root_0);


                        }
                        break;

                    }

                    char_literal114 = (Token) match(input, 25, FOLLOW_25_in_string_comparecondition649);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition652);
                    string_fourexpr115 = string_fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, string_fourexpr115.getTree());
                    char_literal116 = (Token) match(input, 27, FOLLOW_27_in_string_comparecondition654);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition657);
                    string_fourexpr117 = string_fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, string_fourexpr117.getTree());
                    char_literal118 = (Token) match(input, 26, FOLLOW_26_in_string_comparecondition659);
                    char_literal119 = (Token) match(input, 29, FOLLOW_29_in_string_comparecondition662);

                }
                break;
                case 3:
                    // AdvancedExpression.g:151:4: '[' string_fourexpr ( '<' | '<=' | '>' | '>=' | '==' | '!=' ) string_fourexpr ']'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    char_literal120 = (Token) match(input, 28, FOLLOW_28_in_string_comparecondition668);
                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition671);
                    string_fourexpr121 = string_fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, string_fourexpr121.getTree());
                    // AdvancedExpression.g:151:25: ( '<' | '<=' | '>' | '>=' | '==' | '!=' )
                    int alt22 = 6;
                    switch (input.LA(1)) {
                        case 40: {
                            alt22 = 1;
                        }
                        break;
                        case 41: {
                            alt22 = 2;
                        }
                        break;
                        case 42: {
                            alt22 = 3;
                        }
                        break;
                        case 43: {
                            alt22 = 4;
                        }
                        break;
                        case 38: {
                            alt22 = 5;
                        }
                        break;
                        case 39: {
                            alt22 = 6;
                        }
                        break;
                        default:
                            NoViableAltException nvae =
                                    new NoViableAltException("", 22, 0, input);

                            throw nvae;
                    }

                    switch (alt22) {
                        case 1:
                            // AdvancedExpression.g:151:26: '<'
                        {
                            char_literal122 = (Token) match(input, 40, FOLLOW_40_in_string_comparecondition674);
                            char_literal122_tree = (CommonTree) adaptor.create(char_literal122);
                            root_0 = (CommonTree) adaptor.becomeRoot(char_literal122_tree, root_0);


                        }
                        break;
                        case 2:
                            // AdvancedExpression.g:151:31: '<='
                        {
                            string_literal123 = (Token) match(input, 41, FOLLOW_41_in_string_comparecondition677);
                            string_literal123_tree = (CommonTree) adaptor.create(string_literal123);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal123_tree, root_0);


                        }
                        break;
                        case 3:
                            // AdvancedExpression.g:151:37: '>'
                        {
                            char_literal124 = (Token) match(input, 42, FOLLOW_42_in_string_comparecondition680);
                            char_literal124_tree = (CommonTree) adaptor.create(char_literal124);
                            root_0 = (CommonTree) adaptor.becomeRoot(char_literal124_tree, root_0);


                        }
                        break;
                        case 4:
                            // AdvancedExpression.g:151:42: '>='
                        {
                            string_literal125 = (Token) match(input, 43, FOLLOW_43_in_string_comparecondition683);
                            string_literal125_tree = (CommonTree) adaptor.create(string_literal125);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal125_tree, root_0);


                        }
                        break;
                        case 5:
                            // AdvancedExpression.g:151:48: '=='
                        {
                            string_literal126 = (Token) match(input, 38, FOLLOW_38_in_string_comparecondition686);
                            string_literal126_tree = (CommonTree) adaptor.create(string_literal126);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal126_tree, root_0);


                        }
                        break;
                        case 6:
                            // AdvancedExpression.g:151:54: '!='
                        {
                            string_literal127 = (Token) match(input, 39, FOLLOW_39_in_string_comparecondition689);
                            string_literal127_tree = (CommonTree) adaptor.create(string_literal127);
                            root_0 = (CommonTree) adaptor.becomeRoot(string_literal127_tree, root_0);


                        }
                        break;

                    }

                    pushFollow(FOLLOW_string_fourexpr_in_string_comparecondition693);
                    string_fourexpr128 = string_fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, string_fourexpr128.getTree());
                    char_literal129 = (Token) match(input, 29, FOLLOW_29_in_string_comparecondition695);

                }
                break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    // $ANTLR start "func"
    // AdvancedExpression.g:154:1: func : ( F0 '(' ')' | F1 '(' fourexpr ')' | F2 '(' fourexpr ',' fourexpr ')' );
    public final func_return func() throws RecognitionException {
        func_return retval = new func_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token F0130 = null;
        Token char_literal131 = null;
        Token char_literal132 = null;
        Token F1133 = null;
        Token char_literal134 = null;
        Token char_literal136 = null;
        Token F2137 = null;
        Token char_literal138 = null;
        Token char_literal140 = null;
        Token char_literal142 = null;
        fourexpr_return fourexpr135 = null;

        fourexpr_return fourexpr139 = null;

        fourexpr_return fourexpr141 = null;


        CommonTree F0130_tree = null;
        CommonTree char_literal131_tree = null;
        CommonTree char_literal132_tree = null;
        CommonTree F1133_tree = null;
        CommonTree char_literal134_tree = null;
        CommonTree char_literal136_tree = null;
        CommonTree F2137_tree = null;
        CommonTree char_literal138_tree = null;
        CommonTree char_literal140_tree = null;
        CommonTree char_literal142_tree = null;

        try {
            // AdvancedExpression.g:155:2: ( F0 '(' ')' | F1 '(' fourexpr ')' | F2 '(' fourexpr ',' fourexpr ')' )
            int alt24 = 3;
            switch (input.LA(1)) {
                case F0: {
                    alt24 = 1;
                }
                break;
                case F1: {
                    alt24 = 2;
                }
                break;
                case F2: {
                    alt24 = 3;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 24, 0, input);

                    throw nvae;
            }

            switch (alt24) {
                case 1:
                    // AdvancedExpression.g:155:4: F0 '(' ')'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    F0130 = (Token) match(input, F0, FOLLOW_F0_in_func707);
                    F0130_tree = (CommonTree) adaptor.create(F0130);
                    root_0 = (CommonTree) adaptor.becomeRoot(F0130_tree, root_0);

                    char_literal131 = (Token) match(input, 25, FOLLOW_25_in_func710);
                    char_literal132 = (Token) match(input, 26, FOLLOW_26_in_func713);

                }
                break;
                case 2:
                    // AdvancedExpression.g:156:4: F1 '(' fourexpr ')'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    F1133 = (Token) match(input, F1, FOLLOW_F1_in_func719);
                    F1133_tree = (CommonTree) adaptor.create(F1133);
                    root_0 = (CommonTree) adaptor.becomeRoot(F1133_tree, root_0);

                    char_literal134 = (Token) match(input, 25, FOLLOW_25_in_func722);
                    pushFollow(FOLLOW_fourexpr_in_func725);
                    fourexpr135 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr135.getTree());
                    char_literal136 = (Token) match(input, 26, FOLLOW_26_in_func727);

                }
                break;
                case 3:
                    // AdvancedExpression.g:157:4: F2 '(' fourexpr ',' fourexpr ')'
                {
                    root_0 = (CommonTree) adaptor.nil();

                    F2137 = (Token) match(input, F2, FOLLOW_F2_in_func733);
                    F2137_tree = (CommonTree) adaptor.create(F2137);
                    root_0 = (CommonTree) adaptor.becomeRoot(F2137_tree, root_0);

                    char_literal138 = (Token) match(input, 25, FOLLOW_25_in_func736);
                    pushFollow(FOLLOW_fourexpr_in_func739);
                    fourexpr139 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr139.getTree());
                    char_literal140 = (Token) match(input, 27, FOLLOW_27_in_func741);
                    pushFollow(FOLLOW_fourexpr_in_func744);
                    fourexpr141 = fourexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, fourexpr141.getTree());
                    char_literal142 = (Token) match(input, 26, FOLLOW_26_in_func746);

                }
                break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        } catch (RecognitionException e) {
            throw e;
        } finally {
        }
        return retval;
    }

    public static class prog_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class expr_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class fourexpr_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class multexpr_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class atom_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class string_fourexpr_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class string_itom_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class ifcondition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class elseifcondition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class elsecondition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class condition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class andcondition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class notcondition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class itemcondition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class comparecondition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class string_comparecondition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

    public static class func_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return tree;
        }
    }

}