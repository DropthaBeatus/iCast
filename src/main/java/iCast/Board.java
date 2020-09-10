package iCast;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class Board {
    Pieces[][] pieces;
    String[][] board;
    boolean endTurn;

    int x;
    int y;

    int dirMove;

    public Board(int x, int y){
        this.x = x;
        this.y = y;
        board = new String[x][y];
        pieces = new Pieces[x][y];
    }

    private void boardPlacement() {
        for (int i = 0; i < x; i++) {
            for (int e = 0; e < y; e++) {
                board[i][e] = ":skull:";
                pieces[i][e] = new Pieces(":skull:", 0);
            }
        }
    }
//maybe do if totally sqaure so I can do 1 loop here

    public String printBoard(){
        StringBuilder message = new StringBuilder();
        for(int i = 0; i < x; i++){
            for(int e = 0; e < x; e++){
                    message.append(pieces[i][e].piece);
            }
            message.append("\n");
        }
        return message.toString();
    }

    private void movePiece(int a, int b, int i, int e){
        pieces[i][e].piece = new String(pieces[x][y].piece);
        pieces[i][e].value = pieces[a][b].value;

        pieces[a][b].piece = board[a][b];
        pieces[a][b].value = 0;
    }

    //might need to fix this here
    public String makeTurn(int i, int e){
        if(checkBoard(i, e)){
            return printBoard();
        }
            return "Piece cannot move to specified location!";
    }


    //need to check if this conflicts with base class!!!!
    public Boolean checkBoard(int i, int e){
        return i < x - 1 && e < y - 1;
    }





    public void endGame(){ }

}