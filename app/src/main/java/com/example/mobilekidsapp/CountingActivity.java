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
    CountingViewerFragment countingViewerFragment;
    Button caClearBtn;
    ImageButton caForwardBtn, caBackBtn, caSpeechBtn;
    ProgressBar caProgressBar;
    StudentDd dbHelper;
    String caProfileColor, caProfileShape;
    private TextToSpeech caTTS;
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

        caClearBtn = (Button) findViewById(R.id.caClearBtn);

        caForwardBtn = findViewById(R.id.caForwardBtn);
        caBackBtn = findViewById(R.id.caBackBtn);
        caSpeechBtn = findViewById(R.id.caSpeechBtn);

        caProgressBar = findViewById(R.id.caProgressBar);

        caProfileColor = getIntent().getStringExtra("colorName");
        caProfileShape = getIntent().getStringExtra("shapeName");

        if (caProfileColor == null || caProfileShape == null) {
            Toast.makeText(this, "Missing profile data!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        int caSavedProgress = dbHelper.getProgress(caProfileColor, caProfileShape, "counting");
        caProgressBar.setProgress(caSavedProgress);

        //Load fragment
        FragmentManager fManager = getSupportFragmentManager();
        Fragment fragment = fManager.findFragmentById(R.id.countingActivityFragment);

        if (fragment instanceof CountingViewerFragment) {
            countingViewerFragment = (CountingViewerFragment) fragment;
        }
        else {
            countingViewerFragment = new CountingViewerFragment();
            fManager.beginTransaction().replace(R.id.countingActivityFragment, countingViewerFragment).commit();
            fManager.executePendingTransactions();
        }

        countingViewerFragment.setCurrentNumberIndex(caSavedProgress);

        caClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countingViewerFragment != null){
                    countingViewerFragment.clearCanvas();
                }
            }
        });

        caForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countingViewerFragment != null){
                    boolean didWrite = countingViewerFragment.nextNumberCanvas();
                    if(didWrite){
                        int barProgress = caProgressBar.getProgress();
                        if (barProgress < caProgressBar.getMax()){
                            barProgress++;
                            caProgressBar.setProgress(barProgress);
                            dbHelper.updateProgress(caProfileColor, caProfileShape, "counting", barProgress);
                        }
                    }
                }
            }
        });

        caBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countingViewerFragment != null){
                    countingViewerFragment.previousNumberCanvas();
                }
            }
        });

        caTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                caTTS.setLanguage(Locale.ENGLISH);
            }
        });

        caSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countingViewerFragment != null){
                    int index = countingViewerFragment.getCurrentNumberIndex();
                    if(index >= 0 && index < numberPhrases.length){
                        caTTS.speak(numberPhrases[index], TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.counting_menu, menu);
        return true;
    }

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