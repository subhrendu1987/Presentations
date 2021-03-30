// ----------------------  LogicError  -----------------------
//
// Each instance of this exception class contains a message describing
// a program logic problem;  A bug-free program should not throw this
// exception but it can be useful in testing.  For example:
//         if (nextToken != Token.WHILE) {
//             throw new LogicError ("NextToken is messed up");
//         }
// When thrown, this expection will result in a stack trace, which can
// may be useful in debugging. For example:
//         LogicError: NextToken is messed up
//                 at Parser.parseStmts(Parser.java:678)
//                 at Parser.parseBody(Parser.java:345)
//                 at Parser.parseProgram(Parser.java:122)
//                 at Main.main(Main.java:32)
//
// Harry Porter -- 01/15/03
//              -- 02/04/04
//
class LogicError extends FatalError {
    LogicError (String message) {
        super (message);
    }
}
