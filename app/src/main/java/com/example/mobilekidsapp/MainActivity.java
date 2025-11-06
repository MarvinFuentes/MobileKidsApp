package com.example.mobilekidsapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button mNewStudentBtn, mExistingStudentBtn;
    TextToSpeech tts;
    ImageButton mTxtToSpeechBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mNewStudentBtn = (Button) findViewById(R.id.newStudentBtn);
        mExistingStudentBtn = (Button) findViewById(R.id.ExistingStudentBtn);
        mTxtToSpeechBtn = findViewById(R.id.speechBtn);

        mNewStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileCreator.class);
                startActivity(intent);
            }
        });


        mExistingStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExistingProfile.class);
                startActivity(intent);
            }
        });

        tts = new TextToSpeech(this, status -> {
            if(status == TextToSpeech.SUCCESS){
                tts.setLanguage(Locale.ENGLISH);
            }
        });

        mTxtToSpeechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtToSpeech = getString(R.string.main_page_speech);
                tts.speak(txtToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}