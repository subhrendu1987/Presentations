// --------------------------- PrintAst ------------------------------
//
// Code to Print the Abstract Syntax Tree - Project 4 Version
//
// Harry Porter -- 10/22/05
//
// This class contains a method to print an Abstract Syntax Tree (AST) in
// all is gory detail.  This is useful for debugging, to make sure the AST
// is being built properly.
//
// The primary method here is called "printAst" and it can be invoked as follows
//       Ast.Node ast = ...;
//       ...
//       PrintAst.printAst (ast);
//
// All methods in this class are static; no instances are created.
//
// Each node in the AST is printed on several lines.  For example, a BinaryOp
// node might be printed like this:
//
//       ---------- BinaryOp ----------
//         lineNumber=2
//         op=PLUS
//         expr1=
//           ... left subtree ...
//         expr2=
//           ... right subtree ...
//       ------------------------------
//
// Note that each field of the object is printed.  When some field points to
// another node in the AST, that node will be printed, with proper indentation,
// to show the branching structure of the tree.  Here is the same example,
// showing a little more of the two child nodes of the BinaryOp node.
//
//       ---------- BinaryOp ----------
//         lineNumber=2
//         op=PLUS
//         expr1=
//           ---------- UnaryOp ----------
//             lineNumber=2
//             ... other fields in this node ...
//           ------------------------------
//         expr2=
//           ---------- IntegerConst ----------
//             lineNumber=2
//             ... other fields in this node ...
//           ------------------------------
//       ------------------------------
//
// In a tree, every node (except the root) is pointed to by exactly one other
// node (its parent).  However, a given AST may not be a "proper" tree.  For example,
// some node may be pointed to by several other nodes.  The methods here are designed
// to print such an AST in a way that will make clear the actual shape of the AST.
// In particular, a shared node (one with multiple parents) will be printed only once,
// to avoid confusion.
//
// During printing, each node is assigned a number, which is prefixed with a # sign.
// This number serves to identify the node in case it is pointed to by more than
// one other node.
//
// Here is a fragment, showing how trees with shared nodes are printed.
//
//    #2:       ---------- VarDecl ----------
//                lineNumber=2
//                id="x"
//                typeName=
//    #3:           ---------- TypeName ----------
//                    lineNumber=2
//                    id="integer"
//                  ------------------------------
//                expr=
//                  ...
//              ------------------------------
//    #5:       ---------- VarDecl ----------
//                lineNumber=2
//                id="y"
//                typeName=
//                  *****  This node was printed earlier (#3: TypeName) *****
//                expr=
//                  ...
//              ------------------------------
//
// This technique allows graphs (not just trees or DAGs) to be printed out without
// infinite looping.
//
// The methods here are written in such a way as to be as "bullet-proof" as possible.
// No matter how badly mangled your AST might be, I believe that these methods will
// succeed in printing something reasonable.  As a consequence, the way I have coded
// these methods is not altogether clear and simple.  If, during the course of your
// debugging, problems arise while printing your AST (for example, one of the methods
// here crashes or loops), please let me know.
//
// The printing of some fields may be commented out; These fields are not needed until
// a later project and can be ignored safely.
//
// Complex, higly nested expressions generate lots of output and even with the
// indenting to help, it is difficult to check that the expression is being parsed
// correctly and, if not, exactly what is occurring.  To help understand
// expression trees, the printing of return statements has been augmented to print
// the return expression with full parentheses.  For example, the program
//
//    program is
//      begin
//        return 1 * 2 + 3 and 4 / 5 - 6 < 7 * 8;
//      end;
//
// produces this output:
//
//    #1:   ---------- Body ----------
//            lineNumber=2
//            typeDecls=NULL
//            procDecls=NULL
//            varDecls=NULL
//            stmts=
//    #2:       ---------- ReturnStmt ----------
//                lineNumber=3
//                summary= ((((1 * 2) + ((3 AND 4) / 5)) - 6) < (7 * 8))
//                expr=
//                  ... about 74 lines omitted here ...
//              ------------------------------
//          ------------------------------
//
// The field named "summary" is not really a field in the ReturnStmt object, but
// is just used to print the summary of the return expression with parentheses.
//
// While you are free to study the code here, it is not necessary that you understand
// how it works it in order to complete the class projects.
//



import java.io.*;
import java.util.*;


class PrintAst {


    //
    // Constants
    //
    final static int TABAMT = 2;    // The number of spaces by which to indent
    final static int PROJECT = 6;   // Change to use with different projects


    //
    // Static Variables
    //
    // "ptrMap" is a mapping from ptrs to integers, used to give each object a
    // unique number.  See the "printPtr" method for more details.
    //
    static private Map ptrMap = new HashMap ();
    static int nextLabel = 1;                      // Next number to use
    //
    // "alreadyPrinted" is a mapping used to see if we have already
    // printed a node before.
    //
    static private Map alreadyPrinted = new HashMap ();
    //
    // "alreadyPrinted2" is a mapping used to see if we have already
    // printed a node before in printExpr ()...
    //
    static private Map alreadyPrinted2 = new HashMap ();



    //
    // printAst (ptrToNode)
    //
    // This method prints the abstract syntax tree pointed to by "ptrToNode".
    //
    static void printAst (Ast.Node ptrToNode) {
        printAst (6, ptrToNode);
    }



    //
    // printAst (indent, ptrToNode)
    //
    // This method prints the node pointed to by "ptrToNode".  This is
    // a recursive method that calls itself to print the children of
    // this node.
    //
    // The tree is printed with "indent" characters of indentation.
    // Initially, it should be called with an "indent" of about 6 (to
    // allow enough space to print the #999: labels.)  The indentation is
    // increased for the children.
    //
    static void printAst (int indent, Ast.Node ptrToNode) {

        // If we have are passed a null pointer, print "NULL"...
        if (ptrToNode == null) {
            printLine (indent, "NULL");
            return;
        }

        // Check to see if we have printed out this node before.
        // If so, print a message...
        if (alreadyPrinted.get (ptrToNode) != null) {
          printIndent (indent);
          System.out.print ("*****  This node was printed earlier (");
          printPtr (ptrToNode);
          System.out.print (": ");
          System.out.print (ptrToNode.getClass ().getName ().substring (4));
          System.out.println (") *****");
          return;
        } else {
          alreadyPrinted.put (ptrToNode, ptrToNode);   // Value doesn't matter as
                                                       // long as it is not null.
        }

        // Look at the class of this node and print it accordingly...
        if (ptrToNode instanceof Ast.Body) {
            Ast.Body p = (Ast.Body) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "typeDecls=", p.typeDecls);
            printItem (indent, "procDecls=", p.procDecls);
            printItem (indent, "varDecls=",  p.varDecls);
            printItem (indent, "stmts=",     p.stmts);
            if (PROJECT >= 9) {
                printFieldName (indent, "frameSize=");
                System.out.println (p.frameSize);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.VarDecl) {
            Ast.VarDecl p = (Ast.VarDecl) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            printItem (indent, "typeName=", p.typeName);
            printItem (indent, "expr=", p.expr);
            if (PROJECT >= 5) {
              printFieldName (indent, "lexLevel=");
              System.out.println (p.lexLevel);
            }
            if (PROJECT >= 9) {
                printFieldName (indent, "offset=");
                System.out.println (p.offset);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.TypeDecl) {
            Ast.TypeDecl p = (Ast.TypeDecl) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            printItem (indent, "compoundType=", p.compoundType);
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.TypeName) { 
            Ast.TypeName p = (Ast.TypeName) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            if (PROJECT >= 5) {
                printItem (indent, "myDef=", p.myDef);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.ProcDecl) {
            Ast.ProcDecl p = (Ast.ProcDecl) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            if (PROJECT >= 5) {
                printFieldName (indent, "lexLevel=");
                System.out.println (p.lexLevel);
            }
            printItem (indent, "formals=", p.formals);
            printItem (indent, "retType=", p.retType);
            printItem (indent, "body=", p.body);
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.Formal) {
            Ast.Formal p = (Ast.Formal) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            printItem (indent, "typeName=", p.typeName);
            if (PROJECT >= 5) {
                printFieldName (indent, "lexLevel=");
                System.out.println (p.lexLevel);
            }
            if (PROJECT >= 9) {
                printFieldName (indent, "offset=");
                System.out.println (p.offset);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.ArrayType) {
            Ast.ArrayType p = (Ast.ArrayType) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "elementType=", p.elementType);
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.RecordType) {
            Ast.RecordType p = (Ast.RecordType) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "fieldDecls=", p.fieldDecls);
            if (PROJECT >= 9) {
                printFieldName (indent, "size=");
                System.out.println (p.size);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.FieldDecl) {
            Ast.FieldDecl p = (Ast.FieldDecl) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            printItem (indent, "typeName=", p.typeName);
            if (PROJECT >= 9) {
                printFieldName (indent, "offset=");
                System.out.println (p.offset);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.AssignStmt) {
            Ast.AssignStmt p = (Ast.AssignStmt) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "lValue=", p.lValue);
            printItem (indent, "expr=", p.expr);
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.CallStmt) {
            Ast.CallStmt p = (Ast.CallStmt) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            printItem (indent, "args=", p.args);
            if (PROJECT >= 5) {
                printMyDef (indent, p.myDef);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.ReadStmt) {
            Ast.ReadStmt p = (Ast.ReadStmt) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "readArgs=", p.readArgs);
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.ReadArg) {
            Ast.ReadArg p = (Ast.ReadArg) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "lValue=", p.lValue);
            if (PROJECT >= 6) {
                printMode (indent, p.mode);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.WriteStmt) {
            Ast.WriteStmt p = (Ast.WriteStmt) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "args=", p.args);
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.IfStmt) {
            Ast.IfStmt p = (Ast.IfStmt) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "expr=", p.expr);
            printItem (indent, "thenStmts=", p.thenStmts);
            printItem (indent, "elseStmts=", p.elseStmts);
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.WhileStmt) {
            Ast.WhileStmt p = (Ast.WhileStmt) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "expr=", p.expr);
            printItem (indent, "stmts=", p.stmts);
            if (PROJECT >= 8) {
                printStringField (indent, "exitLabel=", p.exitLabel);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.LoopStmt) {
            Ast.LoopStmt p = (Ast.LoopStmt) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "stmts=", p.stmts);
            if (PROJECT >= 8) {
                printStringField (indent, "exitLabel=", p.exitLabel);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.ForStmt) {
            Ast.ForStmt p = (Ast.ForStmt) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "lValue=", p.lValue);
            printItem (indent, "expr1=", p.expr1);
            printItem (indent, "expr2=", p.expr2);
            printItem (indent, "expr3=", p.expr3);
            printItem (indent, "stmts=", p.stmts);
            if (PROJECT >= 8) {
                printStringField (indent, "exitLabel=", p.exitLabel);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.ExitStmt) {
            Ast.ExitStmt p = (Ast.ExitStmt) ptrToNode;
            printHeader (indent, p);
            if (PROJECT >= 6) {
                printItem (indent, "myLoop=", p.myLoop);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.ReturnStmt) {
            Ast.ReturnStmt p = (Ast.ReturnStmt) ptrToNode;
            printHeader (indent, p);
            printFieldName (indent, "summary= ");
            printExpr (p.expr);
            System.out.println ();
            printItem (indent, "expr=", p.expr);
            if (PROJECT >= 6) {
                printItem (indent, "myProc=", p.myProc);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.BinaryOp) {
            Ast.BinaryOp p = (Ast.BinaryOp) ptrToNode;
            printHeader (indent, p);
            printOperator (indent, p.op);
            printItem (indent, "expr1=", p.expr1);
            printItem (indent, "expr2=", p.expr2);
            if (PROJECT >= 6) {
                printMode (indent, p.mode);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.UnaryOp) {
            Ast.UnaryOp p = (Ast.UnaryOp) ptrToNode;
            printHeader (indent, p);
            printOperator (indent, p.op);
            printItem (indent, "expr=", p.expr);
            if (PROJECT >= 6) {
                printMode (indent, p.mode);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.IntToReal) {
            Ast.IntToReal p = (Ast.IntToReal) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "expr=", p.expr);
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.FunctionCall) {
            Ast.FunctionCall p = (Ast.FunctionCall) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            printItem (indent, "args=", p.args);
            if (PROJECT >= 5) {
                printMyDef (indent, p.myDef);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.Argument) {
            Ast.Argument p = (Ast.Argument) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "expr=", p.expr);
            if (PROJECT >= 6) {
                printMode (indent, p.mode);
            }
            if (PROJECT >= 8) {
                printItem (indent, "location=", p.location);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.ArrayConstructor) {
            Ast.ArrayConstructor p = (Ast.ArrayConstructor) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            printItem (indent, "values=", p.values);
            if (PROJECT >= 5) {
                printMyDef (indent, p.myDef);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.ArrayValue) {
            Ast.ArrayValue p = (Ast.ArrayValue) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "countExpr=", p.countExpr);
            printItem (indent, "valueExpr=", p.valueExpr);
            if (PROJECT >= 9) {
                printItem (indent, "tempCount=", p.tempCount);
                printItem (indent, "tempValue=", p.tempValue);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.RecordConstructor) {
            Ast.RecordConstructor p = (Ast.RecordConstructor) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            printItem (indent, "fieldInits=", p.fieldInits);
            if (PROJECT >= 5) {
                printMyDef (indent, p.myDef);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.FieldInit) {
            Ast.FieldInit p = (Ast.FieldInit) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            printItem (indent, "expr=", p.expr);
            if (PROJECT >= 6) {
                printItem (indent, "myFieldDecl=", p.myFieldDecl);
            }
            printFooter (indent);
            if (p.next != null) {
                printAst (indent, p.next);
            }

        } else if (ptrToNode instanceof Ast.IntegerConst) {
            Ast.IntegerConst p = (Ast.IntegerConst) ptrToNode;
            printHeader (indent, p);
            printIndent (indent+TABAMT);
            System.out.println ("iValue=" + p.iValue);
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.RealConst) {
            Ast.RealConst p = (Ast.RealConst) ptrToNode;
            printHeader (indent, p);
            printIndent (indent+TABAMT);
            System.out.println ("rValue=" + p.rValue);
            if (PROJECT >= 9) {
                printStringField (indent, "nameOfConstant=", p.nameOfConstant);
                printItem (indent, "next=", p.next);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.StringConst) {
            Ast.StringConst p = (Ast.StringConst) ptrToNode;
            printHeader (indent, p);
            printStringField (indent, "sValue=", p.sValue);
            if (PROJECT >= 6) {
                printStringField (indent, "nameOfConstant=", p.nameOfConstant);
                printItem (indent, "next=", p.next);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.BooleanConst) {
            Ast.BooleanConst p = (Ast.BooleanConst) ptrToNode;
            printHeader (indent, p);
            printFieldName (indent, "iValue=");
            System.out.println (p.iValue);
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.NilConst) {
            printHeader (indent, ptrToNode);
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.ValueOf) {
            Ast.ValueOf p = (Ast.ValueOf) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "lValue=", p.lValue);
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.Variable) {
            Ast.Variable p = (Ast.Variable) ptrToNode;
            printHeader (indent, p);
            printId (indent, p.id);
            if (PROJECT >= 5) {
                printMyDef (indent, p.myDef);
            }
            if (PROJECT >= 5) {
                printFieldName (indent, "currentLevel=");
                System.out.println (p.currentLevel);
            }
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.ArrayDeref) {
            Ast.ArrayDeref p = (Ast.ArrayDeref) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "lValue=", p.lValue);
            printItem (indent, "expr=", p.expr);
            printFooter (indent);

        } else if (ptrToNode instanceof Ast.RecordDeref) {
            Ast.RecordDeref p = (Ast.RecordDeref) ptrToNode;
            printHeader (indent, p);
            printItem (indent, "lValue=", p.lValue);
            printId (indent, p.id);
            if (PROJECT >= 6) {
                printItem (indent, "myFieldDecl=", p.myFieldDecl);
            }
            printFooter (indent);

        } else {
            printLine (indent, "(********** Class is unknown!!! **********)");
        }
    }



    //
    // printHeader (indent, p)
    //
    // This method indents, then prints the class of object p, then
    // prints p.lineNumber, then finally prints a newline.
    //
    static void printHeader (int indent, Ast.Node p) {
        int i = printPtr (p);
        System.out.print (": ");
        i = indent - i - TABAMT;
        printIndent (i);
        System.out.print ("---------- ");
        System.out.print (p.getClass ().getName ().substring (4));
        System.out.println (" ----------");
        printFieldName (indent, "lineNumber=");
        System.out.println (p.lineNumber);
    }



    //
    // printFooter (indent)
    //
    // This method indents, then the closing bracketting symbol.
    //
    static void printFooter (int indent) {
        printIndent (indent);
        System.out.println ("------------------------------");
    }



    //
    // printIndent (indent)
    //
    // This method prints "indent" spaces.
    //
    static void printIndent (int indent) {
        for (int i = indent; i>0; i--) {
            System.out.print (" ");
        }
    }



    //
    // printLine (indent, str)
    //
    // This method indents, then prints the given string, then prints newline.
    //
    static void printLine (int indent, String str) {
        printIndent (indent);
        System.out.println (str);
    }



    //
    // printStringField (indent, str1, str2)
    //
    // This method indents by "indent" + TABAMT, then prints str1, then prints
    // str2, then prints newline.
    //
    static void printStringField (int indent, String str1, String str2) {
        printIndent (indent + TABAMT);
        if (str2 == null) {
            System.out.print (str1);
            System.out.println ("NULL");
        } else {
            System.out.print (str1);
            System.out.print ("\"");
            System.out.print (str2);
            System.out.println ("\"");
        }
    }



    //
    // printId (indent, id)
    //
    // This method indents by "indent" + TABAMT, then prints "id=", then prints
    // the id argument, then prints newline.
    //
    static void printId (int indent, String id) {
        printStringField (indent, "id=", id);
    }



    //
    // printMyDef (indent, p)
    //
    // This method indents by "indent" + TABAMT, then prints "myDef=", then prints
    // the argument, then prints newline.
    //
    static void printMyDef (int indent, Ast.Node p) {
        printItem (indent, "myDef=", p);
    }



    //
    // printFieldName (indent, str)
    //
    // This method indents by "indent" + TABAMT, then prints the given string.  It
    // prints no newline.
    //
    static void printFieldName (int indent, String str) {
        printIndent (indent + TABAMT);
        System.out.print (str);
    }



    //
    // printItem (indent, s, t)
    //
    // This method prints the given string on one line (indented by TABAMT more
    // than "indent") and then calls printAst() to print the tree "t" (indented by
    // 2*TABAMT more spaces than "indent").
    //
    static void printItem (int indent, String s, Ast.Node t) {
        printIndent (indent + TABAMT);
        System.out.print (s);
        if (t == null) {
            System.out.println ("NULL");
        } else {
            System.out.println ();
            printAst (indent + TABAMT + TABAMT, t);
        }
    }



    //
    // printOperator (indent, op)
    //
    // This method is passed a token type in "op".  It prints it out in the form:
    //       op=PLUS
    ///
    static void printOperator (int in, int op) {
        printIndent (in+TABAMT);
        switch (op) {
            case Token.LEQ:
                System.out.println ("op=LEQ");
                return;
            case Token.GEQ:
                System.out.println ("op=GEQ");
                return;
            case Token.NEQ:
                System.out.println ("op=NEQ");
                return;
            case Token.AND:
                System.out.println ("op=AND");
                return;
            case Token.DIV:
                System.out.println ("op=DIV");
                return;
            case Token.MOD:
                System.out.println ("op=MOD");
                return;
            case Token.NOT:
                System.out.println ("op=NOT");
                return;
            case Token.OR:
                System.out.println ("op=OR");
                return;
            case Token.LESS:
                System.out.println ("op=LESS");
                return;
            case Token.GREATER:
                System.out.println ("op=GREATER");
                return;
            case Token.EQUAL:
                System.out.println ("op=EQUAL");
                return;
            case Token.PLUS:
                System.out.println ("op=PLUS");
                return;
            case Token.MINUS:
                System.out.println ("op=MINUS");
                return;
            case Token.STAR:
                System.out.println ("op=STAR");
                return;
            case Token.SLASH:
                System.out.println ("op=SLASH");
                return;
            default:
                System.out.println ("op=***** ERROR: op IS GARBAGE *****");
        }
    }



    //
    // printPtr (p)
    //
    // This method is passed a pointer, possibly NULL.  It prints the pointer.
    // The actual address is not printed, since these may vary from run to run.
    // Instead, this method assigns 'labels' to each address and prints the
    // same label each time.  It saves the mapping between label and address
    // in a static hashMap called "ptrMap".
    //
    // This method returns the number of characters printed.
    //
    static int printPtr (Ast.Node p) {
        int i;

        // If the pointer is null, then print "NULL"...
        if (p == null) {
            System.out.print ("NULL");
            return 4;
        }

        // Figure out what integer goes with this ptr...
        Integer val = (Integer) ptrMap.get (p);
        if (val != null) {
          i = val.intValue ();
        } else {
          i = nextLabel++;
          ptrMap.put (p, new Integer (i));
        }

        // Print the number...
        String str = "#" + i;
        System.out.print (str);

        // Return the number of characters just printed...
        return str.length ();
    }



    //
    // printExpr (p)
    //
    // This method is passed a pointer, possibly NULL, which should point to
    // an expression or an LValue.  It prints it out in a way that is intended
    // to be more human-readable.  It is used for the "summary=" printout.
    //
    // This method method assumes that it is passed a tree-shaped data
    // structure.  If not, it will print a warning.
    //
    static void printExpr (Ast.Node p) {

        // Deal with and print a NULL...
        if (p == null) {
            System.out.print ("NULL");
            return;
        }

        // Check to see if we have printed this node before.
        // If so, this is not a tree, so print a message...
        if (alreadyPrinted2.get (p) != null) {
          System.out.print ("*****  Node Sharing detected!  *****");
          return;
        } else {
          alreadyPrinted2.put (p, p);   // Value doesn't matter as
                                        // long as it is not null.
        }
        if (p instanceof Ast.BinaryOp) {
            Ast.BinaryOp p2 = (Ast.BinaryOp) p;
            System.out.print ("(");
            printExpr (p2.expr1);
            System.out.print (" ");
            System.out.print (Token.stringOf [p2.op]);
            System.out.print (" ");
            printExpr (p2.expr2);
            System.out.print (")");

        } else if (p instanceof Ast.UnaryOp) {
            Ast.UnaryOp p2 = (Ast.UnaryOp) p;
            System.out.print ("(");
            System.out.print (Token.stringOf [p2.op]);
            System.out.print (" ");
            printExpr (p2.expr);
            System.out.print (")");

        } else if (p instanceof Ast.IntToReal) {
            Ast.IntToReal p2 = (Ast.IntToReal) p;
            System.out.print ("intToReal (");
            printExpr (p2.expr);
            System.out.print (")");

        } else if (p instanceof Ast.FunctionCall) {
            Ast.FunctionCall p2 = (Ast.FunctionCall) p;
            System.out.print (p2.id);
            System.out.print ("(");
            for (Ast.Argument arg = p2.args; arg != null; arg = arg.next) {
              printExpr (arg.expr);
              if (arg.next != null) {
                System.out.print (",");
              }
            }
            System.out.print (")");

        } else if (p instanceof Ast.ArrayConstructor) {
            Ast.ArrayConstructor p2 = (Ast.ArrayConstructor) p;
            System.out.print (p2.id);
            System.out.print ("{{");
            for (Ast.ArrayValue arrayValue = p2.values;
                 arrayValue != null;
                 arrayValue = arrayValue.next) {
              if (arrayValue.countExpr != null) {
                printExpr (arrayValue.countExpr);
                System.out.print (" of ");
              }
              printExpr (arrayValue.valueExpr);
              if (arrayValue.next != null) {
                System.out.print (", ");
              }
            }
            System.out.print ("}}");

        } else if (p instanceof Ast.RecordConstructor) {
            Ast.RecordConstructor p2 = (Ast.RecordConstructor) p;
            System.out.print (p2.id);
            System.out.print ("{");
            for (Ast.FieldInit fieldInit = p2.fieldInits;
                 fieldInit != null;
                 fieldInit = fieldInit.next) {
              System.out.print (fieldInit.id);
              System.out.print (" := ");
              printExpr (fieldInit.expr);
              if (fieldInit.next != null) {
                System.out.print ("; ");
              }
            }
            System.out.print ("}");

        } else if (p instanceof Ast.IntegerConst) {
            Ast.IntegerConst p2 = (Ast.IntegerConst) p;
            System.out.print (p2.iValue);

        } else if (p instanceof Ast.RealConst) {
            Ast.RealConst p2 = (Ast.RealConst) p;
            System.out.print (p2.rValue);

        } else if (p instanceof Ast.StringConst) {
            Ast.StringConst p2 = (Ast.StringConst) p;
            System.out.print ("\"");
            System.out.print (p2.sValue);
            System.out.print ("\"");

        } else if (p instanceof Ast.BooleanConst) {
            Ast.BooleanConst p2 = (Ast.BooleanConst) p;
            if (p2.iValue == 0) {
              System.out.print ("FALSE");
            } else {
              System.out.print ("TRUE");
            }

        } else if (p instanceof Ast.NilConst) {
            System.out.print ("NIL");

        } else if (p instanceof Ast.ValueOf) {
            Ast.ValueOf p2 = (Ast.ValueOf) p;
            System.out.print ("valueof(");
            printExpr (p2.lValue);
            System.out.print (")");

        } else if (p instanceof Ast.Variable) {
            Ast.Variable p2 = (Ast.Variable) p;
            System.out.print (p2.id);

        } else if (p instanceof Ast.ArrayDeref) {
            Ast.ArrayDeref p2 = (Ast.ArrayDeref) p;
            System.out.print ("(");
            printExpr (p2.lValue);
            System.out.print ("[");
            printExpr (p2.expr);
            System.out.print ("])");

        } else if (p instanceof Ast.RecordDeref) {
            Ast.RecordDeref p2 = (Ast.RecordDeref) p;
            System.out.print ("(");
            printExpr (p2.lValue);
            System.out.print (".");
            System.out.print (p2.id);
            System.out.print (")");

        } else {
            System.out.print ("********** ??? **********");
        }

    }



    // printMode (indent, m)
    //
    // Print something like "mode=INTEGER_MODE" to indicate the value of argument m.
    //
    static void printMode (int indent, int m) {
        final int INTEGER_MODE = 1;
        final int REAL_MODE = 2;
        final int STRING_MODE = 3;
        final int BOOLEAN_MODE = 4;
        printFieldName (indent, "mode=");
        switch (m) {
            case INTEGER_MODE:
                System.out.print ("INTEGER_MODE");
                break;
            case REAL_MODE:
                System.out.print ("REAL_MODE");
                break;
            case BOOLEAN_MODE:
                System.out.print ("BOOLEAN_MODE");
                break;
            case STRING_MODE:
                System.out.print ("STRING_MODE");
                break;
            case 0:
                System.out.print ("0");
                break;
            default:
                System.out.print ("***** Mode is garbage! *****");
                break;
        }
        System.out.println ();
    }

}
