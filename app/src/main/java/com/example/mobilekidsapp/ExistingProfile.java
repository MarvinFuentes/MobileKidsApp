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

public class ExistingProfile extends AppCompatActivity {
    Button eDoneBtn, eBackBtn;
    TextToSpeech etts;
    ImageButton eTxtToSpeechBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.existing_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        eDoneBtn = (Button) findViewById(R.id.eDoneBtn);
        eBackBtn = (Button) findViewById(R.id.eBackBtn);

        eTxtToSpeechBtn = findViewById(R.id.eSpeechBtn);

        eDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to subject selection page
            }
        });

        eBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExistingProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });


        etts = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                etts.setLanguage(Locale.ENGLISH);
            }
        });

        eTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtToSpeech = getString(R.string.existing_profile);
                etts.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }
}
