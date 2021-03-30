all: pre yapp post

lexer.o: lexer.c lexer.h
	gcc -g -c lexer.c

pre: lexer.c pre.c lexer.o
	gcc -g pre.c lexer.o -o pre

post: post.c 
	gcc -g post.c -o post

yapp: yapp.pcat
	pc yapp
