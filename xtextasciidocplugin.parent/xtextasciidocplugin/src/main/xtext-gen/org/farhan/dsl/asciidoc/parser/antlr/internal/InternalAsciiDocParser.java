package org.farhan.dsl.asciidoc.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalAsciiDocParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_EOL", "RULE_TEXT_BLOCK", "RULE_WORD", "RULE_WS", "RULE_SL_COMMENT", "'='", "'Step-Object:'", "'=='", "'Step-Definition:'", "'==='", "'Step-Parameters:'", "'Test-Suite:'", "'Test-Setup:'", "'Test-Case:'", "'Test-Data:'", "'Given:'", "'When:'", "'Then:'", "'And:'", "'|==='", "'|'", "'file'", "'page'", "'response'", "'dialog'", "'directory'", "'request'", "'goal'", "'job'", "'action'", "'popup'", "'annotation'", "'hover'", "'tooltip'"
    };
    public static final int RULE_WORD=6;
    public static final int RULE_TEXT_BLOCK=5;
    public static final int RULE_SL_COMMENT=8;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__37=37;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__11=11;
    public static final int T__33=33;
    public static final int T__12=12;
    public static final int T__34=34;
    public static final int T__13=13;
    public static final int T__35=35;
    public static final int T__14=14;
    public static final int T__36=36;
    public static final int EOF=-1;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__10=10;
    public static final int T__32=32;
    public static final int T__9=9;
    public static final int RULE_EOL=4;
    public static final int RULE_WS=7;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;

    // delegates
    // delegators


        public InternalAsciiDocParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalAsciiDocParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalAsciiDocParser.tokenNames; }
    public String getGrammarFileName() { return "InternalAsciiDoc.g"; }



     	private AsciiDocGrammarAccess grammarAccess;

        public InternalAsciiDocParser(TokenStream input, AsciiDocGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "Model";
       	}

       	@Override
       	protected AsciiDocGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleModel"
    // InternalAsciiDoc.g:64:1: entryRuleModel returns [EObject current=null] : iv_ruleModel= ruleModel EOF ;
    public final EObject entryRuleModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModel = null;


        try {
            // InternalAsciiDoc.g:64:46: (iv_ruleModel= ruleModel EOF )
            // InternalAsciiDoc.g:65:2: iv_ruleModel= ruleModel EOF
            {
             newCompositeNode(grammarAccess.getModelRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleModel=ruleModel();

            state._fsp--;

             current =iv_ruleModel; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleModel"


    // $ANTLR start "ruleModel"
    // InternalAsciiDoc.g:71:1: ruleModel returns [EObject current=null] : (this_StepObject_0= ruleStepObject | this_TestSuite_1= ruleTestSuite ) ;
    public final EObject ruleModel() throws RecognitionException {
        EObject current = null;

        EObject this_StepObject_0 = null;

        EObject this_TestSuite_1 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:77:2: ( (this_StepObject_0= ruleStepObject | this_TestSuite_1= ruleTestSuite ) )
            // InternalAsciiDoc.g:78:2: (this_StepObject_0= ruleStepObject | this_TestSuite_1= ruleTestSuite )
            {
            // InternalAsciiDoc.g:78:2: (this_StepObject_0= ruleStepObject | this_TestSuite_1= ruleTestSuite )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==9) ) {
                int LA1_1 = input.LA(2);

                if ( (LA1_1==15) ) {
                    alt1=2;
                }
                else if ( (LA1_1==10) ) {
                    alt1=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 1, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // InternalAsciiDoc.g:79:3: this_StepObject_0= ruleStepObject
                    {

                    			newCompositeNode(grammarAccess.getModelAccess().getStepObjectParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_StepObject_0=ruleStepObject();

                    state._fsp--;


                    			current = this_StepObject_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalAsciiDoc.g:88:3: this_TestSuite_1= ruleTestSuite
                    {

                    			newCompositeNode(grammarAccess.getModelAccess().getTestSuiteParserRuleCall_1());
                    		
                    pushFollow(FOLLOW_2);
                    this_TestSuite_1=ruleTestSuite();

                    state._fsp--;


                    			current = this_TestSuite_1;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleModel"


    // $ANTLR start "entryRuleStepObject"
    // InternalAsciiDoc.g:100:1: entryRuleStepObject returns [EObject current=null] : iv_ruleStepObject= ruleStepObject EOF ;
    public final EObject entryRuleStepObject() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStepObject = null;


        try {
            // InternalAsciiDoc.g:100:51: (iv_ruleStepObject= ruleStepObject EOF )
            // InternalAsciiDoc.g:101:2: iv_ruleStepObject= ruleStepObject EOF
            {
             newCompositeNode(grammarAccess.getStepObjectRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleStepObject=ruleStepObject();

            state._fsp--;

             current =iv_ruleStepObject; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStepObject"


    // $ANTLR start "ruleStepObject"
    // InternalAsciiDoc.g:107:1: ruleStepObject returns [EObject current=null] : (otherlv_0= '=' otherlv_1= 'Step-Object:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepDefinitionList_5_0= ruleStepDefinition ) )* ) ;
    public final EObject ruleStepObject() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_description_4_0 = null;

        EObject lv_stepDefinitionList_5_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:113:2: ( (otherlv_0= '=' otherlv_1= 'Step-Object:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepDefinitionList_5_0= ruleStepDefinition ) )* ) )
            // InternalAsciiDoc.g:114:2: (otherlv_0= '=' otherlv_1= 'Step-Object:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepDefinitionList_5_0= ruleStepDefinition ) )* )
            {
            // InternalAsciiDoc.g:114:2: (otherlv_0= '=' otherlv_1= 'Step-Object:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepDefinitionList_5_0= ruleStepDefinition ) )* )
            // InternalAsciiDoc.g:115:3: otherlv_0= '=' otherlv_1= 'Step-Object:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepDefinitionList_5_0= ruleStepDefinition ) )*
            {
            otherlv_0=(Token)match(input,9,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getStepObjectAccess().getEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,10,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getStepObjectAccess().getStepObjectKeyword_1());
            		
            // InternalAsciiDoc.g:123:3: ( (lv_name_2_0= rulePhrase ) )
            // InternalAsciiDoc.g:124:4: (lv_name_2_0= rulePhrase )
            {
            // InternalAsciiDoc.g:124:4: (lv_name_2_0= rulePhrase )
            // InternalAsciiDoc.g:125:5: lv_name_2_0= rulePhrase
            {

            					newCompositeNode(grammarAccess.getStepObjectAccess().getNamePhraseParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_5);
            lv_name_2_0=rulePhrase();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getStepObjectRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.Phrase");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_3=(Token)match(input,RULE_EOL,FOLLOW_6); 

            			newLeafNode(this_EOL_3, grammarAccess.getStepObjectAccess().getEOLTerminalRuleCall_3());
            		
            // InternalAsciiDoc.g:146:3: ( (lv_description_4_0= ruleDescription ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==RULE_WORD) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // InternalAsciiDoc.g:147:4: (lv_description_4_0= ruleDescription )
                    {
                    // InternalAsciiDoc.g:147:4: (lv_description_4_0= ruleDescription )
                    // InternalAsciiDoc.g:148:5: lv_description_4_0= ruleDescription
                    {

                    					newCompositeNode(grammarAccess.getStepObjectAccess().getDescriptionDescriptionParserRuleCall_4_0());
                    				
                    pushFollow(FOLLOW_7);
                    lv_description_4_0=ruleDescription();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getStepObjectRule());
                    					}
                    					set(
                    						current,
                    						"description",
                    						lv_description_4_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Description");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:165:3: ( (lv_stepDefinitionList_5_0= ruleStepDefinition ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==11) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalAsciiDoc.g:166:4: (lv_stepDefinitionList_5_0= ruleStepDefinition )
            	    {
            	    // InternalAsciiDoc.g:166:4: (lv_stepDefinitionList_5_0= ruleStepDefinition )
            	    // InternalAsciiDoc.g:167:5: lv_stepDefinitionList_5_0= ruleStepDefinition
            	    {

            	    					newCompositeNode(grammarAccess.getStepObjectAccess().getStepDefinitionListStepDefinitionParserRuleCall_5_0());
            	    				
            	    pushFollow(FOLLOW_7);
            	    lv_stepDefinitionList_5_0=ruleStepDefinition();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getStepObjectRule());
            	    					}
            	    					add(
            	    						current,
            	    						"stepDefinitionList",
            	    						lv_stepDefinitionList_5_0,
            	    						"org.farhan.dsl.asciidoc.AsciiDoc.StepDefinition");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStepObject"


    // $ANTLR start "entryRuleStepDefinition"
    // InternalAsciiDoc.g:188:1: entryRuleStepDefinition returns [EObject current=null] : iv_ruleStepDefinition= ruleStepDefinition EOF ;
    public final EObject entryRuleStepDefinition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStepDefinition = null;


        try {
            // InternalAsciiDoc.g:188:55: (iv_ruleStepDefinition= ruleStepDefinition EOF )
            // InternalAsciiDoc.g:189:2: iv_ruleStepDefinition= ruleStepDefinition EOF
            {
             newCompositeNode(grammarAccess.getStepDefinitionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleStepDefinition=ruleStepDefinition();

            state._fsp--;

             current =iv_ruleStepDefinition; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStepDefinition"


    // $ANTLR start "ruleStepDefinition"
    // InternalAsciiDoc.g:195:1: ruleStepDefinition returns [EObject current=null] : (otherlv_0= '==' otherlv_1= 'Step-Definition:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepParameterList_5_0= ruleStepParameters ) )* ) ;
    public final EObject ruleStepDefinition() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_description_4_0 = null;

        EObject lv_stepParameterList_5_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:201:2: ( (otherlv_0= '==' otherlv_1= 'Step-Definition:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepParameterList_5_0= ruleStepParameters ) )* ) )
            // InternalAsciiDoc.g:202:2: (otherlv_0= '==' otherlv_1= 'Step-Definition:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepParameterList_5_0= ruleStepParameters ) )* )
            {
            // InternalAsciiDoc.g:202:2: (otherlv_0= '==' otherlv_1= 'Step-Definition:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepParameterList_5_0= ruleStepParameters ) )* )
            // InternalAsciiDoc.g:203:3: otherlv_0= '==' otherlv_1= 'Step-Definition:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_stepParameterList_5_0= ruleStepParameters ) )*
            {
            otherlv_0=(Token)match(input,11,FOLLOW_8); 

            			newLeafNode(otherlv_0, grammarAccess.getStepDefinitionAccess().getEqualsSignEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,12,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getStepDefinitionAccess().getStepDefinitionKeyword_1());
            		
            // InternalAsciiDoc.g:211:3: ( (lv_name_2_0= rulePhrase ) )
            // InternalAsciiDoc.g:212:4: (lv_name_2_0= rulePhrase )
            {
            // InternalAsciiDoc.g:212:4: (lv_name_2_0= rulePhrase )
            // InternalAsciiDoc.g:213:5: lv_name_2_0= rulePhrase
            {

            					newCompositeNode(grammarAccess.getStepDefinitionAccess().getNamePhraseParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_5);
            lv_name_2_0=rulePhrase();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getStepDefinitionRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.Phrase");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_3=(Token)match(input,RULE_EOL,FOLLOW_9); 

            			newLeafNode(this_EOL_3, grammarAccess.getStepDefinitionAccess().getEOLTerminalRuleCall_3());
            		
            // InternalAsciiDoc.g:234:3: ( (lv_description_4_0= ruleDescription ) )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_WORD) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // InternalAsciiDoc.g:235:4: (lv_description_4_0= ruleDescription )
                    {
                    // InternalAsciiDoc.g:235:4: (lv_description_4_0= ruleDescription )
                    // InternalAsciiDoc.g:236:5: lv_description_4_0= ruleDescription
                    {

                    					newCompositeNode(grammarAccess.getStepDefinitionAccess().getDescriptionDescriptionParserRuleCall_4_0());
                    				
                    pushFollow(FOLLOW_10);
                    lv_description_4_0=ruleDescription();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getStepDefinitionRule());
                    					}
                    					set(
                    						current,
                    						"description",
                    						lv_description_4_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Description");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:253:3: ( (lv_stepParameterList_5_0= ruleStepParameters ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==13) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // InternalAsciiDoc.g:254:4: (lv_stepParameterList_5_0= ruleStepParameters )
            	    {
            	    // InternalAsciiDoc.g:254:4: (lv_stepParameterList_5_0= ruleStepParameters )
            	    // InternalAsciiDoc.g:255:5: lv_stepParameterList_5_0= ruleStepParameters
            	    {

            	    					newCompositeNode(grammarAccess.getStepDefinitionAccess().getStepParameterListStepParametersParserRuleCall_5_0());
            	    				
            	    pushFollow(FOLLOW_10);
            	    lv_stepParameterList_5_0=ruleStepParameters();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getStepDefinitionRule());
            	    					}
            	    					add(
            	    						current,
            	    						"stepParameterList",
            	    						lv_stepParameterList_5_0,
            	    						"org.farhan.dsl.asciidoc.AsciiDoc.StepParameters");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStepDefinition"


    // $ANTLR start "entryRuleStepParameters"
    // InternalAsciiDoc.g:276:1: entryRuleStepParameters returns [EObject current=null] : iv_ruleStepParameters= ruleStepParameters EOF ;
    public final EObject entryRuleStepParameters() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStepParameters = null;


        try {
            // InternalAsciiDoc.g:276:55: (iv_ruleStepParameters= ruleStepParameters EOF )
            // InternalAsciiDoc.g:277:2: iv_ruleStepParameters= ruleStepParameters EOF
            {
             newCompositeNode(grammarAccess.getStepParametersRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleStepParameters=ruleStepParameters();

            state._fsp--;

             current =iv_ruleStepParameters; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStepParameters"


    // $ANTLR start "ruleStepParameters"
    // InternalAsciiDoc.g:283:1: ruleStepParameters returns [EObject current=null] : (otherlv_0= '===' otherlv_1= 'Step-Parameters:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )? ) ;
    public final EObject ruleStepParameters() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_description_4_0 = null;

        EObject lv_table_5_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:289:2: ( (otherlv_0= '===' otherlv_1= 'Step-Parameters:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )? ) )
            // InternalAsciiDoc.g:290:2: (otherlv_0= '===' otherlv_1= 'Step-Parameters:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )? )
            {
            // InternalAsciiDoc.g:290:2: (otherlv_0= '===' otherlv_1= 'Step-Parameters:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )? )
            // InternalAsciiDoc.g:291:3: otherlv_0= '===' otherlv_1= 'Step-Parameters:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )?
            {
            otherlv_0=(Token)match(input,13,FOLLOW_11); 

            			newLeafNode(otherlv_0, grammarAccess.getStepParametersAccess().getEqualsSignEqualsSignEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,14,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getStepParametersAccess().getStepParametersKeyword_1());
            		
            // InternalAsciiDoc.g:299:3: ( (lv_name_2_0= rulePhrase ) )
            // InternalAsciiDoc.g:300:4: (lv_name_2_0= rulePhrase )
            {
            // InternalAsciiDoc.g:300:4: (lv_name_2_0= rulePhrase )
            // InternalAsciiDoc.g:301:5: lv_name_2_0= rulePhrase
            {

            					newCompositeNode(grammarAccess.getStepParametersAccess().getNamePhraseParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_5);
            lv_name_2_0=rulePhrase();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getStepParametersRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.Phrase");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_3=(Token)match(input,RULE_EOL,FOLLOW_12); 

            			newLeafNode(this_EOL_3, grammarAccess.getStepParametersAccess().getEOLTerminalRuleCall_3());
            		
            // InternalAsciiDoc.g:322:3: ( (lv_description_4_0= ruleDescription ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULE_WORD) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // InternalAsciiDoc.g:323:4: (lv_description_4_0= ruleDescription )
                    {
                    // InternalAsciiDoc.g:323:4: (lv_description_4_0= ruleDescription )
                    // InternalAsciiDoc.g:324:5: lv_description_4_0= ruleDescription
                    {

                    					newCompositeNode(grammarAccess.getStepParametersAccess().getDescriptionDescriptionParserRuleCall_4_0());
                    				
                    pushFollow(FOLLOW_13);
                    lv_description_4_0=ruleDescription();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getStepParametersRule());
                    					}
                    					set(
                    						current,
                    						"description",
                    						lv_description_4_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Description");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:341:3: ( (lv_table_5_0= ruleTable ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==23) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // InternalAsciiDoc.g:342:4: (lv_table_5_0= ruleTable )
                    {
                    // InternalAsciiDoc.g:342:4: (lv_table_5_0= ruleTable )
                    // InternalAsciiDoc.g:343:5: lv_table_5_0= ruleTable
                    {

                    					newCompositeNode(grammarAccess.getStepParametersAccess().getTableTableParserRuleCall_5_0());
                    				
                    pushFollow(FOLLOW_2);
                    lv_table_5_0=ruleTable();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getStepParametersRule());
                    					}
                    					set(
                    						current,
                    						"table",
                    						lv_table_5_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Table");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStepParameters"


    // $ANTLR start "entryRuleTestSuite"
    // InternalAsciiDoc.g:364:1: entryRuleTestSuite returns [EObject current=null] : iv_ruleTestSuite= ruleTestSuite EOF ;
    public final EObject entryRuleTestSuite() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTestSuite = null;


        try {
            // InternalAsciiDoc.g:364:50: (iv_ruleTestSuite= ruleTestSuite EOF )
            // InternalAsciiDoc.g:365:2: iv_ruleTestSuite= ruleTestSuite EOF
            {
             newCompositeNode(grammarAccess.getTestSuiteRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTestSuite=ruleTestSuite();

            state._fsp--;

             current =iv_ruleTestSuite; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTestSuite"


    // $ANTLR start "ruleTestSuite"
    // InternalAsciiDoc.g:371:1: ruleTestSuite returns [EObject current=null] : (otherlv_0= '=' otherlv_1= 'Test-Suite:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepContainerList_5_0= ruleTestStepContainer ) )* ) ;
    public final EObject ruleTestSuite() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_description_4_0 = null;

        EObject lv_testStepContainerList_5_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:377:2: ( (otherlv_0= '=' otherlv_1= 'Test-Suite:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepContainerList_5_0= ruleTestStepContainer ) )* ) )
            // InternalAsciiDoc.g:378:2: (otherlv_0= '=' otherlv_1= 'Test-Suite:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepContainerList_5_0= ruleTestStepContainer ) )* )
            {
            // InternalAsciiDoc.g:378:2: (otherlv_0= '=' otherlv_1= 'Test-Suite:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepContainerList_5_0= ruleTestStepContainer ) )* )
            // InternalAsciiDoc.g:379:3: otherlv_0= '=' otherlv_1= 'Test-Suite:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepContainerList_5_0= ruleTestStepContainer ) )*
            {
            otherlv_0=(Token)match(input,9,FOLLOW_14); 

            			newLeafNode(otherlv_0, grammarAccess.getTestSuiteAccess().getEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,15,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getTestSuiteAccess().getTestSuiteKeyword_1());
            		
            // InternalAsciiDoc.g:387:3: ( (lv_name_2_0= rulePhrase ) )
            // InternalAsciiDoc.g:388:4: (lv_name_2_0= rulePhrase )
            {
            // InternalAsciiDoc.g:388:4: (lv_name_2_0= rulePhrase )
            // InternalAsciiDoc.g:389:5: lv_name_2_0= rulePhrase
            {

            					newCompositeNode(grammarAccess.getTestSuiteAccess().getNamePhraseParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_5);
            lv_name_2_0=rulePhrase();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getTestSuiteRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.Phrase");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_3=(Token)match(input,RULE_EOL,FOLLOW_6); 

            			newLeafNode(this_EOL_3, grammarAccess.getTestSuiteAccess().getEOLTerminalRuleCall_3());
            		
            // InternalAsciiDoc.g:410:3: ( (lv_description_4_0= ruleDescription ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_WORD) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // InternalAsciiDoc.g:411:4: (lv_description_4_0= ruleDescription )
                    {
                    // InternalAsciiDoc.g:411:4: (lv_description_4_0= ruleDescription )
                    // InternalAsciiDoc.g:412:5: lv_description_4_0= ruleDescription
                    {

                    					newCompositeNode(grammarAccess.getTestSuiteAccess().getDescriptionDescriptionParserRuleCall_4_0());
                    				
                    pushFollow(FOLLOW_7);
                    lv_description_4_0=ruleDescription();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getTestSuiteRule());
                    					}
                    					set(
                    						current,
                    						"description",
                    						lv_description_4_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Description");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:429:3: ( (lv_testStepContainerList_5_0= ruleTestStepContainer ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==11) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // InternalAsciiDoc.g:430:4: (lv_testStepContainerList_5_0= ruleTestStepContainer )
            	    {
            	    // InternalAsciiDoc.g:430:4: (lv_testStepContainerList_5_0= ruleTestStepContainer )
            	    // InternalAsciiDoc.g:431:5: lv_testStepContainerList_5_0= ruleTestStepContainer
            	    {

            	    					newCompositeNode(grammarAccess.getTestSuiteAccess().getTestStepContainerListTestStepContainerParserRuleCall_5_0());
            	    				
            	    pushFollow(FOLLOW_7);
            	    lv_testStepContainerList_5_0=ruleTestStepContainer();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getTestSuiteRule());
            	    					}
            	    					add(
            	    						current,
            	    						"testStepContainerList",
            	    						lv_testStepContainerList_5_0,
            	    						"org.farhan.dsl.asciidoc.AsciiDoc.TestStepContainer");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTestSuite"


    // $ANTLR start "entryRuleTestStepContainer"
    // InternalAsciiDoc.g:452:1: entryRuleTestStepContainer returns [EObject current=null] : iv_ruleTestStepContainer= ruleTestStepContainer EOF ;
    public final EObject entryRuleTestStepContainer() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTestStepContainer = null;


        try {
            // InternalAsciiDoc.g:452:58: (iv_ruleTestStepContainer= ruleTestStepContainer EOF )
            // InternalAsciiDoc.g:453:2: iv_ruleTestStepContainer= ruleTestStepContainer EOF
            {
             newCompositeNode(grammarAccess.getTestStepContainerRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTestStepContainer=ruleTestStepContainer();

            state._fsp--;

             current =iv_ruleTestStepContainer; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTestStepContainer"


    // $ANTLR start "ruleTestStepContainer"
    // InternalAsciiDoc.g:459:1: ruleTestStepContainer returns [EObject current=null] : (this_TestSetup_0= ruleTestSetup | this_TestCase_1= ruleTestCase ) ;
    public final EObject ruleTestStepContainer() throws RecognitionException {
        EObject current = null;

        EObject this_TestSetup_0 = null;

        EObject this_TestCase_1 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:465:2: ( (this_TestSetup_0= ruleTestSetup | this_TestCase_1= ruleTestCase ) )
            // InternalAsciiDoc.g:466:2: (this_TestSetup_0= ruleTestSetup | this_TestCase_1= ruleTestCase )
            {
            // InternalAsciiDoc.g:466:2: (this_TestSetup_0= ruleTestSetup | this_TestCase_1= ruleTestCase )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==11) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==17) ) {
                    alt10=2;
                }
                else if ( (LA10_1==16) ) {
                    alt10=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // InternalAsciiDoc.g:467:3: this_TestSetup_0= ruleTestSetup
                    {

                    			newCompositeNode(grammarAccess.getTestStepContainerAccess().getTestSetupParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_TestSetup_0=ruleTestSetup();

                    state._fsp--;


                    			current = this_TestSetup_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalAsciiDoc.g:476:3: this_TestCase_1= ruleTestCase
                    {

                    			newCompositeNode(grammarAccess.getTestStepContainerAccess().getTestCaseParserRuleCall_1());
                    		
                    pushFollow(FOLLOW_2);
                    this_TestCase_1=ruleTestCase();

                    state._fsp--;


                    			current = this_TestCase_1;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTestStepContainer"


    // $ANTLR start "entryRuleTestSetup"
    // InternalAsciiDoc.g:488:1: entryRuleTestSetup returns [EObject current=null] : iv_ruleTestSetup= ruleTestSetup EOF ;
    public final EObject entryRuleTestSetup() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTestSetup = null;


        try {
            // InternalAsciiDoc.g:488:50: (iv_ruleTestSetup= ruleTestSetup EOF )
            // InternalAsciiDoc.g:489:2: iv_ruleTestSetup= ruleTestSetup EOF
            {
             newCompositeNode(grammarAccess.getTestSetupRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTestSetup=ruleTestSetup();

            state._fsp--;

             current =iv_ruleTestSetup; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTestSetup"


    // $ANTLR start "ruleTestSetup"
    // InternalAsciiDoc.g:495:1: ruleTestSetup returns [EObject current=null] : (otherlv_0= '==' otherlv_1= 'Test-Setup:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )* ) ;
    public final EObject ruleTestSetup() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_description_4_0 = null;

        EObject lv_testStepList_5_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:501:2: ( (otherlv_0= '==' otherlv_1= 'Test-Setup:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )* ) )
            // InternalAsciiDoc.g:502:2: (otherlv_0= '==' otherlv_1= 'Test-Setup:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )* )
            {
            // InternalAsciiDoc.g:502:2: (otherlv_0= '==' otherlv_1= 'Test-Setup:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )* )
            // InternalAsciiDoc.g:503:3: otherlv_0= '==' otherlv_1= 'Test-Setup:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )*
            {
            otherlv_0=(Token)match(input,11,FOLLOW_15); 

            			newLeafNode(otherlv_0, grammarAccess.getTestSetupAccess().getEqualsSignEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,16,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getTestSetupAccess().getTestSetupKeyword_1());
            		
            // InternalAsciiDoc.g:511:3: ( (lv_name_2_0= rulePhrase ) )
            // InternalAsciiDoc.g:512:4: (lv_name_2_0= rulePhrase )
            {
            // InternalAsciiDoc.g:512:4: (lv_name_2_0= rulePhrase )
            // InternalAsciiDoc.g:513:5: lv_name_2_0= rulePhrase
            {

            					newCompositeNode(grammarAccess.getTestSetupAccess().getNamePhraseParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_5);
            lv_name_2_0=rulePhrase();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getTestSetupRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.Phrase");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_3=(Token)match(input,RULE_EOL,FOLLOW_9); 

            			newLeafNode(this_EOL_3, grammarAccess.getTestSetupAccess().getEOLTerminalRuleCall_3());
            		
            // InternalAsciiDoc.g:534:3: ( (lv_description_4_0= ruleDescription ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==RULE_WORD) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalAsciiDoc.g:535:4: (lv_description_4_0= ruleDescription )
                    {
                    // InternalAsciiDoc.g:535:4: (lv_description_4_0= ruleDescription )
                    // InternalAsciiDoc.g:536:5: lv_description_4_0= ruleDescription
                    {

                    					newCompositeNode(grammarAccess.getTestSetupAccess().getDescriptionDescriptionParserRuleCall_4_0());
                    				
                    pushFollow(FOLLOW_10);
                    lv_description_4_0=ruleDescription();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getTestSetupRule());
                    					}
                    					set(
                    						current,
                    						"description",
                    						lv_description_4_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Description");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:553:3: ( (lv_testStepList_5_0= ruleTestStep ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==13) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // InternalAsciiDoc.g:554:4: (lv_testStepList_5_0= ruleTestStep )
            	    {
            	    // InternalAsciiDoc.g:554:4: (lv_testStepList_5_0= ruleTestStep )
            	    // InternalAsciiDoc.g:555:5: lv_testStepList_5_0= ruleTestStep
            	    {

            	    					newCompositeNode(grammarAccess.getTestSetupAccess().getTestStepListTestStepParserRuleCall_5_0());
            	    				
            	    pushFollow(FOLLOW_10);
            	    lv_testStepList_5_0=ruleTestStep();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getTestSetupRule());
            	    					}
            	    					add(
            	    						current,
            	    						"testStepList",
            	    						lv_testStepList_5_0,
            	    						"org.farhan.dsl.asciidoc.AsciiDoc.TestStep");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTestSetup"


    // $ANTLR start "entryRuleTestCase"
    // InternalAsciiDoc.g:576:1: entryRuleTestCase returns [EObject current=null] : iv_ruleTestCase= ruleTestCase EOF ;
    public final EObject entryRuleTestCase() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTestCase = null;


        try {
            // InternalAsciiDoc.g:576:49: (iv_ruleTestCase= ruleTestCase EOF )
            // InternalAsciiDoc.g:577:2: iv_ruleTestCase= ruleTestCase EOF
            {
             newCompositeNode(grammarAccess.getTestCaseRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTestCase=ruleTestCase();

            state._fsp--;

             current =iv_ruleTestCase; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTestCase"


    // $ANTLR start "ruleTestCase"
    // InternalAsciiDoc.g:583:1: ruleTestCase returns [EObject current=null] : (otherlv_0= '==' otherlv_1= 'Test-Case:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )* ( (lv_testDataList_6_0= ruleTestData ) )* ) ;
    public final EObject ruleTestCase() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_description_4_0 = null;

        EObject lv_testStepList_5_0 = null;

        EObject lv_testDataList_6_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:589:2: ( (otherlv_0= '==' otherlv_1= 'Test-Case:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )* ( (lv_testDataList_6_0= ruleTestData ) )* ) )
            // InternalAsciiDoc.g:590:2: (otherlv_0= '==' otherlv_1= 'Test-Case:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )* ( (lv_testDataList_6_0= ruleTestData ) )* )
            {
            // InternalAsciiDoc.g:590:2: (otherlv_0= '==' otherlv_1= 'Test-Case:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )* ( (lv_testDataList_6_0= ruleTestData ) )* )
            // InternalAsciiDoc.g:591:3: otherlv_0= '==' otherlv_1= 'Test-Case:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_testStepList_5_0= ruleTestStep ) )* ( (lv_testDataList_6_0= ruleTestData ) )*
            {
            otherlv_0=(Token)match(input,11,FOLLOW_16); 

            			newLeafNode(otherlv_0, grammarAccess.getTestCaseAccess().getEqualsSignEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,17,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getTestCaseAccess().getTestCaseKeyword_1());
            		
            // InternalAsciiDoc.g:599:3: ( (lv_name_2_0= rulePhrase ) )
            // InternalAsciiDoc.g:600:4: (lv_name_2_0= rulePhrase )
            {
            // InternalAsciiDoc.g:600:4: (lv_name_2_0= rulePhrase )
            // InternalAsciiDoc.g:601:5: lv_name_2_0= rulePhrase
            {

            					newCompositeNode(grammarAccess.getTestCaseAccess().getNamePhraseParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_5);
            lv_name_2_0=rulePhrase();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getTestCaseRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.Phrase");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_3=(Token)match(input,RULE_EOL,FOLLOW_9); 

            			newLeafNode(this_EOL_3, grammarAccess.getTestCaseAccess().getEOLTerminalRuleCall_3());
            		
            // InternalAsciiDoc.g:622:3: ( (lv_description_4_0= ruleDescription ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==RULE_WORD) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // InternalAsciiDoc.g:623:4: (lv_description_4_0= ruleDescription )
                    {
                    // InternalAsciiDoc.g:623:4: (lv_description_4_0= ruleDescription )
                    // InternalAsciiDoc.g:624:5: lv_description_4_0= ruleDescription
                    {

                    					newCompositeNode(grammarAccess.getTestCaseAccess().getDescriptionDescriptionParserRuleCall_4_0());
                    				
                    pushFollow(FOLLOW_10);
                    lv_description_4_0=ruleDescription();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getTestCaseRule());
                    					}
                    					set(
                    						current,
                    						"description",
                    						lv_description_4_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Description");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:641:3: ( (lv_testStepList_5_0= ruleTestStep ) )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==13) ) {
                    int LA14_1 = input.LA(2);

                    if ( ((LA14_1>=19 && LA14_1<=22)) ) {
                        alt14=1;
                    }


                }


                switch (alt14) {
            	case 1 :
            	    // InternalAsciiDoc.g:642:4: (lv_testStepList_5_0= ruleTestStep )
            	    {
            	    // InternalAsciiDoc.g:642:4: (lv_testStepList_5_0= ruleTestStep )
            	    // InternalAsciiDoc.g:643:5: lv_testStepList_5_0= ruleTestStep
            	    {

            	    					newCompositeNode(grammarAccess.getTestCaseAccess().getTestStepListTestStepParserRuleCall_5_0());
            	    				
            	    pushFollow(FOLLOW_10);
            	    lv_testStepList_5_0=ruleTestStep();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getTestCaseRule());
            	    					}
            	    					add(
            	    						current,
            	    						"testStepList",
            	    						lv_testStepList_5_0,
            	    						"org.farhan.dsl.asciidoc.AsciiDoc.TestStep");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            // InternalAsciiDoc.g:660:3: ( (lv_testDataList_6_0= ruleTestData ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==13) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // InternalAsciiDoc.g:661:4: (lv_testDataList_6_0= ruleTestData )
            	    {
            	    // InternalAsciiDoc.g:661:4: (lv_testDataList_6_0= ruleTestData )
            	    // InternalAsciiDoc.g:662:5: lv_testDataList_6_0= ruleTestData
            	    {

            	    					newCompositeNode(grammarAccess.getTestCaseAccess().getTestDataListTestDataParserRuleCall_6_0());
            	    				
            	    pushFollow(FOLLOW_10);
            	    lv_testDataList_6_0=ruleTestData();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getTestCaseRule());
            	    					}
            	    					add(
            	    						current,
            	    						"testDataList",
            	    						lv_testDataList_6_0,
            	    						"org.farhan.dsl.asciidoc.AsciiDoc.TestData");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTestCase"


    // $ANTLR start "entryRuleTestData"
    // InternalAsciiDoc.g:683:1: entryRuleTestData returns [EObject current=null] : iv_ruleTestData= ruleTestData EOF ;
    public final EObject entryRuleTestData() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTestData = null;


        try {
            // InternalAsciiDoc.g:683:49: (iv_ruleTestData= ruleTestData EOF )
            // InternalAsciiDoc.g:684:2: iv_ruleTestData= ruleTestData EOF
            {
             newCompositeNode(grammarAccess.getTestDataRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTestData=ruleTestData();

            state._fsp--;

             current =iv_ruleTestData; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTestData"


    // $ANTLR start "ruleTestData"
    // InternalAsciiDoc.g:690:1: ruleTestData returns [EObject current=null] : (otherlv_0= '===' otherlv_1= 'Test-Data:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )? ) ;
    public final EObject ruleTestData() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_3=null;
        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_description_4_0 = null;

        EObject lv_table_5_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:696:2: ( (otherlv_0= '===' otherlv_1= 'Test-Data:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )? ) )
            // InternalAsciiDoc.g:697:2: (otherlv_0= '===' otherlv_1= 'Test-Data:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )? )
            {
            // InternalAsciiDoc.g:697:2: (otherlv_0= '===' otherlv_1= 'Test-Data:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )? )
            // InternalAsciiDoc.g:698:3: otherlv_0= '===' otherlv_1= 'Test-Data:' ( (lv_name_2_0= rulePhrase ) ) this_EOL_3= RULE_EOL ( (lv_description_4_0= ruleDescription ) )? ( (lv_table_5_0= ruleTable ) )?
            {
            otherlv_0=(Token)match(input,13,FOLLOW_17); 

            			newLeafNode(otherlv_0, grammarAccess.getTestDataAccess().getEqualsSignEqualsSignEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,18,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getTestDataAccess().getTestDataKeyword_1());
            		
            // InternalAsciiDoc.g:706:3: ( (lv_name_2_0= rulePhrase ) )
            // InternalAsciiDoc.g:707:4: (lv_name_2_0= rulePhrase )
            {
            // InternalAsciiDoc.g:707:4: (lv_name_2_0= rulePhrase )
            // InternalAsciiDoc.g:708:5: lv_name_2_0= rulePhrase
            {

            					newCompositeNode(grammarAccess.getTestDataAccess().getNamePhraseParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_5);
            lv_name_2_0=rulePhrase();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getTestDataRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.Phrase");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_3=(Token)match(input,RULE_EOL,FOLLOW_12); 

            			newLeafNode(this_EOL_3, grammarAccess.getTestDataAccess().getEOLTerminalRuleCall_3());
            		
            // InternalAsciiDoc.g:729:3: ( (lv_description_4_0= ruleDescription ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_WORD) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalAsciiDoc.g:730:4: (lv_description_4_0= ruleDescription )
                    {
                    // InternalAsciiDoc.g:730:4: (lv_description_4_0= ruleDescription )
                    // InternalAsciiDoc.g:731:5: lv_description_4_0= ruleDescription
                    {

                    					newCompositeNode(grammarAccess.getTestDataAccess().getDescriptionDescriptionParserRuleCall_4_0());
                    				
                    pushFollow(FOLLOW_13);
                    lv_description_4_0=ruleDescription();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getTestDataRule());
                    					}
                    					set(
                    						current,
                    						"description",
                    						lv_description_4_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Description");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:748:3: ( (lv_table_5_0= ruleTable ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==23) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // InternalAsciiDoc.g:749:4: (lv_table_5_0= ruleTable )
                    {
                    // InternalAsciiDoc.g:749:4: (lv_table_5_0= ruleTable )
                    // InternalAsciiDoc.g:750:5: lv_table_5_0= ruleTable
                    {

                    					newCompositeNode(grammarAccess.getTestDataAccess().getTableTableParserRuleCall_5_0());
                    				
                    pushFollow(FOLLOW_2);
                    lv_table_5_0=ruleTable();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getTestDataRule());
                    					}
                    					set(
                    						current,
                    						"table",
                    						lv_table_5_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Table");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTestData"


    // $ANTLR start "entryRuleTestStep"
    // InternalAsciiDoc.g:771:1: entryRuleTestStep returns [EObject current=null] : iv_ruleTestStep= ruleTestStep EOF ;
    public final EObject entryRuleTestStep() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTestStep = null;


        try {
            // InternalAsciiDoc.g:771:49: (iv_ruleTestStep= ruleTestStep EOF )
            // InternalAsciiDoc.g:772:2: iv_ruleTestStep= ruleTestStep EOF
            {
             newCompositeNode(grammarAccess.getTestStepRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTestStep=ruleTestStep();

            state._fsp--;

             current =iv_ruleTestStep; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTestStep"


    // $ANTLR start "ruleTestStep"
    // InternalAsciiDoc.g:778:1: ruleTestStep returns [EObject current=null] : (this_Given_0= ruleGiven | this_When_1= ruleWhen | this_Then_2= ruleThen | this_And_3= ruleAnd ) ;
    public final EObject ruleTestStep() throws RecognitionException {
        EObject current = null;

        EObject this_Given_0 = null;

        EObject this_When_1 = null;

        EObject this_Then_2 = null;

        EObject this_And_3 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:784:2: ( (this_Given_0= ruleGiven | this_When_1= ruleWhen | this_Then_2= ruleThen | this_And_3= ruleAnd ) )
            // InternalAsciiDoc.g:785:2: (this_Given_0= ruleGiven | this_When_1= ruleWhen | this_Then_2= ruleThen | this_And_3= ruleAnd )
            {
            // InternalAsciiDoc.g:785:2: (this_Given_0= ruleGiven | this_When_1= ruleWhen | this_Then_2= ruleThen | this_And_3= ruleAnd )
            int alt18=4;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==13) ) {
                switch ( input.LA(2) ) {
                case 19:
                    {
                    alt18=1;
                    }
                    break;
                case 22:
                    {
                    alt18=4;
                    }
                    break;
                case 20:
                    {
                    alt18=2;
                    }
                    break;
                case 21:
                    {
                    alt18=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // InternalAsciiDoc.g:786:3: this_Given_0= ruleGiven
                    {

                    			newCompositeNode(grammarAccess.getTestStepAccess().getGivenParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_Given_0=ruleGiven();

                    state._fsp--;


                    			current = this_Given_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalAsciiDoc.g:795:3: this_When_1= ruleWhen
                    {

                    			newCompositeNode(grammarAccess.getTestStepAccess().getWhenParserRuleCall_1());
                    		
                    pushFollow(FOLLOW_2);
                    this_When_1=ruleWhen();

                    state._fsp--;


                    			current = this_When_1;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 3 :
                    // InternalAsciiDoc.g:804:3: this_Then_2= ruleThen
                    {

                    			newCompositeNode(grammarAccess.getTestStepAccess().getThenParserRuleCall_2());
                    		
                    pushFollow(FOLLOW_2);
                    this_Then_2=ruleThen();

                    state._fsp--;


                    			current = this_Then_2;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 4 :
                    // InternalAsciiDoc.g:813:3: this_And_3= ruleAnd
                    {

                    			newCompositeNode(grammarAccess.getTestStepAccess().getAndParserRuleCall_3());
                    		
                    pushFollow(FOLLOW_2);
                    this_And_3=ruleAnd();

                    state._fsp--;


                    			current = this_And_3;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTestStep"


    // $ANTLR start "entryRuleGiven"
    // InternalAsciiDoc.g:825:1: entryRuleGiven returns [EObject current=null] : iv_ruleGiven= ruleGiven EOF ;
    public final EObject entryRuleGiven() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGiven = null;


        try {
            // InternalAsciiDoc.g:825:46: (iv_ruleGiven= ruleGiven EOF )
            // InternalAsciiDoc.g:826:2: iv_ruleGiven= ruleGiven EOF
            {
             newCompositeNode(grammarAccess.getGivenRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleGiven=ruleGiven();

            state._fsp--;

             current =iv_ruleGiven; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleGiven"


    // $ANTLR start "ruleGiven"
    // InternalAsciiDoc.g:832:1: ruleGiven returns [EObject current=null] : (otherlv_0= '===' otherlv_1= 'Given:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? ) ;
    public final EObject ruleGiven() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_4=null;
        AntlrDatatypeRuleToken lv_stepObjectName_2_0 = null;

        AntlrDatatypeRuleToken lv_stepDefinitionName_3_0 = null;

        EObject lv_table_5_0 = null;

        EObject lv_text_6_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:838:2: ( (otherlv_0= '===' otherlv_1= 'Given:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? ) )
            // InternalAsciiDoc.g:839:2: (otherlv_0= '===' otherlv_1= 'Given:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? )
            {
            // InternalAsciiDoc.g:839:2: (otherlv_0= '===' otherlv_1= 'Given:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? )
            // InternalAsciiDoc.g:840:3: otherlv_0= '===' otherlv_1= 'Given:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )?
            {
            otherlv_0=(Token)match(input,13,FOLLOW_18); 

            			newLeafNode(otherlv_0, grammarAccess.getGivenAccess().getEqualsSignEqualsSignEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,19,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getGivenAccess().getGivenKeyword_1());
            		
            // InternalAsciiDoc.g:848:3: ( (lv_stepObjectName_2_0= ruleStepObjectRef ) )
            // InternalAsciiDoc.g:849:4: (lv_stepObjectName_2_0= ruleStepObjectRef )
            {
            // InternalAsciiDoc.g:849:4: (lv_stepObjectName_2_0= ruleStepObjectRef )
            // InternalAsciiDoc.g:850:5: lv_stepObjectName_2_0= ruleStepObjectRef
            {

            					newCompositeNode(grammarAccess.getGivenAccess().getStepObjectNameStepObjectRefParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_4);
            lv_stepObjectName_2_0=ruleStepObjectRef();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getGivenRule());
            					}
            					set(
            						current,
            						"stepObjectName",
            						lv_stepObjectName_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.StepObjectRef");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalAsciiDoc.g:867:3: ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) )
            // InternalAsciiDoc.g:868:4: (lv_stepDefinitionName_3_0= ruleStepDefinitionRef )
            {
            // InternalAsciiDoc.g:868:4: (lv_stepDefinitionName_3_0= ruleStepDefinitionRef )
            // InternalAsciiDoc.g:869:5: lv_stepDefinitionName_3_0= ruleStepDefinitionRef
            {

            					newCompositeNode(grammarAccess.getGivenAccess().getStepDefinitionNameStepDefinitionRefParserRuleCall_3_0());
            				
            pushFollow(FOLLOW_5);
            lv_stepDefinitionName_3_0=ruleStepDefinitionRef();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getGivenRule());
            					}
            					set(
            						current,
            						"stepDefinitionName",
            						lv_stepDefinitionName_3_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.StepDefinitionRef");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_4=(Token)match(input,RULE_EOL,FOLLOW_19); 

            			newLeafNode(this_EOL_4, grammarAccess.getGivenAccess().getEOLTerminalRuleCall_4());
            		
            // InternalAsciiDoc.g:890:3: ( (lv_table_5_0= ruleTable ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==23) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // InternalAsciiDoc.g:891:4: (lv_table_5_0= ruleTable )
                    {
                    // InternalAsciiDoc.g:891:4: (lv_table_5_0= ruleTable )
                    // InternalAsciiDoc.g:892:5: lv_table_5_0= ruleTable
                    {

                    					newCompositeNode(grammarAccess.getGivenAccess().getTableTableParserRuleCall_5_0());
                    				
                    pushFollow(FOLLOW_20);
                    lv_table_5_0=ruleTable();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getGivenRule());
                    					}
                    					set(
                    						current,
                    						"table",
                    						lv_table_5_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Table");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:909:3: ( (lv_text_6_0= ruleText ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==RULE_TEXT_BLOCK) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // InternalAsciiDoc.g:910:4: (lv_text_6_0= ruleText )
                    {
                    // InternalAsciiDoc.g:910:4: (lv_text_6_0= ruleText )
                    // InternalAsciiDoc.g:911:5: lv_text_6_0= ruleText
                    {

                    					newCompositeNode(grammarAccess.getGivenAccess().getTextTextParserRuleCall_6_0());
                    				
                    pushFollow(FOLLOW_2);
                    lv_text_6_0=ruleText();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getGivenRule());
                    					}
                    					set(
                    						current,
                    						"text",
                    						lv_text_6_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Text");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleGiven"


    // $ANTLR start "entryRuleWhen"
    // InternalAsciiDoc.g:932:1: entryRuleWhen returns [EObject current=null] : iv_ruleWhen= ruleWhen EOF ;
    public final EObject entryRuleWhen() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleWhen = null;


        try {
            // InternalAsciiDoc.g:932:45: (iv_ruleWhen= ruleWhen EOF )
            // InternalAsciiDoc.g:933:2: iv_ruleWhen= ruleWhen EOF
            {
             newCompositeNode(grammarAccess.getWhenRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWhen=ruleWhen();

            state._fsp--;

             current =iv_ruleWhen; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWhen"


    // $ANTLR start "ruleWhen"
    // InternalAsciiDoc.g:939:1: ruleWhen returns [EObject current=null] : (otherlv_0= '===' otherlv_1= 'When:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? ) ;
    public final EObject ruleWhen() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_4=null;
        AntlrDatatypeRuleToken lv_stepObjectName_2_0 = null;

        AntlrDatatypeRuleToken lv_stepDefinitionName_3_0 = null;

        EObject lv_table_5_0 = null;

        EObject lv_text_6_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:945:2: ( (otherlv_0= '===' otherlv_1= 'When:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? ) )
            // InternalAsciiDoc.g:946:2: (otherlv_0= '===' otherlv_1= 'When:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? )
            {
            // InternalAsciiDoc.g:946:2: (otherlv_0= '===' otherlv_1= 'When:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? )
            // InternalAsciiDoc.g:947:3: otherlv_0= '===' otherlv_1= 'When:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )?
            {
            otherlv_0=(Token)match(input,13,FOLLOW_21); 

            			newLeafNode(otherlv_0, grammarAccess.getWhenAccess().getEqualsSignEqualsSignEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,20,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getWhenAccess().getWhenKeyword_1());
            		
            // InternalAsciiDoc.g:955:3: ( (lv_stepObjectName_2_0= ruleStepObjectRef ) )
            // InternalAsciiDoc.g:956:4: (lv_stepObjectName_2_0= ruleStepObjectRef )
            {
            // InternalAsciiDoc.g:956:4: (lv_stepObjectName_2_0= ruleStepObjectRef )
            // InternalAsciiDoc.g:957:5: lv_stepObjectName_2_0= ruleStepObjectRef
            {

            					newCompositeNode(grammarAccess.getWhenAccess().getStepObjectNameStepObjectRefParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_4);
            lv_stepObjectName_2_0=ruleStepObjectRef();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getWhenRule());
            					}
            					set(
            						current,
            						"stepObjectName",
            						lv_stepObjectName_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.StepObjectRef");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalAsciiDoc.g:974:3: ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) )
            // InternalAsciiDoc.g:975:4: (lv_stepDefinitionName_3_0= ruleStepDefinitionRef )
            {
            // InternalAsciiDoc.g:975:4: (lv_stepDefinitionName_3_0= ruleStepDefinitionRef )
            // InternalAsciiDoc.g:976:5: lv_stepDefinitionName_3_0= ruleStepDefinitionRef
            {

            					newCompositeNode(grammarAccess.getWhenAccess().getStepDefinitionNameStepDefinitionRefParserRuleCall_3_0());
            				
            pushFollow(FOLLOW_5);
            lv_stepDefinitionName_3_0=ruleStepDefinitionRef();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getWhenRule());
            					}
            					set(
            						current,
            						"stepDefinitionName",
            						lv_stepDefinitionName_3_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.StepDefinitionRef");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_4=(Token)match(input,RULE_EOL,FOLLOW_19); 

            			newLeafNode(this_EOL_4, grammarAccess.getWhenAccess().getEOLTerminalRuleCall_4());
            		
            // InternalAsciiDoc.g:997:3: ( (lv_table_5_0= ruleTable ) )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==23) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // InternalAsciiDoc.g:998:4: (lv_table_5_0= ruleTable )
                    {
                    // InternalAsciiDoc.g:998:4: (lv_table_5_0= ruleTable )
                    // InternalAsciiDoc.g:999:5: lv_table_5_0= ruleTable
                    {

                    					newCompositeNode(grammarAccess.getWhenAccess().getTableTableParserRuleCall_5_0());
                    				
                    pushFollow(FOLLOW_20);
                    lv_table_5_0=ruleTable();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getWhenRule());
                    					}
                    					set(
                    						current,
                    						"table",
                    						lv_table_5_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Table");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:1016:3: ( (lv_text_6_0= ruleText ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==RULE_TEXT_BLOCK) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // InternalAsciiDoc.g:1017:4: (lv_text_6_0= ruleText )
                    {
                    // InternalAsciiDoc.g:1017:4: (lv_text_6_0= ruleText )
                    // InternalAsciiDoc.g:1018:5: lv_text_6_0= ruleText
                    {

                    					newCompositeNode(grammarAccess.getWhenAccess().getTextTextParserRuleCall_6_0());
                    				
                    pushFollow(FOLLOW_2);
                    lv_text_6_0=ruleText();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getWhenRule());
                    					}
                    					set(
                    						current,
                    						"text",
                    						lv_text_6_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Text");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWhen"


    // $ANTLR start "entryRuleThen"
    // InternalAsciiDoc.g:1039:1: entryRuleThen returns [EObject current=null] : iv_ruleThen= ruleThen EOF ;
    public final EObject entryRuleThen() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleThen = null;


        try {
            // InternalAsciiDoc.g:1039:45: (iv_ruleThen= ruleThen EOF )
            // InternalAsciiDoc.g:1040:2: iv_ruleThen= ruleThen EOF
            {
             newCompositeNode(grammarAccess.getThenRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleThen=ruleThen();

            state._fsp--;

             current =iv_ruleThen; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleThen"


    // $ANTLR start "ruleThen"
    // InternalAsciiDoc.g:1046:1: ruleThen returns [EObject current=null] : (otherlv_0= '===' otherlv_1= 'Then:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? ) ;
    public final EObject ruleThen() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_4=null;
        AntlrDatatypeRuleToken lv_stepObjectName_2_0 = null;

        AntlrDatatypeRuleToken lv_stepDefinitionName_3_0 = null;

        EObject lv_table_5_0 = null;

        EObject lv_text_6_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:1052:2: ( (otherlv_0= '===' otherlv_1= 'Then:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? ) )
            // InternalAsciiDoc.g:1053:2: (otherlv_0= '===' otherlv_1= 'Then:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? )
            {
            // InternalAsciiDoc.g:1053:2: (otherlv_0= '===' otherlv_1= 'Then:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? )
            // InternalAsciiDoc.g:1054:3: otherlv_0= '===' otherlv_1= 'Then:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )?
            {
            otherlv_0=(Token)match(input,13,FOLLOW_22); 

            			newLeafNode(otherlv_0, grammarAccess.getThenAccess().getEqualsSignEqualsSignEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,21,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getThenAccess().getThenKeyword_1());
            		
            // InternalAsciiDoc.g:1062:3: ( (lv_stepObjectName_2_0= ruleStepObjectRef ) )
            // InternalAsciiDoc.g:1063:4: (lv_stepObjectName_2_0= ruleStepObjectRef )
            {
            // InternalAsciiDoc.g:1063:4: (lv_stepObjectName_2_0= ruleStepObjectRef )
            // InternalAsciiDoc.g:1064:5: lv_stepObjectName_2_0= ruleStepObjectRef
            {

            					newCompositeNode(grammarAccess.getThenAccess().getStepObjectNameStepObjectRefParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_4);
            lv_stepObjectName_2_0=ruleStepObjectRef();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getThenRule());
            					}
            					set(
            						current,
            						"stepObjectName",
            						lv_stepObjectName_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.StepObjectRef");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalAsciiDoc.g:1081:3: ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) )
            // InternalAsciiDoc.g:1082:4: (lv_stepDefinitionName_3_0= ruleStepDefinitionRef )
            {
            // InternalAsciiDoc.g:1082:4: (lv_stepDefinitionName_3_0= ruleStepDefinitionRef )
            // InternalAsciiDoc.g:1083:5: lv_stepDefinitionName_3_0= ruleStepDefinitionRef
            {

            					newCompositeNode(grammarAccess.getThenAccess().getStepDefinitionNameStepDefinitionRefParserRuleCall_3_0());
            				
            pushFollow(FOLLOW_5);
            lv_stepDefinitionName_3_0=ruleStepDefinitionRef();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getThenRule());
            					}
            					set(
            						current,
            						"stepDefinitionName",
            						lv_stepDefinitionName_3_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.StepDefinitionRef");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_4=(Token)match(input,RULE_EOL,FOLLOW_19); 

            			newLeafNode(this_EOL_4, grammarAccess.getThenAccess().getEOLTerminalRuleCall_4());
            		
            // InternalAsciiDoc.g:1104:3: ( (lv_table_5_0= ruleTable ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==23) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // InternalAsciiDoc.g:1105:4: (lv_table_5_0= ruleTable )
                    {
                    // InternalAsciiDoc.g:1105:4: (lv_table_5_0= ruleTable )
                    // InternalAsciiDoc.g:1106:5: lv_table_5_0= ruleTable
                    {

                    					newCompositeNode(grammarAccess.getThenAccess().getTableTableParserRuleCall_5_0());
                    				
                    pushFollow(FOLLOW_20);
                    lv_table_5_0=ruleTable();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getThenRule());
                    					}
                    					set(
                    						current,
                    						"table",
                    						lv_table_5_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Table");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:1123:3: ( (lv_text_6_0= ruleText ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==RULE_TEXT_BLOCK) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // InternalAsciiDoc.g:1124:4: (lv_text_6_0= ruleText )
                    {
                    // InternalAsciiDoc.g:1124:4: (lv_text_6_0= ruleText )
                    // InternalAsciiDoc.g:1125:5: lv_text_6_0= ruleText
                    {

                    					newCompositeNode(grammarAccess.getThenAccess().getTextTextParserRuleCall_6_0());
                    				
                    pushFollow(FOLLOW_2);
                    lv_text_6_0=ruleText();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getThenRule());
                    					}
                    					set(
                    						current,
                    						"text",
                    						lv_text_6_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Text");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleThen"


    // $ANTLR start "entryRuleAnd"
    // InternalAsciiDoc.g:1146:1: entryRuleAnd returns [EObject current=null] : iv_ruleAnd= ruleAnd EOF ;
    public final EObject entryRuleAnd() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAnd = null;


        try {
            // InternalAsciiDoc.g:1146:44: (iv_ruleAnd= ruleAnd EOF )
            // InternalAsciiDoc.g:1147:2: iv_ruleAnd= ruleAnd EOF
            {
             newCompositeNode(grammarAccess.getAndRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAnd=ruleAnd();

            state._fsp--;

             current =iv_ruleAnd; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAnd"


    // $ANTLR start "ruleAnd"
    // InternalAsciiDoc.g:1153:1: ruleAnd returns [EObject current=null] : (otherlv_0= '===' otherlv_1= 'And:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? ) ;
    public final EObject ruleAnd() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token this_EOL_4=null;
        AntlrDatatypeRuleToken lv_stepObjectName_2_0 = null;

        AntlrDatatypeRuleToken lv_stepDefinitionName_3_0 = null;

        EObject lv_table_5_0 = null;

        EObject lv_text_6_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:1159:2: ( (otherlv_0= '===' otherlv_1= 'And:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? ) )
            // InternalAsciiDoc.g:1160:2: (otherlv_0= '===' otherlv_1= 'And:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? )
            {
            // InternalAsciiDoc.g:1160:2: (otherlv_0= '===' otherlv_1= 'And:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )? )
            // InternalAsciiDoc.g:1161:3: otherlv_0= '===' otherlv_1= 'And:' ( (lv_stepObjectName_2_0= ruleStepObjectRef ) ) ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) ) this_EOL_4= RULE_EOL ( (lv_table_5_0= ruleTable ) )? ( (lv_text_6_0= ruleText ) )?
            {
            otherlv_0=(Token)match(input,13,FOLLOW_23); 

            			newLeafNode(otherlv_0, grammarAccess.getAndAccess().getEqualsSignEqualsSignEqualsSignKeyword_0());
            		
            otherlv_1=(Token)match(input,22,FOLLOW_4); 

            			newLeafNode(otherlv_1, grammarAccess.getAndAccess().getAndKeyword_1());
            		
            // InternalAsciiDoc.g:1169:3: ( (lv_stepObjectName_2_0= ruleStepObjectRef ) )
            // InternalAsciiDoc.g:1170:4: (lv_stepObjectName_2_0= ruleStepObjectRef )
            {
            // InternalAsciiDoc.g:1170:4: (lv_stepObjectName_2_0= ruleStepObjectRef )
            // InternalAsciiDoc.g:1171:5: lv_stepObjectName_2_0= ruleStepObjectRef
            {

            					newCompositeNode(grammarAccess.getAndAccess().getStepObjectNameStepObjectRefParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_4);
            lv_stepObjectName_2_0=ruleStepObjectRef();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getAndRule());
            					}
            					set(
            						current,
            						"stepObjectName",
            						lv_stepObjectName_2_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.StepObjectRef");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalAsciiDoc.g:1188:3: ( (lv_stepDefinitionName_3_0= ruleStepDefinitionRef ) )
            // InternalAsciiDoc.g:1189:4: (lv_stepDefinitionName_3_0= ruleStepDefinitionRef )
            {
            // InternalAsciiDoc.g:1189:4: (lv_stepDefinitionName_3_0= ruleStepDefinitionRef )
            // InternalAsciiDoc.g:1190:5: lv_stepDefinitionName_3_0= ruleStepDefinitionRef
            {

            					newCompositeNode(grammarAccess.getAndAccess().getStepDefinitionNameStepDefinitionRefParserRuleCall_3_0());
            				
            pushFollow(FOLLOW_5);
            lv_stepDefinitionName_3_0=ruleStepDefinitionRef();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getAndRule());
            					}
            					set(
            						current,
            						"stepDefinitionName",
            						lv_stepDefinitionName_3_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.StepDefinitionRef");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_4=(Token)match(input,RULE_EOL,FOLLOW_19); 

            			newLeafNode(this_EOL_4, grammarAccess.getAndAccess().getEOLTerminalRuleCall_4());
            		
            // InternalAsciiDoc.g:1211:3: ( (lv_table_5_0= ruleTable ) )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==23) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // InternalAsciiDoc.g:1212:4: (lv_table_5_0= ruleTable )
                    {
                    // InternalAsciiDoc.g:1212:4: (lv_table_5_0= ruleTable )
                    // InternalAsciiDoc.g:1213:5: lv_table_5_0= ruleTable
                    {

                    					newCompositeNode(grammarAccess.getAndAccess().getTableTableParserRuleCall_5_0());
                    				
                    pushFollow(FOLLOW_20);
                    lv_table_5_0=ruleTable();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getAndRule());
                    					}
                    					set(
                    						current,
                    						"table",
                    						lv_table_5_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Table");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }

            // InternalAsciiDoc.g:1230:3: ( (lv_text_6_0= ruleText ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==RULE_TEXT_BLOCK) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // InternalAsciiDoc.g:1231:4: (lv_text_6_0= ruleText )
                    {
                    // InternalAsciiDoc.g:1231:4: (lv_text_6_0= ruleText )
                    // InternalAsciiDoc.g:1232:5: lv_text_6_0= ruleText
                    {

                    					newCompositeNode(grammarAccess.getAndAccess().getTextTextParserRuleCall_6_0());
                    				
                    pushFollow(FOLLOW_2);
                    lv_text_6_0=ruleText();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getAndRule());
                    					}
                    					set(
                    						current,
                    						"text",
                    						lv_text_6_0,
                    						"org.farhan.dsl.asciidoc.AsciiDoc.Text");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAnd"


    // $ANTLR start "entryRuleText"
    // InternalAsciiDoc.g:1253:1: entryRuleText returns [EObject current=null] : iv_ruleText= ruleText EOF ;
    public final EObject entryRuleText() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleText = null;


        try {
            // InternalAsciiDoc.g:1253:45: (iv_ruleText= ruleText EOF )
            // InternalAsciiDoc.g:1254:2: iv_ruleText= ruleText EOF
            {
             newCompositeNode(grammarAccess.getTextRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleText=ruleText();

            state._fsp--;

             current =iv_ruleText; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleText"


    // $ANTLR start "ruleText"
    // InternalAsciiDoc.g:1260:1: ruleText returns [EObject current=null] : ( ( (lv_content_0_0= RULE_TEXT_BLOCK ) ) this_EOL_1= RULE_EOL ) ;
    public final EObject ruleText() throws RecognitionException {
        EObject current = null;

        Token lv_content_0_0=null;
        Token this_EOL_1=null;


        	enterRule();

        try {
            // InternalAsciiDoc.g:1266:2: ( ( ( (lv_content_0_0= RULE_TEXT_BLOCK ) ) this_EOL_1= RULE_EOL ) )
            // InternalAsciiDoc.g:1267:2: ( ( (lv_content_0_0= RULE_TEXT_BLOCK ) ) this_EOL_1= RULE_EOL )
            {
            // InternalAsciiDoc.g:1267:2: ( ( (lv_content_0_0= RULE_TEXT_BLOCK ) ) this_EOL_1= RULE_EOL )
            // InternalAsciiDoc.g:1268:3: ( (lv_content_0_0= RULE_TEXT_BLOCK ) ) this_EOL_1= RULE_EOL
            {
            // InternalAsciiDoc.g:1268:3: ( (lv_content_0_0= RULE_TEXT_BLOCK ) )
            // InternalAsciiDoc.g:1269:4: (lv_content_0_0= RULE_TEXT_BLOCK )
            {
            // InternalAsciiDoc.g:1269:4: (lv_content_0_0= RULE_TEXT_BLOCK )
            // InternalAsciiDoc.g:1270:5: lv_content_0_0= RULE_TEXT_BLOCK
            {
            lv_content_0_0=(Token)match(input,RULE_TEXT_BLOCK,FOLLOW_5); 

            					newLeafNode(lv_content_0_0, grammarAccess.getTextAccess().getContentTEXT_BLOCKTerminalRuleCall_0_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getTextRule());
            					}
            					setWithLastConsumed(
            						current,
            						"content",
            						lv_content_0_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.TEXT_BLOCK");
            				

            }


            }

            this_EOL_1=(Token)match(input,RULE_EOL,FOLLOW_2); 

            			newLeafNode(this_EOL_1, grammarAccess.getTextAccess().getEOLTerminalRuleCall_1());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleText"


    // $ANTLR start "entryRuleDescription"
    // InternalAsciiDoc.g:1294:1: entryRuleDescription returns [EObject current=null] : iv_ruleDescription= ruleDescription EOF ;
    public final EObject entryRuleDescription() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDescription = null;


        try {
            // InternalAsciiDoc.g:1294:52: (iv_ruleDescription= ruleDescription EOF )
            // InternalAsciiDoc.g:1295:2: iv_ruleDescription= ruleDescription EOF
            {
             newCompositeNode(grammarAccess.getDescriptionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleDescription=ruleDescription();

            state._fsp--;

             current =iv_ruleDescription; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDescription"


    // $ANTLR start "ruleDescription"
    // InternalAsciiDoc.g:1301:1: ruleDescription returns [EObject current=null] : ( (lv_lineList_0_0= ruleLine ) )+ ;
    public final EObject ruleDescription() throws RecognitionException {
        EObject current = null;

        EObject lv_lineList_0_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:1307:2: ( ( (lv_lineList_0_0= ruleLine ) )+ )
            // InternalAsciiDoc.g:1308:2: ( (lv_lineList_0_0= ruleLine ) )+
            {
            // InternalAsciiDoc.g:1308:2: ( (lv_lineList_0_0= ruleLine ) )+
            int cnt27=0;
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==RULE_WORD) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // InternalAsciiDoc.g:1309:3: (lv_lineList_0_0= ruleLine )
            	    {
            	    // InternalAsciiDoc.g:1309:3: (lv_lineList_0_0= ruleLine )
            	    // InternalAsciiDoc.g:1310:4: lv_lineList_0_0= ruleLine
            	    {

            	    				newCompositeNode(grammarAccess.getDescriptionAccess().getLineListLineParserRuleCall_0());
            	    			
            	    pushFollow(FOLLOW_24);
            	    lv_lineList_0_0=ruleLine();

            	    state._fsp--;


            	    				if (current==null) {
            	    					current = createModelElementForParent(grammarAccess.getDescriptionRule());
            	    				}
            	    				add(
            	    					current,
            	    					"lineList",
            	    					lv_lineList_0_0,
            	    					"org.farhan.dsl.asciidoc.AsciiDoc.Line");
            	    				afterParserOrEnumRuleCall();
            	    			

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt27 >= 1 ) break loop27;
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDescription"


    // $ANTLR start "entryRuleTable"
    // InternalAsciiDoc.g:1330:1: entryRuleTable returns [EObject current=null] : iv_ruleTable= ruleTable EOF ;
    public final EObject entryRuleTable() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTable = null;


        try {
            // InternalAsciiDoc.g:1330:46: (iv_ruleTable= ruleTable EOF )
            // InternalAsciiDoc.g:1331:2: iv_ruleTable= ruleTable EOF
            {
             newCompositeNode(grammarAccess.getTableRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleTable=ruleTable();

            state._fsp--;

             current =iv_ruleTable; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTable"


    // $ANTLR start "ruleTable"
    // InternalAsciiDoc.g:1337:1: ruleTable returns [EObject current=null] : (otherlv_0= '|===' this_EOL_1= RULE_EOL ( (lv_rowList_2_0= ruleRow ) )+ otherlv_3= '|===' this_EOL_4= RULE_EOL ) ;
    public final EObject ruleTable() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token this_EOL_1=null;
        Token otherlv_3=null;
        Token this_EOL_4=null;
        EObject lv_rowList_2_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:1343:2: ( (otherlv_0= '|===' this_EOL_1= RULE_EOL ( (lv_rowList_2_0= ruleRow ) )+ otherlv_3= '|===' this_EOL_4= RULE_EOL ) )
            // InternalAsciiDoc.g:1344:2: (otherlv_0= '|===' this_EOL_1= RULE_EOL ( (lv_rowList_2_0= ruleRow ) )+ otherlv_3= '|===' this_EOL_4= RULE_EOL )
            {
            // InternalAsciiDoc.g:1344:2: (otherlv_0= '|===' this_EOL_1= RULE_EOL ( (lv_rowList_2_0= ruleRow ) )+ otherlv_3= '|===' this_EOL_4= RULE_EOL )
            // InternalAsciiDoc.g:1345:3: otherlv_0= '|===' this_EOL_1= RULE_EOL ( (lv_rowList_2_0= ruleRow ) )+ otherlv_3= '|===' this_EOL_4= RULE_EOL
            {
            otherlv_0=(Token)match(input,23,FOLLOW_5); 

            			newLeafNode(otherlv_0, grammarAccess.getTableAccess().getVerticalLineEqualsSignEqualsSignEqualsSignKeyword_0());
            		
            this_EOL_1=(Token)match(input,RULE_EOL,FOLLOW_25); 

            			newLeafNode(this_EOL_1, grammarAccess.getTableAccess().getEOLTerminalRuleCall_1());
            		
            // InternalAsciiDoc.g:1353:3: ( (lv_rowList_2_0= ruleRow ) )+
            int cnt28=0;
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==24) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // InternalAsciiDoc.g:1354:4: (lv_rowList_2_0= ruleRow )
            	    {
            	    // InternalAsciiDoc.g:1354:4: (lv_rowList_2_0= ruleRow )
            	    // InternalAsciiDoc.g:1355:5: lv_rowList_2_0= ruleRow
            	    {

            	    					newCompositeNode(grammarAccess.getTableAccess().getRowListRowParserRuleCall_2_0());
            	    				
            	    pushFollow(FOLLOW_26);
            	    lv_rowList_2_0=ruleRow();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getTableRule());
            	    					}
            	    					add(
            	    						current,
            	    						"rowList",
            	    						lv_rowList_2_0,
            	    						"org.farhan.dsl.asciidoc.AsciiDoc.Row");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt28 >= 1 ) break loop28;
                        EarlyExitException eee =
                            new EarlyExitException(28, input);
                        throw eee;
                }
                cnt28++;
            } while (true);

            otherlv_3=(Token)match(input,23,FOLLOW_5); 

            			newLeafNode(otherlv_3, grammarAccess.getTableAccess().getVerticalLineEqualsSignEqualsSignEqualsSignKeyword_3());
            		
            this_EOL_4=(Token)match(input,RULE_EOL,FOLLOW_2); 

            			newLeafNode(this_EOL_4, grammarAccess.getTableAccess().getEOLTerminalRuleCall_4());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTable"


    // $ANTLR start "entryRuleRow"
    // InternalAsciiDoc.g:1384:1: entryRuleRow returns [EObject current=null] : iv_ruleRow= ruleRow EOF ;
    public final EObject entryRuleRow() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRow = null;


        try {
            // InternalAsciiDoc.g:1384:44: (iv_ruleRow= ruleRow EOF )
            // InternalAsciiDoc.g:1385:2: iv_ruleRow= ruleRow EOF
            {
             newCompositeNode(grammarAccess.getRowRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRow=ruleRow();

            state._fsp--;

             current =iv_ruleRow; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRow"


    // $ANTLR start "ruleRow"
    // InternalAsciiDoc.g:1391:1: ruleRow returns [EObject current=null] : ( ( (lv_cellList_0_0= ruleCell ) )+ this_EOL_1= RULE_EOL ) ;
    public final EObject ruleRow() throws RecognitionException {
        EObject current = null;

        Token this_EOL_1=null;
        EObject lv_cellList_0_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:1397:2: ( ( ( (lv_cellList_0_0= ruleCell ) )+ this_EOL_1= RULE_EOL ) )
            // InternalAsciiDoc.g:1398:2: ( ( (lv_cellList_0_0= ruleCell ) )+ this_EOL_1= RULE_EOL )
            {
            // InternalAsciiDoc.g:1398:2: ( ( (lv_cellList_0_0= ruleCell ) )+ this_EOL_1= RULE_EOL )
            // InternalAsciiDoc.g:1399:3: ( (lv_cellList_0_0= ruleCell ) )+ this_EOL_1= RULE_EOL
            {
            // InternalAsciiDoc.g:1399:3: ( (lv_cellList_0_0= ruleCell ) )+
            int cnt29=0;
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==24) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // InternalAsciiDoc.g:1400:4: (lv_cellList_0_0= ruleCell )
            	    {
            	    // InternalAsciiDoc.g:1400:4: (lv_cellList_0_0= ruleCell )
            	    // InternalAsciiDoc.g:1401:5: lv_cellList_0_0= ruleCell
            	    {

            	    					newCompositeNode(grammarAccess.getRowAccess().getCellListCellParserRuleCall_0_0());
            	    				
            	    pushFollow(FOLLOW_27);
            	    lv_cellList_0_0=ruleCell();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getRowRule());
            	    					}
            	    					add(
            	    						current,
            	    						"cellList",
            	    						lv_cellList_0_0,
            	    						"org.farhan.dsl.asciidoc.AsciiDoc.Cell");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt29 >= 1 ) break loop29;
                        EarlyExitException eee =
                            new EarlyExitException(29, input);
                        throw eee;
                }
                cnt29++;
            } while (true);

            this_EOL_1=(Token)match(input,RULE_EOL,FOLLOW_2); 

            			newLeafNode(this_EOL_1, grammarAccess.getRowAccess().getEOLTerminalRuleCall_1());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRow"


    // $ANTLR start "entryRuleCell"
    // InternalAsciiDoc.g:1426:1: entryRuleCell returns [EObject current=null] : iv_ruleCell= ruleCell EOF ;
    public final EObject entryRuleCell() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCell = null;


        try {
            // InternalAsciiDoc.g:1426:45: (iv_ruleCell= ruleCell EOF )
            // InternalAsciiDoc.g:1427:2: iv_ruleCell= ruleCell EOF
            {
             newCompositeNode(grammarAccess.getCellRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCell=ruleCell();

            state._fsp--;

             current =iv_ruleCell; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleCell"


    // $ANTLR start "ruleCell"
    // InternalAsciiDoc.g:1433:1: ruleCell returns [EObject current=null] : (otherlv_0= '|' ( (lv_name_1_0= rulePhrase ) ) ) ;
    public final EObject ruleCell() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        AntlrDatatypeRuleToken lv_name_1_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:1439:2: ( (otherlv_0= '|' ( (lv_name_1_0= rulePhrase ) ) ) )
            // InternalAsciiDoc.g:1440:2: (otherlv_0= '|' ( (lv_name_1_0= rulePhrase ) ) )
            {
            // InternalAsciiDoc.g:1440:2: (otherlv_0= '|' ( (lv_name_1_0= rulePhrase ) ) )
            // InternalAsciiDoc.g:1441:3: otherlv_0= '|' ( (lv_name_1_0= rulePhrase ) )
            {
            otherlv_0=(Token)match(input,24,FOLLOW_4); 

            			newLeafNode(otherlv_0, grammarAccess.getCellAccess().getVerticalLineKeyword_0());
            		
            // InternalAsciiDoc.g:1445:3: ( (lv_name_1_0= rulePhrase ) )
            // InternalAsciiDoc.g:1446:4: (lv_name_1_0= rulePhrase )
            {
            // InternalAsciiDoc.g:1446:4: (lv_name_1_0= rulePhrase )
            // InternalAsciiDoc.g:1447:5: lv_name_1_0= rulePhrase
            {

            					newCompositeNode(grammarAccess.getCellAccess().getNamePhraseParserRuleCall_1_0());
            				
            pushFollow(FOLLOW_2);
            lv_name_1_0=rulePhrase();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getCellRule());
            					}
            					set(
            						current,
            						"name",
            						lv_name_1_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.Phrase");
            					afterParserOrEnumRuleCall();
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCell"


    // $ANTLR start "entryRuleLine"
    // InternalAsciiDoc.g:1468:1: entryRuleLine returns [EObject current=null] : iv_ruleLine= ruleLine EOF ;
    public final EObject entryRuleLine() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleLine = null;


        try {
            // InternalAsciiDoc.g:1468:45: (iv_ruleLine= ruleLine EOF )
            // InternalAsciiDoc.g:1469:2: iv_ruleLine= ruleLine EOF
            {
             newCompositeNode(grammarAccess.getLineRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleLine=ruleLine();

            state._fsp--;

             current =iv_ruleLine; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleLine"


    // $ANTLR start "ruleLine"
    // InternalAsciiDoc.g:1475:1: ruleLine returns [EObject current=null] : ( ( (lv_content_0_0= rulePhrase ) ) this_EOL_1= RULE_EOL ) ;
    public final EObject ruleLine() throws RecognitionException {
        EObject current = null;

        Token this_EOL_1=null;
        AntlrDatatypeRuleToken lv_content_0_0 = null;



        	enterRule();

        try {
            // InternalAsciiDoc.g:1481:2: ( ( ( (lv_content_0_0= rulePhrase ) ) this_EOL_1= RULE_EOL ) )
            // InternalAsciiDoc.g:1482:2: ( ( (lv_content_0_0= rulePhrase ) ) this_EOL_1= RULE_EOL )
            {
            // InternalAsciiDoc.g:1482:2: ( ( (lv_content_0_0= rulePhrase ) ) this_EOL_1= RULE_EOL )
            // InternalAsciiDoc.g:1483:3: ( (lv_content_0_0= rulePhrase ) ) this_EOL_1= RULE_EOL
            {
            // InternalAsciiDoc.g:1483:3: ( (lv_content_0_0= rulePhrase ) )
            // InternalAsciiDoc.g:1484:4: (lv_content_0_0= rulePhrase )
            {
            // InternalAsciiDoc.g:1484:4: (lv_content_0_0= rulePhrase )
            // InternalAsciiDoc.g:1485:5: lv_content_0_0= rulePhrase
            {

            					newCompositeNode(grammarAccess.getLineAccess().getContentPhraseParserRuleCall_0_0());
            				
            pushFollow(FOLLOW_5);
            lv_content_0_0=rulePhrase();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getLineRule());
            					}
            					set(
            						current,
            						"content",
            						lv_content_0_0,
            						"org.farhan.dsl.asciidoc.AsciiDoc.Phrase");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            this_EOL_1=(Token)match(input,RULE_EOL,FOLLOW_2); 

            			newLeafNode(this_EOL_1, grammarAccess.getLineAccess().getEOLTerminalRuleCall_1());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleLine"


    // $ANTLR start "entryRulePhrase"
    // InternalAsciiDoc.g:1510:1: entryRulePhrase returns [String current=null] : iv_rulePhrase= rulePhrase EOF ;
    public final String entryRulePhrase() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePhrase = null;


        try {
            // InternalAsciiDoc.g:1510:46: (iv_rulePhrase= rulePhrase EOF )
            // InternalAsciiDoc.g:1511:2: iv_rulePhrase= rulePhrase EOF
            {
             newCompositeNode(grammarAccess.getPhraseRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePhrase=rulePhrase();

            state._fsp--;

             current =iv_rulePhrase.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePhrase"


    // $ANTLR start "rulePhrase"
    // InternalAsciiDoc.g:1517:1: rulePhrase returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_WORD_0= RULE_WORD )+ ;
    public final AntlrDatatypeRuleToken rulePhrase() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WORD_0=null;


        	enterRule();

        try {
            // InternalAsciiDoc.g:1523:2: ( (this_WORD_0= RULE_WORD )+ )
            // InternalAsciiDoc.g:1524:2: (this_WORD_0= RULE_WORD )+
            {
            // InternalAsciiDoc.g:1524:2: (this_WORD_0= RULE_WORD )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==RULE_WORD) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // InternalAsciiDoc.g:1525:3: this_WORD_0= RULE_WORD
            	    {
            	    this_WORD_0=(Token)match(input,RULE_WORD,FOLLOW_24); 

            	    			current.merge(this_WORD_0);
            	    		

            	    			newLeafNode(this_WORD_0, grammarAccess.getPhraseAccess().getWORDTerminalRuleCall());
            	    		

            	    }
            	    break;

            	default :
            	    if ( cnt30 >= 1 ) break loop30;
                        EarlyExitException eee =
                            new EarlyExitException(30, input);
                        throw eee;
                }
                cnt30++;
            } while (true);


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePhrase"


    // $ANTLR start "entryRuleStepObjectRef"
    // InternalAsciiDoc.g:1536:1: entryRuleStepObjectRef returns [String current=null] : iv_ruleStepObjectRef= ruleStepObjectRef EOF ;
    public final String entryRuleStepObjectRef() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleStepObjectRef = null;


        try {
            // InternalAsciiDoc.g:1536:53: (iv_ruleStepObjectRef= ruleStepObjectRef EOF )
            // InternalAsciiDoc.g:1537:2: iv_ruleStepObjectRef= ruleStepObjectRef EOF
            {
             newCompositeNode(grammarAccess.getStepObjectRefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleStepObjectRef=ruleStepObjectRef();

            state._fsp--;

             current =iv_ruleStepObjectRef.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStepObjectRef"


    // $ANTLR start "ruleStepObjectRef"
    // InternalAsciiDoc.g:1543:1: ruleStepObjectRef returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (this_WORD_0= RULE_WORD )+ (kw= 'file' | kw= 'page' | kw= 'response' | kw= 'dialog' | kw= 'directory' | kw= 'request' | kw= 'goal' | kw= 'job' | kw= 'action' | kw= 'popup' | kw= 'annotation' | kw= 'hover' | kw= 'tooltip' ) ) ;
    public final AntlrDatatypeRuleToken ruleStepObjectRef() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WORD_0=null;
        Token kw=null;


        	enterRule();

        try {
            // InternalAsciiDoc.g:1549:2: ( ( (this_WORD_0= RULE_WORD )+ (kw= 'file' | kw= 'page' | kw= 'response' | kw= 'dialog' | kw= 'directory' | kw= 'request' | kw= 'goal' | kw= 'job' | kw= 'action' | kw= 'popup' | kw= 'annotation' | kw= 'hover' | kw= 'tooltip' ) ) )
            // InternalAsciiDoc.g:1550:2: ( (this_WORD_0= RULE_WORD )+ (kw= 'file' | kw= 'page' | kw= 'response' | kw= 'dialog' | kw= 'directory' | kw= 'request' | kw= 'goal' | kw= 'job' | kw= 'action' | kw= 'popup' | kw= 'annotation' | kw= 'hover' | kw= 'tooltip' ) )
            {
            // InternalAsciiDoc.g:1550:2: ( (this_WORD_0= RULE_WORD )+ (kw= 'file' | kw= 'page' | kw= 'response' | kw= 'dialog' | kw= 'directory' | kw= 'request' | kw= 'goal' | kw= 'job' | kw= 'action' | kw= 'popup' | kw= 'annotation' | kw= 'hover' | kw= 'tooltip' ) )
            // InternalAsciiDoc.g:1551:3: (this_WORD_0= RULE_WORD )+ (kw= 'file' | kw= 'page' | kw= 'response' | kw= 'dialog' | kw= 'directory' | kw= 'request' | kw= 'goal' | kw= 'job' | kw= 'action' | kw= 'popup' | kw= 'annotation' | kw= 'hover' | kw= 'tooltip' )
            {
            // InternalAsciiDoc.g:1551:3: (this_WORD_0= RULE_WORD )+
            int cnt31=0;
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==RULE_WORD) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // InternalAsciiDoc.g:1552:4: this_WORD_0= RULE_WORD
            	    {
            	    this_WORD_0=(Token)match(input,RULE_WORD,FOLLOW_28); 

            	    				current.merge(this_WORD_0);
            	    			

            	    				newLeafNode(this_WORD_0, grammarAccess.getStepObjectRefAccess().getWORDTerminalRuleCall_0());
            	    			

            	    }
            	    break;

            	default :
            	    if ( cnt31 >= 1 ) break loop31;
                        EarlyExitException eee =
                            new EarlyExitException(31, input);
                        throw eee;
                }
                cnt31++;
            } while (true);

            // InternalAsciiDoc.g:1560:3: (kw= 'file' | kw= 'page' | kw= 'response' | kw= 'dialog' | kw= 'directory' | kw= 'request' | kw= 'goal' | kw= 'job' | kw= 'action' | kw= 'popup' | kw= 'annotation' | kw= 'hover' | kw= 'tooltip' )
            int alt32=13;
            switch ( input.LA(1) ) {
            case 25:
                {
                alt32=1;
                }
                break;
            case 26:
                {
                alt32=2;
                }
                break;
            case 27:
                {
                alt32=3;
                }
                break;
            case 28:
                {
                alt32=4;
                }
                break;
            case 29:
                {
                alt32=5;
                }
                break;
            case 30:
                {
                alt32=6;
                }
                break;
            case 31:
                {
                alt32=7;
                }
                break;
            case 32:
                {
                alt32=8;
                }
                break;
            case 33:
                {
                alt32=9;
                }
                break;
            case 34:
                {
                alt32=10;
                }
                break;
            case 35:
                {
                alt32=11;
                }
                break;
            case 36:
                {
                alt32=12;
                }
                break;
            case 37:
                {
                alt32=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // InternalAsciiDoc.g:1561:4: kw= 'file'
                    {
                    kw=(Token)match(input,25,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getFileKeyword_1_0());
                    			

                    }
                    break;
                case 2 :
                    // InternalAsciiDoc.g:1567:4: kw= 'page'
                    {
                    kw=(Token)match(input,26,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getPageKeyword_1_1());
                    			

                    }
                    break;
                case 3 :
                    // InternalAsciiDoc.g:1573:4: kw= 'response'
                    {
                    kw=(Token)match(input,27,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getResponseKeyword_1_2());
                    			

                    }
                    break;
                case 4 :
                    // InternalAsciiDoc.g:1579:4: kw= 'dialog'
                    {
                    kw=(Token)match(input,28,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getDialogKeyword_1_3());
                    			

                    }
                    break;
                case 5 :
                    // InternalAsciiDoc.g:1585:4: kw= 'directory'
                    {
                    kw=(Token)match(input,29,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getDirectoryKeyword_1_4());
                    			

                    }
                    break;
                case 6 :
                    // InternalAsciiDoc.g:1591:4: kw= 'request'
                    {
                    kw=(Token)match(input,30,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getRequestKeyword_1_5());
                    			

                    }
                    break;
                case 7 :
                    // InternalAsciiDoc.g:1597:4: kw= 'goal'
                    {
                    kw=(Token)match(input,31,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getGoalKeyword_1_6());
                    			

                    }
                    break;
                case 8 :
                    // InternalAsciiDoc.g:1603:4: kw= 'job'
                    {
                    kw=(Token)match(input,32,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getJobKeyword_1_7());
                    			

                    }
                    break;
                case 9 :
                    // InternalAsciiDoc.g:1609:4: kw= 'action'
                    {
                    kw=(Token)match(input,33,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getActionKeyword_1_8());
                    			

                    }
                    break;
                case 10 :
                    // InternalAsciiDoc.g:1615:4: kw= 'popup'
                    {
                    kw=(Token)match(input,34,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getPopupKeyword_1_9());
                    			

                    }
                    break;
                case 11 :
                    // InternalAsciiDoc.g:1621:4: kw= 'annotation'
                    {
                    kw=(Token)match(input,35,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getAnnotationKeyword_1_10());
                    			

                    }
                    break;
                case 12 :
                    // InternalAsciiDoc.g:1627:4: kw= 'hover'
                    {
                    kw=(Token)match(input,36,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getHoverKeyword_1_11());
                    			

                    }
                    break;
                case 13 :
                    // InternalAsciiDoc.g:1633:4: kw= 'tooltip'
                    {
                    kw=(Token)match(input,37,FOLLOW_2); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getStepObjectRefAccess().getTooltipKeyword_1_12());
                    			

                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStepObjectRef"


    // $ANTLR start "entryRuleStepDefinitionRef"
    // InternalAsciiDoc.g:1643:1: entryRuleStepDefinitionRef returns [String current=null] : iv_ruleStepDefinitionRef= ruleStepDefinitionRef EOF ;
    public final String entryRuleStepDefinitionRef() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleStepDefinitionRef = null;


        try {
            // InternalAsciiDoc.g:1643:57: (iv_ruleStepDefinitionRef= ruleStepDefinitionRef EOF )
            // InternalAsciiDoc.g:1644:2: iv_ruleStepDefinitionRef= ruleStepDefinitionRef EOF
            {
             newCompositeNode(grammarAccess.getStepDefinitionRefRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleStepDefinitionRef=ruleStepDefinitionRef();

            state._fsp--;

             current =iv_ruleStepDefinitionRef.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStepDefinitionRef"


    // $ANTLR start "ruleStepDefinitionRef"
    // InternalAsciiDoc.g:1650:1: ruleStepDefinitionRef returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_WORD_0= RULE_WORD )+ ;
    public final AntlrDatatypeRuleToken ruleStepDefinitionRef() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_WORD_0=null;


        	enterRule();

        try {
            // InternalAsciiDoc.g:1656:2: ( (this_WORD_0= RULE_WORD )+ )
            // InternalAsciiDoc.g:1657:2: (this_WORD_0= RULE_WORD )+
            {
            // InternalAsciiDoc.g:1657:2: (this_WORD_0= RULE_WORD )+
            int cnt33=0;
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==RULE_WORD) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // InternalAsciiDoc.g:1658:3: this_WORD_0= RULE_WORD
            	    {
            	    this_WORD_0=(Token)match(input,RULE_WORD,FOLLOW_24); 

            	    			current.merge(this_WORD_0);
            	    		

            	    			newLeafNode(this_WORD_0, grammarAccess.getStepDefinitionRefAccess().getWORDTerminalRuleCall());
            	    		

            	    }
            	    break;

            	default :
            	    if ( cnt33 >= 1 ) break loop33;
                        EarlyExitException eee =
                            new EarlyExitException(33, input);
                        throw eee;
                }
                cnt33++;
            } while (true);


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStepDefinitionRef"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000000842L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000002042L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000800042L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000800022L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000001800000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000001000010L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000003FFE000040L});

}