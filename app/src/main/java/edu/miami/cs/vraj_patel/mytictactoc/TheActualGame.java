package edu.miami.cs.vraj_patel.mytictactoc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Matrix3f;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.net.URI;
import java.sql.Array;
import java.util.Arrays;

public class TheActualGame extends AppCompatActivity {
    Handler myHandler = new Handler();
    int clickTime; // click time for the rating bar
    int plays = 0; //number of buttons pressed, initialized to zero
    ProgressBar myBar; //ProgressBar object that will be linked to the resource id of the one in the XML
    String playeronename; //string that holds the name of player 1
    String playertwoname; //string that holds the name of player 2
    int[][] playingboard; //the 2d array which will hold the values of the buttons
    Uri playeroneicon; //Uri that will eventually be assigned to player 1's icon from MainActivity
    Uri playertwoicon; ////Uri that will eventually be assigned to player 2's icon from MainActivity
    int playTime; // playtime which will eventually be assigned the value of the one sent from MainActivity
    int currentPlayer; //id of which player is currently playing (will be initially assigned as the identity of which player will go first from MainActivity)
    int currentID; //integer to hold the resource ID of the current players icon

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_actual_game);
         currentPlayer = this.getIntent().getIntExtra("firstplayer", 0); // figure out which player starts from the int passed in from MainActivity
        // Log.i("LOG/I","THIS PLAYER AT START"+ currentPlayer);
       playTime = this.getIntent().getIntExtra("bartime",0); // get the playTime selected from the options menu in MainActivity
       // Log.i("LOG/I","play Time"+ playTime);
        playeronename = TheActualGame.this.getIntent().getStringExtra("Playerone"); //Get player 1's name from the MainActivity
        playertwoname = TheActualGame.this.getIntent().getStringExtra("Playertwo");//Get player 2's name from the MainActivity
        playeroneicon = TheActualGame.this.getIntent().getParcelableExtra("p1picture"); //Get player 1's icon from the MainActivity (a subsequent method will determine if there even is a Uri passed through)
       playertwoicon = TheActualGame.this.getIntent().getParcelableExtra("p2picture");  //Get player 2's icon from the MainActivity (a subsequent method will determine if there even is a Uri passed through)
        setURI(playeroneicon,R.id.p1icon); // calling the method that checks and sets the Uri sent through the Intent
        setURI(playertwoicon,R.id.p2icon); // calling the method that checks and sets the Uri sent through the Intent
        playingboard = new int[3][3]; // initializing the board as a 2 dimensional array, visually represented as a 3x3 gird
        myBar = findViewById(R.id.progbar); //linking the progress bar object to the progress bar in the XML
        myBar.setMax(playTime); //setting the maximum time to the playTime passed through the Intent
        startPlayer();

    }
    public void setURI(Uri fromMain,int iconID) {
        //Converting the Uri for each icon sent from the Main activity to a drawable to set for the icon pictures here
        if (fromMain != null){ //if no Uri is sent, the method never enters the if statement and is automatically returned
            Context context = TheActualGame.this;
            Drawable image = null;
            try {
                image = Drawable.createFromStream(context.getContentResolver().openInputStream(fromMain), fromMain.toString()); // magic to convert the Uri to a drawable
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            findViewById(iconID).setBackgroundDrawable(image); //set the newly converted drawable as the background for the players icon
        }
    }

    void startPlayer() {
        // Log.i("LOG/I", "REACHED START PLAYER");
        TextView bigName = findViewById(R.id.playername);
        if (currentPlayer == 1) { //If player 1 is active
            bigName.setText(playeronename); //set the name to player 1's name
            findViewById(R.id.p1icon).setVisibility(View.VISIBLE); //show player 1s icon
            findViewById(R.id.p2icon).setVisibility(View.INVISIBLE); // hide player 2s icon
            currentID = R.id.p1icon;
        } else {
            bigName.setText(playertwoname); // If player 2 is active
            findViewById(R.id.p2icon).setVisibility(View.VISIBLE); //set the name to player 2's name
            findViewById(R.id.p1icon).setVisibility(View.INVISIBLE); //hide player 2's icon
            currentID = R.id.p2icon;
        }
        myBar.setProgress(myBar.getMax());
        forBar.run(); //start the runnable
    }


    private final Runnable forBar = new Runnable() {
        // private Handler myHandler = new Handler();
        public void run() {
            myHandler.removeCallbacks(forBar);
            clickTime = 20;
            myBar.setProgress(myBar.getProgress()-clickTime);
            if (myBar.getProgress()<= 0) {
                //once the progress bar runs out, switch the players and then (at the end of the if statement) call startPlayer()
                    if(currentPlayer ==1){
                        currentPlayer =2;
                    }else{
                        currentPlayer =1;
                    }
                startPlayer();
               // myBar.setProgress(myBar.getMax());
               // Log.i("LOG/I", "ENTERED IF PROGRESS BAR");

            }
            if (!myHandler.postDelayed(forBar, clickTime)) {
                Log.i("LOG/I", "POST DELAYED");
                Log.e("Error", "Cannot post Delayed");
            }

        }
    };


    public void gameClick(View view) {
        this.myHandler.removeCallbacks(forBar);
        switch (view.getId()) { // sends the position and resource id of the selected button as well as the identity of the current player to the method which handles what happens to the selected button
            case (R.id.Button1):
                individualButton(0,0,R.id.Button1);
                break;
            case (R.id.Button2):
                individualButton(0,1,R.id.Button2);
                break;
            case (R.id.Button3):
                individualButton(0,2,R.id.Button3);
                break;
            case (R.id.Button4):
                individualButton(1,0,R.id.Button4);
                break;
            case (R.id.Button5):
                individualButton(1,1,R.id.Button5);
                break;
            case (R.id.Button6):
                individualButton(1,2,R.id.Button6);
                break;
            case (R.id.Button7):
                individualButton(2,0,R.id.Button7);
                break;
            case (R.id.Button8):
                individualButton(2,1,R.id.Button8);
                break;
            case (R.id.Button9):
                individualButton(2,2,R.id.Button9);
                break;
        }
        myBar.setProgress(myBar.getMax());
    }

    public void individualButton(int x, int y, int id) {

        if (playingboard[x][y] == 0) { //if this button has not been clicked, enter the if statement
            plays++; //button has been clicked so it is added to the running tally of plays
           // Log.i("LOG/I","this player value " + currentPlayer);
            playingboard[x][y] = currentPlayer; //setting the value of the current button to that of the current player to be able to calculate a win later
           // int checker = playingboard[x][y];
          //  Log.i("LOG/I","this board value " + checker);
            findViewById(id).setForeground(findViewById(currentID).getBackground()); //setting the buttons color/image to that of the given players icon/image in the top corner
            findViewById(id).setClickable(false);
            if (plays >= 3) { //only checks for a win if there have been at least three plays, if less, then its not possible to win so to me, there is no point in checking for those scenarios
                checkWin(x, y);
            }
           // Log.i("LOG/I", "ENTERING SWITCH");
            if(currentPlayer==1){ //switching to the other player
              //  Log.i("LOG/I", "ENTERING case 1");
                currentPlayer =2;
               // Log.i("LOG/I", "CALLING STARTPLAYER1");
            }else {
                currentPlayer = 1;  //switching to the other player
             //   Log.i("LOG/I", "ENTERING case 2");
             //   Log.i("LOG/I", "CALLING STARTPLAYER2");
            }
            startPlayer();

        }else{
          //  Log.i("LOG/I", "BUTTON HAS BEEN DOUBLE CLICKED");
        }
    }

    public void checkWin(int rowPlayed, int columnPlayed) {
        Intent backHome = new Intent(); //new Intent to return to MainActivity.java
        backHome.setClassName("edu.miami.cs.vraj_patel.mytictactoc", "edu.miami.cs.vraj_patel.mytictactoc.MainActivity");
        setResult(RESULT_OK);
        int rowCheck = 0; // counter for buttons that have been clicked by the user in the given row of the last button clicked
        int columnCheck = 0; // counter for buttons that have been clicked by the user in the given column of the last button clicked
        int diagonalCheck = 0; // counter for buttons that have been clicked by the user in the given diagonal of the last button clicked
        int opposite_diagonalCheck = 0; // counter for buttons that have been clicked by the user in the given reverse diagonal of the last button clicked
        for (int index = 0; index < 3; index++) { //for loop to iterate through the row, column, diagonal, and opposite diagonal
            //Look for a column win
            if (playingboard[index][columnPlayed] == currentPlayer) { //checking to see if there is a win in the column of the clicked button
                Log.i("LOG/I", "index "+ index);
                Log.i("LOG/I","column "+columnPlayed);

                columnCheck++;
            }
            if (playingboard[rowPlayed][index] == currentPlayer) { //checking to see if there is a win in the row of the clicked button
              //  Log.i("LOG/I", "rowCheck");
                rowCheck++;
            }
            if (playingboard[index][index] == currentPlayer) {    //checking to see if there is a win in a diagonal angle of the clicked button
               // Log.i("LOG/I", "regulardiagonalCheck");
                diagonalCheck++;
            }
                if (playingboard[2-index][index] == currentPlayer) { //checking to see if there is a win the reverse diagonal angle of the clicked button
                 //   Log.i("LOG/I", "opposite_diagonalCheck");
                    opposite_diagonalCheck++;
                }

        }
        if ((columnCheck == 3 || rowCheck == 3 || diagonalCheck == 3 || opposite_diagonalCheck == 3)) { //if there are 3 buttons that match any win case, the win is awarded to the rightful player
          //  Log.i("IN checkWin","");
            backHome.putExtra("Winner", currentPlayer);
           // Log.i("LOG/I", "eNTERED WINNER IF");
            setResult(RESULT_OK,backHome);
        } else if (plays == 9){ // Tie case, all buttons have been clicked but there is no instance of a win
            backHome.putExtra("Winner", -1);
            setResult(RESULT_CANCELED);
        }else{  //in case the back button has been pressed
            setResult(RESULT_CANCELED);
           // Log.i("LOG/I", "ENTERED RETURN ELSE");
            return;
        }
        this.finish();
    }
    public void onDestroy() {
        super.onDestroy();
        this.myHandler.removeCallbacks(forBar);
    }
}
