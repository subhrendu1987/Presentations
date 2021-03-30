// --------------------------- Ast ------------------------------
//
// This files contains the classes used to represent an
// Abstract Syntax Tree.
//
// Harry Porter -- 10/21/05
//

class Ast {



    //-------------------------- Node ---------------------------

    abstract static class Node {
        int lineNumber;

        Node() {
            super();
            lineNumber = Main.parser.lexer.lineNumber;
        }

    }



    //-------------------------- Body ---------------------------

    static class Body extends Node {
        TypeDecl    typeDecls;
        ProcDecl    procDecls;
        VarDecl     varDecls;
        Stmt        stmts;
        int         frameSize;               // Not used until proj 9
    }



    //-------------------------- VarDecl ---------------------------
        
    static class VarDecl extends Node {
        String      id;
        TypeName    typeName;
        Expr        expr;
        VarDecl     next;
        int         lexLevel;                // Not used until proj 5
        int         offset;                  // Not used until proj 9
    }



    //-------------------------- TypeDecl ---------------------------

    static class TypeDecl extends Node {
        String       id;
        CompoundType compoundType;
        TypeDecl     next;
    }



    //-------------------------- TypeName ---------------------------

    static class TypeName extends Node {
        String       id;
        CompoundType myDef;                  // Not used until proj 5
    }



    //-------------------------- ProcDecl ---------------------------

    static class ProcDecl extends Node {
        String      id;
        Formal      formals;
        TypeName    retType;
        Body        body;
        ProcDecl    next;
        int         lexLevel;                // Not used until proj 5
    }



    //-------------------------- Formal ---------------------------

    static class Formal extends Node {
        String      id;
        TypeName    typeName;
        Formal      next;
        int         lexLevel;                // Not used until proj 5
        int         offset;                  // Not used until proj 9
    }



    //-------------------------- CompoundType ---------------------------

    abstract static class CompoundType extends Node { }



    //-------------------------- ArrayType ---------------------------

    static class ArrayType extends CompoundType {
        TypeName     elementType;
    }



    //-------------------------- RecordType ---------------------------

    static class RecordType extends CompoundType {
        FieldDecl    fieldDecls;
        int          size;                   // Not used until proj 9
    }



    //-------------------------- FieldDecl ---------------------------

    static class FieldDecl extends Node {
        String       id;
        TypeName     typeName;
        FieldDecl    next;
        int          offset;                 // Not used until proj 9
    }



    //-------------------------- Stmt ---------------------------

    abstract static class Stmt extends Node {
        Stmt     next;
    }



    //-------------------------- AssignStmt ---------------------------

    static class AssignStmt extends Stmt {
        LValue      lValue;
        Expr        expr;
    }



    //-------------------------- CallStmt ---------------------------

    static class CallStmt extends Stmt {
        String      id;
        Argument    args;
        ProcDecl    myDef;                   // Not used until proj 5
    }



    //-------------------------- ReadStmt ---------------------------

    static class ReadStmt extends Stmt {
        ReadArg     readArgs;
    }



    //-------------------------- ReadArg ---------------------------

    static class ReadArg extends Node {
        ReadArg     next;
        LValue      lValue;
        int         mode;                    // Not used until proj 6
    }



    //-------------------------- WriteStmt ---------------------------

    static class WriteStmt extends Stmt {
        Argument  args;
    }



    //-------------------------- IfStmt ---------------------------

    static class IfStmt extends Stmt {
        Expr        expr;
        Stmt        thenStmts;
        Stmt        elseStmts;
    }



    //-------------------------- WhileStmt ---------------------------

    static class WhileStmt extends Stmt {
        Expr        expr;
        Stmt        stmts;
        String      exitLabel;               // Not used until proj 8
    }



    //-------------------------- LoopStmt ---------------------------

    static class LoopStmt extends Stmt {
        Stmt        stmts;
        String      exitLabel;               // Not used until proj 8
    }



    //-------------------------- ForStmt ---------------------------

    static class ForStmt extends Stmt {
        LValue      lValue;
        Expr        expr1;
        Expr        expr2;
        Expr        expr3;
        Stmt        stmts;
        String      exitLabel;               // Not used until proj 8
    }



    //-------------------------- ExitStmt ---------------------------

    static class ExitStmt extends Stmt {
        Stmt        myLoop;                  // Not used until proj 6
    }



    //-------------------------- ReturnStmt ---------------------------

    static class ReturnStmt extends Stmt {
        Expr         expr;
        ProcDecl     myProc;                 // Not used until proj 6
    }



    //-------------------------- Expr ---------------------------

    abstract static class Expr extends Node { }



    //-------------------------- BinaryOp ---------------------------

    static class BinaryOp extends Expr {
        int         op;
        Expr        expr1;
        Expr        expr2;
        int         mode;                    // Not used until proj 6
    }



    //-------------------------- UnaryOp ---------------------------

    static class UnaryOp extends Expr {
        int         op;
        Expr        expr;
        int         mode;                    // Not used until proj 6
    }



    //-------------------------- IntToReal ---------------------------

    static class IntToReal extends Expr {    // Not used until proj 6
        Expr        expr;
    }



    //-------------------------- FunctionCall ---------------------------

    static class FunctionCall extends Expr {
        String      id;
        Argument    args;
        ProcDecl    myDef;                   // Not used until proj 5
    }



    //-------------------------- Argument ---------------------------

    static class Argument extends Node {
        Argument    next;
        Expr        expr;
        int         mode;                    // Not used until proj 6
        Node        location;                // Not used until proj 8
    }



    //-------------------------- ArrayConstructor ---------------------------

    static class ArrayConstructor extends Expr {
        String        id;
        ArrayValue    values;
        TypeDecl      myDef;                 // Not used until proj 5
    }



    //-------------------------- ArrayValue ---------------------------

    static class ArrayValue extends Node {
        ArrayValue    next;
        Expr          countExpr;
        Expr          valueExpr;
        Node          tempCount;             // Not used until proj 9
        Node          tempValue;             // Not used until proj 9
    }



    //-------------------------- RecordConstructor ---------------------------

    static class RecordConstructor extends Expr {
        String             id;
        FieldInit          fieldInits;
        TypeDecl           myDef;            // Not used until proj 5
    }



    //-------------------------- FieldInit ---------------------------

    static class FieldInit extends Node {
        FieldInit           next;
        String              id;
        Expr                expr;
        FieldDecl           myFieldDecl;     // Not used until proj 6
    }



    //-------------------------- IntegerConst ---------------------------

    static class IntegerConst extends Expr {
        int            iValue;
    }



    //-------------------------- RealConst ---------------------------

    static class RealConst extends Expr {
        double       rValue;
        String       nameOfConstant;         // Not used until proj 9
        RealConst    next;                   // Not used until proj 9
    }



    //-------------------------- StringConst ---------------------------

    static class StringConst extends Expr {
        String         sValue;
        String         nameOfConstant;       // Not used until proj 9
        StringConst    next;                 // Not used until proj 9
    }



    //-------------------------- BooleanConst ---------------------------

    static class BooleanConst extends Expr {
        int            iValue;
    }



    //-------------------------- NilConst ---------------------------

    static class NilConst extends Expr {
    }



    //-------------------------- ValueOf ---------------------------

    static class ValueOf extends Expr {
        LValue         lValue;
    }



    //-------------------------- LValue ---------------------------

    abstract static class LValue extends Node { }



    //-------------------------- Variable ---------------------------

    static class Variable extends LValue {
        String      id;
        Node        myDef;                  // Not used until proj 5
                                            // (will be either Formal or VarDecl)
        int         currentLevel;           // Not used until proj 5
    }



    //-------------------------- ArrayDeref ---------------------------

    static class ArrayDeref extends LValue {
        LValue      lValue;
        Expr        expr;
    }



    //-------------------------- RecordDeref ---------------------------

    static class RecordDeref extends LValue {
        LValue        lValue;
        String        id;
        FieldDecl     myFieldDecl;          // Not used until proj 6
    }

}