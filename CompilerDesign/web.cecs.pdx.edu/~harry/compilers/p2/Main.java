// -------------------------------- Main --------------------------------
//
// Compiler Project 2 - Lexical Analyzer Main
//
// This class is never instantiated.  It includes the "main" method to exercise
// the lexical analyzer.  Ultimately, this class will contain methods that
// relate to the overall behavior of the compiler.
//
// Harry Porter -- 10/10/05
//

import java.io.*;

class Main {

    //
    // Static Fields
    //
    static int errorCount = 0;     // How many errors, so far
    static Lexer lexer;            // The lexer


    //
    // Main ()
    //
    // In a loop, scan and print tokens until EOF is encountered.
    //
    public static void main (String [] args) {
        String previousAbcString = null;
        try {

            // Create the lexer object...
            try {
                if (args.length == 0) {
                    lexer = new Lexer (new InputStreamReader (System.in));
                } else if (args.length == 1) {
                    lexer = new Lexer (new FileReader (args[0]));
                } else {
                    throw new FatalError ("Command line requires 0 or 1 file name");
                }
            } catch (FileNotFoundException fnfexn) {
                throw new FatalError ("File not found: " + args[0]);
            } catch (IOException e) {
                throw new FatalError (e.getMessage ());
            }


            // Read and print tokens until EOF is encountered...
            while (true) {
                int token = lexer.getToken ();
                if (token == Token.EOF) break;
                printToken (token);
                // Make sure the sValues for equal ID's are set to the identical object;
                // This test relates to file "tst/test5.pcat"...
                if (token == Token.ID && lexer.sValue.equals ("abc")) {
                    if (previousAbcString == null) {
                        previousAbcString = lexer.sValue;
                    } else if (previousAbcString != lexer.sValue) {
                        System.err.println ("Cannonical form of string is not used");
                    }
                }
        
            }

        } catch (LogicError e) {
            // The next stmt will print the message (if any) and info about where
            // the error was thrown.
            e.printStackTrace ();
        } catch (FatalError e) {
            if (e.getMessage () != null) {
                System.err.println (e.getMessage ());
            }
        }
        if (errorCount > 0) {
            System.err.println (errorCount + " error(s) were detected!");
            System.exit (1);
        }
    }



    //
    // printToken (token) --> String
    //
    // This method prints this token, in a form such as:
    //     "9      INTEGER    12345"
    //
    static void printToken (int token) {
        if (token == Token.INTEGER) {
            System.out.println (lexer.lineNumber + "\t"
                  + Token.stringOf [token] + "\t" + lexer.iValue);
        } else if (token == Token.REAL) {
            System.out.println (lexer.lineNumber + "\t"
                  + Token.stringOf [token] + "\t" + lexer.rValue);
        } else if (token == Token.STRING) {
            System.out.println (lexer.lineNumber + "\t"
                  + Token.stringOf [token] + "\t\"" + lexer.sValue + "\"");
        } else if (token == Token.ID) {
            System.out.println (lexer.lineNumber + "\t"
                  + Token.stringOf [token] + "\t\"" + lexer.sValue + "\"");
        } else {
            System.out.println (lexer.lineNumber + "\t" + Token.stringOf [token]);
        }
    }



}
