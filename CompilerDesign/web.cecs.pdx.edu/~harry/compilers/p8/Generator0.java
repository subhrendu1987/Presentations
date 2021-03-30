// ----------------------------- IR Code Generation -----------------------------
//
// Methods to walk an Abstract Syntax Tree, generating the Intermediate
// Representation (IR) code.
//
// There will be only one instance of this class.  The primary method is:
//      generator.generateIR (ast)
// This method will walk the AST (calling other methods as necessary).  As it
// walks the tree, it will generate the IR code.
//
// <Your Name Here> -- <Date>
//

import java.io.*;

class Generator {


    //
    // Constants
    //
    static final int INITIAL_VARIABLE_OFFSET      =  -4;
    static final int VARIABLE_OFFSET_INCR         =  -4;
    static final int INITIAL_FORMAL_OFFSET        = +68;
    static final int FORMAL_OFFSET_INCR           =  +4;
    static final int REGISTER_SAVE_AREA_SIZE      = +64;
    static final int DISPLAY_REG_SAVE_AREA_OFFSET = +64;

    static final int INTEGER_MODE = 1;
    static final int REAL_MODE    = 2;
    static final int STRING_MODE  = 3;
    static final int BOOLEAN_MODE = 4;



    //
    // Fields
    //
    int lexicalLev = 0;
    int maxLexicalLevel = 0;
    Ast.Body currentBody;
    Ast.StringConst stringList = null;
    Ast.RealConst floatList = null;
    int nextLabelNumber = 1;
    int nextTempNumber = 1;



    //
    //  Constructor
    //
    Generator () { }



    // generateIR (ast)
    //
    // This method is called to generate the IR code.  It is passed
    // a pointer to an Abstract Syntax Tree "ast".
    //
    void generateIR (Ast.Body ast)
        throws FatalError
    {
        Main.parser.lexer.lineNumber = 0;
        genBody (ast);
    }
    

...
    
    
    // genAssignStmt (assignStmt)
    //
    // Generate IR code for the AssignStmt pointed to by "assignStmt".
    //
    void genAssignStmt (Ast.AssignStmt assignStmt)
        throws FatalError
    {
        IR.comment ("ASSIGNMENT STMT...");
        Ast.Node x = genLValue (assignStmt.lValue);
        Ast.Node y = genExpr (assignStmt.expr);
        IR.store (x, y);
    }


...


    // newLabel ()
    //
    // This method returns a newly created string of the form "Label_43".  The
    // integer part is incremented on each call, making the returned string unique
    // from all previous strings returned by this function.
    //
    String newLabel () {
        return "Label_" + (nextLabelNumber++);
    }



    // newTemp ()
    //
    // This method creates a new local variable with a name such as "t47"
    // and adds it to the current Body.
    //
    // This method creates a new VarDecl node, fills it in, and links it into
    // the list of VarDecls for the current body.  It assumes that "currentBody"
    // points to the Body node for the current routine.
    //
    // This method returns a pointer to the VarDecl just created.
    //
    Ast.VarDecl newTemp () {
        Ast.VarDecl vd = new Ast.VarDecl ();
        vd.lineNumber = 0;
        vd.id = "t" + (nextTempNumber++);
        vd.typeName = null;
        vd.expr = null;
        vd.lexLevel = -1;           //  -1 signals a temporary variable
        vd.next = null;
        vd.offset = 0;
        if (currentBody.varDecls == null) {
            currentBody.varDecls = vd;
        } else {
            Ast.VarDecl p = currentBody.varDecls;
            Ast.VarDecl last = null;
            while (p != null) {
                last = p;
                p = p.next;
            }
            if (last == null) {
                currentBody.varDecls = vd;
            } else {
                last.next = vd;
            }
        }
        return vd;
    }


}
