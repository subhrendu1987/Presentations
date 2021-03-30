// ----------------------  FatalError  -----------------------
//
// This exception should be thrown to terminate the compiler.  If
// a message is provided, it will be printed first.
//
// This class has been changed slightly from Project 2, to add
// the second constructor.
//
// See also the subclass called "LogicError":
//      FatalError -- stack trace is NOT printed
//      LogicError -- stack trace is printed
//
// Harry Porter -- 01/15/03
//              -- 02/04/04
//
class FatalError extends Exception {
    FatalError (String message) {
        super (message);
    }
    FatalError () {
        super ();
    }
}

