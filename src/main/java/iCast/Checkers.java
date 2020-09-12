package iCast;


import java.util.stream.Stream;

public class Checkers extends Board{


    private static String red = ":banana:";
    private static String black = ":peanuts:";
    private static String redKing = ":monkey:";
    private static String blackKing = ":coconut:";

    private static String white_block = ":white_large_square:";
    private static String black_block = ":black_large_square:";
    private boolean blnStarted = false;

   // private static int numBlackPiece = 0;
   // private static int numRedPiece = 0;


    public Checkers() {
        super(8,8);

        boardPlacement();
        piecePlacementStart();
    }


    private void boardPlacement(){
        for(int i = 0; i < x; i++){
            for(int e = 0; e < y; e++){
                if((i + e) % 2 == 0){
                    board[i][e] = black_block;
                }
                else{
                    board[i][e] = white_block;
                }

            }
        }
    }

    private void piecePlacementStart() {
        for (int i = 0; i < x; i++) {
            for (int e = 0; e < y; e++) {
                if (board[i][e].equals(black_block) && i <= 2) {
                    pieces[i][e] = new Pieces(black, -2);
                }
                else if(board[i][e].equals(black_block) && i >= 5){
                    pieces[i][e] = new Pieces(red, 1);
                }
                else{
                    pieces[i][e] = new Pieces(board[i][e], 0);
                }
            }
        }
    }

    public String parseMessage(String Message){
        int[] coords = Stream.of(Message.replaceAll("[^0-9]", "").split("")).mapToInt(Integer::parseInt).toArray();;
        return makeTurn(coords[0] - 1, coords[1] - 1, coords[2] - 1, coords[3] - 1);
    }

//This Section is here to check movement
    private String makeTurn(int a, int b, int i, int e){
        if(checkPieceMovement(a, b, i, e) && checkBoard(a, b, i, e)){
            return printBoard();
        }
        return "Piece cannot move to specified location!";
    }

    //HAVE A CHECK TO STOP THIS FROM EXTENDING INDEFINITLY
    private Boolean checkBoard(int a, int b, int i, int e){
        if(e < x && i < y && pieces[a][b].value != 0 && i >= 0 && e >= 0){
            if(pieces[i][e].value == 0){
                movePiece(a, b, i, e);
                return true;
            }
            //FIX THIS FROM FIRING TWICE!
            //MAKE SURE TO FIX PRINT FUNCTION AS WELL
            else if(pieces[a][b].value%2 != pieces[i][e].value%2){
                if(checkBoard(a, b, i + ((a - i) * -1), e + ((b - e) * -1))){
                    pieces[i][e].value = 0;
                    pieces[i][e].isNotImportant();
                    pieces[i][e].piece = board[i][e];
                    return true;
                }
                //include a bool here to continue turn maybe??
            }
            return false;
        }
        return false;
    }

    //BLACK KING IS -4
    //Black is going to move to 7
    //Red is going to move to 0
    //RED KING IS 3
    //black is -2 and starts at the top
    //red is 1 and starts at the bottom
    //0 IS NULL
    //0 value is
    //maybe return string here

    private Boolean checkPieceMovement(int a, int b, int i, int e){
        int yMove = a - i;
        int xMove = b - e;
        if(Math.abs(xMove) < 1 || Math.abs(yMove) < 1){
            return false;
        }
        //need to find a way to have king ignore this with

        return(yMove * pieces[a][b].isNotImportant * pieces[a][b].value > -1);
    }




//This section is here to move the actual pieces and king if y =0, or 7
// need to check if this conflicts with base class!!!!
    public void movePiece(int a, int b, int i, int e){

            pieces[i][e].piece = new String(pieces[a][b].piece);
            pieces[i][e].value = pieces[a][b].value;
            pieces[i][e].isNotImportant = pieces[a][b].isNotImportant;


            //do not need to do new string here
            pieces[a][b].piece = new String(board[a][b]);
            pieces[a][b].isNotImportant();
            pieces[a][b].value = 0;

            if(i == y-1 && pieces[i][e].value%2 == 0){
                monarchMe(pieces[i][e], blackKing);
            }
            else if(i == 0 && pieces[i][e].value%2 == 1){
                monarchMe(pieces[i][e], redKing);
            }


    }






    private void monarchMe(Pieces piece, String Monarch){
        piece.piece = new String(Monarch);
        piece.isImportant();
        //negative for red piece
        //positive for black piece
        piece.value = piece.value + (2 * Integer.signum(piece.value));
    }


    public String printBoard(){
        String[] numStr= {":one:",":two:",":three:",":four:",":five:",":six:",":seven:",":eight:"};
        StringBuilder message = new StringBuilder();
        message.append(":negative_squared_cross_mark::one::two::three::four::five::six::seven::eight::negative_squared_cross_mark:\n");
        for(int i = 0; i < x; i++){
            message.append(numStr[i]);
            for(int e = 0; e < x; e++){
                message.append(pieces[i][e].piece);
            }
            message.append(numStr[i] + "\n");
        }
        message.append(":negative_squared_cross_mark::one::two::three::four::five::six::seven::eight::negative_squared_cross_mark:\n");
        return message.toString();
    }

    //might be best to add this to board as interface?
    public String commands(String command){
        command = command.replace("..pc ", "");

        if(command.startsWith("move") && blnStarted){
            return parseMessage(command);
        }
        else if(command.startsWith("move") && !blnStarted){
            return "Checkers game is not started. Type '..pc new game' to get started";
        }
        else if(command == "new game"){
            boardPlacement();
            piecePlacementStart();
            blnStarted = true;
            return printBoard();
        }
        else if(command == "help"){
            String strHelpMessage = "..pc------------------------------------------------------\n\n";
            strHelpMessage += "--------move (coordinates of piece) (coordinates of where the piece will move to)\n";
            strHelpMessage += "----------------------Moves piece from coordinates to that coordinates.\n";
            strHelpMessage += "----------------------e.g. 'Move 1,1 2,2.\n";
            strHelpMessage += "----------------------If jumping over piece the coordinates will be where the attacked piece is.\n";
            strHelpMessage += "--------new game\n";
            strHelpMessage += "----------------------Starts a new game.\n";
            strHelpMessage += "----------------------Must start new game before moving pieces\n";
            return strHelpMessage;
        }
        else{
            return "No " + command + " command exist of that type. Type '..pc help' for more information about tBot Checkers.";
        }

    }


}
