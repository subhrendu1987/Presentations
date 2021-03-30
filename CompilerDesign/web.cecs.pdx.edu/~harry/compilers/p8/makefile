#
# Type 'make' with this 'makefile' file in your current directory to compile
# the components of the PCAT compiler.
#

JJ=javac
JFLAGS=

all: Main.class

# Lexer.class: Lexer.java
# 	$(JJ) $(JFLAGS) Lexer.java

StringTable.class: StringTable.java
	$(JJ) $(JFLAGS) StringTable.java

Token.class: Token.java
	$(JJ) $(JFLAGS) Token.java

FatalError.class: FatalError.java
	$(JJ) $(JFLAGS) FatalError.java

LogicError.class: LogicError.java
	$(JJ) $(JFLAGS) LogicError.java

# Parser.class: Parser.java
# 	$(JJ) $(JFLAGS) Parser.java

Ast.class: Ast.java
	$(JJ) $(JFLAGS) Ast.java

PrintAst.class: PrintAst.java
	$(JJ) $(JFLAGS) PrintAst.java

PrettyPrint.class: PrettyPrint.java
	$(JJ) $(JFLAGS) PrettyPrint.java

SymbolTable.class: SymbolTable.java
	$(JJ) $(JFLAGS) SymbolTable.java

# Checker.class: Checker.java
# 	$(JJ) $(JFLAGS) Checker.java

IR.class: IR.java
	$(JJ) $(JFLAGS) IR.java

Generator.class: Generator.java
	$(JJ) $(JFLAGS) Generator.java

Main.class: Main.java Lexer.class StringTable.class Token.class FatalError.class LogicError.class Parser.class Ast.class PrintAst.class PrettyPrint.class SymbolTable.class Checker.class IR.class Generator.class
	$(JJ) $(JFLAGS) Main.java
