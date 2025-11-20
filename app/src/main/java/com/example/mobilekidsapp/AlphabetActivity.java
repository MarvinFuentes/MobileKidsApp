package com.example.mobilekidsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Locale;

public class AlphabetActivity extends AppCompatActivity {
    // Setting all of our variables for the AlphabetViewerFragment, Button, TextToSpeech,
    // ImageButtons, ProgressBar, StudentDb, Strings, TextToSpeech, and the array of phrases.
    AlphabetViewerFragment alphabetViewerFragment;
    Button aaClearBtn;
    ImageButton aaForwardBtn, aaBackBtn, aaSpeechBtn;
    ProgressBar aaProgressBar;
    // dbHelper will be extremely useful when getting or updating progress
    // for all saved student profiles.
    StudentDd dbHelper;
    // These strings help us locate the correct student profile from the database.
    String aaProfileColor, aaProfileShape;
    private TextToSpeech aaTTS;
    // This array holds all of the phrases dictated in the Alphabet Activity.
    private final String[] letterPhrases ={"A is for Airplane", "B is for Bike", "C is for Cat", "D is for Dog", "E is for Egg", "F is for Fox",
    "G is for Goat", "H is for Hat", "I is for Ice cream", "J is for Jelly", "K is for Kiwi", "L is for Leaf", "M is for Milk", "N is for Nose",
    "O is for Owl", "P is for Panda", "Q is for Queen", "R is for Robot", "S is for Star", "T is for Tiger", "U is for Uncle", "V is for Volcano",
    "W is for World", "X is for Xylophone", "Y is for Yogurt", "Z if for Zebra"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        dbHelper = new StudentDd(this);
        setContentView(R.layout.alphabet_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // We locate the clear Button by using findViewById().
        aaClearBtn = (Button) findViewById(R.id.aaClearBtn);

        // We locate all ImageButtons by using findViewById().
        aaForwardBtn = findViewById(R.id.aaForwardBtn);
        aaBackBtn = findViewById(R.id.aaBackBtn);
        aaSpeechBtn = findViewById(R.id.aaSpeechBtn);

        // We locate all of our ProgressBar using findViewById().
        aaProgressBar = findViewById(R.id.aaProgressBar);

        // We retrieve the selected profile from the Intent using the color and shape.
        aaProfileColor = getIntent().getStringExtra("colorName");
        aaProfileShape = getIntent().getStringExtra("shapeName");

        // If the profile is somehow missing, we show a Toast message and stop the activity.
        if (aaProfileColor == null || aaProfileShape == null) {
            Toast.makeText(this, "Missing profile data!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // We load any previously saved progress for this profile.
        // This lets the student continue exactly where they left off.
        int aaSavedProgress = dbHelper.getProgress(aaProfileColor, aaProfileShape, "alphabet");
        aaProgressBar.setProgress(aaSavedProgress);

        // We load the fragment that displays the tracing area.
        FragmentManager fManager = getSupportFragmentManager();
        Fragment fragment = fManager.findFragmentById(R.id.alphabetActivityFragment);

        // If check if the fragment already exists, if does then we reuse it. Otherwise, we create a new one.
        if (fragment instanceof AlphabetViewerFragment) {
            alphabetViewerFragment = (AlphabetViewerFragment) fragment;
        }
        else {
            alphabetViewerFragment = new AlphabetViewerFragment();
            fManager.beginTransaction().replace(R.id.alphabetActivityFragment, alphabetViewerFragment).commit();
            fManager.executePendingTransactions();
        }

        // Now that the fragment is loaded we set it to the correct letter base on the saved progress.
        alphabetViewerFragment.setCurrentLetterIndex(aaSavedProgress);

        /* We set an onClickListener() for the clear button
        so when it is clicked it clears the drawing canvas*/
        aaClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We first check that the viewer fragment is not null then we call
                // clearCanvas() to clear the canvas.
                if(alphabetViewerFragment != null){
                    alphabetViewerFragment.clearCanvas();
                }
            }
        });

        /* We set an onclickListener() to check if the forward image button is clicked.
        It moves to the next letter only if the student drew something. This prevents
        skipping letters without completing them.*/
        aaForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We check if the viewer fragment is not null.
                if(alphabetViewerFragment != null){

                    // didWrite will return true if the student drew something on the canvas.
                    boolean didWrite = alphabetViewerFragment.nextLetterCanvas();

                    if(didWrite){
                        int barProgress = aaProgressBar.getProgress();

                        // We only move the progress bar forward if we have not reached the end.
                        if (barProgress < aaProgressBar.getMax()){
                            barProgress++;
                            aaProgressBar.setProgress(barProgress);

                            // We save the progress into the database with updateProgress().
                            dbHelper.updateProgress(aaProfileColor, aaProfileShape, "alphabet", barProgress);
                        }
                    }
                }
            }
        });

        /* We set an setOnClickListener() for the back button.
         It is responsible for loading previous stored letters and its drawing from
         student_drawing table. */
        aaBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alphabetViewerFragment != null){
                    alphabetViewerFragment.previousLetterCanvas();
                }
            }
        });

        // We now initialize the TextToSpeech and if the setup is successful, then we set the
        // language to English.
        aaTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                aaTTS.setLanguage(Locale.ENGLISH);
            }
        });

        // We set an onClickListen for the text to speech image button.
        aaSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alphabetViewerFragment != null){

                    // We call getCurrentLetterIndex() to know exactly what phrase we want to use.
                    int index = alphabetViewerFragment.getCurrentLetterIndex();

                    // We make sure that the index is within the range of letterPhrases array.
                    if(index >= 0 && index < letterPhrases.length){
                        aaTTS.speak(letterPhrases[index], TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        });

    }

    /* onCreateOptionsMenu() inflates the menu action bar for the alphabet activity. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alphabet_menu, menu);
        return true;
    }

    /* onOptionsItemSelected() handles the menu button clicks like save or return to SubjectSelection. */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.amSaveBtn){
            Toast.makeText(this, "Save button pressed!", Toast.LENGTH_LONG).show();
            if(alphabetViewerFragment != null){
                alphabetViewerFragment.saveCurrentCanvas();
            }
        }
        else if(itemId == R.id.amMenuBtn){
            if(alphabetViewerFragment != null){
                alphabetViewerFragment.saveCurrentCanvas();
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}