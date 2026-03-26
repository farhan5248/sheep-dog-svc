package org.farhan.dsl.asciidoc.ide.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalAsciiDocLexer extends Lexer {
    public static final int RULE_WORD=4;
    public static final int RULE_TEXT_BLOCK=6;
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
    public static final int RULE_EOL=5;
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

    public InternalAsciiDocLexer() {;} 
    public InternalAsciiDocLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalAsciiDocLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "InternalAsciiDoc.g"; }

    // $ANTLR start "T__9"
    public final void mT__9() throws RecognitionException {
        try {
            int _type = T__9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:11:6: ( 'file' )
            // InternalAsciiDoc.g:11:8: 'file'
            {
            match("file"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__9"

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:12:7: ( 'page' )
            // InternalAsciiDoc.g:12:9: 'page'
            {
            match("page"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:13:7: ( 'response' )
            // InternalAsciiDoc.g:13:9: 'response'
            {
            match("response"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:14:7: ( 'dialog' )
            // InternalAsciiDoc.g:14:9: 'dialog'
            {
            match("dialog"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:15:7: ( 'directory' )
            // InternalAsciiDoc.g:15:9: 'directory'
            {
            match("directory"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:16:7: ( 'request' )
            // InternalAsciiDoc.g:16:9: 'request'
            {
            match("request"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:17:7: ( 'goal' )
            // InternalAsciiDoc.g:17:9: 'goal'
            {
            match("goal"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:18:7: ( 'job' )
            // InternalAsciiDoc.g:18:9: 'job'
            {
            match("job"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:19:7: ( 'action' )
            // InternalAsciiDoc.g:19:9: 'action'
            {
            match("action"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:20:7: ( 'popup' )
            // InternalAsciiDoc.g:20:9: 'popup'
            {
            match("popup"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:21:7: ( 'annotation' )
            // InternalAsciiDoc.g:21:9: 'annotation'
            {
            match("annotation"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:22:7: ( 'hover' )
            // InternalAsciiDoc.g:22:9: 'hover'
            {
            match("hover"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:23:7: ( 'tooltip' )
            // InternalAsciiDoc.g:23:9: 'tooltip'
            {
            match("tooltip"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:24:7: ( '=' )
            // InternalAsciiDoc.g:24:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:25:7: ( 'Step-Object:' )
            // InternalAsciiDoc.g:25:9: 'Step-Object:'
            {
            match("Step-Object:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:26:7: ( '==' )
            // InternalAsciiDoc.g:26:9: '=='
            {
            match("=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:27:7: ( 'Step-Definition:' )
            // InternalAsciiDoc.g:27:9: 'Step-Definition:'
            {
            match("Step-Definition:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:28:7: ( '===' )
            // InternalAsciiDoc.g:28:9: '==='
            {
            match("==="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:29:7: ( 'Step-Parameters:' )
            // InternalAsciiDoc.g:29:9: 'Step-Parameters:'
            {
            match("Step-Parameters:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:30:7: ( 'Test-Suite:' )
            // InternalAsciiDoc.g:30:9: 'Test-Suite:'
            {
            match("Test-Suite:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:31:7: ( 'Test-Setup:' )
            // InternalAsciiDoc.g:31:9: 'Test-Setup:'
            {
            match("Test-Setup:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:32:7: ( 'Test-Case:' )
            // InternalAsciiDoc.g:32:9: 'Test-Case:'
            {
            match("Test-Case:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:33:7: ( 'Test-Data:' )
            // InternalAsciiDoc.g:33:9: 'Test-Data:'
            {
            match("Test-Data:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:34:7: ( 'Given:' )
            // InternalAsciiDoc.g:34:9: 'Given:'
            {
            match("Given:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:35:7: ( 'When:' )
            // InternalAsciiDoc.g:35:9: 'When:'
            {
            match("When:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:36:7: ( 'Then:' )
            // InternalAsciiDoc.g:36:9: 'Then:'
            {
            match("Then:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:37:7: ( 'And:' )
            // InternalAsciiDoc.g:37:9: 'And:'
            {
            match("And:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:38:7: ( '|===' )
            // InternalAsciiDoc.g:38:9: '|==='
            {
            match("|==="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:39:7: ( '|' )
            // InternalAsciiDoc.g:39:9: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "RULE_WS"
    public final void mRULE_WS() throws RecognitionException {
        try {
            int _type = RULE_WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:3813:9: ( ( ' ' | '\\t' | '\\r' )+ )
            // InternalAsciiDoc.g:3813:11: ( ' ' | '\\t' | '\\r' )+
            {
            // InternalAsciiDoc.g:3813:11: ( ' ' | '\\t' | '\\r' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\t'||LA1_0=='\r'||LA1_0==' ') ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalAsciiDoc.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_WS"

    // $ANTLR start "RULE_SL_COMMENT"
    public final void mRULE_SL_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:3815:17: ( '#' (~ ( '\\n' ) )* RULE_EOL )
            // InternalAsciiDoc.g:3815:19: '#' (~ ( '\\n' ) )* RULE_EOL
            {
            match('#'); 
            // InternalAsciiDoc.g:3815:23: (~ ( '\\n' ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\u0000' && LA2_0<='\t')||(LA2_0>='\u000B' && LA2_0<='\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // InternalAsciiDoc.g:3815:23: ~ ( '\\n' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            mRULE_EOL(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_SL_COMMENT"

    // $ANTLR start "RULE_EOL"
    public final void mRULE_EOL() throws RecognitionException {
        try {
            int _type = RULE_EOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:3817:10: ( ( '\\n' )+ )
            // InternalAsciiDoc.g:3817:12: ( '\\n' )+
            {
            // InternalAsciiDoc.g:3817:12: ( '\\n' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='\n') ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // InternalAsciiDoc.g:3817:12: '\\n'
            	    {
            	    match('\n'); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_EOL"

    // $ANTLR start "RULE_TEXT_BLOCK"
    public final void mRULE_TEXT_BLOCK() throws RecognitionException {
        try {
            int _type = RULE_TEXT_BLOCK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:3819:17: ( '----' '\\n' ( . )+ '\\n' '----' )
            // InternalAsciiDoc.g:3819:19: '----' '\\n' ( . )+ '\\n' '----'
            {
            match("----"); 

            match('\n'); 
            // InternalAsciiDoc.g:3819:31: ( . )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='\n') ) {
                    int LA4_1 = input.LA(2);

                    if ( (LA4_1=='-') ) {
                        int LA4_3 = input.LA(3);

                        if ( (LA4_3=='-') ) {
                            int LA4_4 = input.LA(4);

                            if ( (LA4_4=='-') ) {
                                int LA4_5 = input.LA(5);

                                if ( (LA4_5=='-') ) {
                                    alt4=2;
                                }
                                else if ( ((LA4_5>='\u0000' && LA4_5<=',')||(LA4_5>='.' && LA4_5<='\uFFFF')) ) {
                                    alt4=1;
                                }


                            }
                            else if ( ((LA4_4>='\u0000' && LA4_4<=',')||(LA4_4>='.' && LA4_4<='\uFFFF')) ) {
                                alt4=1;
                            }


                        }
                        else if ( ((LA4_3>='\u0000' && LA4_3<=',')||(LA4_3>='.' && LA4_3<='\uFFFF')) ) {
                            alt4=1;
                        }


                    }
                    else if ( ((LA4_1>='\u0000' && LA4_1<=',')||(LA4_1>='.' && LA4_1<='\uFFFF')) ) {
                        alt4=1;
                    }


                }
                else if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // InternalAsciiDoc.g:3819:31: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

            match('\n'); 
            match("----"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_TEXT_BLOCK"

    // $ANTLR start "RULE_WORD"
    public final void mRULE_WORD() throws RecognitionException {
        try {
            int _type = RULE_WORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // InternalAsciiDoc.g:3821:11: ( (~ ( ( ' ' | '\\t' | '\\r' | '\\n' ) ) )+ )
            // InternalAsciiDoc.g:3821:13: (~ ( ( ' ' | '\\t' | '\\r' | '\\n' ) ) )+
            {
            // InternalAsciiDoc.g:3821:13: (~ ( ( ' ' | '\\t' | '\\r' | '\\n' ) ) )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='\u0000' && LA5_0<='\b')||(LA5_0>='\u000B' && LA5_0<='\f')||(LA5_0>='\u000E' && LA5_0<='\u001F')||(LA5_0>='!' && LA5_0<='\uFFFF')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // InternalAsciiDoc.g:3821:13: ~ ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\u001F')||(input.LA(1)>='!' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_WORD"

    public void mTokens() throws RecognitionException {
        // InternalAsciiDoc.g:1:8: ( T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | RULE_WS | RULE_SL_COMMENT | RULE_EOL | RULE_TEXT_BLOCK | RULE_WORD )
        int alt6=34;
        alt6 = dfa6.predict(input);
        switch (alt6) {
            case 1 :
                // InternalAsciiDoc.g:1:10: T__9
                {
                mT__9(); 

                }
                break;
            case 2 :
                // InternalAsciiDoc.g:1:15: T__10
                {
                mT__10(); 

                }
                break;
            case 3 :
                // InternalAsciiDoc.g:1:21: T__11
                {
                mT__11(); 

                }
                break;
            case 4 :
                // InternalAsciiDoc.g:1:27: T__12
                {
                mT__12(); 

                }
                break;
            case 5 :
                // InternalAsciiDoc.g:1:33: T__13
                {
                mT__13(); 

                }
                break;
            case 6 :
                // InternalAsciiDoc.g:1:39: T__14
                {
                mT__14(); 

                }
                break;
            case 7 :
                // InternalAsciiDoc.g:1:45: T__15
                {
                mT__15(); 

                }
                break;
            case 8 :
                // InternalAsciiDoc.g:1:51: T__16
                {
                mT__16(); 

                }
                break;
            case 9 :
                // InternalAsciiDoc.g:1:57: T__17
                {
                mT__17(); 

                }
                break;
            case 10 :
                // InternalAsciiDoc.g:1:63: T__18
                {
                mT__18(); 

                }
                break;
            case 11 :
                // InternalAsciiDoc.g:1:69: T__19
                {
                mT__19(); 

                }
                break;
            case 12 :
                // InternalAsciiDoc.g:1:75: T__20
                {
                mT__20(); 

                }
                break;
            case 13 :
                // InternalAsciiDoc.g:1:81: T__21
                {
                mT__21(); 

                }
                break;
            case 14 :
                // InternalAsciiDoc.g:1:87: T__22
                {
                mT__22(); 

                }
                break;
            case 15 :
                // InternalAsciiDoc.g:1:93: T__23
                {
                mT__23(); 

                }
                break;
            case 16 :
                // InternalAsciiDoc.g:1:99: T__24
                {
                mT__24(); 

                }
                break;
            case 17 :
                // InternalAsciiDoc.g:1:105: T__25
                {
                mT__25(); 

                }
                break;
            case 18 :
                // InternalAsciiDoc.g:1:111: T__26
                {
                mT__26(); 

                }
                break;
            case 19 :
                // InternalAsciiDoc.g:1:117: T__27
                {
                mT__27(); 

                }
                break;
            case 20 :
                // InternalAsciiDoc.g:1:123: T__28
                {
                mT__28(); 

                }
                break;
            case 21 :
                // InternalAsciiDoc.g:1:129: T__29
                {
                mT__29(); 

                }
                break;
            case 22 :
                // InternalAsciiDoc.g:1:135: T__30
                {
                mT__30(); 

                }
                break;
            case 23 :
                // InternalAsciiDoc.g:1:141: T__31
                {
                mT__31(); 

                }
                break;
            case 24 :
                // InternalAsciiDoc.g:1:147: T__32
                {
                mT__32(); 

                }
                break;
            case 25 :
                // InternalAsciiDoc.g:1:153: T__33
                {
                mT__33(); 

                }
                break;
            case 26 :
                // InternalAsciiDoc.g:1:159: T__34
                {
                mT__34(); 

                }
                break;
            case 27 :
                // InternalAsciiDoc.g:1:165: T__35
                {
                mT__35(); 

                }
                break;
            case 28 :
                // InternalAsciiDoc.g:1:171: T__36
                {
                mT__36(); 

                }
                break;
            case 29 :
                // InternalAsciiDoc.g:1:177: T__37
                {
                mT__37(); 

                }
                break;
            case 30 :
                // InternalAsciiDoc.g:1:183: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 31 :
                // InternalAsciiDoc.g:1:191: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 32 :
                // InternalAsciiDoc.g:1:207: RULE_EOL
                {
                mRULE_EOL(); 

                }
                break;
            case 33 :
                // InternalAsciiDoc.g:1:216: RULE_TEXT_BLOCK
                {
                mRULE_TEXT_BLOCK(); 

                }
                break;
            case 34 :
                // InternalAsciiDoc.g:1:232: RULE_WORD
                {
                mRULE_WORD(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    static final String DFA6_eotS =
        "\1\uffff\11\25\1\42\5\25\1\52\1\uffff\1\25\1\uffff\1\25\1\uffff\13\25\1\74\1\uffff\7\25\1\uffff\1\25\1\uffff\11\25\1\115\4\25\1\122\1\uffff\10\25\1\133\1\134\5\25\1\142\1\uffff\4\25\1\uffff\5\25\1\154\1\155\1\25\2\uffff\1\157\4\25\1\uffff\2\25\1\166\3\25\1\176\1\25\1\u0080\4\uffff\2\25\1\u0083\1\25\1\u0085\1\25\1\uffff\7\25\1\uffff\1\u008f\1\uffff\1\25\1\u0091\1\uffff\1\25\1\uffff\1\25\1\u0094\7\25\1\uffff\1\u009c\1\uffff\2\25\1\uffff\7\25\1\uffff\1\u00a6\10\25\1\uffff\1\u00af\5\25\1\u00b5\1\u00b6\1\uffff\3\25\1\u00ba\1\u00bb\2\uffff\1\u00bc\2\25\3\uffff\6\25\1\u00c5\1\u00c6\2\uffff";
    static final String DFA6_eofS =
        "\u00c7\uffff";
    static final String DFA6_minS =
        "\1\0\1\151\1\141\1\145\1\151\2\157\1\143\2\157\1\0\1\164\1\145\1\151\1\150\1\156\1\0\1\uffff\1\0\1\uffff\1\55\1\uffff\1\154\1\147\1\160\1\161\2\141\1\142\1\164\1\156\1\166\1\157\1\0\1\uffff\1\145\1\163\1\145\1\166\1\145\1\144\1\75\1\uffff\1\0\1\uffff\1\55\2\145\1\165\1\160\1\165\1\154\1\145\1\154\1\0\1\151\1\157\1\145\1\154\1\0\1\uffff\1\160\1\164\1\156\1\145\1\156\1\72\1\75\1\55\2\0\1\160\1\157\1\145\1\157\1\143\1\0\1\uffff\1\157\1\164\1\162\1\164\1\uffff\2\55\1\72\1\156\1\72\2\0\1\12\2\uffff\1\0\1\156\1\163\1\147\1\164\1\uffff\1\156\1\141\1\0\1\151\1\104\1\103\1\0\1\72\1\0\4\uffff\1\163\1\164\1\0\1\157\1\0\1\164\1\uffff\1\160\1\142\1\145\1\141\1\145\2\141\1\uffff\1\0\1\uffff\1\145\1\0\1\uffff\1\162\1\uffff\1\151\1\0\1\152\1\146\1\162\1\151\1\164\1\163\1\164\1\uffff\1\0\1\uffff\1\171\1\157\1\uffff\1\145\1\151\1\141\1\164\1\165\1\145\1\141\1\uffff\1\0\1\156\1\143\1\156\1\155\1\145\1\160\2\72\1\uffff\1\0\1\164\1\151\1\145\2\72\2\0\1\uffff\1\72\2\164\2\0\2\uffff\1\0\1\151\1\145\3\uffff\1\157\1\162\1\156\1\163\2\72\2\0\2\uffff";
    static final String DFA6_maxS =
        "\1\uffff\1\151\1\157\1\145\1\151\2\157\1\156\2\157\1\uffff\1\164\1\150\1\151\1\150\1\156\1\uffff\1\uffff\1\uffff\1\uffff\1\55\1\uffff\1\154\1\147\1\160\1\163\1\162\1\141\1\142\1\164\1\156\1\166\1\157\1\uffff\1\uffff\1\145\1\163\1\145\1\166\1\145\1\144\1\75\1\uffff\1\uffff\1\uffff\1\55\2\145\1\165\1\160\1\165\1\154\1\145\1\154\1\uffff\1\151\1\157\1\145\1\154\1\uffff\1\uffff\1\160\1\164\1\156\1\145\1\156\1\72\1\75\1\55\2\uffff\1\160\1\157\1\145\1\157\1\143\1\uffff\1\uffff\1\157\1\164\1\162\1\164\1\uffff\2\55\1\72\1\156\1\72\2\uffff\1\12\2\uffff\1\uffff\1\156\1\163\1\147\1\164\1\uffff\1\156\1\141\1\uffff\1\151\1\120\1\123\1\uffff\1\72\1\uffff\4\uffff\1\163\1\164\1\uffff\1\157\1\uffff\1\164\1\uffff\1\160\1\142\1\145\1\141\1\165\2\141\1\uffff\1\uffff\1\uffff\1\145\1\uffff\1\uffff\1\162\1\uffff\1\151\1\uffff\1\152\1\146\1\162\1\151\1\164\1\163\1\164\1\uffff\1\uffff\1\uffff\1\171\1\157\1\uffff\1\145\1\151\1\141\1\164\1\165\1\145\1\141\1\uffff\1\uffff\1\156\1\143\1\156\1\155\1\145\1\160\2\72\1\uffff\1\uffff\1\164\1\151\1\145\2\72\2\uffff\1\uffff\1\72\2\164\2\uffff\2\uffff\1\uffff\1\151\1\145\3\uffff\1\157\1\162\1\156\1\163\2\72\2\uffff\2\uffff";
    static final String DFA6_acceptS =
        "\21\uffff\1\36\1\uffff\1\40\1\uffff\1\42\14\uffff\1\16\7\uffff\1\35\1\uffff\1\37\17\uffff\1\20\20\uffff\1\10\4\uffff\1\22\10\uffff\1\1\1\2\5\uffff\1\7\11\uffff\1\33\1\34\1\41\1\12\6\uffff\1\14\7\uffff\1\32\1\uffff\1\31\2\uffff\1\4\1\uffff\1\11\11\uffff\1\30\1\uffff\1\6\2\uffff\1\15\7\uffff\1\3\11\uffff\1\5\10\uffff\1\13\5\uffff\1\26\1\27\3\uffff\1\24\1\25\1\17\10\uffff\1\21\1\23";
    static final String DFA6_specialS =
        "\1\15\11\uffff\1\26\5\uffff\1\0\1\uffff\1\34\16\uffff\1\11\11\uffff\1\17\12\uffff\1\24\4\uffff\1\36\11\uffff\1\13\1\14\5\uffff\1\23\13\uffff\1\10\1\12\3\uffff\1\27\7\uffff\1\31\3\uffff\1\7\1\uffff\1\6\6\uffff\1\20\1\uffff\1\25\12\uffff\1\5\2\uffff\1\22\4\uffff\1\32\10\uffff\1\16\14\uffff\1\21\11\uffff\1\30\5\uffff\1\3\1\4\4\uffff\1\1\1\2\2\uffff\1\33\13\uffff\1\35\1\37\2\uffff}>";
    static final String[] DFA6_transitionS = {
            "\11\25\1\21\1\23\2\25\1\21\22\25\1\21\2\25\1\22\11\25\1\24\17\25\1\12\3\25\1\17\5\25\1\15\13\25\1\13\1\14\2\25\1\16\11\25\1\7\2\25\1\4\1\25\1\1\1\5\1\10\1\25\1\6\5\25\1\2\1\25\1\3\1\25\1\11\7\25\1\20\uff83\25",
            "\1\26",
            "\1\27\15\uffff\1\30",
            "\1\31",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35\12\uffff\1\36",
            "\1\37",
            "\1\40",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\34\25\1\41\uffc2\25",
            "\1\43",
            "\1\44\2\uffff\1\45",
            "\1\46",
            "\1\47",
            "\1\50",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\34\25\1\51\uffc2\25",
            "",
            "\11\53\2\54\2\53\1\54\22\53\1\54\uffdf\53",
            "",
            "\1\55",
            "",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\62\1\uffff\1\61",
            "\1\63\20\uffff\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\34\25\1\73\uffc2\25",
            "",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "",
            "\11\53\2\54\2\53\1\54\22\53\1\54\uffdf\53",
            "",
            "\1\104",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\116",
            "\1\117",
            "\1\120",
            "\1\121",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\156",
            "",
            "",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "",
            "\1\164",
            "\1\165",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\167",
            "\1\171\12\uffff\1\170\1\172",
            "\1\174\1\175\16\uffff\1\173",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\177",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "",
            "",
            "",
            "",
            "\1\u0081",
            "\1\u0082",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\u0084",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\u0086",
            "",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008c\17\uffff\1\u008b",
            "\1\u008d",
            "\1\u008e",
            "",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "",
            "\1\u0090",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "",
            "\1\u0092",
            "",
            "\1\u0093",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\u0095",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\1\u009b",
            "",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "",
            "\1\u009d",
            "\1\u009e",
            "",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\1\u00a4",
            "\1\u00a5",
            "",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ab",
            "\1\u00ac",
            "\1\u00ad",
            "\1\u00ae",
            "",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "",
            "\1\u00b7",
            "\1\u00b8",
            "\1\u00b9",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "",
            "",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\1\u00bd",
            "\1\u00be",
            "",
            "",
            "",
            "\1\u00bf",
            "\1\u00c0",
            "\1\u00c1",
            "\1\u00c2",
            "\1\u00c3",
            "\1\u00c4",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
            "\11\25\2\uffff\2\25\1\uffff\22\25\1\uffff\uffdf\25",
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

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
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
            return "1:1: Tokens : ( T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | RULE_WS | RULE_SL_COMMENT | RULE_EOL | RULE_TEXT_BLOCK | RULE_WORD );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA6_16 = input.LA(1);

                        s = -1;
                        if ( (LA6_16=='=') ) {s = 41;}

                        else if ( ((LA6_16>='\u0000' && LA6_16<='\b')||(LA6_16>='\u000B' && LA6_16<='\f')||(LA6_16>='\u000E' && LA6_16<='\u001F')||(LA6_16>='!' && LA6_16<='<')||(LA6_16>='>' && LA6_16<='\uFFFF')) ) {s = 21;}

                        else s = 42;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA6_179 = input.LA(1);

                        s = -1;
                        if ( ((LA6_179>='\u0000' && LA6_179<='\b')||(LA6_179>='\u000B' && LA6_179<='\f')||(LA6_179>='\u000E' && LA6_179<='\u001F')||(LA6_179>='!' && LA6_179<='\uFFFF')) ) {s = 21;}

                        else s = 186;

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA6_180 = input.LA(1);

                        s = -1;
                        if ( ((LA6_180>='\u0000' && LA6_180<='\b')||(LA6_180>='\u000B' && LA6_180<='\f')||(LA6_180>='\u000E' && LA6_180<='\u001F')||(LA6_180>='!' && LA6_180<='\uFFFF')) ) {s = 21;}

                        else s = 187;

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA6_173 = input.LA(1);

                        s = -1;
                        if ( ((LA6_173>='\u0000' && LA6_173<='\b')||(LA6_173>='\u000B' && LA6_173<='\f')||(LA6_173>='\u000E' && LA6_173<='\u001F')||(LA6_173>='!' && LA6_173<='\uFFFF')) ) {s = 21;}

                        else s = 181;

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA6_174 = input.LA(1);

                        s = -1;
                        if ( ((LA6_174>='\u0000' && LA6_174<='\b')||(LA6_174>='\u000B' && LA6_174<='\f')||(LA6_174>='\u000E' && LA6_174<='\u001F')||(LA6_174>='!' && LA6_174<='\uFFFF')) ) {s = 21;}

                        else s = 182;

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA6_127 = input.LA(1);

                        s = -1;
                        if ( ((LA6_127>='\u0000' && LA6_127<='\b')||(LA6_127>='\u000B' && LA6_127<='\f')||(LA6_127>='\u000E' && LA6_127<='\u001F')||(LA6_127>='!' && LA6_127<='\uFFFF')) ) {s = 21;}

                        else s = 143;

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA6_107 = input.LA(1);

                        s = -1;
                        if ( ((LA6_107>='\u0000' && LA6_107<='\b')||(LA6_107>='\u000B' && LA6_107<='\f')||(LA6_107>='\u000E' && LA6_107<='\u001F')||(LA6_107>='!' && LA6_107<='\uFFFF')) ) {s = 21;}

                        else s = 128;

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA6_105 = input.LA(1);

                        s = -1;
                        if ( ((LA6_105>='\u0000' && LA6_105<='\b')||(LA6_105>='\u000B' && LA6_105<='\f')||(LA6_105>='\u000E' && LA6_105<='\u001F')||(LA6_105>='!' && LA6_105<='\uFFFF')) ) {s = 21;}

                        else s = 126;

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA6_88 = input.LA(1);

                        s = -1;
                        if ( ((LA6_88>='\u0000' && LA6_88<='\b')||(LA6_88>='\u000B' && LA6_88<='\f')||(LA6_88>='\u000E' && LA6_88<='\u001F')||(LA6_88>='!' && LA6_88<='\uFFFF')) ) {s = 21;}

                        else s = 108;

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA6_33 = input.LA(1);

                        s = -1;
                        if ( (LA6_33=='=') ) {s = 59;}

                        else if ( ((LA6_33>='\u0000' && LA6_33<='\b')||(LA6_33>='\u000B' && LA6_33<='\f')||(LA6_33>='\u000E' && LA6_33<='\u001F')||(LA6_33>='!' && LA6_33<='<')||(LA6_33>='>' && LA6_33<='\uFFFF')) ) {s = 21;}

                        else s = 60;

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA6_89 = input.LA(1);

                        s = -1;
                        if ( ((LA6_89>='\u0000' && LA6_89<='\b')||(LA6_89>='\u000B' && LA6_89<='\f')||(LA6_89>='\u000E' && LA6_89<='\u001F')||(LA6_89>='!' && LA6_89<='\uFFFF')) ) {s = 21;}

                        else s = 109;

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA6_69 = input.LA(1);

                        s = -1;
                        if ( ((LA6_69>='\u0000' && LA6_69<='\b')||(LA6_69>='\u000B' && LA6_69<='\f')||(LA6_69>='\u000E' && LA6_69<='\u001F')||(LA6_69>='!' && LA6_69<='\uFFFF')) ) {s = 21;}

                        else s = 91;

                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA6_70 = input.LA(1);

                        s = -1;
                        if ( ((LA6_70>='\u0000' && LA6_70<='\b')||(LA6_70>='\u000B' && LA6_70<='\f')||(LA6_70>='\u000E' && LA6_70<='\u001F')||(LA6_70>='!' && LA6_70<='\uFFFF')) ) {s = 21;}

                        else s = 92;

                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA6_0 = input.LA(1);

                        s = -1;
                        if ( (LA6_0=='f') ) {s = 1;}

                        else if ( (LA6_0=='p') ) {s = 2;}

                        else if ( (LA6_0=='r') ) {s = 3;}

                        else if ( (LA6_0=='d') ) {s = 4;}

                        else if ( (LA6_0=='g') ) {s = 5;}

                        else if ( (LA6_0=='j') ) {s = 6;}

                        else if ( (LA6_0=='a') ) {s = 7;}

                        else if ( (LA6_0=='h') ) {s = 8;}

                        else if ( (LA6_0=='t') ) {s = 9;}

                        else if ( (LA6_0=='=') ) {s = 10;}

                        else if ( (LA6_0=='S') ) {s = 11;}

                        else if ( (LA6_0=='T') ) {s = 12;}

                        else if ( (LA6_0=='G') ) {s = 13;}

                        else if ( (LA6_0=='W') ) {s = 14;}

                        else if ( (LA6_0=='A') ) {s = 15;}

                        else if ( (LA6_0=='|') ) {s = 16;}

                        else if ( (LA6_0=='\t'||LA6_0=='\r'||LA6_0==' ') ) {s = 17;}

                        else if ( (LA6_0=='#') ) {s = 18;}

                        else if ( (LA6_0=='\n') ) {s = 19;}

                        else if ( (LA6_0=='-') ) {s = 20;}

                        else if ( ((LA6_0>='\u0000' && LA6_0<='\b')||(LA6_0>='\u000B' && LA6_0<='\f')||(LA6_0>='\u000E' && LA6_0<='\u001F')||(LA6_0>='!' && LA6_0<='\"')||(LA6_0>='$' && LA6_0<=',')||(LA6_0>='.' && LA6_0<='<')||(LA6_0>='>' && LA6_0<='@')||(LA6_0>='B' && LA6_0<='F')||(LA6_0>='H' && LA6_0<='R')||(LA6_0>='U' && LA6_0<='V')||(LA6_0>='X' && LA6_0<='`')||(LA6_0>='b' && LA6_0<='c')||LA6_0=='e'||LA6_0=='i'||(LA6_0>='k' && LA6_0<='o')||LA6_0=='q'||LA6_0=='s'||(LA6_0>='u' && LA6_0<='{')||(LA6_0>='}' && LA6_0<='\uFFFF')) ) {s = 21;}

                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA6_144 = input.LA(1);

                        s = -1;
                        if ( ((LA6_144>='\u0000' && LA6_144<='\b')||(LA6_144>='\u000B' && LA6_144<='\f')||(LA6_144>='\u000E' && LA6_144<='\u001F')||(LA6_144>='!' && LA6_144<='\uFFFF')) ) {s = 21;}

                        else s = 156;

                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA6_43 = input.LA(1);

                        s = -1;
                        if ( ((LA6_43>='\u0000' && LA6_43<='\b')||(LA6_43>='\u000B' && LA6_43<='\f')||(LA6_43>='\u000E' && LA6_43<='\u001F')||(LA6_43>='!' && LA6_43<='\uFFFF')) ) {s = 43;}

                        else if ( ((LA6_43>='\t' && LA6_43<='\n')||LA6_43=='\r'||LA6_43==' ') ) {s = 44;}

                        else s = 21;

                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA6_114 = input.LA(1);

                        s = -1;
                        if ( ((LA6_114>='\u0000' && LA6_114<='\b')||(LA6_114>='\u000B' && LA6_114<='\f')||(LA6_114>='\u000E' && LA6_114<='\u001F')||(LA6_114>='!' && LA6_114<='\uFFFF')) ) {s = 21;}

                        else s = 131;

                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA6_157 = input.LA(1);

                        s = -1;
                        if ( ((LA6_157>='\u0000' && LA6_157<='\b')||(LA6_157>='\u000B' && LA6_157<='\f')||(LA6_157>='\u000E' && LA6_157<='\u001F')||(LA6_157>='!' && LA6_157<='\uFFFF')) ) {s = 21;}

                        else s = 166;

                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA6_130 = input.LA(1);

                        s = -1;
                        if ( ((LA6_130>='\u0000' && LA6_130<='\b')||(LA6_130>='\u000B' && LA6_130<='\f')||(LA6_130>='\u000E' && LA6_130<='\u001F')||(LA6_130>='!' && LA6_130<='\uFFFF')) ) {s = 21;}

                        else s = 145;

                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA6_76 = input.LA(1);

                        s = -1;
                        if ( ((LA6_76>='\u0000' && LA6_76<='\b')||(LA6_76>='\u000B' && LA6_76<='\f')||(LA6_76>='\u000E' && LA6_76<='\u001F')||(LA6_76>='!' && LA6_76<='\uFFFF')) ) {s = 21;}

                        else s = 98;

                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA6_54 = input.LA(1);

                        s = -1;
                        if ( ((LA6_54>='\u0000' && LA6_54<='\b')||(LA6_54>='\u000B' && LA6_54<='\f')||(LA6_54>='\u000E' && LA6_54<='\u001F')||(LA6_54>='!' && LA6_54<='\uFFFF')) ) {s = 21;}

                        else s = 77;

                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA6_116 = input.LA(1);

                        s = -1;
                        if ( ((LA6_116>='\u0000' && LA6_116<='\b')||(LA6_116>='\u000B' && LA6_116<='\f')||(LA6_116>='\u000E' && LA6_116<='\u001F')||(LA6_116>='!' && LA6_116<='\uFFFF')) ) {s = 21;}

                        else s = 133;

                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA6_10 = input.LA(1);

                        s = -1;
                        if ( (LA6_10=='=') ) {s = 33;}

                        else if ( ((LA6_10>='\u0000' && LA6_10<='\b')||(LA6_10>='\u000B' && LA6_10<='\f')||(LA6_10>='\u000E' && LA6_10<='\u001F')||(LA6_10>='!' && LA6_10<='<')||(LA6_10>='>' && LA6_10<='\uFFFF')) ) {s = 21;}

                        else s = 34;

                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA6_93 = input.LA(1);

                        s = -1;
                        if ( ((LA6_93>='\u0000' && LA6_93<='\b')||(LA6_93>='\u000B' && LA6_93<='\f')||(LA6_93>='\u000E' && LA6_93<='\u001F')||(LA6_93>='!' && LA6_93<='\uFFFF')) ) {s = 21;}

                        else s = 111;

                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA6_167 = input.LA(1);

                        s = -1;
                        if ( ((LA6_167>='\u0000' && LA6_167<='\b')||(LA6_167>='\u000B' && LA6_167<='\f')||(LA6_167>='\u000E' && LA6_167<='\u001F')||(LA6_167>='!' && LA6_167<='\uFFFF')) ) {s = 21;}

                        else s = 175;

                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA6_101 = input.LA(1);

                        s = -1;
                        if ( ((LA6_101>='\u0000' && LA6_101<='\b')||(LA6_101>='\u000B' && LA6_101<='\f')||(LA6_101>='\u000E' && LA6_101<='\u001F')||(LA6_101>='!' && LA6_101<='\uFFFF')) ) {s = 21;}

                        else s = 118;

                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA6_135 = input.LA(1);

                        s = -1;
                        if ( ((LA6_135>='\u0000' && LA6_135<='\b')||(LA6_135>='\u000B' && LA6_135<='\f')||(LA6_135>='\u000E' && LA6_135<='\u001F')||(LA6_135>='!' && LA6_135<='\uFFFF')) ) {s = 21;}

                        else s = 148;

                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA6_183 = input.LA(1);

                        s = -1;
                        if ( ((LA6_183>='\u0000' && LA6_183<='\b')||(LA6_183>='\u000B' && LA6_183<='\f')||(LA6_183>='\u000E' && LA6_183<='\u001F')||(LA6_183>='!' && LA6_183<='\uFFFF')) ) {s = 21;}

                        else s = 188;

                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA6_18 = input.LA(1);

                        s = -1;
                        if ( ((LA6_18>='\u0000' && LA6_18<='\b')||(LA6_18>='\u000B' && LA6_18<='\f')||(LA6_18>='\u000E' && LA6_18<='\u001F')||(LA6_18>='!' && LA6_18<='\uFFFF')) ) {s = 43;}

                        else if ( ((LA6_18>='\t' && LA6_18<='\n')||LA6_18=='\r'||LA6_18==' ') ) {s = 44;}

                        else s = 21;

                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA6_195 = input.LA(1);

                        s = -1;
                        if ( ((LA6_195>='\u0000' && LA6_195<='\b')||(LA6_195>='\u000B' && LA6_195<='\f')||(LA6_195>='\u000E' && LA6_195<='\u001F')||(LA6_195>='!' && LA6_195<='\uFFFF')) ) {s = 21;}

                        else s = 197;

                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA6_59 = input.LA(1);

                        s = -1;
                        if ( ((LA6_59>='\u0000' && LA6_59<='\b')||(LA6_59>='\u000B' && LA6_59<='\f')||(LA6_59>='\u000E' && LA6_59<='\u001F')||(LA6_59>='!' && LA6_59<='\uFFFF')) ) {s = 21;}

                        else s = 82;

                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA6_196 = input.LA(1);

                        s = -1;
                        if ( ((LA6_196>='\u0000' && LA6_196<='\b')||(LA6_196>='\u000B' && LA6_196<='\f')||(LA6_196>='\u000E' && LA6_196<='\u001F')||(LA6_196>='!' && LA6_196<='\uFFFF')) ) {s = 21;}

                        else s = 198;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 6, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}