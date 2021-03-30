//
// The "E" Language Interpreter
//
// This program parses, checks, and interprets a program written in the
// "E" language.  The language is described in the project assignment.
//
// Program Author History:
//    Andrew Tolmach        -- 10/07/02
//    Harry Porter          -- 01/10/03
//    Harry Porter          -- 01/19/04
//    <Your name here>      -- <date>
//
//

import java.util.*;
import java.io.*;



// ------------------------  Proj1 -----------------------------------
//
// Driver for reading, checking, and executing "E" programs.
//
class Proj1 {
    public static void main (String [] args) {
        try {
            Reader rdr = new FileReader (args[0]); 
            Expr expr = Parser.parseProgram (rdr);
            System.out.println ("Abstract Syntax Tree = " + expr);
            boolean ok = expr.check ();
            if (!ok) {
                throw new CompileTimeError ("Semantic errors occured... Aborting!", 0);
            }
            int result = expr.interpret ();
            System.out.println ("Result = " + result);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println ("Expecting filename as command line argument");
        } catch (FileNotFoundException fnfexn) {
            System.err.println ("File not found: " + args[0]);
        } catch (CompileTimeError e) {
            System.err.println (e.getMessage ());
        }
    }
}



// ---------------------- CompileTimeError -----------------------
//
// Each instance of this exception class contains a message
// with the line number and some text describing the problem.
//
class CompileTimeError extends Exception {
    CompileTimeError (String message, int lineno) {
        super ("Error on line " + lineno + ": " + message);
    }
}



// ---------------------- Token ----------------------------
//
// This class encapsulates definitions related to tokens.
// This class is never instantiated.
//
class Token {

    //
    // Constructor -- This makes creating instances impossible.
    //
    private Token () { }

    //
    // Token kinds
    //
    // This list must match the "stringOf" array.  Keywords and symbols
    // must be kept together and their order must match the corresponding
    // "keywords" and "symbols" arrays.
    //
    final static int 
        VAR = 0,        // 'var' keyword
        SET = 1,        // 'set' keyword
        ID = 2,         // identifier
        NUM = 3,        // numeric literal
        EQ = 4,         // =
        LBRACE = 5,     // {
        RBRACE = 6,     // }
        LPAREN = 7,     // (
        RPAREN = 8,     // )
        PLUS = 9,       // +
        SEMI = 10,      // ;
        EOF = 11;       // end of file

    //
    // Printable representations of token kinds.  This list must match the
    // token kind codes, above.
    //
    final static String [] stringOf = {
        "'var'",
        "'set'", 
        "identifier",
        "number", 
        "'='",
        "'{'",
        "'}'",
        "'('",
        "')'",
        "'+'",
        "';'",
        "end of file"
    };

    //
    // Keyword List
    //
    // This array must correspond to the token type codes given above.
    //
    final static String [] keywords = {
        "var",
        "set"
    };

    //
    // Token kind value corresponding to first keyword.
    //
    final static int firstKeyword = VAR; 

    //
    // Symbol List
    //
    // This array must correspond to the token type codes given above.
    //
    final static char [] symbols = {
        '=',
        '{',
        '}',
        '(',
        ')',
        '+',
        ';'
    };

    //
    // Token kind value corresponding to first symbol.
    //
    final static int firstSymbol = EQ;
}



// ------------------------ Lexer ----------------------------
//
// This class is intended to be instantiated exactly once.  The instance
// of this class is a lexical analyzer for a particular input source.
// The key method is "getToken", which reads and returns the next token.
//
class Lexer {

    //
    // Fields
    //
    int lineno;                       // Current line number
    Object attribute;                 // Attribute for most recently returned
                                      //   token, or null if inapplicable
    private PushbackReader inSource;  // Source from which we are reading


    //
    // Constants
    //
    static final char                 // Newline character, determined in a portable way.
        NEWLINE = System.getProperty ("line.separator").charAt (0);


    //
    // Constructor
    //
    // Creates a new Lexer.  The parameter "rdr" is the input source
    // that will be used by this Lexer.  We wrap "rdr" in a "PushbackReader",
    // which will allow us to "unread" characters from the input stream.
    //
    Lexer (Reader rdr) {
        inSource = new PushbackReader (rdr);
        lineno = 1;
    }


    //
    // getToken () --> int
    //
    // This method reads the next token from the input source.  It
    // returns a code indicating the type of the new token.  If there is
    // additional info about the token (e.g., the value of a NUM or chars of
    // an ID), this information is stored in the "attribute" field.
    //
    // If errors occur, this method throws a "CompileTimeError" exception,
    // describing the problem.
    //
    int getToken ()
        throws CompileTimeError
    {
        try {
            attribute = null;
            while (true) {
                char c = (char) inSource.read ();
                if (c == (char) (-1)) {
                    return Token.EOF;
                } else if (c == NEWLINE) {
                    lineno++;
                    continue;
                } else if (c == '\r') {
                    continue;
                } else if (Character.isWhitespace (c)) {
                    continue;
                } else if (Character.isLetter (c)) {
                    String str = new String ();
                    str = str + c;
                    c = (char) inSource.read ();
                    while (Character.isLetterOrDigit (c)) {
                        str = str + c;
                        c = (char) inSource.read ();
                    }
                    inSource.unread (c);
                    for (int i = 0; i < Token.keywords.length; i++) {
                        if (Token.keywords[i].equals (str)) {
                            return Token.firstKeyword + i;
                        }
                    }
                    attribute = str;
                    return Token.ID;
                } else if (Character.isDigit (c)) {
                    String str = new String ();
                    str = str + c;
                    c = (char) inSource.read ();
                    while (Character.isDigit (c)) {
                        str = str + c;
                        c = (char) inSource.read ();
                    }
                    inSource.unread (c);
                    attribute = Integer.valueOf (str);
                    return Token.NUM;
                } else {
                    for (int i = 0; i < Token.symbols.length; i++) {
                        if (Token.symbols[i] == c) {
                            return Token.firstSymbol + i;
                        }
                    }
                    throw new CompileTimeError ("Invalid character \'"  + c
                               + "\' in source", lineno);
                }
            }
        } catch (IOException exn) {         // from read or unread
            throw new CompileTimeError ("I/O error: " + exn.getMessage(), lineno);
        }
    }

}



// ------------------------ AST Classes ------------------------
//
// This group of classes is used to construct the AST (Abstract Syntax Tree).
// Each type of node in the AST is represented by an instance of one of these
// classes.  Here is the class hierarchy:
//
//    Expr             -- An abstract class
//      VarExpr        -- Decribes a variable
//      ConstExpr      -- Describes a NUM constant
//      PlusExpr       -- Describes an expression of the form "X + Y"
//      SetExpr        -- Describes an expression of the form "set X = expr"
//      BlockExpr      -- Describes an expression of the form "{ ... }"
//
// Each node in the tree understands the following messages:
//
//    toString  -- Returns a printable version of the AST.
//    check     -- Returns true if this subtree is semantically okay.
//    interpret -- Returns the integer result of the interpretation.
//



// ------------------------ Expr --------------------------
//
abstract class Expr {


    //
    // toString () --> String
    //
    // This method returns a printable version of the tree.
    //
    abstract public String toString (); 


    //
    // check () --> boolean
    //
    // This method checks the entire tree.  It makes sure all variables are
    // declared before being used.
    //
    boolean check() {
        return check(Env.empty);
    }


    //
    // check (env) --> boolean
    //
    // This method checks the sub-tree rooted at the receiver.  It makes
    // sure all variables are declared before being used.  It is passed an
    // environment "env" telling which variables are already defined.  It
    // returns "true" if everything is okay.  If errors arise, they will be
    // printed on "stderr" and this method will return "false".
    //
    abstract boolean check(Env env);


    //
    // interpret () --> int
    //
    // This method executes the program and returns the result.
    //
    int interpret () {
        System.err.println ("Interpret not yet implemented!");
        return -1;
    }

}



// ----------------------------- VarExpr --------------------------------
//
final class VarExpr extends Expr {

    //
    // Fields
    //
    private String nameOfVar;


    //
    // Constructor
    //
    VarExpr (String s) {
        nameOfVar = s;
    }


    //
    // toString () --> String
    //
    // This method returns a printable version of this node.
    //
    public String toString () {
        return nameOfVar;
    }


    //
    // check (env) --> boolean
    //
    // This method checks that this variable has been declared, i.e.,
    // that an entry for it can be found in "env".  It this variable
    // is not defined, it prints a message and returns false.
    //
    boolean check (Env env) {
        if (Env.find (env, nameOfVar) != null) {
            return true;
        } else {
            System.err.println ("Semantic error: The variable \"" 
                    + nameOfVar + "\" was used but was not declared!");
            return false;
        }
    }

}



// ----------------------------- ConstExpr --------------------------------
//
final class ConstExpr extends Expr {

    //
    // Fields
    //
    private int intVal;


    //
    // Constructor
    //
    ConstExpr (int i) {
        intVal = i;
    }


    //
    // toString () --> String
    //
    // This method returns a printable version of this node.
    //
    public String toString () {
        return Integer.toString (intVal);
    }


    //
    // check (env) --> boolean
    //
    // This method checks this node for semantic errors and returns
    // true if everything is okay.
    //
    boolean check (Env env) {
        return true;
    }

}



// ----------------------------- PlusExpr --------------------------------
//
final class PlusExpr extends Expr {

    //
    // Fields
    //
    private Expr leftOperand;     // Pointer to tree representing left sub-expr
    private Expr rightOperand;    // Pointer to tree representing right sub-expr


    //
    // Constructor
    //
    PlusExpr (Expr l, Expr r) {
        leftOperand = l;
        rightOperand = r;
    }


    //
    // toString () --> String
    //
    // This method returns a printable version of this sub-tree.
    //
    public String toString () {
        return "(+ " + leftOperand + " " + rightOperand + ")";
    }


    //
    // check (env) --> boolean
    //
    // This method checks this expression tree for semantic errors
    // and returns true if everything is okay.  It makes sure the
    // sub-trees are okay with a recursive call to check each of them.
    //
    boolean check(Env env) {
        boolean leftOK = leftOperand.check (env);
        boolean rightOK = rightOperand.check (env);
        return leftOK && rightOK;
    }

}



// ----------------------------- SetExpr --------------------------------
//
final class SetExpr extends Expr {

    //
    // Fields
    //
    private String varName;         // ID to the left of "="
    private Expr expr;              // Expression to the right of "="


    //
    // Constructor
    //
    SetExpr (String i, Expr e) {
        varName = i;
        expr = e;
    }


    //
    // toString () --> String
    //
    // This method returns a printable version of this sub-tree.
    //
    public String toString () {
        return "(set " + varName + " " + expr + ")";
    }


    //
    // check (env) --> boolean
    //
    // This method checks this set expression tree for semantic errors
    // and returns true if everything is okay.
    //
    boolean check (Env env) {
        if (Env.find (env, varName) != null)
            return expr.check (env);
        else {
            System.err.println ("Semantic error: Variable \"" 
                        + varName + "\" set but not declared");
            expr.check (env);
            return false;
        }
    }

}



// ----------------------------- BlockExpr --------------------------------
//
final class BlockExpr extends Expr {

    //
    // Fields
    //
    private List idList;        // A List of Strings, representing declared IDs
    private List exprList;      // A List of Expr's


    //
    // Constructor
    //
    BlockExpr (List is, List es) {
        idList = is;
        exprList = es;
    }


    //
    // toString () --> String
    //
    // This method returns a printable version of this sub-tree.
    //
    public String toString() {
        String r = "(block ( ";
        for (Iterator it = idList.iterator (); it.hasNext (); ) {
            r += (String) it.next() + " ";
        }
        r += ") ( ";
        for (Iterator it = exprList.iterator (); it.hasNext (); ) {
            r += ((Expr) it.next ()) + " ";
        }
        return r + "))";
    }


    //
    // check (env) --> boolean
    //
    // This method checks this block for semantic errors and returns
    // true if everything is okay.  It works as follows:
    //
    // First, run thru the idList.  For each ID, add it to the environment.
    // Second, run thru the exprList.  For each Expr, call "check" recursively
    // to make sure it is okay.
    // 
    //
    boolean check(Env env) {
        // System.out.println ("Checking block...");
        for (Iterator it = idList.iterator (); it.hasNext (); ) {
            String id = (String) it.next ();
            env = new Env (id, env); 
        }
        // System.out.println ("  Environment = " + env);
        boolean isOkay = true;
        for (Iterator it = exprList.iterator (); it.hasNext (); ) {
            Expr expr = (Expr) it.next ();
            isOkay = expr.check (env) && isOkay;
        }
        return isOkay;
    }

}



// ------------------------------ Env ------------------------------
//
// Each instance of this class is an element in a linked list.  Each
// element in the list contains a "key", which is a String.  The linked
// list is used to represent an "environment", which is a set of ID's.
//
class Env {

    //
    // Fields
    //
    String key;                // The ID
    Env next;                  // Pointer to next element in the list


    //
    // Constant: "empty"
    //
    // The empty environment is represented as an empty list.
    //
    static final Env empty = null;


    //
    // Constructor
    //
    Env (String k, Env n) {
        key = k;
        next = n;
    }


    //
    // toString ()
    //
    // Return a printable representation of this list.
    //
    public String toString () {
        String r = "[ ";
        for (Env env = this; env != null; env = env.next) { 
            r += env.key + " ";
        }
        r += "]";
        return r;
    }


    //
    // find (env, targetKey) --> Env
    //
    // This method searches this linked list for the targetKey.  If it
    // finds the targetKey in the list, it returns a pointer to the Env
    // element containing it.  Otherwise, it returns null.
    //
    static Env find (Env env, String targetKey) {
        for (; env != null; env = env.next) {
            if (env.key.equals (targetKey)) {
                return env;
            }
        }
        return null;
    }

}



// --------------------------- Parser ------------------------------
//
// This class is never instantiated.  Its methods comprise a
// "recursive descent" parser for the E language.
//
final class Parser {

    //
    // Constructor -- This makes creating instances impossible.
    //
    private Parser () { }


    //
    // Static fields
    //
    static Lexer lexer;                 // The lexer we are using
    static int tok;                     // The current token


    //
    // syntaxError (int)
    //
    // This method throws an error when an unexpected token is
    // encountered.  The parameter is the kind of token we are expecting.
    //
    static void syntaxError (int expected)
        throws CompileTimeError
    {
        throw new CompileTimeError("Expecting " + Token.stringOf [expected]
                               + ", but found " + Token.stringOf [tok]
                               + " instead!",
                               lexer.lineno);
    }


    //
    // scan ()
    //
    // Move to the next token.  Will modify the "tok" and "attribute"
    // variables on the side.  Will throw a CompileTimeError if problems
    // occur within the Lexer.
    //
    static void scan ()
        throws CompileTimeError
    {
        tok = lexer.getToken ();
    }


    //
    // mustHave (expectedKind)
    //
    // This method is passed an expected kind of token.  If the current
    // token matches, then it will advance to the next token.  Else it will
    // throw a "CompileTimeError".
    //
    static void mustHave (int expectedKind)
        throws CompileTimeError
    {
        if (tok == expectedKind) {
            tok = lexer.getToken ();
        } else {
            syntaxError (expectedKind);
        }
    }


    //
    // parseProgram (reader) --> Expr
    //
    // This method will parse an "E" program.  It will construct and return
    // an Abstract Syntax Tree (AST) representing the parsed program.  The argument
    // is the stream from which to get the tokens.  If errors arise, it will
    // throw a "CompileTimeError".
    //
    static Expr parseProgram (Reader rd)
        throws CompileTimeError
    {
        lexer = new Lexer (rd);
        tok = lexer.getToken ();
        Expr e = parseExpr ();
        mustHave (Token.EOF);
        return e;
    }


    //
    // parseId () --> String
    //
    // This method parses an Identifier and returns a String representing the
    // ID.  If errors arise, it will throw a "CompileTimeError".
    //
    static String parseId ()
        throws CompileTimeError
    {
        if (tok == Token.ID) {
            String i = (String) lexer.attribute;
            scan ();
            return i;
        } else {
            syntaxError (Token.ID);
            return "";
        }
    }


    //
    // parseExpr () --> Expr
    //
    // This method parses an expression and returns an AST representing the
    // expression.  If any problems arise, it will throw a "CompileTimeError".
    //
    static Expr parseExpr ()
        throws CompileTimeError
    {
        if (tok == Token.SET) {
            scan ();
            String i = parseId ();
            mustHave (Token.EQ);
            Expr e = parseExpr ();
            return new SetExpr (i, e);
        } else {
            Expr el = parseTerm ();
            if (tok == Token.PLUS) {
                scan ();
                Expr er = parseTerm ();
                return new PlusExpr (el, er);
            } else {
                return el;
            }
        }
    }


    //
    // parseTerm () --> Expr
    //
    // This method parses a term and returns an AST representing the
    // term.  If any problems arise, it will throw a "CompileTimeError".
    //
    static Expr parseTerm ()
        throws CompileTimeError
    {
        if (tok == Token.NUM) {
            int n = ((Integer) lexer.attribute).intValue ();
            scan ();
            return new ConstExpr (n);
        } else if (tok == Token.LBRACE) {
            scan ();
            ArrayList is = new ArrayList ();
            if (tok == Token.VAR) {
                scan ();
                String id = parseId ();
                is.add (id);
                while (tok != Token.SEMI) {
                    id = parseId ();
                    is.add (id);
                }
                scan ();         // Move past the SEMI
            }
            ArrayList es = new ArrayList ();
            Expr e = parseExpr ();
            es.add (e);
            while (tok != Token.RBRACE) {
                mustHave (Token.SEMI);
                e = parseExpr ();
                es.add(e);
            }
            scan ();             // Move past the RBRACE
            return new BlockExpr (is, es);
        } else if (tok == Token.LPAREN) {
            scan ();
            Expr e = parseExpr ();
            mustHave (Token.RPAREN);
            return e;
        } else {
            String i = parseId ();
            return new VarExpr (i);
        }
    }

}
