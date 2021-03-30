#
# Type 'make' with this 'makefile' file in your current directory to compile
# the components of the PCAT compiler.
#

JJ=javac
JFLAGS=

all: Main.class

Lexer.class: Lexer.java
	$(JJ) $(JFLAGS) Lexer.java

StringTable.class: StringTable.java
	$(JJ) $(JFLAGS) StringTable.java

Token.class: Token.java
	$(JJ) $(JFLAGS) Token.java

FatalError.class: FatalError.java
	$(JJ) $(JFLAGS) FatalError.java

LogicError.class: LogicError.java
	$(JJ) $(JFLAGS) LogicError.java

Parser.class: Parser.java
	$(JJ) $(JFLAGS) Parser.java

Main.class: Main.java Lexer.class StringTable.class Token.class FatalError.class LogicError.class Parser.class
	$(JJ) $(JFLAGS) Main.java
