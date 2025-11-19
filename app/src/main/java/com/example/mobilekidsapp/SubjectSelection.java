package com.example.mobilekidsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class SubjectSelection extends AppCompatActivity {
    Button sBackBtn;
    TextToSpeech sTTS;
    ImageButton sTxtToSpeechBtn;
    TextView sAlphabetBtn, sCountingBtn, sMathBtn;
    ProgressBar sAlphabetBar, sCountingBar, sMathBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.subject_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sBackBtn = (Button) findViewById(R.id.sBackBtn);
        sAlphabetBtn = (TextView) findViewById(R.id.sAlphabetBtn);
        sCountingBtn = (TextView) findViewById(R.id.sCountingBtn);
        sMathBtn = (TextView) findViewById(R.id.sMathBtn);

        sTxtToSpeechBtn = findViewById(R.id.sSpeechBtn);

        sAlphabetBar = (ProgressBar) findViewById(R.id.sAlphabetBar);
        sCountingBar = (ProgressBar) findViewById(R.id.sCountingBar);

        sBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubjectSelection.this, ExistingProfile.class);
                startActivity(intent);
            }
        });

        sAlphabetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String colorName = getIntent().getStringExtra("colorName");
                String shapeName = getIntent().getStringExtra("shapeName");

                Intent intent = new Intent(SubjectSelection.this, AlphabetActivity.class);
                intent.putExtra("colorName", colorName);
                intent.putExtra("shapeName", shapeName);
                startActivity(intent);
            }
        });

        sCountingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String colorName = getIntent().getStringExtra("colorName");
                String shapeName = getIntent().getStringExtra("shapeName");

                Intent intent = new Intent(SubjectSelection.this, CountingActivity.class);
                intent.putExtra("colorName", colorName);
                intent.putExtra("shapeName", shapeName);
                startActivity(intent);
            }
        });

        sMathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String colorName = getIntent().getStringExtra("colorName");
                String shapeName = getIntent().getStringExtra("shapeName");

                Intent intent = new Intent(SubjectSelection.this, MathActivity.class);
                intent.putExtra("colorName", colorName);
                intent.putExtra("shapeName", shapeName);
                startActivity(intent);
            }
        });

        sTTS = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                sTTS.setLanguage(Locale.ENGLISH);
            }
        });

        sTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtToSpeech = getString(R.string.subject_selection);
                sTTS.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(sTTS != null){
            sTTS.stop();
            sTTS.shutdown();
        }
        super.onDestroy();
    }
}
