// --------------------------------  Main  --------------------------------
//
// Compiler Project 8 - PCAT Compiler Main
//
// This class is never instantiated.  It includes the "main" method
// to exercise components of the PCAT compiler.
//
// Harry Porter -- 01/26/03
//                 02/07/03 -- Modified for Project 4
//                 02/12/03 -- Modified for Project 5
//                 04/09/03 -- Modified for Project 8
//

import java.io.*;

class Main {

    //
    // Static Fields
    //
    static int errorCount = 0;           // How many errors, so far
    static Ast.Body ast = null;          // The Abstract Syntax Tree
    static Parser parser = null;         // The parser
    static Checker checker = null;       // The checker
    static Generator generator = null;   // The IR generator


    //
    // Main ()
    //
    // Create a parser, parse a PCAT source file, and print the AST.
    //
    public static void main (String [] args) {
        try {

            // Parse the source...
            System.err.println ("Parsing Source Code...");
            parser = new Parser (args);
            ast = parser.parseProgram ();

            // Check the AST...
            System.err.println ("Semantic Error Checking...");
            checker = new Checker ();
            checker.checkAst (ast);

            // Print the AST, using printAst...
            // PrintAst.printAst (ast);

            // Pretty-print the AST...
            PrettyPrint.prettyPrintAst (ast);

            if (errorCount == 0) {

                // Generate the Intermediate Representation (IR)...
                generator = new Generator ();
                System.err.println ("Generating Intermediate Code...");
                generator.generateIR (ast);
                IR.printIR ();

            }

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
