package iCast;

public class Pieces {
    public int value;
    public String piece;
    public int isNotImportant;

    Pieces(String piece, int value){
        this.value = value;
        this.piece = piece;
        isNotImportant = 1;
    }

    public void isImportant(){
        isNotImportant = 0;
    }

    public void isNotImportant(){
        isNotImportant = 1;
    }

}
