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

public class AlphabetActivity extends AppCompatActivity {
    AlphabetViewerFragment alphabetViewerFragment;
    Button aaClearBtn;
    ImageButton aaForwardBtn, aaBackBtn, aaSpeechBtn;
    ProgressBar aaProgressBar;
    StudentDd dbHelper;
    String aaProfileColor, aaProfileShape;
    private TextToSpeech aaTTS;
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

        aaClearBtn = (Button) findViewById(R.id.aaClearBtn);

        aaForwardBtn = findViewById(R.id.aaForwardBtn);
        aaBackBtn = findViewById(R.id.aaBackBtn);
        aaSpeechBtn = findViewById(R.id.aaSpeechBtn);

        aaProgressBar = findViewById(R.id.aaProgressBar);

        aaProfileColor = getIntent().getStringExtra("colorName");
        aaProfileShape = getIntent().getStringExtra("shapeName");

        if (aaProfileColor == null || aaProfileShape == null) {
            Toast.makeText(this, "Missing profile data!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        int aaSavedProgress = dbHelper.getProgress(aaProfileColor, aaProfileShape, "alphabet");
        aaProgressBar.setProgress(aaSavedProgress);

        //Load fragment
        FragmentManager fManager = getSupportFragmentManager();
        Fragment fragment = fManager.findFragmentById(R.id.alphabetActivityFragment);

        if (fragment instanceof AlphabetViewerFragment) {
            alphabetViewerFragment = (AlphabetViewerFragment) fragment;
        }
        else {
            alphabetViewerFragment = new AlphabetViewerFragment();
            fManager.beginTransaction().replace(R.id.alphabetActivityFragment, alphabetViewerFragment).commit();
            fManager.executePendingTransactions();
        }

        alphabetViewerFragment.setCurrentLetterIndex(aaSavedProgress);

        aaClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alphabetViewerFragment != null){
                    alphabetViewerFragment.clearCanvas();
                }
            }
        });

        aaForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alphabetViewerFragment != null){
                    boolean didWrite = alphabetViewerFragment.nextLetterCanvas();
                    if(didWrite){
                        int barProgress = aaProgressBar.getProgress();
                        if (barProgress < aaProgressBar.getMax()){
                            barProgress++;
                            aaProgressBar.setProgress(barProgress);
                            dbHelper.updateProgress(aaProfileColor, aaProfileShape, "alphabet", barProgress);
                        }
                    }
                }
            }
        });

        aaBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alphabetViewerFragment != null){
                    alphabetViewerFragment.previousLetterCanvas();
                }
            }
        });

        aaTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                aaTTS.setLanguage(Locale.ENGLISH);
            }
        });

        aaSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alphabetViewerFragment != null){
                    int index = alphabetViewerFragment.getCurrentLetterIndex();
                    if(index >= 0 && index < letterPhrases.length){
                        aaTTS.speak(letterPhrases[index], TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alphabet_menu, menu);
        return true;
    }

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