// --------------------------- Parser ------------------------------
//
// Each instance of this class is a recursive descent parser for the
// PCAT language.  There will only be one Parser object created.
//
// <Your name here> -- 01/26/03
//

import java.io.*;

class Parser {

    //
    // Fields
    //
    Lexer lexer;             // The lexer being used by this parser
    int nextToken;           // The type of the current token


    //
    // Constructor
    //
    Parser (String [] args)
        throws FatalError
    {

        // Create a lexer object...
        try {
            if (args.length == 0) {
                lexer = new Lexer (new InputStreamReader (System.in));
            } else if (args.length == 1) {
                lexer = new Lexer (new FileReader (args [0]));
            } else {
                throw new FatalError ("Command line requires 0 or 1 file name");
            }
        } catch (FileNotFoundException fnfexn) {
            throw new FatalError ("File not found: " + args [0]);
        } catch (IOException e) {
            throw new FatalError (e.getMessage ());
        }

        // Initialize the lexer and nextToken...
        nextToken = Token.EOF+1;       // Something besides EOF
        scan ();
    }



    //
    // scan ()
    //
    // Move to the next token.  This will modify the "nextToken" and
    // "lexer.attribute" variables on the side.  It will throw a
    // FatalError if problems occur within the Lexer.
    //
    void scan ()
        throws FatalError
    {
        if (nextToken != Token.EOF) {
            nextToken = lexer.getToken ();
            // System.out.println ("SCANNING: nextToken = "
            //                        + Token.stringOf [nextToken]);
        }
    }



    //
    // syntaxError (msg)
    //
    // This method prints an error message and then throws a FatalError.
    // It should be called whenever an unexpected token is encountered.
    //
    void syntaxError (String msg)
        throws FatalError
    {
        Main.errorCount++;
        System.err.println ("Syntax error on line " + lexer.lineNumber + ": " + msg);
        throw new FatalError ();
    }



    //
    // mustHave (expectedToken, msg)
    //
    // This method is passed an expected kind of token.  If the current
    // token matches, then it will advance to the next token.  Else it will
    // invoke "syntaxError".
    //
    void mustHave (int expectedToken, String msg)
        throws FatalError
    {
        if (nextToken == expectedToken) {
            scan ();
        } else {
            syntaxError (msg);
        }
    }



    //
    // parseProgram ()
    //
    // This method parses a PCAT Program according to the grammar rule:
    //
    //    Program --> PROGRAM IS Body ';'
    //
    void parseProgram ()
        throws FatalError
    {
        mustHave (Token.PROGRAM, "Expecting PROGRAM");
        mustHave (Token.IS, "Expecting IS after PROGRAM");
        parseBody ();
        mustHave (Token.SEMI, "Expecting ';' after program body");
        mustHave (Token.EOF, "Expecting EOF after PROGRAM IS body");
    }

...

    //
    // parseIfStmt ()
    //
    // This method parses an IfStmt according to the grammar rule:
    //
    //    IfStmt  -->  IF Expr THEN { Statement }
    //                    { ELSEIF Expr THEN { Statement } } 
    //                    [ ELSE { Statement } ] END ';'
    //
    // On entry, we've already checked that nextToken is IF.
    //
    void parseIfStmt ()
        throws FatalError
    {
        mustHave (Token.IF, "Compiler logic error in parseIfStmt");
        System.out.println ("IF");
        parseExpr ();
        mustHave (Token.THEN, "Expecting THEN in IF statement following expr");
        System.out.println ("THEN");
        parseStmts ();
        while (nextToken == Token.ELSEIF) {
            System.out.println ("ELSEIF");
            scan ();
            parseExpr ();
            mustHave (Token.THEN, "Expecting THEN after ELSEIF expr in IF statement");
            parseStmts ();
        }
        if (nextToken == Token.ELSE) {
            System.out.println ("ELSE");
            scan ();
            parseStmts ();
        }
        mustHave (Token.END, "Expecting ELSEIF, ELSE, or END in IF statement");
        mustHave (Token.SEMI, "Expecting ';' after END in IF statement");
        System.out.println ("ENDIF");
    }

...

    //
    // parseExpr ()
    //
    // This method parses an Expr according to the grammar rule:
    //
    //    Expr  -->  Expr2  {  ('<' | '>' | '<=' | '>=' | '=' | '<>')  Expr2  }
    //
    //
    void parseExpr ()
        throws FatalError
    {
        parseExpr2 ();
        while (true) {
            if (nextToken == Token.LESS) {
                scan ();
                parseExpr2 ();
                System.out.println ("LESS");
            } else if (nextToken == Token.GREATER) {
                scan ();
                parseExpr2 ();
                System.out.println ("GREATER");
...

            } else {
                break;
            }
        }
    }

...
