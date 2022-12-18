// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.
// (LOOP)
//     @R0
//     D=M
//     @R2
//     M=D+M
//     @R1
//     M=M-1
//     @R1
//     D=M
//     @END
//     D;JLE // infinite loop
//     @LOOP
//     @SCREEN
//     M=1
// (END) 
//     @END
//     0;JMP




(LOOP)
    @SCREEN
    D=A
    @addr
    M=D

    @8192
    D=A
    @n
    M=D

    @i
    M=0

    @KBD
    D=M
	@BLACKEN
	D;JNE

(WHITEN)
    @i
    D=M
    @n
    D=D-M
    @LOOP
    D;JGE

    @addr
    A=M
    M=0 // RAM[addr]=1111111111111111

    @i
    M=M+1
    @1
    D=A
    @addr
    M=D+M
    @WHITEN
    0;JMP

(BLACKEN)
    @i
    D=M
    @n
    D=D-M
    @LOOP
    D;JGE

    @addr
    A=M
    M=-1 // RAM[addr]=1111111111111111

    @i
    M=M+1
    @1
    D=A
    @addr
    M=D+M
    @BLACKEN
    0;JMP

