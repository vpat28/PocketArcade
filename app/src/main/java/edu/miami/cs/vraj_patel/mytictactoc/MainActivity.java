package edu.miami.cs.vraj_patel.mytictactoc;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    EditText player1; //EditText object for player 1's name
    EditText player2; //EditText object for player 2's name
    int playTime =8000; //default playtime
    int uriID; //resource id of the selected icon to set the image for
    double startChance = 0.5; // used to calculate which player starts first
    Uri player1Icon; //Uri for player 1's image
    Uri player2Icon;//Uri for player 2's image
    RatingBar player1bar; // rating bar for player 1
    RatingBar player2bar; // rating bar for player 2
    Button playButton; //the main play button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         player1 =findViewById(R.id.player1input); //linking this EditText object to the one in the XML
         player2 =findViewById(R.id.player2input); //linking this EditText object to the one in the XML
         player1bar = findViewById(R.id.ratingBar1); //linking this RatingBar object to the one in the XML
        player2bar = findViewById(R.id.ratingBar2); //linking this RatingBar object to the one in the XML
        playButton = findViewById(R.id.playbutton);// linking this button object to the one in the XML
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return (true);
    }
    public void ClickMaster (View view){
        String playeronename = player1.getText().toString(); //getting the name player 1 entered from the EditText element in the XML
        String playertwoname = player2.getText().toString(); //getting the name player 2 entered from the EditText element in the XML;
        player1.setText(playeronename);
        player2.setText(playertwoname);
        Intent nextActivity;
        nextActivity = new Intent(MainActivity.this,TheActualGame.class);
        switch (view.getId()) {
            case (R.id.playbutton): // If the play button is pressed, its time to go to work
            nextActivity.putExtra("playtime", playTime); //passing the play time to the next activity
            nextActivity.putExtra("Playerone",player1.getText().toString()); //passing player 1's name to the next activity
            nextActivity.putExtra("Playertwo",player2.getText().toString()); //passing player 2's name to the next activity
            nextActivity.putExtra("bartime",playTime); //passing the playtime selected by the user in the options menu (or the default if none have been selected_
            nextActivity.putExtra("p1picture",player1Icon); // passing player 1's icon as a URI
            nextActivity.putExtra("p2picture",player2Icon);// passing player 2's icon as a URI
               int firstplayer;
               double randomNum = Math.random(); // picks a random number from 0.0 to 1.0
                        if(randomNum > startChance) { // if the number is greater than 0.5, player 1 goes first
                            firstplayer = 1;
                        }
                        else{  //if the number is less than 0.5, player 2 goes first
                            firstplayer = 2;
                    }
                    nextActivity.putExtra("firstplayer",firstplayer); // sending which player goes first through the intent
                    startPlay.launch(nextActivity); // launching Intent using the startPlay Activity Result Launcher
                break;
            case (R.id.p1button):
                uriID = R.id.p1button;
                startGallery.launch("image/*"); // open the gallery so the user can select their icon image
                //Log.i("LOG/I", "STARTING IF STATEMENT");
                break;
            case (R.id.p2button):
                uriID = R.id.p2button;
                startGallery.launch("image/*"); // open the gallery so the user can select their icon image
                //Log.i("LOG/I", "STARTING IF STATEMENT");
                break;
            default:
                break;
        }
    }

    ActivityResultLauncher<String> startGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri resultUri) {
                    // Log.i("LOG onActivityResult","OnActivityResult launched");
                    if (resultUri != null) {
                        Context context = MainActivity.this;
                        Drawable image = null;
                        try {
                            image = Drawable.createFromStream(context.getContentResolver().openInputStream(resultUri),resultUri.toString()); // use magic to convert the URI to a drawable
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        findViewById(uriID).setBackgroundDrawable(image);
                            if(uriID == R.id.p1button){ //Basically assigning the Uri to another Uri specific to which players icon it is to send to the next activity
                                player1Icon = resultUri;
                            }else{
                                player2Icon =resultUri;
                            }
                    } else {
                        Toast.makeText(MainActivity.this,"No picture? No worries.",Toast.LENGTH_LONG).show(); // a little something extra for the user
                    }
                   // Log.i("LOG/I", "FINISHING ACTIVITY RESULT");
                }
            });
    public boolean onOptionsItemSelected(MenuItem item) { //for the menu options

        switch (item.getItemId()) {
            case R.id.reset:
                player1bar.setRating(0);
                player2bar.setRating(0);
                playButton.setVisibility(View.VISIBLE);
                return (true);
            // following cases just change play times to different second intervals
            //NOTE: some playtimes are techinically a few seconds shorter, but this is to make the progress bar smooth with the clicktime
            //I've timed each while playing and they are the times they're supposed to be.
            case R.id.onesec:
                playTime = 800; // Clicktime is set to 20 in the next activity, so this equates to one second
                return (true);
            case R.id.twosec:
               playTime = 1600; // Clicktime is set to 20 in the next activity, so this equates to two seconds
                return (true);
            case R.id.fivesec:
               playTime = 4000; // Clicktime is set to 20 in the next activity, so this equates to five seconds
                return (true);
            case R.id.tensec:
               playTime =8000; // Clicktime is set to 20 in the next activity, so this equates to 10 seconds
                return (true);
            default:
                return (super.onOptionsItemSelected(item));
        }
    }

    ActivityResultLauncher<Intent> startPlay = registerForActivityResult( //ARL for the intent that launches the actual game itself
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Log.i("LOG onActivityResult","OnActivityResult launched!?");
                    if (result.getResultCode() == Activity.RESULT_OK) { //RESULT_OK is only sent back if a winner has been determined
                        int champion;
                        champion = result.getData().getIntExtra("Winner", 0); //getting which player wom that round
                        if (champion == 1) {
                            player1bar.setRating((player1bar.getRating() + 1)); //since player 1 won the match, add a star to their rating bar
                            Toast.makeText(MainActivity.this, player1.getText().toString()+ " wins this round!", Toast.LENGTH_SHORT).show(); //message for player 1 winning the round
                            if (player1bar.getRating() == 5) { // if after the previous if statement, player 1's total wins is brought to five, then:
                                playButton.setVisibility(View.INVISIBLE); //hide the play button
                                Toast.makeText(MainActivity.this, player1.getText().toString()+" IS THE CHAMPION!!!", Toast.LENGTH_LONG).show(); //a little victory message for player 1
                            }
                        } else { //if RESULT_OK, but the winner is not player 1, then the winner is player 2
                            player2bar.setRating((player2bar.getRating() + 1)); // since player 2 won the match, add a star to their rating bar
                            Toast.makeText(MainActivity.this, player2.getText().toString()+ " wins this round!", Toast.LENGTH_SHORT).show(); //message for player 2 winning the round
                            if (player2bar.getRating() == 5) { // if after the previous if statement, player 1's total wins is brought to five, then:
                                playButton.setVisibility(View.INVISIBLE); //hide the play button
                                Toast.makeText(MainActivity.this, player2.getText().toString()+" IS THE CHAMPION!!!", Toast.LENGTH_LONG).show(); //a little victory message for player 1
                            }
                        }
                    }else if(result.getResultCode()==Activity.RESULT_CANCELED){
                        // in the event that there is a tie case or the back button is pressed, the result code is RESULT_CANCELED so this message informs the user that nobody scored
                        Toast.makeText(MainActivity.this, "No stars for either players!", Toast.LENGTH_LONG).show();
                    }
                }
            });
}