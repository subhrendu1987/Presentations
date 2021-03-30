// --------------------------------  Main  --------------------------------
//
// Compiler Project 3 - Parser Main
//
// This class is never instantiated.  It includes the "main" method
// to exercise the PCAT parser.
//
// Harry Porter -- 01/26/03
//

import java.io.*;

class Main {

    //
    // Static Fields
    //
    static int errorCount = 0;     // How many errors, so far


    //
    // Main ()
    //
    // Create a parser and parse a PCAT source file.
    //
    public static void main (String [] args) {
        try {
            System.err.println ("Parsing Source Code...");
            new Parser (args). parseProgram ();
        } catch (LogicError e) {
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

}
