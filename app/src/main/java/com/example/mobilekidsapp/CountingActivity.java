package com.example.mobilekidsapp;

import android.content.Intent;
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

public class CountingActivity extends AppCompatActivity {
    // Setting all of our variables for the CountingViewerFragment, Button, TextToSpeech,
    // ImageButtons, ProgressBar, StudentDb, Strings, TextToSpeech, and the array of phrases.
    CountingViewerFragment countingViewerFragment;
    Button caClearBtn;
    ImageButton caForwardBtn, caBackBtn, caSpeechBtn;
    ProgressBar caProgressBar;
    // dbHelper will be extremely useful when getting or updating progress
    // for all saved student profiles.
    StudentDd dbHelper;
    // These strings help us locate the correct student profile from the database.
    String caProfileColor, caProfileShape;
    private TextToSpeech caTTS;
    // This array holds all of the phrases dictated in the Counting Activity.
    private final String[] numberPhrases = {"1, There is 1 moon in the sky.", "2, There are 2 wheels on a bike.",
            "3, A triangle has 3 sides.", "4, There are 4 wheels on a car.", "5, A star has 5 points", "6, A hexagon has 6 sides.",
            "7, There are 7 colors in the rainbow.", "8, A spider has 8 legs.", "9, A cat has 9 lives.", "10, 10 is a double digit number."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new StudentDd(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.counting_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // We locate the clear Button by using findViewById().
        caClearBtn = (Button) findViewById(R.id.caClearBtn);

        // We locate all ImageButtons by using findViewById()
        caForwardBtn = findViewById(R.id.caForwardBtn);
        caBackBtn = findViewById(R.id.caBackBtn);
        caSpeechBtn = findViewById(R.id.caSpeechBtn);

        // We locate our ProgressBar using findViewById().
        caProgressBar = findViewById(R.id.caProgressBar);

        // We retrieve the selected profile from the Intent using the color and shape.
        caProfileColor = getIntent().getStringExtra("colorName");
        caProfileShape = getIntent().getStringExtra("shapeName");

        // If the profile is somehow missing, we show a Toast message and stop the activity.
        if (caProfileColor == null || caProfileShape == null) {
            Toast.makeText(this, "Missing profile data!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // We load any previously saved progress for this profile.
        // This lets the student continue exactly where they left off.
        int caSavedProgress = dbHelper.getProgress(caProfileColor, caProfileShape, "counting");
        caProgressBar.setProgress(caSavedProgress);

        // We load the fragment that displays the tracing area.
        FragmentManager fManager = getSupportFragmentManager();
        Fragment fragment = fManager.findFragmentById(R.id.countingActivityFragment);

        // We check if the fragment already exists, if does then we reuse it. Otherwise, we create a new one.
        if (fragment instanceof CountingViewerFragment) {
            countingViewerFragment = (CountingViewerFragment) fragment;
        }
        else {
            countingViewerFragment = new CountingViewerFragment();
            fManager.beginTransaction().replace(R.id.countingActivityFragment, countingViewerFragment).commit();
            fManager.executePendingTransactions();
        }

        // Now that the fragment is loaded we set it to the correct number base on the saved progress.
        countingViewerFragment.setCurrentNumberIndex(caSavedProgress);

        /* We set an onClickListener() for the clear button
        so when it is clicked it clears the drawing canvas*/
        caClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We first check that the viewer fragment is not null then we call
                // clearCanvas() to clear the canvas.
                if(countingViewerFragment != null){
                    countingViewerFragment.clearCanvas();
                }
            }
        });

        /* We set an onclickListener() to check if the forward image button is clicked.
        It moves to the next number only if the student drew something. This prevents
        skipping numbers without completing them.*/
        caForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We check if the viewer fragment is not null.
                if(countingViewerFragment != null){

                    // didWrite will return true if the student drew something on the canvas.
                    boolean didWrite = countingViewerFragment.nextNumberCanvas();
                    if(didWrite){
                        int barProgress = caProgressBar.getProgress();

                        // We only move the progress bar forward if we have not reached the end.
                        if (barProgress < caProgressBar.getMax()){
                            barProgress++;
                            caProgressBar.setProgress(barProgress);

                            // We save the progress into the database with updateProgress().
                            dbHelper.updateProgress(caProfileColor, caProfileShape, "counting", barProgress);
                        }
                    }
                }
            }
        });

        /* We set an setOnClickListener() for the back button.
         It is responsible for loading previous stored numbers and its drawing from
         student_drawing table. */
        caBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countingViewerFragment != null){
                    countingViewerFragment.previousNumberCanvas();
                }
            }
        });

        /* We now initialize the TextToSpeech and if the setup is successful, then we set the
        language to English. */
        caTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                caTTS.setLanguage(Locale.ENGLISH);
            }
        });

        // We set an onClickListen for the text to speech image button.
        caSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countingViewerFragment != null){

                    // We call getCurrentNumberIndex() to know exactly what phrase we want to use.
                    int index = countingViewerFragment.getCurrentNumberIndex();

                    // We make sure that the index is within the range of numberPhrases array.
                    if(index >= 0 && index < numberPhrases.length){
                        caTTS.speak(numberPhrases[index], TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        });
    }

    /* onCreateOptionsMenu() inflates the menu action bar for the counting activity. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.counting_menu, menu);
        return true;
    }

    /* onOptionsItemSelected() handles the menu button clicks like save or return to SubjectSelection. */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.cmSaveBtn){
            Toast.makeText(this, "Save button pressed!", Toast.LENGTH_LONG).show();
            if(countingViewerFragment != null){
                countingViewerFragment.saveCurrentCanvas();
            }
        }
        else if(itemId == R.id.cmMenuBtn){
            if(countingViewerFragment != null){
                countingViewerFragment.saveCurrentCanvas();
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}