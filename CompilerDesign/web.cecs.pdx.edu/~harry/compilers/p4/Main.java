// --------------------------------  Main  --------------------------------
//
// Compiler Project 4 - Parser Main
//
// This class is never instantiated.  It includes the "main" method
// to exercise the PCAT parser.
//
// Harry Porter -- 10/21/05 -- Modified for Project 4
//

import java.io.*;

class Main {

    //
    // Static Fields
    //
    static int errorCount = 0;         // How many errors, so far
    static Ast.Body ast = null;        // The Abstract Syntax Tree
    static Parser parser = null;       // The parser


    //
    // Main ()
    //
    // Create a parser, parse a PCAT source file, and print the AST.
    //
    public static void main (String [] args) {
        try {
            System.err.println ("Parsing Source Code...");
            parser = new Parser (args);
            ast = parser.parseProgram ();
            PrintAst.printAst (ast);
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
