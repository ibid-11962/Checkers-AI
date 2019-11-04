# Checkers AI

This is a Java program that plays checkers using a heuristic-based negamax search with alpha-beta pruning.

# Set Up

Checkers.jar is the full program compiled with Java 11. You can also do it yourself from the source.

It needs to be run in the command line as it is a text based program. The terminal should support ANSI Escape Codes, as these are used to color the board and clear the screen.

# Usage

When you run the program there are a few settings that you can adjust, such as how many seconds the CPU has per move, what position the board should start in, and whether you (the human) should be allowed to undo moves.

You can then choose between three game modes: Human goes first, Computer goes first, or the computer just plays against itself.

The Game board will always keep the last move highlighted.

![screenshot](/docs/screenshot.png)

When it's your turn to move a list of legal moves will be displayed for you to choose from.

After the computer makes a move it will say how many seconds it spent finding it and how many moves ahead it had a chance to fully search.

The program exits when one player wins.

# Implementation

The Board class represents a unique game state. A game state includes a 2D matrix of the board as well as a String representing the last move. It contains a bunch of methods related to the state, like printing the board, generating a list of legal moves, moving a piece, generating a heuristic of the position, and switching the perspective. The Negamax searching algorithm is also contained in this class.

The Main class handles everything else, such as the actual gameplay, menus, and user input. It keeps a stack of `Boards` for the undo feature.

Negamax is a simplification of a MinMax search algorithm that is valid when using a zero-sum heuristic. In a MinMax search we search to find the maximum heuristic during our turn with the understanding that the other player will try finding the minimum heuristic during their turn. In a Negamax search we take advantage of the fact that a zero-sum heuristic is always equal the opposite for the opponent as it is for us, and thus can use the same recursive function for both perspectives as long as we negate the result before passing it up.

Alpha Beta pruning is employed to premptively prune off branches that we know won't be taken, thus increasing the maximum search depth. Iterative deepening is used to so that the search can be measured in time rather than depth. Whenever the time limit gets reached the Negamax exits and the last fully searched depth is used, and if more than half the time has already passed the next depth won't even begin to be searched.

The heuristic was evaluated based on the following factors:

 - Did we win? Did we lose? (this is diluted a bit each time it gets passed up, so a quick win will be more appealing and a quick loss more unappealing) 
 - How many pieces do we have on the board? How many does our opponent? (kings count as two)
 - How many total pieces are on the board? (This is considered a bad thing if we're ahead, but a goodd thing if we're behind.)
 - How far advanced are our pawns? How about our opponent? (A pawn in the backrow is considered more valuable than the second and third rows for this, because leaving them there blocks the other player from getting kings)
 - A random integer to make equal moves no longer equal and the game nondeterministic.
 
 # Misc
 
 A memorable bug I encountered when writing this program made me learn the hard way that `-1 * Integer.MIN_VALUE` does NOT equal `Integer.MAX_VALUE`. 
