package com.example.mobilekidsapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class SubjectSelection extends AppCompatActivity {
    Button sAlphabetBtn, sCountingBtn, sMathBtn, sBackBtn;
    TextToSpeech sTTS;
    ImageButton sTxtToSpeechBtn;

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
        sAlphabetBtn = (Button) findViewById(R.id.sAlphabetBtn);
        sCountingBtn = (Button) findViewById(R.id.sCountingBtn);
        sMathBtn = (Button) findViewById(R.id.sMathBtn);
        sBackBtn = (Button) findViewById(R.id.sBackBtn);

        sTxtToSpeechBtn = findViewById(R.id.sSpeechBtn);

        sAlphabetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Take to the alphabet activity
            }
        });

        sCountingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Take to the counting activity
            }
        });

        sMathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Take to the counting activity
            }
        });

        sBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubjectSelection.this, ExistingProfile.class);
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
