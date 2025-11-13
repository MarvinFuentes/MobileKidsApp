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

public class CountingActivity extends AppCompatActivity {
    CountingViewerFragment countingViewerFragment;
    Button caClearBtn;
    ImageButton caForwardBtn, caBackBtn, caSpeechBtn;
    ProgressBar caProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        caProgressBar.setProgress(0);

        FragmentManager fManager = getSupportFragmentManager();
        Fragment fragment = fManager.findFragmentById(R.id.countingActivityFragment);

        if (fragment instanceof CountingViewerFragment) {
            countingViewerFragment = (CountingViewerFragment) fragment;
        }
        else {
            countingViewerFragment = new CountingViewerFragment();
            fManager.beginTransaction().replace(R.id.countingActivityFragment, countingViewerFragment).commit();
        }

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
                            caProgressBar.setProgress(barProgress + 1);
                        }
                    }
                }
            }
        });

        caBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countingViewerFragment != null) countingViewerFragment.previousNumberCanvas();
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
        }
        else if(itemId == R.id.cmMenuBtn){
            Intent intent = new Intent(CountingActivity.this, SubjectSelection.class);
            /*
            intent.putExtra("colorName", existingProfile.eSelectedColorName);
            intent.putExtra("shapeName", existingProfile.eSelectedShapeName);*/
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}