package com.checkers;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    private static Scanner S = new Scanner(System.in);
    private static boolean undo = true;
    private static Stack<Board> history = new Stack<>();

    public static void main(String[] args) {
        int gameMode = -1;
        int secondsPerMove = 5;
        boolean standard = true;
        Board b = new Board();
        while (gameMode == -1) {
            System.out.println("Starting position: " + (standard ? "standard" : "custom") + "\nTime per CPU move: " + (secondsPerMove) + "\nUndo: " + (undo ? "enabled" : "disabled") + "\n\nChoose a game mode:\n1: Human goes first\n2: AI goes first\n3: AI vs AI\nOr:\n4: Adjust settings");
            while (gameMode < 0 || gameMode > 3) {
                gameMode = getInt()-1;
            }
            if (gameMode == 3) {
                gameMode = -1;
                int selection = -1;
                while (selection != 4) {
                    System.out.println("Choose an option to adjust:\n1: starting position\n2: Time per CPU move\n3: Undo\n4: Return");
                    selection = getInt();
                    if (selection == 1) {
                        standard = false;
                        System.out.println("The standard board looks like this:\n8: 02020202\n7: 20202020\n6: 02020202\n5: 00000000\n4: 00000000\n3: 10101010\n2: 01010101\n1: 10101010\n   ABCDEFGH\n\n0 - empty\n1 - human pawn\n2 - CPU pawn\n3 - human king\n4 - CPU king\n\nPlease enter the new board line by line as a series of eight digit numbers:\n");
                        for (int i = 0; i < 8; i++) {
                            System.out.print((8-i)+": ");
                            int line = getInt();
                            for (int j = 0; j < 8; j++) {
                                b.setPeice(i,7-j,line % 10);
                                line = line / 10;
                            }
                        }
                        System.out.println("   ABCDEFGH");
                        b.printBoard(false);
                        System.out.println("New board set.\n");
                    } else if (selection == 2) {
                        System.out.println("How many seconds can the AI take per move?");
                        secondsPerMove = getInt();
                    } else if (selection ==3) {
                        System.out.println("1: Enable Undo\n2: Disable Undo");
                        int sel = 0;
                        while (sel != 1 && sel != 2)
                            sel = getInt();
                        undo = (sel == 1);
                    }
                }
            }
        }
        boolean aiturn = (gameMode == 1);
        if (aiturn)
            b.switchTurn();
        while (true) {
            ArrayList<Board> moves = b.findLegalMoves();
            b.printBoard(aiturn);
            if (b.getTime() != -1) {
                System.out.printf("Elapsed time: %.2f\nSearched %d moves ahead.\n",b.getTime(),b.getDepth());
            }
            if (aiturn || gameMode==2) {
                if (moves.size()==0) {
                    System.out.println((gameMode == 2 ? "Player "+ (aiturn?"1":"2") : "You") + " Won");
                    break;
                }
                long t1 = System.currentTimeMillis();
                b = aIIterativeRecursiveHeuristic(moves,secondsPerMove);
                b.setTime(((float)(System.currentTimeMillis()-t1)/1000));
            } else {
                if (moves.size()==0) {
                    System.out.println("GAME OVER");
                    break;
                }
                history.push(b);
                b = humanMove(moves);
            }
            b.switchTurn();
            aiturn = !aiturn;
        }
        S.close();
    }

    private static Board aIIterativeRecursiveHeuristic(ArrayList<Board> moves,int time) {
        if (moves.size() == 1) {
            return moves.get(0);
        }
        long t2 = System.currentTimeMillis() - 10 + (time * 1000);
        Board max = moves.get(0), newmax = moves.get(0);
        int depth = 0;
        while (System.currentTimeMillis() < t2 ) {
            System.out.print(".");
            max = newmax;
            if (System.currentTimeMillis() > (t2 - (time * 500))  ) {
                return max;
            }
            depth++;
            int finalDepth = depth;
            newmax = Collections.max(moves, Comparator.comparing(m -> m.getRecursiveHeuristic(finalDepth,t2)));
            newmax.setDepth(depth);
        }
        return max;
    }

    private static Board humanMove(ArrayList<Board> moves) {
        System.out.println("\nSelect a move:");
        for (int i = 0; i < moves.size(); i++) {
            System.out.print(i);
            long t2 = System.currentTimeMillis()+2000;
            System.out.println(": "+moves.get(i).getLastMove());// +" "+moves.get(i).getRecursiveHeuristic(0,t2)+" "+moves.get(i).getRecursiveHeuristic(1,t2)+" "+moves.get(i).getRecursiveHeuristic(2,t2)+" "+moves.get(i).getRecursiveHeuristic(3,t2)+" "+moves.get(i).getRecursiveHeuristic(6,t2));
        }
        if (undo && history.size() > 1)
            System.out.println(moves.size()+": undo last move");
        boolean loop = true;
        int move = 0;
        while (loop) {
            move = getInt();
            if (move >= 0 && move < (undo && history.size() > 1 ? moves.size()+1 : moves.size()))
                loop = false;
        }
        if (move == moves.size()) {
            history.pop();
            Board old = history.peek();
            old.printBoard(false);
            return humanMove(old.findLegalMoves());
        }
        return moves.get(move);
    }

    private static int getInt() {
        while (!S.hasNextInt()) {
            S.next();
        }
        return S.nextInt();
    }
}