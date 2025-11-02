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

public class ProfileCreator extends AppCompatActivity {

    Button pDoneBtn, pBackBtn;
    TextToSpeech ptts;
    ImageButton pTxtToSpeechBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_creator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pDoneBtn = (Button) findViewById(R.id.pDoneBtn);
        pBackBtn = (Button) findViewById(R.id.pBackBtn);
        pTxtToSpeechBtn = findViewById(R.id.pSpeechBtn);

        pDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to subject selection page
            }
        });

        pBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileCreator.this, MainActivity.class);
                startActivity(intent);
            }
        });


        ptts = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                ptts.setLanguage(Locale.ENGLISH);
            }
        });

        pTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtToSpeech = getString(R.string.profile_creation);
                ptts.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

    }
}
