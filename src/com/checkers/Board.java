package com.checkers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class Board {
    private String lastMove;
    private int[][] board1;
    private int[][] board2;
    private float time = -1;
    private int depth = 0;

    Board() {
        board1 = new int[][]{   {0, 2, 0, 2, 0, 2, 0, 2},
                                {2, 0, 2, 0, 2, 0, 2, 0},
                                {0, 2, 0, 2, 0, 2, 0, 2},
                                {0, 0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 0},
                                {1, 0, 1, 0, 1, 0, 1, 0},
                                {0, 1, 0, 1, 0, 1, 0, 1},
                                {1, 0, 1, 0, 1, 0, 1, 0}};

        board2 = new int[][]{   {0, 2, 0, 2, 0, 2, 0, 2},
                                {2, 0, 2, 0, 2, 0, 2, 0},
                                {0, 2, 0, 2, 0, 2, 0, 2},
                                {0, 0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 0},
                                {1, 0, 1, 0, 1, 0, 1, 0},
                                {0, 1, 0, 1, 0, 1, 0, 1},
                                {1, 0, 1, 0, 1, 0, 1, 0}};
        lastMove = "";
    }

    private Board(int[][] board1, int[][] board2, String lastMove) {
        this.board1 = board1;
        this.board2 = board2;
        this.lastMove = lastMove;
    }

    void setPeice(int x, int y, int value) {
        board1[x][y] = value;
        board2[7-x][7-y] = "02143".indexOf(value+'0');
    }

    String getLastMove() {
        return lastMove;
    }

    ArrayList<Board> findLegalMoves() {
        ArrayList<Board> captures = new ArrayList<>();
        ArrayList<Board> regmoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int value = board1[i][j];
                if (value == 1 || value == 3) {
                    captures.addAll(findCaptures(i, j, value));
                    regmoves.addAll(findRegMoves(i, j, value));
                }
            }
        }
        if (captures.size()>0) {
            return captures;
        } else {
            return regmoves;
        }
    }

    private ArrayList<Board> findCaptures(int i, int j, int value) {
        ArrayList<Board> moves = new ArrayList<>();
        for (int k = -1; k < value; k+=2) {
            for (int l = -1; l < 3; l+=2) {
                int passx = i+k; int passy = j+l;
                int landx = i+(2*k); int landy = j+(2*l);
                if (Math.min(landx,landy) >= 0 && Math.max(landx,landy) <= 7 &&
                        board1[landx][landy]==0 && (board1[passx][passy]==2||board1[passx][passy]==4)) {
                    Board bprime = new Board(deepCopy(board1),deepCopy(board2),cordsToString(i, j) + " to " + cordsToString(landx, landy));
                    bprime.setPeice(landx,landy,landx==0 ? 3 : value);
                    bprime.setPeice(passx,passy,0);
                    bprime.setPeice(i,j,0);
                    ArrayList<Board> captures = bprime.findCaptures(landx,landy,value);
                    if (captures.size()>0) {
                        for (Board m : captures) {
                            moves.add(new Board(m.board1,m.board2,cordsToString(i, j) + " to " + m.lastMove));
                        }
                    } else {
                        moves.add(bprime);
                    }
                }
            }
        }
       return moves;
    }

    private ArrayList<Board> findRegMoves(int i, int j, int value) {
        ArrayList<Board> moves = new ArrayList<>();
        for (int k = -1; k < value; k+=2) {
            for (int l = -1; l < 3; l += 2) {
                int landx = i + k;
                int landy = j + l;
                if (Math.min(landx, landy) >= 0 && Math.max(landx, landy) <= 7 && board1[landx][landy] == 0) {
                    Board bprime = new Board(deepCopy(board1),deepCopy(board2),cordsToString(i, j) + " to " + cordsToString(landx, landy));
                    bprime.setPeice(landx,landy,landx==0 ? 3 : value);
                    bprime.setPeice(i,j,0);
                    moves.add(bprime);
                }
            }
        }
        return moves;
    }
    private int[][] deepCopy(int[][] matrix) {
        return java.util.Arrays.stream(matrix).map(int[]::clone).toArray($ -> matrix.clone());
    }

    private String cordsToString(int i, int j) {
        return String.valueOf("12345678".charAt(7-i)) + "ABCDEFGH".charAt(j);
    }

    void printBoard(boolean inverse) {
//        System.out.println(Arrays.deepToString(board1).replace("], ", "]\n"));
        System.out.print("\033[H\033[2J\n");
        for (int i = 0; i < 8; i++) {
            System.out.print("\033[0m");
            System.out.print(8-i);
            System.out.print(" ");
            for (int j = 0; j < 8; j++) {
                int v = inverse ? board2[i][j] : board1[i][j];
                String color = lastMove.contains(cordsToString(inverse ? i : 7 - i, inverse ? j :7 - j)) ? "103" : "0";
                if (v == 1 || v == 3)
                    System.out.print("\033["+color+";32m "+v+" ");
                else if (v == 2 || v == 4)
                    System.out.print("\033["+color+";31m "+v+" ");
                else if ((i+j)%2==0)
                    System.out.print("\033[47m   \033[0m");
                else
                    System.out.print("\033["+color+"m   ");
            }
            System.out.println("\033[0m");
        }
        System.out.println("   A  B  C  D  E  F  G  H");
    }

    void switchTurn() {
        int[][] temp = board1;
        board1 = board2;
        board2 = temp;
    }

    int getHeuristic() {
        int[] counts = new int[5];
        List<Integer> places = Arrays.asList(0,7,6,5,4,2,1,3);
        int rowCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int value = board1[i][j];
                counts[value]++;
                if (value == 1) rowCount += places.get(i);
                if (value == 3) rowCount -= places.get(7-i);
            }
        }
        int c1 = counts[1]+2*counts[3];
        int c2 = counts[2]+2*counts[4];
        if (c2 == 0)
            return Integer.MAX_VALUE;
        else
            return  1000000*(c1-c2)+10000*(Integer.signum(c2 - c1) * (c1+c2))+ rowCount*100 + ThreadLocalRandom.current().nextInt(100);
    }

    int getRecursiveHeuristic(int depth,long t2) {
        return getRecursiveHeuristicAlg(depth,t2,Integer.MIN_VALUE,Integer.MIN_VALUE);
    }
    private int getRecursiveHeuristicAlg(int depth, long t2, int a, int b) {
        if (depth == 0) {
            return getHeuristic();
        }
        if (System.currentTimeMillis() > t2) {
            return 0;
        }
        switchTurn();
        int max = Integer.MIN_VALUE+1;
        for (Board newmove : findLegalMoves()) {
            int newHeuristic = (newmove.getRecursiveHeuristicAlg(depth - 1, t2,b,a));
            if (newHeuristic > 2147480000) newHeuristic--;
            if (newHeuristic < -2147480000) newHeuristic++;
            if (newHeuristic > max) {
                max = newHeuristic;
            }
            if (newHeuristic > a) {
                a = newHeuristic;
            }
            if (newHeuristic * -1 < b) {
                switchTurn();
                return -1*max;
            }
        }
        switchTurn();
        return -1*max;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
