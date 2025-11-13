package com.example.mobilekidsapp;

import android.content.Intent;
import android.os.Bundle;
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

public class AlphabetActivity extends AppCompatActivity {
    ExistingProfile existingProfile;
    AlphabetViewerFragment alphabetViewerFragment;
    Button aaClearBtn;
    ImageButton aaForwardBtn, aaBackBtn, aaSpeechBtn;
    ProgressBar aaProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.alphabet_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FragmentManager fManager = getSupportFragmentManager();
        Fragment fragment = fManager.findFragmentById(R.id.alphabetActivityFragment);

        if (fragment instanceof AlphabetViewerFragment) {
            alphabetViewerFragment = (AlphabetViewerFragment) fragment;
        } else {
            alphabetViewerFragment = new AlphabetViewerFragment();
            fManager.beginTransaction()
                    .replace(R.id.alphabetActivityFragment, alphabetViewerFragment)
                    .commit();
        }

        aaProgressBar = findViewById(R.id.aaProgressBar);
        aaProgressBar.setProgress(0);

        aaClearBtn = (Button) findViewById(R.id.aaClearBtn);
        aaForwardBtn = findViewById(R.id.aaForwardBtn);
        aaBackBtn = findViewById(R.id.aaBackBtn);
        aaSpeechBtn = findViewById(R.id.aaSpeechBtn);

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
                            aaProgressBar.setProgress(barProgress + 1);
                        }
                    }
                }
            }
        });

        aaBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alphabetViewerFragment != null) alphabetViewerFragment.previousLetterCanvas();
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
        }
        else if(itemId == R.id.amMenuBtn){
            Intent intent = new Intent(AlphabetActivity.this, SubjectSelection.class);
            /*
            intent.putExtra("colorName", existingProfile.eSelectedColorName);
            intent.putExtra("shapeName", existingProfile.eSelectedShapeName);*/
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}